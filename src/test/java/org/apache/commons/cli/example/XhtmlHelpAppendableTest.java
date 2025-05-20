/*
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      https://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package org.apache.commons.cli.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.cli.help.TableDefinition;
import org.apache.commons.cli.help.TextStyle;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link XhtmlHelpAppendable}.
 */
public class XhtmlHelpAppendableTest {

    private StringBuilder sb;
    private XhtmlHelpAppendable underTest;

    @BeforeEach
    public void beforeEach() {
        sb = new StringBuilder();
        underTest = new XhtmlHelpAppendable(sb);
    }

    @Test
    public void testAppendHeaderTest() throws IOException {
        underTest.appendHeader(1, "Hello World");
        assertEquals(String.format("<h1>Hello World</h1>%n"), sb.toString());
        sb.setLength(0);
        underTest.appendHeader(2, "Hello World");
        assertEquals(String.format("<h2>Hello World</h2>%n"), sb.toString());
        sb.setLength(0);
        assertThrows(IllegalArgumentException.class, () -> underTest.appendHeader(0, "Hello World"));
    }

    @Test
    public void testAppendListTest() throws IOException {
        final String[] entries = { "one", "two", "three" };
        underTest.appendList(true, Arrays.asList(entries));
        assertEquals(String.format("<ol>%n  <li>one</li>%n  <li>two</li>%n  <li>three</li>%n</ol>%n"), sb.toString());
        sb.setLength(0);
        underTest.appendList(false, Arrays.asList(entries));
        assertEquals(String.format("<ul>%n  <li>one</li>%n  <li>two</li>%n  <li>three</li>%n</ul>%n"), sb.toString());
    }

    @Test
    public void testAppendParagraphFormatTest() throws IOException {
        underTest.appendParagraphFormat("Hello %s World %,d", "Joe", 309);
        assertEquals(String.format("<p>Hello Joe World 309</p>%n"), sb.toString());
    }

    @Test
    public void testAppendParagraphTest() throws IOException {
        underTest.appendParagraph("Hello World");
        assertEquals(String.format("<p>Hello World</p>%n"), sb.toString());
    }

    @Test
    public void testAppendTableTest() throws IOException {
        final List<TextStyle> styles = Arrays.asList(TextStyle.DEFAULT, TextStyle.DEFAULT, TextStyle.DEFAULT);
        final String[] headers = { "one", "two", "three" };
        // @formatter:off
        final List<List<String>> rows = Arrays.asList(
                Arrays.asList(new String[]{"uno", "dos", "tres"}),
                Arrays.asList(new String[]{"aon", "dhá", "trí"}),
                Arrays.asList(new String[]{"واحد", "اثنين", "ثلاثة"})
        );
        // @formatter:on
        List<String> expected = new ArrayList<>();
        expected.add("<table class='commons_cli_table'>");
        expected.add("  <caption>The caption</caption>");
        expected.add("  <tr>");
        expected.add("    <th>one</th>");
        expected.add("    <th>two</th>");
        expected.add("    <th>three</th>");
        expected.add("  </tr>");
        expected.add("  <tr>");
        expected.add("    <td>uno</td>");
        expected.add("    <td>dos</td>");
        expected.add("    <td>tres</td>");
        expected.add("  </tr>");
        expected.add("  <tr>");
        expected.add("    <td>aon</td>");
        expected.add("    <td>dh&aacute;</td>");
        expected.add("    <td>tr&iacute;</td>");
        expected.add("  </tr>");
        expected.add("  <tr>");
        expected.add("    <td>واحد</td>");
        expected.add("    <td>اثنين</td>");
        expected.add("    <td>ثلاثة</td>");
        expected.add("  </tr>");
        expected.add("</table>");
        TableDefinition table = TableDefinition.from("The caption", styles, Arrays.asList(headers), rows);
        underTest.appendTable(table);
        List<String> actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual, "full table failed");
        table = TableDefinition.from(null, styles, Arrays.asList(headers), rows);
        expected.remove(1);
        sb.setLength(0);
        underTest.appendTable(table);
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual);
        table = TableDefinition.from(null, styles, Arrays.asList(headers), Collections.emptyList());
        expected = new ArrayList<>();
        expected.add("<table class='commons_cli_table'>");
        expected.add("  <tr>");
        expected.add("    <th>one</th>");
        expected.add("    <th>two</th>");
        expected.add("    <th>three</th>");
        expected.add("  </tr>");
        expected.add("</table>");
        sb.setLength(0);
        underTest.appendTable(table);
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual, "no rows test failed");
    }

    @Test
    public void testAppendTitleTest() throws IOException {
        underTest.appendTitle("Hello World");
        assertEquals(String.format("<span class='commons_cli_title'>Hello World</span>%n"), sb.toString());
    }
}
