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
 * Tests {@link AptHelpAppendable}.
 */
public class AptHelpAppendableTest {

    private StringBuilder sb;
    private AptHelpAppendable underTest;

    @BeforeEach
    public void beforeEach() {
        sb = new StringBuilder();
        underTest = new AptHelpAppendable(sb);
    }

    @Test
    void testAppendFormatTest() throws IOException {
        underTest.appendFormat("Big %s and Phantom %,d", "Joe", 309);
        assertEquals(String.format("Big Joe and Phantom 309"), sb.toString());
    }

    @Test
    void testAppendHeaderTest() throws IOException {
        underTest.appendHeader(1, "Hello World");
        assertEquals(String.format("* Hello World%n%n"), sb.toString());
        sb.setLength(0);
        underTest.appendHeader(2, "Hello World");
        assertEquals(String.format("** Hello World%n%n"), sb.toString());
        sb.setLength(0);
        assertThrows(IllegalArgumentException.class, () -> underTest.appendHeader(0, "Hello World"));
    }

    @Test
    void testAppendListTest() throws IOException {
        final String[] entries = { "one", "two", "three" };
        underTest.appendList(true, Arrays.asList(entries));
        assertEquals(String.format("    [[1]] one%n    [[2]] two%n    [[3]] three%n%n"), sb.toString());
        sb.setLength(0);
        underTest.appendList(false, Arrays.asList(entries));
        assertEquals(String.format("    * one%n    * two%n    * three%n%n"), sb.toString());
    }

    @Test
    void testAppendParagraphFormatTest() throws IOException {
        underTest.appendParagraphFormat("Hello %s World %,d", "Big Joe", 309);
        assertEquals(String.format("  Hello Big Joe World 309%n%n"), sb.toString());
    }

    @Test
    void testAppendParagraphTest() throws IOException {
        underTest.appendParagraph("Hello World");
        assertEquals(String.format("  Hello World%n%n"), sb.toString());
    }

    @Test
    void testAppendTableTest() throws IOException {
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
        expected.add("*-----+-----+-------+");
        expected.add("| one | two | three |");
        expected.add("*-----+-----+-------+");
        expected.add("| uno | dos | tres |");
        expected.add("*-----+-----+-------+");
        expected.add("| aon | dhá | trí |");
        expected.add("*-----+-----+-------+");
        expected.add("| واحد | اثنين | ثلاثة |");
        expected.add("*-----+-----+-------+");
        expected.add("The caption");
        expected.add("");
        TableDefinition table = TableDefinition.from("The caption", styles, Arrays.asList(headers), rows);
        underTest.appendTable(table);
        List<String> actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual, "full table failed");
        table = TableDefinition.from(null, styles, Arrays.asList(headers), rows);
        expected.remove(9);
        sb.setLength(0);
        underTest.appendTable(table);
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual);
        table = TableDefinition.from(null, styles, Arrays.asList(headers), Collections.emptyList());
        expected = new ArrayList<>();
        expected.add("*-----+-----+-------+");
        expected.add("| one | two | three |");
        expected.add("*-----+-----+-------+");
        expected.add("");
        sb.setLength(0);
        underTest.appendTable(table);
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual, "no rows test failed");

    }

    @Test
    void testAppendTitleTest() throws IOException {
        sb.setLength(0);
        underTest.appendTitle("Hello World");
        assertEquals(String.format("        -----%n        Hello World%n        -----%n%nHello World%n%n"), sb.toString());
    }
}
