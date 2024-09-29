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
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public final class TextSerializerTest {

    private StringBuilder sb;
    private TextSerializer underTest;

    @BeforeEach
    public void setUp() {
        sb = new StringBuilder();
        underTest = new TextSerializer(sb);
    }

    @Test
    public void writeTitleTest() throws IOException {
        String[] expected = {" Hello World", " ###########", ""};

        sb.setLength(0);
        underTest.writeTitle("Hello World");
        List<String> actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(Arrays.asList(expected), actual);
    }

    @Test
    public void writeParaTest() throws IOException {
        String[] expected = {" Hello World", ""};

        sb.setLength(0);
        underTest.writePara("Hello World");
        List<String> actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(Arrays.asList(expected), actual);
    }

    @Test
    public void writeHeaderTest() throws IOException {
        String[] expected = {" Hello World", " ===========", ""};

        sb.setLength(0);
        underTest.writeHeader(1, "Hello World");
        List<String> actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(Arrays.asList(expected), actual, "header 1 failed");

        sb.setLength(0);
        underTest.writeHeader(2, "Hello World");
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        expected[1] = " %%%%%%%%%%%";
        assertEquals(Arrays.asList(expected), actual, "header 2 failed");

        sb.setLength(0);
        underTest.writeHeader(3, "Hello World");
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        expected[1] = " +++++++++++";
        assertEquals(Arrays.asList(expected), actual, "header 3 failed");

        sb.setLength(0);
        underTest.writeHeader(4, "Hello World");
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        expected[1] = " ___________";
        assertEquals(Arrays.asList(expected), actual, "header 4 failed");

        sb.setLength(0);
        underTest.writeHeader(5, "Hello World");
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(Arrays.asList(expected), actual, "header 5 failed");

        sb.setLength(0);
        assertThrows(IllegalArgumentException.class, () -> underTest.writeHeader(0, "Hello World"));
    }
    @Test
    public void writeListTest() throws IOException {
        List<String> expected = new ArrayList<>();
        String[] entries = {"one", "two", "three"};
        for (int i = 0; i < entries.length; i++) {
            expected.add(format("  %s. %s", i + 1, entries[i]));
        }
        expected.add("");

        sb.setLength(0);
        underTest.writeList(true, Arrays.asList(entries));
        List<String> actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual, "ordered list failed");

        sb.setLength(0);
        expected.clear();
        for (int i = 0; i < entries.length; i++) {
            expected.add(format("  * %s", entries[i]));
        }
        expected.add("");
        underTest.writeList(false, Arrays.asList(entries));
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual, "unordered list failed");

        sb.setLength(0);
        expected.clear();
        underTest.writeList(false, Collections.emptyList());
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual, "empty list failed");
    }

    @Test
    public void writeTableTest() throws IOException {
        TextStyle.Builder styleBuilder = new TextStyle.Builder();
        List<TextStyle> styles = new ArrayList<>();
        styles.add(styleBuilder.setIndent(2).get());
        styles.add(styleBuilder.setIndent(0).setLeftPad(5).setAlignment(TextStyle.Alignment.RIGHT).get());

        String[] headers = { "fox", "time"};

        List[] rows = {
                Arrays.asList("The quick brown fox jumps over the lazy dog",
                        "Now is the time for all good people to come to the aid of their country"),
                Arrays.asList("Léimeann an sionnach donn gasta thar an madra leisciúil",
                        "Anois an t-am do na daoine maithe go léir teacht i gcabhair ar a dtír"),
        };

        List<String> expected = new ArrayList<>();
        expected.add(" Common Phrases");
        expected.add("");
        expected.add("               fox                                       time                   ");
        expected.add(" The quick brown fox jumps over           Now is the time for all good people to");
        expected.add("   the lazy dog                                 come to the aid of their country");
        expected.add(" Léimeann an sionnach donn gasta       Anois an t-am do na daoine maithe go léir");
        expected.add("   thar an madra leisciúil                           teacht i gcabhair ar a dtír");
        expected.add("");

        TableDef table = TableDef.from("Common Phrases", styles, Arrays.asList(headers), Arrays.asList(rows));
        sb.setLength(0);
        underTest.setMaxWidth(80);
        underTest.writeTable(table);
        List<String> actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual, "full table failed");

        table = TableDef.from(null, styles, Arrays.asList(headers), Arrays.asList(rows));
        expected.remove(1);
        expected.remove(0);
        sb.setLength(0);
        underTest.writeTable(table);
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual);

        table = TableDef.from(null, styles, Arrays.asList(headers), Collections.emptyList());
        expected = new ArrayList<>();
        expected.add(" fox     time");
        expected.add("");
        sb.setLength(0);
        underTest.writeTable(table);
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual, "no rows test failed");
    }

    @Test
    public void makeColumnQueueTest() {
        String text = "The quick brown fox jumps over the lazy dog";
        TextStyle.Builder styleBuilder = new TextStyle.Builder().setMaxWidth(10).setIndent(0).setLeftPad(0);

        Queue<String> expected = new LinkedList<>();
        expected.add("The quick ");
        expected.add("brown fox ");
        expected.add("jumps over");
        expected.add("the lazy  ");
        expected.add("dog       ");

        Queue<String> result = underTest.makeColumnQueue(text, styleBuilder.get());
        assertEquals(expected, result, "left aligned failed");

        expected.clear();
        expected.add(" The quick");
        expected.add(" brown fox");
        expected.add("jumps over");
        expected.add("  the lazy");
        expected.add("       dog");
        styleBuilder.setAlignment(TextStyle.Alignment.RIGHT);

        result = underTest.makeColumnQueue(text, styleBuilder.get());
        assertEquals(expected, result, "right aligned failed");

        expected.clear();
        expected.add("The quick ");
        expected.add("brown fox ");
        expected.add("jumps over");
        expected.add(" the lazy ");
        expected.add("   dog    ");
        styleBuilder.setAlignment(TextStyle.Alignment.CENTER);

        result = underTest.makeColumnQueue(text, styleBuilder.get());
        assertEquals(expected, result, "center aligned failed");

        expected = new LinkedList<>();
        expected.add("      The quick");
        expected.add("          brown");
        expected.add("            fox");
        expected.add("          jumps");
        expected.add("       over the");
        expected.add("       lazy dog");
        styleBuilder.setAlignment(TextStyle.Alignment.RIGHT).setLeftPad(5).setIndent(2);

        result = underTest.makeColumnQueue(text, styleBuilder.get());
        assertEquals(expected, result, "right aligned failed");
    }


    @Test
    public void writeColumnQueuesTest() throws IOException {
        List<Queue<String>> queues = new ArrayList<>();

        Queue<String> queue = new LinkedList<>();
        queue.add("The quick ");
        queue.add("brown fox ");
        queue.add("jumps over");
        queue.add("the lazy  ");
        queue.add("dog       ");

        queues.add(queue);

        queue = new LinkedList<>();
        queue.add("     Now is the");
        queue.add("     time for  ");
        queue.add("     all good  ");
        queue.add("     people to ");
        queue.add("     come to   ");
        queue.add("     the aid of");
        queue.add("     their     ");
        queue.add("     country   ");

        queues.add(queue);

        String text = "The quick brown fox jumps over the lazy dog";
        TextStyle.Builder styleBuilder = new TextStyle.Builder().setMaxWidth(10).setIndent(0).setLeftPad(0);

        List<TextStyle> columns = new ArrayList<>();
        columns.add(styleBuilder.get());
        columns.add(styleBuilder.setLeftPad(5).get());

        List<String> expected = new ArrayList<>();
        expected.add(" The quick      Now is the");
        expected.add(" brown fox      time for  ");
        expected.add(" jumps over     all good  ");
        expected.add(" the lazy       people to ");
        expected.add(" dog            come to   ");
        expected.add("                the aid of");
        expected.add("                their     ");
        expected.add("                country   ");

        sb.setLength(0);
        underTest.writeColumnQueues(queues, columns);
        List<String> actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual);
    }
}
