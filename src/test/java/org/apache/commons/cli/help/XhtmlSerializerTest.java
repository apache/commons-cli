/*
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package org.apache.commons.cli.help;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.example.XhtmlSerializer;
import org.junit.jupiter.api.Test;

public class XhtmlSerializerTest {

    private StringBuilder sb = new StringBuilder();
    private XhtmlSerializer underTest = new XhtmlSerializer(sb);

    @Test
    public void writeTitleTest() throws IOException {
        sb.setLength(0);
        underTest.writeTitle("Hello World");
        assertEquals(format("<span class='commons_cli_title'>Hello World</span>%n"), sb.toString());
    }

    @Test
    public void writeParaTest() throws IOException {
        sb.setLength(0);
        underTest.writePara("Hello World");
        assertEquals(format("<p>Hello World</p>%n"), sb.toString());
    }

    @Test
    public void writeHeaderTest() throws IOException {
        sb.setLength(0);
        underTest.writeHeader(1, "Hello World");
        assertEquals(format("<h1>Hello World</h1>%n"), sb.toString());
        sb.setLength(0);
        underTest.writeHeader(2, "Hello World");
        assertEquals(format("<h2>Hello World</h2>%n"), sb.toString());
        sb.setLength(0);
        assertThrows(IllegalArgumentException.class, () -> underTest.writeHeader(0, "Hello World"));
    }

    @Test
    public void writeListTest() throws IOException {
        String[] entries = {"one", "two", "three"};
        sb.setLength(0);
        underTest.writeList(true, Arrays.asList(entries));
        assertEquals(format("<ol>%n  <li>one</li>%n  <li>two</li>%n  <li>three</li>%n</ol>%n"), sb.toString());

        sb.setLength(0);
        underTest.writeList(false, Arrays.asList(entries));
        assertEquals(format("<ul>%n  <li>one</li>%n  <li>two</li>%n  <li>three</li>%n</ul>%n"), sb.toString());
    }

    @Test
    public void writeTableTest() throws IOException {
        List<TextStyle> styles = Arrays.asList(TextStyle.DEFAULT, TextStyle.DEFAULT, TextStyle.DEFAULT);
        String[] headers = {"one", "two", "three"};
        List[] rows = {
                Arrays.asList(new String[]{"uno", "dos", "tres"}),
                Arrays.asList(new String[]{"aon", "dhá", "trí"}),
                Arrays.asList(new String[]{"واحد", "اثنين", "ثلاثة"})
        };

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
        expected.add("    <td>dhá</td>");
        expected.add("    <td>trí</td>");
        expected.add("  </tr>");
        expected.add("  <tr>");
        expected.add("    <td>واحد</td>");
        expected.add("    <td>اثنين</td>");
        expected.add("    <td>ثلاثة</td>");
        expected.add("  </tr>");
        expected.add("</table>");

        TableDef table = TableDef.from("The caption", styles, Arrays.asList(headers), Arrays.asList(rows));
        sb.setLength(0);
        underTest.writeTable(table);
        List<String> actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual, "full table failed");


        table = TableDef.from(null, styles, Arrays.asList(headers), Arrays.asList(rows));
        expected.remove(1);
        sb.setLength(0);
        underTest.writeTable(table);
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual);

        table = TableDef.from(null, styles, Arrays.asList(headers), Collections.emptyList());
        expected = new ArrayList<>();
        expected.add("<table class='commons_cli_table'>");
        expected.add("  <tr>");
        expected.add("    <th>one</th>");
        expected.add("    <th>two</th>");
        expected.add("    <th>three</th>");
        expected.add("  </tr>");
        expected.add("</table>");

        sb.setLength(0);
        underTest.writeTable(table);
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual, "no rows test failed");

    }
}
