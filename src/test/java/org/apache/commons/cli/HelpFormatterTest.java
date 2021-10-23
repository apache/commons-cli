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

package org.apache.commons.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Comparator;

import org.junit.Test;

/**
 * Test case for the HelpFormatter class.
 */
public class HelpFormatterTest {
    private static final String EOL = System.getProperty("line.separator");

    @Test
    public void testAccessors() {
        final HelpFormatter formatter = new HelpFormatter();

        formatter.setArgName("argname");
        assertEquals("arg name", "argname", formatter.getArgName());

        formatter.setDescPadding(3);
        assertEquals("desc padding", 3, formatter.getDescPadding());

        formatter.setLeftPadding(7);
        assertEquals("left padding", 7, formatter.getLeftPadding());

        formatter.setLongOptPrefix("~~");
        assertEquals("long opt prefix", "~~", formatter.getLongOptPrefix());

        formatter.setNewLine("\n");
        assertEquals("new line", "\n", formatter.getNewLine());

        formatter.setOptPrefix("~");
        assertEquals("opt prefix", "~", formatter.getOptPrefix());

        formatter.setSyntaxPrefix("-> ");
        assertEquals("syntax prefix", "-> ", formatter.getSyntaxPrefix());

        formatter.setWidth(80);
        assertEquals("width", 80, formatter.getWidth());
    }

    @Test
    public void testAutomaticUsage() {
        final HelpFormatter hf = new HelpFormatter();
        Options options;
        String expected = "usage: app [-a]";
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintWriter pw = new PrintWriter(out);

        options = new Options().addOption("a", false, "aaaa aaaa aaaa aaaa aaaa");
        hf.printUsage(pw, 60, "app", options);
        pw.flush();
        assertEquals("simple auto usage", expected, out.toString().trim());
        out.reset();

        expected = "usage: app [-a] [-b]";
        options = new Options().addOption("a", false, "aaaa aaaa aaaa aaaa aaaa").addOption("b", false, "bbb");
        hf.printUsage(pw, 60, "app", options);
        pw.flush();
        assertEquals("simple auto usage", expected, out.toString().trim());
        out.reset();
    }

    @Test
    public void testDefaultArgName() {
        final Option option = Option.builder("f").hasArg().required(true).build();

        final Options options = new Options();
        options.addOption(option);

        final StringWriter out = new StringWriter();

        final HelpFormatter formatter = new HelpFormatter();
        formatter.setArgName("argument");
        formatter.printUsage(new PrintWriter(out), 80, "app", options);

        assertEquals("usage: app -f <argument>" + EOL, out.toString());
    }

    @Test
    public void testFindWrapPos() {
        final HelpFormatter hf = new HelpFormatter();

        String text = "This is a test.";
        // text width should be max 8; the wrap position is 7
        assertEquals("wrap position", 7, hf.findWrapPos(text, 8, 0));

        // starting from 8 must give -1 - the wrap pos is after end
        assertEquals("wrap position 2", -1, hf.findWrapPos(text, 8, 8));

        // words longer than the width are cut
        text = "aaaa aa";
        assertEquals("wrap position 3", 3, hf.findWrapPos(text, 3, 0));

        // last word length is equal to the width
        text = "aaaaaa aaaaaa";
        assertEquals("wrap position 4", 6, hf.findWrapPos(text, 6, 0));
        assertEquals("wrap position 4", -1, hf.findWrapPos(text, 6, 7));

        text = "aaaaaa\n aaaaaa";
        assertEquals("wrap position 5", 7, hf.findWrapPos(text, 6, 0));

        text = "aaaaaa\t aaaaaa";
        assertEquals("wrap position 6", 7, hf.findWrapPos(text, 6, 0));
    }

    @Test
    public void testHeaderStartingWithLineSeparator() {
        // related to Bugzilla #21215
        final Options options = new Options();
        final HelpFormatter formatter = new HelpFormatter();
        final String header = EOL + "Header";
        final String footer = "Footer";
        final StringWriter out = new StringWriter();
        formatter.printHelp(new PrintWriter(out), 80, "foobar", header, options, 2, 2, footer, true);
        //@formatter:off
        assertEquals(
                "usage: foobar" + EOL +
                "" + EOL +
                "Header" + EOL +
                "" + EOL +
                "Footer" + EOL,
                out.toString());
        //@formatter:on
    }

    @Test
    public void testHelpWithLongOptSeparator() {
        final Options options = new Options();
        options.addOption("f", true, "the file");
        options.addOption(Option.builder("s").longOpt("size").desc("the size").hasArg().argName("SIZE").build());
        options.addOption(Option.builder().longOpt("age").desc("the age").hasArg().build());

        final HelpFormatter formatter = new HelpFormatter();
        assertEquals(HelpFormatter.DEFAULT_LONG_OPT_SEPARATOR, formatter.getLongOptSeparator());
        formatter.setLongOptSeparator("=");
        assertEquals("=", formatter.getLongOptSeparator());

        final StringWriter out = new StringWriter();

        formatter.printHelp(new PrintWriter(out), 80, "create", "header", options, 2, 2, "footer");

        //@formatter:off
        assertEquals(
                "usage: create" + EOL +
                "header" + EOL +
                "     --age=<arg>    the age" + EOL +
                "  -f <arg>          the file" + EOL +
                "  -s,--size=<SIZE>  the size" + EOL +
                "footer" + EOL,
                out.toString());
        //@formatter:on
    }

    @Test
    public void testIndentedHeaderAndFooter() {
        // related to CLI-207
        final Options options = new Options();
        final HelpFormatter formatter = new HelpFormatter();
        final String header = "  Header1\n  Header2";
        final String footer = "  Footer1\n  Footer2";
        final StringWriter out = new StringWriter();
        formatter.printHelp(new PrintWriter(out), 80, "foobar", header, options, 2, 2, footer, true);
        //@formatter:off
        assertEquals(
                "usage: foobar" + EOL +
                "  Header1" + EOL +
                "  Header2" + EOL +
                "" + EOL +
                "  Footer1" + EOL +
                "  Footer2" + EOL,
                out.toString());
        //@formatter:on
    }

    @Test
    public void testOptionWithoutShortFormat() {
        // related to Bugzilla #19383 (CLI-67)
        final Options options = new Options();
        options.addOption(new Option("a", "aaa", false, "aaaaaaa"));
        options.addOption(new Option(null, "bbb", false, "bbbbbbb"));
        options.addOption(new Option("c", null, false, "ccccccc"));

        final HelpFormatter formatter = new HelpFormatter();
        final StringWriter out = new StringWriter();
        formatter.printHelp(new PrintWriter(out), 80, "foobar", "", options, 2, 2, "", true);
        //@formatter:off
        assertEquals(
                "usage: foobar [-a] [--bbb] [-c]" + EOL +
                "  -a,--aaa  aaaaaaa" + EOL +
                "     --bbb  bbbbbbb" + EOL +
                "  -c        ccccccc" + EOL,
                out.toString());
        //@formatter:on
    }

    @Test
    public void testOptionWithoutShortFormat2() {
        // related to Bugzilla #27635 (CLI-26)
        final Option help = new Option("h", "help", false, "print this message");
        final Option version = new Option("v", "version", false, "print version information");
        final Option newRun = new Option("n", "new", false, "Create NLT cache entries only for new items");
        final Option trackerRun = new Option("t", "tracker", false, "Create NLT cache entries only for tracker items");

        //@formatter:off
        final Option timeLimit = Option.builder("l")
                                 .longOpt("limit")
                                 .hasArg()
                                 .valueSeparator()
                                 .desc("Set time limit for execution, in mintues")
                                 .build();

        final Option age = Option.builder("a").longOpt("age")
                                        .hasArg()
                                        .valueSeparator()
                                        .desc("Age (in days) of cache item before being recomputed")
                                        .build();

        final Option server = Option.builder("s").longOpt("server")
                                           .hasArg()
                                           .valueSeparator()
                                           .desc("The NLT server address")
                                           .build();

        final Option numResults = Option.builder("r").longOpt("results")
                                               .hasArg()
                                               .valueSeparator()
                                               .desc("Number of results per item")
                                               .build();

        final Option configFile = Option.builder().longOpt("config")
                                            .hasArg()
                                            .valueSeparator()
                                            .desc("Use the specified configuration file")
                                            .build();
        //@formatter:on

        final Options mOptions = new Options();
        mOptions.addOption(help);
        mOptions.addOption(version);
        mOptions.addOption(newRun);
        mOptions.addOption(trackerRun);
        mOptions.addOption(timeLimit);
        mOptions.addOption(age);
        mOptions.addOption(server);
        mOptions.addOption(numResults);
        mOptions.addOption(configFile);

        final HelpFormatter formatter = new HelpFormatter();
        final String eol = System.getProperty("line.separator");
        final StringWriter out = new StringWriter();
        formatter.printHelp(new PrintWriter(out), 80, "commandline", "header", mOptions, 2, 2, "footer", true);
        //@formatter:off
        assertEquals(
                "usage: commandline [-a <arg>] [--config <arg>] [-h] [-l <arg>] [-n] [-r <arg>]" + eol +
                "       [-s <arg>] [-t] [-v]" + eol +
                "header" + eol +
                "  -a,--age <arg>      Age (in days) of cache item before being recomputed" + eol +
                "     --config <arg>   Use the specified configuration file" + eol +
                "  -h,--help           print this message" + eol +
                "  -l,--limit <arg>    Set time limit for execution, in mintues" + eol +
                "  -n,--new            Create NLT cache entries only for new items" + eol +
                "  -r,--results <arg>  Number of results per item" + eol +
                "  -s,--server <arg>   The NLT server address" + eol +
                "  -t,--tracker        Create NLT cache entries only for tracker items" + eol +
                "  -v,--version        print version information" + eol +
                "footer" + eol,
                out.toString());
        //@formatter:on
    }

    @Test
    public void testPrintHelpNewlineFooter() {
        final HelpFormatter formatter = new HelpFormatter();
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintWriter pw = new PrintWriter(out);

        final Options options = new Options();
        options.addOption("a", "b");

        formatter.printHelp(
            pw,
            80,
            "test" + EOL,
            "header" + EOL,
            options,
            0,
            0,
            EOL
        );
        final String expected = "usage: test" + EOL +
                          "header" + EOL +
                          "-ab" + EOL +
                          EOL;
        pw.flush();
        assertEquals("footer newline", expected, out.toString());
    }

    @Test
    public void testPrintHelpNewlineHeader() {
        final HelpFormatter formatter = new HelpFormatter();
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final PrintWriter pw = new PrintWriter(out);

        final Options options = new Options();
        options.addOption("a", "b");

        formatter.printHelp(
            pw,
            80,
            "test" + EOL,
            EOL,
            options,
            0,
            0,
            "footer" + EOL
        );
        final String expected = "usage: test" + EOL +
                          EOL +
                          "-ab" + EOL +
                          "footer" + EOL;
        pw.flush();
        assertEquals("header newline", expected, out.toString());
    }

    @Test
    public void testPrintHelpWithEmptySyntax() {
        final HelpFormatter formatter = new HelpFormatter();
        try {
            formatter.printHelp(null, new Options());
            fail("null command line syntax should be rejected");
        } catch (final IllegalArgumentException e) {
            // expected
        }

        try {
            formatter.printHelp("", new Options());
            fail("empty command line syntax should be rejected");
        } catch (final IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testPrintOptionGroupUsage() {
        final OptionGroup group = new OptionGroup();
        group.addOption(Option.builder("a").build());
        group.addOption(Option.builder("b").build());
        group.addOption(Option.builder("c").build());

        final Options options = new Options();
        options.addOptionGroup(group);

        final StringWriter out = new StringWriter();

        final HelpFormatter formatter = new HelpFormatter();
        formatter.printUsage(new PrintWriter(out), 80, "app", options);

        assertEquals("usage: app [-a | -b | -c]" + EOL, out.toString());
    }

    @Test
    public void testPrintOptions() {
        final StringBuffer sb = new StringBuffer();
        final HelpFormatter hf = new HelpFormatter();
        final int leftPad = 1;
        final int descPad = 3;
        final String lpad = hf.createPadding(leftPad);
        final String dpad = hf.createPadding(descPad);
        Options options;
        String expected;

        options = new Options().addOption("a", false, "aaaa aaaa aaaa aaaa aaaa");
        expected = lpad + "-a" + dpad + "aaaa aaaa aaaa aaaa aaaa";
        hf.renderOptions(sb, 60, options, leftPad, descPad);
        assertEquals("simple non-wrapped option", expected, sb.toString());

        int nextLineTabStop = leftPad + descPad + "-a".length();
        expected = lpad + "-a" + dpad + "aaaa aaaa aaaa" + EOL + hf.createPadding(nextLineTabStop) + "aaaa aaaa";
        sb.setLength(0);
        hf.renderOptions(sb, nextLineTabStop + 17, options, leftPad, descPad);
        assertEquals("simple wrapped option", expected, sb.toString());

        options = new Options().addOption("a", "aaa", false, "dddd dddd dddd dddd");
        expected = lpad + "-a,--aaa" + dpad + "dddd dddd dddd dddd";
        sb.setLength(0);
        hf.renderOptions(sb, 60, options, leftPad, descPad);
        assertEquals("long non-wrapped option", expected, sb.toString());

        nextLineTabStop = leftPad + descPad + "-a,--aaa".length();
        expected = lpad + "-a,--aaa" + dpad + "dddd dddd" + EOL + hf.createPadding(nextLineTabStop) + "dddd dddd";
        sb.setLength(0);
        hf.renderOptions(sb, 25, options, leftPad, descPad);
        assertEquals("long wrapped option", expected, sb.toString());

        options = new Options().addOption("a", "aaa", false, "dddd dddd dddd dddd").addOption("b", false, "feeee eeee eeee eeee");
        expected = lpad + "-a,--aaa" + dpad + "dddd dddd" + EOL + hf.createPadding(nextLineTabStop) + "dddd dddd" + EOL + lpad + "-b      " + dpad
            + "feeee eeee" + EOL + hf.createPadding(nextLineTabStop) + "eeee eeee";
        sb.setLength(0);
        hf.renderOptions(sb, 25, options, leftPad, descPad);
        assertEquals("multiple wrapped options", expected, sb.toString());
    }

    @Test
    public void testPrintOptionWithEmptyArgNameUsage() {
        final Option option = new Option("f", true, null);
        option.setArgName("");
        option.setRequired(true);

        final Options options = new Options();
        options.addOption(option);

        final StringWriter out = new StringWriter();

        final HelpFormatter formatter = new HelpFormatter();
        formatter.printUsage(new PrintWriter(out), 80, "app", options);

        assertEquals("usage: app -f" + EOL, out.toString());
    }

    @Test
    public void testPrintRequiredOptionGroupUsage() {
        final OptionGroup group = new OptionGroup();
        group.addOption(Option.builder("a").build());
        group.addOption(Option.builder("b").build());
        group.addOption(Option.builder("c").build());
        group.setRequired(true);

        final Options options = new Options();
        options.addOptionGroup(group);

        final StringWriter out = new StringWriter();

        final HelpFormatter formatter = new HelpFormatter();
        formatter.printUsage(new PrintWriter(out), 80, "app", options);

        assertEquals("usage: app -a | -b | -c" + EOL, out.toString());
    }

    // uses the test for CLI-131 to implement CLI-155
    @Test
    public void testPrintSortedUsage() {
        final Options opts = new Options();
        opts.addOption(new Option("a", "first"));
        opts.addOption(new Option("b", "second"));
        opts.addOption(new Option("c", "third"));

        final HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.setOptionComparator(new Comparator<Option>() {
            @Override
            public int compare(final Option opt1, final Option opt2) {
                // reverses the functionality of the default comparator
                return opt2.getKey().compareToIgnoreCase(opt1.getKey());
            }
        });

        final StringWriter out = new StringWriter();
        helpFormatter.printUsage(new PrintWriter(out), 80, "app", opts);

        assertEquals("usage: app [-c] [-b] [-a]" + EOL, out.toString());
    }

    @Test
    public void testPrintSortedUsageWithNullComparator() {
        final Options opts = new Options();
        opts.addOption(new Option("c", "first"));
        opts.addOption(new Option("b", "second"));
        opts.addOption(new Option("a", "third"));

        final HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.setOptionComparator(null);

        final StringWriter out = new StringWriter();
        helpFormatter.printUsage(new PrintWriter(out), 80, "app", opts);

        assertEquals("usage: app [-c] [-b] [-a]" + EOL, out.toString());
    }

    // This test ensures the options are properly sorted
    // See https://issues.apache.org/jira/browse/CLI-131
    @Test
    public void testPrintUsage() {
        final Option optionA = new Option("a", "first");
        final Option optionB = new Option("b", "second");
        final Option optionC = new Option("c", "third");
        final Options opts = new Options();
        opts.addOption(optionA);
        opts.addOption(optionB);
        opts.addOption(optionC);
        final HelpFormatter helpFormatter = new HelpFormatter();
        final ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        try (PrintWriter printWriter = new PrintWriter(bytesOut)) {
            helpFormatter.printUsage(printWriter, 80, "app", opts);
        }
        assertEquals("usage: app [-a] [-b] [-c]" + EOL, bytesOut.toString());
    }

    @Test
    public void testRenderWrappedTextMultiLine() {
        // multi line text
        final int width = 16;
        final int padding = 0;
        //@formatter:off
        final String expected = "aaaa aaaa aaaa" + EOL +
                                "aaaaaa" + EOL +
                                "aaaaa";
        //@formatter:on

        final StringBuffer sb = new StringBuffer();
        new HelpFormatter().renderWrappedText(sb, width, padding, expected);
        assertEquals("multi line text", expected, sb.toString());
    }

    @Test
    public void testRenderWrappedTextMultiLinePadded() {
        // multi-line padded text
        final int width = 16;
        final int padding = 4;
        //@formatter:off
        final String text = "aaaa aaaa aaaa" + EOL +
                      "aaaaaa" + EOL +
                      "aaaaa";
        final String expected = "aaaa aaaa aaaa" + EOL +
                          "    aaaaaa" + EOL +
                          "    aaaaa";
        //@formatter:on

        final StringBuffer sb = new StringBuffer();
        new HelpFormatter().renderWrappedText(sb, width, padding, text);
        assertEquals("multi-line padded text", expected, sb.toString());
    }

    @Test
    public void testRenderWrappedTextSingleLine() {
        // single line text
        final int width = 12;
        final int padding = 0;
        final String text = "This is a test.";
        final String expected = "This is a" + EOL + "test.";

        final StringBuffer sb = new StringBuffer();
        new HelpFormatter().renderWrappedText(sb, width, padding, text);
        assertEquals("single line text", expected, sb.toString());
    }

    @Test
    public void testRenderWrappedTextSingleLinePadded() {
        // single line padded text
        final int width = 12;
        final int padding = 4;
        final String text = "This is a test.";
        final String expected = "This is a" + EOL + "    test.";

        final StringBuffer sb = new StringBuffer();
        new HelpFormatter().renderWrappedText(sb, width, padding, text);
        assertEquals("single line padded text", expected, sb.toString());
    }

    @Test
    public void testRenderWrappedTextSingleLinePadded2() {
        // single line padded text 2
        final int width = 53;
        final int padding = 24;
        //@formatter:off
        final String text = "  -p,--period <PERIOD>  PERIOD is time duration of form " +
                            "DATE[-DATE] where DATE has form YYYY[MM[DD]]";
        final String expected = "  -p,--period <PERIOD>  PERIOD is time duration of" + EOL +
                                "                        form DATE[-DATE] where DATE" + EOL +
                                "                        has form YYYY[MM[DD]]";
        //@formatter:on

        final StringBuffer sb = new StringBuffer();
        new HelpFormatter().renderWrappedText(sb, width, padding, text);
        assertEquals("single line padded text 2", expected, sb.toString());
    }

    @Test
    public void testRenderWrappedTextWordCut() {
        final int width = 7;
        final int padding = 0;
        final String text = "Thisisatest.";
        final String expected = "Thisisa" + EOL + "test.";

        final StringBuffer sb = new StringBuffer();
        new HelpFormatter().renderWrappedText(sb, width, padding, text);
        assertEquals("cut and wrap", expected, sb.toString());
    }

    @Test
    public void testRtrim() {
        final HelpFormatter formatter = new HelpFormatter();

        assertNull(formatter.rtrim(null));
        assertEquals("", formatter.rtrim(""));
        assertEquals("  foo", formatter.rtrim("  foo  "));
    }

    @Test
    public void testUsageWithLongOptSeparator() {
        final Options options = new Options();
        options.addOption("f", true, "the file");
        options.addOption(Option.builder("s").longOpt("size").desc("the size").hasArg().argName("SIZE").build());
        options.addOption(Option.builder().longOpt("age").desc("the age").hasArg().build());

        final HelpFormatter formatter = new HelpFormatter();
        formatter.setLongOptSeparator("=");

        final StringWriter out = new StringWriter();

        formatter.printUsage(new PrintWriter(out), 80, "create", options);

        assertEquals("usage: create [--age=<arg>] [-f <arg>] [-s <SIZE>]", out.toString().trim());
    }
}
