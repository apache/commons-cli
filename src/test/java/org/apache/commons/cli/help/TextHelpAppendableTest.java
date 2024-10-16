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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests {@link TextHelpAppendable}.
 */
public final class TextHelpAppendableTest {

    private StringBuilder sb;
    private TextHelpAppendable underTest;

    @BeforeEach
    public void setUp() {
        sb = new StringBuilder();
        underTest = new TextHelpAppendable(sb);
    }

    @Test
    public void tesstMakeColumnQueue() {
        final String text = "The quick brown fox jumps over the lazy dog";
        final TextStyle.Builder styleBuilder = TextStyle.builder().setMaxWidth(10).setIndent(0).setLeftPad(0);

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
    public void testAdjustTableFormat() {
        // test width smaller than header
        // @formatter:off
        final TableDefinition tableDefinition = TableDefinition.from("Testing",
                Collections.singletonList(TextStyle.builder().setMaxWidth(3).get()),
                Collections.singletonList("header"),
                // "data" shorter than "header"
                Collections.singletonList(Collections.singletonList("data"))
        );
        // @formatter:on
        final TableDefinition actual = underTest.adjustTableFormat(tableDefinition);
        assertEquals("header".length(), actual.columnTextStyles().get(0).getMaxWidth());
        assertEquals("header".length(), actual.columnTextStyles().get(0).getMinWidth());
    }

    @Test
    public void testAppend() throws IOException {
        final char c = (char) 0x1F44D;
        underTest.append(c);
        assertEquals(1, sb.length());
        assertEquals(String.valueOf(c), sb.toString());

        sb.setLength(0);
        underTest.append("Hello");
        assertEquals("Hello", sb.toString());
    }

    @Test
    public void testAppendHeader() throws IOException {
        final String[] expected = { " Hello World", " ===========", "" };

        sb.setLength(0);
        underTest.appendHeader(1, "Hello World");
        List<String> actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(Arrays.asList(expected), actual, "header 1 failed");

        sb.setLength(0);
        underTest.appendHeader(2, "Hello World");
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        expected[1] = " %%%%%%%%%%%";
        assertEquals(Arrays.asList(expected), actual, "header 2 failed");

        sb.setLength(0);
        underTest.appendHeader(3, "Hello World");
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        expected[1] = " +++++++++++";
        assertEquals(Arrays.asList(expected), actual, "header 3 failed");

        sb.setLength(0);
        underTest.appendHeader(4, "Hello World");
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        expected[1] = " ___________";
        assertEquals(Arrays.asList(expected), actual, "header 4 failed");

        sb.setLength(0);
        underTest.appendHeader(5, "Hello World");
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(Arrays.asList(expected), actual, "header 5 failed");

        sb.setLength(0);
        assertThrows(IllegalArgumentException.class, () -> underTest.appendHeader(0, "Hello World"));

        sb.setLength(0);
        underTest.appendHeader(5, "");
        assertEquals(0, sb.length(), "empty string test failed");

        sb.setLength(0);
        underTest.appendHeader(5, null);
        assertEquals(0, sb.length(), "null test failed");
    }

    @Test
    public void testAppendList() throws IOException {
        final List<String> expected = new ArrayList<>();
        final String[] entries = { "one", "two", "three" };
        for (int i = 0; i < entries.length; i++) {
            expected.add(String.format("  %s. %s", i + 1, entries[i]));
        }
        expected.add("");

        sb.setLength(0);
        underTest.appendList(true, Arrays.asList(entries));
        List<String> actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual, "ordered list failed");

        sb.setLength(0);
        expected.clear();
        for (final String entry : entries) {
            expected.add(String.format("  * %s", entry));
        }
        expected.add("");
        underTest.appendList(false, Arrays.asList(entries));
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual, "unordered list failed");

        sb.setLength(0);
        expected.clear();
        underTest.appendList(false, Collections.emptyList());
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual, "empty list failed");

        sb.setLength(0);
        expected.clear();
        underTest.appendList(false, null);
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual, "null list failed");
    }

    @Test
    public void testAppendParagraph() throws IOException {
        final String[] expected = { " Hello World", "" };

        sb.setLength(0);
        underTest.appendParagraph("Hello World");
        final List<String> actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(Arrays.asList(expected), actual);

        sb.setLength(0);
        underTest.appendParagraph("");
        assertEquals(0, sb.length(), "empty string test failed");

        sb.setLength(0);
        underTest.appendParagraph(null);
        assertEquals(0, sb.length(), "null test failed");
    }

    @Test
    public void testAppendTable() throws IOException {
        final TextStyle.Builder styleBuilder = TextStyle.builder();
        final List<TextStyle> styles = new ArrayList<>();
        styles.add(styleBuilder.setIndent(2).get());
        styles.add(styleBuilder.setIndent(0).setLeftPad(5).setAlignment(TextStyle.Alignment.RIGHT).get());
        final String[] headers = { "fox", "time" };
        // @formatter:off
        final List<List<String>> rows = Arrays.asList(
                Arrays.asList("The quick brown fox jumps over the lazy dog",
                        "Now is the time for all good people to come to the aid of their country"),
                Arrays.asList("Léimeann an sionnach donn gasta thar an madra leisciúil",
                        "Anois an t-am do na daoine maithe go léir teacht i gcabhair ar a dtír")
        );
        // @formatter:on

        List<String> expected = new ArrayList<>();
        expected.add(" Common Phrases");
        expected.add("");
        expected.add("               fox                                       time                   ");
        expected.add(" The quick brown fox jumps over           Now is the time for all good people to");
        expected.add("   the lazy dog                                 come to the aid of their country");
        expected.add(" Léimeann an sionnach donn gasta       Anois an t-am do na daoine maithe go léir");
        expected.add("   thar an madra leisciúil                           teacht i gcabhair ar a dtír");
        expected.add("");

        TableDefinition table = TableDefinition.from("Common Phrases", styles, Arrays.asList(headers), rows);
        sb.setLength(0);
        underTest.setMaxWidth(80);
        underTest.appendTable(table);
        List<String> actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual, "full table failed");

        table = TableDefinition.from(null, styles, Arrays.asList(headers), rows);
        expected.remove(1);
        expected.remove(0);
        sb.setLength(0);
        underTest.appendTable(table);
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual);

        table = TableDefinition.from(null, styles, Arrays.asList(headers), Collections.emptyList());
        expected = new ArrayList<>();
        expected.add(" fox     time");
        expected.add("");
        sb.setLength(0);
        underTest.appendTable(table);
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual, "no rows test failed");
    }

    @Test
    public void testAppendTitle() throws IOException {
        final String[] expected = { " Hello World", " ###########", "" };

        sb.setLength(0);
        underTest.appendTitle("Hello World");
        final List<String> actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(Arrays.asList(expected), actual);

        sb.setLength(0);
        underTest.appendTitle("");
        assertEquals(0, sb.length(), "empty string test failed");

        sb.setLength(0);
        underTest.appendTitle(null);
        assertEquals(0, sb.length(), "null test failed");

    }

    @Test
    public void testindexOfWrapPos() {
        final String testString = "The quick brown fox jumps over\tthe lazy dog";

        assertEquals(9, TextHelpAppendable.indexOfWrap(testString, 10, 0), "did not find end of word");
        assertEquals(9, TextHelpAppendable.indexOfWrap(testString, 14, 0), "did not backup to end of word");
        assertEquals(15, TextHelpAppendable.indexOfWrap(testString, 15, 0), "did not find word at 15");
        assertEquals(15, TextHelpAppendable.indexOfWrap(testString, 16, 0));
        assertEquals(30, TextHelpAppendable.indexOfWrap(testString, 15, 20), "did not find break character");
        assertEquals(30, TextHelpAppendable.indexOfWrap(testString, 150, 0), "did not handle text shorter than width");

        assertThrows(IllegalArgumentException.class, () -> TextHelpAppendable.indexOfWrap("", 0, 0));
        assertEquals(3, TextHelpAppendable.indexOfWrap("Hello", 4, 0));
    }

    @ParameterizedTest
    @MethodSource("org.apache.commons.cli.help.UtilTest#charArgs")
    public void testindexOfWrapPosWithWhitespace(final Character c, final boolean isWhitespace) {
        final String text = String.format("Hello%cWorld", c);
        assertEquals(isWhitespace ? 5 : 6, TextHelpAppendable.indexOfWrap(text, 7, 0));
    }

    @Test
    public void testGetStyleBuilder() {
        final TextStyle.Builder builder = underTest.getTextStyleBuilder();
        assertEquals(TextHelpAppendable.DEFAULT_INDENT, builder.getIndent(), "Default indent value was changed, some tests may fail");
        assertEquals(TextHelpAppendable.DEFAULT_LEFT_PAD, builder.getLeftPad(), "Default left pad value was changed, some tests may fail");
        assertEquals(TextHelpAppendable.DEFAULT_WIDTH, builder.getMaxWidth(), "Default width value was changed, some tests may fail");
    }

    @Test
    public void testPrintWrapped() throws IOException {
        String text = "The quick brown fox jumps over the lazy dog";
        final TextStyle.Builder styleBuilder = TextStyle.builder().setMaxWidth(10).setIndent(0).setLeftPad(0);

        final List<String> expected = new ArrayList<>();
        expected.add("The quick");
        expected.add("brown fox");
        expected.add("jumps over");
        expected.add("the lazy");
        expected.add("dog");
        underTest.printWrapped(text, styleBuilder.get());
        List<String> actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual, "left aligned failed");

        sb.setLength(0);
        expected.clear();
        expected.add(" The quick");
        expected.add(" brown fox");
        expected.add("jumps over");
        expected.add("  the lazy");
        expected.add("       dog");
        styleBuilder.setAlignment(TextStyle.Alignment.RIGHT);

        underTest.printWrapped(text, styleBuilder.get());
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual, "right aligned failed");

        sb.setLength(0);
        expected.clear();
        expected.add("The quick");
        expected.add("brown fox");
        expected.add("jumps over");
        expected.add(" the lazy");
        expected.add("   dog");
        styleBuilder.setAlignment(TextStyle.Alignment.CENTER);

        underTest.printWrapped(text, styleBuilder.get());
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual, "center aligned failed");

        sb.setLength(0);
        expected.clear();
        expected.add(" The quick brown fox jumps over the lazy dog");

        assertEquals(1, underTest.getLeftPad(), "unexpected page left pad");
        assertEquals(3, underTest.getIndent(), "unexpected page indent");
        assertEquals(74, underTest.getMaxWidth(), "unexpected page width");
        underTest.printWrapped(text);
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual, "default format aligned failed");

        sb.setLength(0);
        text += ".\nNow is the time for all good people to come to the aid of their country.";
        expected.clear();
        expected.add(" The quick brown fox jumps over the lazy dog.");
        expected.add("    Now is the time for all good people to come to the aid of their");
        expected.add("    country.");
        underTest.printWrapped(text);
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual, "default format aligned failed");
    }

    @Test
    public void testResize() {
        TextStyle.Builder tsBuilder = TextStyle.builder().setIndent(2).setMaxWidth(3);
        underTest.resize(tsBuilder, 0.5);
        assertEquals(0, tsBuilder.getIndent());

        tsBuilder = TextStyle.builder().setIndent(4).setMaxWidth(6);
        underTest.resize(tsBuilder, 0.5);
        assertEquals(1, tsBuilder.getIndent());
    }

    @Test
    public void testResizeTableFormat() {
        underTest.setMaxWidth(150);
        final TableDefinition tableDefinition = TableDefinition.from("Caption",
                Collections.singletonList(TextStyle.builder().setMinWidth(20).setMaxWidth(100).get()), Collections.singletonList("header"),
                Collections.singletonList(Collections.singletonList("one")));
        final TableDefinition result = underTest.adjustTableFormat(tableDefinition);
        assertEquals(20, result.columnTextStyles().get(0).getMinWidth(), "Minimum width should not be reset");
        assertEquals(100, result.columnTextStyles().get(0).getMaxWidth(), "Maximum width should not be reset");
    }

    @Test
    public void testSetIndent() {
        assertEquals(TextHelpAppendable.DEFAULT_INDENT, underTest.getIndent(), "Default indent value was changed, some tests may fail");
        underTest.setIndent(TextHelpAppendable.DEFAULT_INDENT + 2);
        assertEquals(underTest.getIndent(), TextHelpAppendable.DEFAULT_INDENT + 2);
    }

    @Test
    public void testWriteColumnQueues() throws IOException {
        final List<Queue<String>> queues = new ArrayList<>();

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

        final TextStyle.Builder styleBuilder = TextStyle.builder().setMaxWidth(10).setIndent(0).setLeftPad(0);

        final List<TextStyle> columns = new ArrayList<>();
        columns.add(styleBuilder.get());
        columns.add(styleBuilder.setLeftPad(5).get());

        final List<String> expected = new ArrayList<>();
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
        final List<String> actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual);
    }
}
