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

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.io.IOUtils;
import org.example.XhtmlSerializer;
import org.junit.jupiter.api.Test;

public class HelpFormatterTest {

    @Test
    public void testPrintOptions() throws IOException {
        StringBuilder sb = new StringBuilder();
        TextSerializer serializer = new TextSerializer(sb);
        HelpFormatter formatter = new HelpFormatter.Builder().setSerializer(serializer).setShowSince(false).build();

        // help format default column styles
        // col  options     description     serializer
        // styl   FIXED     VARIABLE         VARIABLE
        // LPad     0           5               1
        // indent   1           1               3
        //
        // default serializer

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
    public void testPrintHelp() throws IOException {
        StringBuilder sb = new StringBuilder();
        TextSerializer serializer = new TextSerializer(sb);
        Options options = new Options().addOption(Option.builder("a").since("1853").hasArg()
                .desc("aaaa aaaa aaaa aaaa aaaa").build());
        HelpFormatter formatter = new HelpFormatter(serializer);

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

        formatter = new HelpFormatter.Builder().setShowSince(false).setSerializer(serializer).build();
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
    }

    @Test
    public void testPrintHelpXML() throws IOException {
        StringBuilder sb = new StringBuilder();
        XhtmlSerializer serializer = new XhtmlSerializer(sb);
        HelpFormatter formatter = new HelpFormatter(serializer);
        formatter.setShowSince(false);
        Options options = new Options().addOption("a", false, "aaaa aaaa aaaa aaaa aaaa");

        List<String> expected = new ArrayList<>();
        expected.add("<p>usage:  commandSyntax [-a]</p>");
        expected.add("<p>header</p>");
        expected.add("<table class='commons_cli_table'>");
        expected.add("  <tr>");
        expected.add("    <th>Options</th>");
        expected.add("    <th>Description</th>");
        expected.add("  </tr>");
        expected.add("  <tr>");
        expected.add("    <td>-a</td>");
        expected.add("    <td>aaaa aaaa aaaa aaaa aaaa</td>");
        expected.add("  </tr>");
        expected.add("</table>");
        expected.add("<p>footer</p>");

        formatter.printHelp("commandSyntax", "header", options, "footer", true);
        List<String> actual = IOUtils.readLines(new StringReader(sb.toString()));

        assertEquals(expected, actual);
    }

    @Test
    public void asSyntaxOptionGroupTest() {
        HelpFormatter underTest = new HelpFormatter();
        OptionGroup group = new OptionGroup()
                .addOption(Option.builder().option("o").longOpt("one").hasArg().build())
                .addOption(Option.builder().option("t").longOpt("two").hasArg().required().argName("other").build())
                .addOption(Option.builder().option("th").longOpt("three").required().argName("other").build())
                .addOption(Option.builder().option("f").argName("other").build())
                .addOption(Option.builder().longOpt("five").hasArg().argName("other").build())
                .addOption(Option.builder().longOpt("six").required().hasArg().argName("other").build())
                .addOption(Option.builder().option("s").longOpt("sevem").hasArg().build());
        assertEquals("[-f | --five <other> | -o <arg> | -s <arg> | --six <other> | -t <other> | -th]",
                underTest.asSyntaxOptions(group));

        group.setRequired(true);
        assertEquals("-f | --five <other> | -o <arg> | -s <arg> | --six <other> | -t <other> | -th",
                underTest.asSyntaxOptions(group));
    }

    @Test
    public void asSyntaxOptionOptionsTest(){
        HelpFormatter underTest = new HelpFormatter();
        Options options = getTestGroups();
        assertEquals("[-1 <arg> | --aon <arg> | --uno <arg>] [--dos <arg> | --dó <arg> | --two <arg>] " +
                        "[--three <arg> | --tres <arg> | --trí <arg>]",
                underTest.asSyntaxOptions(options),
                "getTestGroup options failed");

          options = new Options()
                .addOption(Option.builder().option("o").longOpt("one").hasArg().build())
                .addOption(Option.builder().option("t").longOpt("two").hasArg().required().argName("other").build())
                .addOption(Option.builder().option("th").longOpt("three").required().argName("other").build())
                .addOption(Option.builder().option("f").argName("other").build())
                .addOption(Option.builder().longOpt("five").hasArg().argName("other").build())
                .addOption(Option.builder().longOpt("six").required().hasArg().argName("other").build())
                .addOption(Option.builder().option("s").longOpt("seven").hasArg().build());
        assertEquals("[-f] [--five <other>] [-o <arg>] [-s <arg>] --six <other> -t <other> -th",
                underTest.asSyntaxOptions(options),
                "assorted options failed");


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
        assertEquals("[-f] [--five <other>] [-o <arg>] [-s <arg>] --six <other> [-t <other> | -th]",
                underTest.asSyntaxOptions(options),
                "option with group failed");

        OptionGroup group1 = new OptionGroup()
                .addOption(Option.builder().option("t").longOpt("two").hasArg().required().argName("other").build())
                .addOption(Option.builder().option("th").longOpt("three").required().argName("other").build());
        group1.setRequired(true);
        options = new Options()
                .addOption(Option.builder().option("o").longOpt("one").hasArg().build())
                .addOptionGroup(group1)
                .addOption(Option.builder().option("f").argName("other").build())
                .addOption(Option.builder().longOpt("five").hasArg().argName("other").build())
                .addOption(Option.builder().longOpt("six").required().hasArg().argName("other").build())
                .addOption(Option.builder().option("s").longOpt("seven").hasArg().build());
        assertEquals("[-f] [--five <other>] [-o <arg>] [-s <arg>] --six <other> -t <other> | -th",
                underTest.asSyntaxOptions(options),
                "options with required group failed");
    }

    @Test
    public void asSyntaxOptionIterableTest() {
        HelpFormatter underTest = new HelpFormatter();
        List<Option> options = new ArrayList<>();

        options.add(Option.builder().option("o").longOpt("one").hasArg().build());
        options.add(Option.builder().option("t").longOpt("two").hasArg().required().argName("other").build());
        options.add(Option.builder().option("th").longOpt("three").required().argName("other").build());
        options.add(Option.builder().option("f").argName("other").build());
        options.add(Option.builder().longOpt("five").hasArg().argName("other").build());
        options.add(Option.builder().longOpt("six").required().hasArg().argName("other").build());
        options.add(Option.builder().option("s").longOpt("sevem").hasArg().build());
        assertEquals("[-f] [--five <other>] [-o <arg>] [-s <arg>] --six <other> -t <other> -th",
                underTest.asSyntaxOptions(options));

    }


    @Test
    public void sortedOptionsTest() {
        Options options = new Options()
                .addOption(Option.builder("a").longOpt("optA").hasArg().desc("The description of A").build())
                .addOption(Option.builder("b").longOpt("BOpt").hasArg().desc("B description").build())
                .addOption(Option.builder().longOpt("COpt").hasArg().desc("A COpt description").build());

        HelpFormatter underTest = new HelpFormatter();
        List<Option> expected = new ArrayList<>();
        expected.add(options.getOption("a"));
        expected.add(options.getOption("b"));
        expected.add(options.getOption("COpt"));

        List<Option> actual = underTest.sortedOptions(options);
        assertEquals(expected, underTest.sortedOptions(options));

        expected.set(0, expected.get(2));
        expected.set(2, options.getOption("a"));
        underTest = new HelpFormatter.Builder().setComparator(AbstractHelpFormatter.DEFAULT_COMPARATOR.reversed()).build();
        actual = underTest.sortedOptions(options);
        assertEquals(expected, underTest.sortedOptions(options));
    }

    private Options getTestGroups() {
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
    }

    @Test
    public void sortedOptionGroupsTest() {
        Options options = getTestGroups();
        List<Option> optList = new ArrayList<>(options.getOptions());
        HelpFormatter underTest = new HelpFormatter();
        List<Option> expected = new ArrayList<>();
        expected.add(optList.get(0)); // because 1 sorts before all long values
        expected.add(optList.get(1));
        expected.add(optList.get(5));
        expected.add(optList.get(4));
        expected.add(optList.get(6));
        expected.add(optList.get(8));
        expected.add(optList.get(7));
        expected.add(optList.get(3));
        expected.add(optList.get(2));
        assertEquals(expected, underTest.sortedOptions(options));
    }
}
