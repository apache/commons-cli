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
package org.apache.commons.cli.help;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.example.XhtmlHelpAppendable;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link HelpFormatter}.
 */
class HelpFormatterTest {

    private Options getTestGroups() {
        // @formatter:off
        return new Options()
            .addOptionGroup(new OptionGroup()
                .addOption(Option.builder("1").longOpt("one").hasArg().desc("English one").build())
                .addOption(Option.builder().longOpt("aon").hasArg().desc("Irish one").build())
                .addOption(Option.builder().longOpt("uno").hasArg().desc("Spanish one").build())
            )
            .addOptionGroup(new OptionGroup()
                .addOption(Option.builder().longOpt("two").hasArg().desc("English two").build())
                .addOption(Option.builder().longOpt("dó").hasArg().desc("Irish twp").build())
                .addOption(Option.builder().longOpt("dos").hasArg().desc("Spanish two").build())
            )
            .addOptionGroup(new OptionGroup()
                .addOption(Option.builder().longOpt("three").hasArg().desc("English three").build())
                .addOption(Option.builder().longOpt("trí").hasArg().desc("Irish three").build())
                .addOption(Option.builder().longOpt("tres").hasArg().desc("Spanish three").build())
            );
        // @formatter:on
    }

    @Test
    void testDefault() {
        final StringBuilder sb = new StringBuilder();
        final TextHelpAppendable serializer = new TextHelpAppendable(sb);
        final HelpFormatter formatter = HelpFormatter.builder().setHelpAppendable(serializer).get();
        assertEquals(serializer, formatter.getSerializer(), "Unexpected helpAppendable tests may fail unexpectedly");
        assertEquals(AbstractHelpFormatter.DEFAULT_COMPARATOR, formatter.getComparator(), "Unexpected comparator tests may fail unexpectedly");
        assertEquals(AbstractHelpFormatter.DEFAULT_SYNTAX_PREFIX, formatter.getSyntaxPrefix(), "Unexpected syntax prefix tests may fail unexpectedly");
    }

    @Test
    void testPrintHelp() throws IOException {
        final StringBuilder sb = new StringBuilder();
        final TextHelpAppendable serializer = new TextHelpAppendable(sb);
        HelpFormatter formatter = HelpFormatter.builder().setHelpAppendable(serializer).get();

        final Options options = new Options().addOption(Option.builder("a").since("1853").hasArg().desc("aaaa aaaa aaaa aaaa aaaa").build());

        List<String> expected = new ArrayList<>();
        expected.add(" usage:  commandSyntax [-a <arg>]");
        expected.add("");
        expected.add(" header");
        expected.add("");
        expected.add(" Options      Since           Description       ");
        expected.add(" -a <arg>     1853      aaaa aaaa aaaa aaaa aaaa");
        expected.add("");
        expected.add(" footer");
        expected.add("");

        formatter.printHelp("commandSyntax", "header", options, "footer", true);
        List<String> actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual);

        formatter = HelpFormatter.builder().setShowSince(false).setHelpAppendable(serializer).get();
        expected = new ArrayList<>();
        expected.add(" usage:  commandSyntax [-a <arg>]");
        expected.add("");
        expected.add(" header");
        expected.add("");
        expected.add(" Options            Description       ");
        expected.add(" -a <arg>     aaaa aaaa aaaa aaaa aaaa");
        expected.add("");
        expected.add(" footer");
        expected.add("");

        sb.setLength(0);
        formatter.printHelp("commandSyntax", "header", options, "footer", true);
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual);

        sb.setLength(0);
        formatter.printHelp("commandSyntax", "header", options, "footer", false);
        expected.set(0, " usage:  commandSyntax");
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual);

        sb.setLength(0);
        formatter.printHelp("commandSyntax", "", options, "footer", false);
        expected.remove(3);
        expected.remove(2);
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual);

        sb.setLength(0);
        formatter.printHelp("commandSyntax", null, options, "footer", false);
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual);

        sb.setLength(0);
        formatter.printHelp("commandSyntax", null, options, "", false);
        expected.remove(6);
        expected.remove(5);
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual);

        sb.setLength(0);
        formatter.printHelp("commandSyntax", null, options, null, false);
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual);

        sb.setLength(0);
        final HelpFormatter fHelp = formatter;
        assertThrows(IllegalArgumentException.class, () -> fHelp.printHelp("", "header", options, "footer", true));
        assertEquals(0, sb.length(), "Should not write to output");
        assertThrows(IllegalArgumentException.class, () -> fHelp.printHelp(null, "header", options, "footer", true));
        assertEquals(0, sb.length(), "Should not write to output");
    }

    @Test
    void testPrintHelpXML() throws IOException {
        final StringBuilder sb = new StringBuilder();
        final XhtmlHelpAppendable serializer = new XhtmlHelpAppendable(sb);
        final HelpFormatter formatter = HelpFormatter.builder().setHelpAppendable(serializer).get();

        final Options options = new Options().addOption("a", false, "aaaa aaaa aaaa aaaa aaaa");

        final List<String> expected = new ArrayList<>();
        expected.add("<p>usage:  commandSyntax [-a]</p>");
        expected.add("<p>header</p>");
        expected.add("<table class='commons_cli_table'>");
        expected.add("  <tr>");
        expected.add("    <th>Options</th>");
        expected.add("    <th>Since</th>");
        expected.add("    <th>Description</th>");
        expected.add("  </tr>");
        expected.add("  <tr>");
        expected.add("    <td>-a</td>");
        expected.add("    <td>--</td>");
        expected.add("    <td>aaaa aaaa aaaa aaaa aaaa</td>");
        expected.add("  </tr>");
        expected.add("</table>");
        expected.add("<p>footer</p>");

        formatter.printHelp("commandSyntax", "header", options, "footer", true);
        final List<String> actual = IOUtils.readLines(new StringReader(sb.toString()));

        assertEquals(expected, actual);
    }

    @Test
    void testPrintOptions() throws IOException {
        final StringBuilder sb = new StringBuilder();
        final TextHelpAppendable serializer = new TextHelpAppendable(sb);
        final HelpFormatter formatter = HelpFormatter.builder().setHelpAppendable(serializer).setShowSince(false).get();

        // help format default column styles
        // col options description helpAppendable
        // styl FIXED VARIABLE VARIABLE
        // LPad 0 5 1
        // indent 1 1 3
        //
        // default helpAppendable

        Options options;
        List<String> expected = new ArrayList<>();
        expected.add(" Options           Description       ");
        expected.add(" -a          aaaa aaaa aaaa aaaa aaaa");
        expected.add("");

        options = new Options().addOption("a", false, "aaaa aaaa aaaa aaaa aaaa");

        formatter.printOptions(options);
        List<String> actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual);

        sb.setLength(0);
        serializer.setMaxWidth(30);
        expected = new ArrayList<>();
        expected.add(" Options        Description    ");
        expected.add(" -a          aaaa aaaa aaaa    ");
        expected.add("              aaaa aaaa        ");
        expected.add("");
        formatter.printOptions(options);
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(31, actual.get(0).length());
        assertEquals(expected, actual);

        sb.setLength(0);
        serializer.setLeftPad(5);
        expected = new ArrayList<>();
        expected.add("     Options        Description    ");
        expected.add("     -a          aaaa aaaa aaaa    ");
        expected.add("                  aaaa aaaa        ");
        expected.add("");
        formatter.printOptions(options);
        actual = IOUtils.readLines(new StringReader(sb.toString()));
        assertEquals(expected, actual);
    }

    @Test
    void testSetOptionFormatBuilderTest() {
        final HelpFormatter.Builder underTest = HelpFormatter.builder();
        final OptionFormatter.Builder ofBuilder = OptionFormatter.builder().setOptPrefix("Just Another ");
        underTest.setOptionFormatBuilder(ofBuilder);
        final HelpFormatter formatter = underTest.get();
        final OptionFormatter oFormatter = formatter.getOptionFormatter(Option.builder("thing").build());
        assertEquals("Just Another thing", oFormatter.getOpt());

    }

    @Test
    void testSetOptionGroupSeparatorTest() {
        final HelpFormatter.Builder underTest = HelpFormatter.builder().setOptionGroupSeparator(" and ");
        final HelpFormatter formatter = underTest.get();
        final String result = formatter.toSyntaxOptions(new OptionGroup().addOption(Option.builder("this").build()).addOption(Option.builder("that").build()));
        assertTrue(result.contains("-that and -this"));
    }

    @Test
    void testSortOptionGroupsTest() {
        final Options options = getTestGroups();
        final List<Option> optList = new ArrayList<>(options.getOptions());
        final HelpFormatter underTest = HelpFormatter.builder().get();
        final List<Option> expected = new ArrayList<>();
        expected.add(optList.get(0)); // because 1 sorts before all long values
        expected.add(optList.get(1));
        expected.add(optList.get(5));
        expected.add(optList.get(4));
        expected.add(optList.get(6));
        expected.add(optList.get(8));
        expected.add(optList.get(7));
        expected.add(optList.get(3));
        expected.add(optList.get(2));
        assertEquals(expected, underTest.sort(options));
    }

    @Test
    void testSortOptionsTest() {
        // @formatter:off
        final Options options = new Options()
            .addOption(Option.builder("a").longOpt("optA").hasArg().desc("The description of A").build())
            .addOption(Option.builder("b").longOpt("BOpt").hasArg().desc("B description").build())
            .addOption(Option.builder().longOpt("COpt").hasArg().desc("A COpt description").build());
        // @formatter:on

        HelpFormatter underTest = HelpFormatter.builder().get();
        final List<Option> expected = new ArrayList<>();
        expected.add(options.getOption("a"));
        expected.add(options.getOption("b"));
        expected.add(options.getOption("COpt"));

        assertEquals(expected, underTest.sort(options));

        expected.set(0, expected.get(2));
        expected.set(2, options.getOption("a"));
        underTest = HelpFormatter.builder().setComparator(AbstractHelpFormatter.DEFAULT_COMPARATOR.reversed()).get();
        assertEquals(expected, underTest.sort(options));

        assertEquals(0, underTest.sort(Collections.emptyList()).size(), "empty colleciton should return empty list");
        assertEquals(0, underTest.sort((Iterable<Option>) null).size(), "null iterable should return empty list");
        assertEquals(0, underTest.sort((Options) null).size(), "null Options should return empty list");
    }

    @Test
    void testSyntaxPrefix() {
        final StringBuilder sb = new StringBuilder();
        final TextHelpAppendable serializer = new TextHelpAppendable(sb);
        final HelpFormatter formatter = HelpFormatter.builder().setHelpAppendable(serializer).get();
        formatter.setSyntaxPrefix("Something new");
        assertEquals("Something new", formatter.getSyntaxPrefix());
        assertEquals(0, sb.length(), "Should not write to output");
    }

    @Test
    void testToArgNameTest() {
        final StringBuilder sb = new StringBuilder();
        final TextHelpAppendable serializer = new TextHelpAppendable(sb);
        final HelpFormatter formatter = HelpFormatter.builder().setHelpAppendable(serializer).get();

        assertEquals("<some Arg>", formatter.toArgName("some Arg"));
        assertEquals("<>", formatter.toArgName(""));
        assertEquals("<>", formatter.toArgName(null));
    }

    @Test
    void testToSyntaxOptionGroupTest() {
        final HelpFormatter underTest = HelpFormatter.builder().get();
        // @formatter:off
        final OptionGroup group = new OptionGroup()
            .addOption(Option.builder().option("o").longOpt("one").hasArg().build())
            .addOption(Option.builder().option("t").longOpt("two").hasArg().required().argName("other").build())
            .addOption(Option.builder().option("th").longOpt("three").required().argName("other").build())
            .addOption(Option.builder().option("f").argName("other").build())
            .addOption(Option.builder().longOpt("five").hasArg().argName("other").build())
            .addOption(Option.builder().longOpt("six").required().hasArg().argName("other").build())
            .addOption(Option.builder().option("s").longOpt("sevem").hasArg().build());
        // @formatter:on
        assertEquals("[-f | --five <other> | -o <arg> | -s <arg> | --six <other> | -t <other> | -th]", underTest.toSyntaxOptions(group));

        group.setRequired(true);
        assertEquals("-f | --five <other> | -o <arg> | -s <arg> | --six <other> | -t <other> | -th", underTest.toSyntaxOptions(group));

        assertEquals("", underTest.toSyntaxOptions(new OptionGroup()), "empty group should return empty string");
    }

    @Test
    void testToSyntaxOptionIterableTest() {
        final HelpFormatter underTest = HelpFormatter.builder().get();
        final List<Option> options = new ArrayList<>();

        options.add(Option.builder().option("o").longOpt("one").hasArg().build());
        options.add(Option.builder().option("t").longOpt("two").hasArg().required().argName("other").build());
        options.add(Option.builder().option("th").longOpt("three").required().argName("other").build());
        options.add(Option.builder().option("f").argName("other").build());
        options.add(Option.builder().longOpt("five").hasArg().argName("other").build());
        options.add(Option.builder().longOpt("six").required().hasArg().argName("other").build());
        options.add(Option.builder().option("s").longOpt("sevem").hasArg().build());
        assertEquals("[-f] [--five <other>] [-o <arg>] [-s <arg>] --six <other> -t <other> -th", underTest.toSyntaxOptions(options));

    }

    @Test
    void testToSyntaxOptionOptionsTest() {
        final HelpFormatter underTest = HelpFormatter.builder().get();
        Options options = getTestGroups();
        assertEquals("[-1 <arg> | --aon <arg> | --uno <arg>] [--dos <arg> | --dó <arg> | --two <arg>] " + "[--three <arg> | --tres <arg> | --trí <arg>]",
                underTest.toSyntaxOptions(options), "getTestGroup options failed");

        // @formatter:off
        options = new Options()
            .addOption(Option.builder().option("o").longOpt("one").hasArg().build())
            .addOption(Option.builder().option("t").longOpt("two").hasArg().required().argName("other").build())
            .addOption(Option.builder().option("th").longOpt("three").required().argName("other").build())
            .addOption(Option.builder().option("f").argName("other").build())
            .addOption(Option.builder().longOpt("five").hasArg().argName("other").build())
            .addOption(Option.builder().longOpt("six").required().hasArg().argName("other").build())
            .addOption(Option.builder().option("s").longOpt("seven").hasArg().build());
        // @formatter:on
        assertEquals("[-f] [--five <other>] [-o <arg>] [-s <arg>] --six <other> -t <other> -th", underTest.toSyntaxOptions(options), "assorted options failed");
        // @formatter:off
        options = new Options()
            .addOption(Option.builder().option("o").longOpt("one").hasArg().build())
            .addOptionGroup(
                new OptionGroup()
                    .addOption(Option.builder().option("t").longOpt("two").hasArg().required().argName("other").build())
                    .addOption(Option.builder().option("th").longOpt("three").required().argName("other").build()))
            .addOption(Option.builder().option("f").argName("other").build())
            .addOption(Option.builder().longOpt("five").hasArg().argName("other").build())
            .addOption(Option.builder().longOpt("six").required().hasArg().argName("other").build())
            .addOption(Option.builder().option("s").longOpt("seven").hasArg().build());
        // @formatter:on
        assertEquals("[-f] [--five <other>] [-o <arg>] [-s <arg>] --six <other> [-t <other> | -th]", underTest.toSyntaxOptions(options),
                "option with group failed");

        // @formatter:off
        final OptionGroup group1 = new OptionGroup()
            .addOption(Option.builder().option("t").longOpt("two").hasArg().required().argName("other").build())
            .addOption(Option.builder().option("th").longOpt("three").required().argName("other").build());
        // @formatter:on
        group1.setRequired(true);
        // @formatter:off
        options = new Options()
            .addOption(Option.builder().option("o").longOpt("one").hasArg().build())
            .addOptionGroup(group1)
            .addOption(Option.builder().option("f").argName("other").build())
            .addOption(Option.builder().longOpt("five").hasArg().argName("other").build())
            .addOption(Option.builder().longOpt("six").required().hasArg().argName("other").build())
            .addOption(Option.builder().option("s").longOpt("seven").hasArg().build());
        // @formatter:on
        assertEquals("[-f] [--five <other>] [-o <arg>] [-s <arg>] --six <other> -t <other> | -th", underTest.toSyntaxOptions(options),
                "options with required group failed");
    }

}
