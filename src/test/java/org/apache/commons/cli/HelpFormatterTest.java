/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Comparator;

import org.junit.Test;

/** 
 * Test case for the HelpFormatter class.
 */
public class HelpFormatterTest
{
    private static final String EOL = System.getProperty("line.separator");

    @Test
    public void testFindWrapPos() throws Exception
    {
        HelpFormatter hf = new HelpFormatter();

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
    public void testRenderWrappedTextWordCut()
    {
        int width = 7;
        int padding = 0;
        String text = "Thisisatest.";
        String expected = "Thisisa" + EOL + 
                          "test.";
        
        StringBuffer sb = new StringBuffer();
        new HelpFormatter().renderWrappedText(sb, width, padding, text);
        assertEquals("cut and wrap", expected, sb.toString());
    }

    @Test
    public void testRenderWrappedTextSingleLine()
    {
        // single line text
        int width = 12;
        int padding = 0;
        String text = "This is a test.";
        String expected = "This is a" + EOL + 
                          "test.";
        
        StringBuffer sb = new StringBuffer();
        new HelpFormatter().renderWrappedText(sb, width, padding, text);
        assertEquals("single line text", expected, sb.toString());
    }

    @Test
    public void testRenderWrappedTextSingleLinePadded()
    {
        // single line padded text
        int width = 12;
        int padding = 4;
        String text = "This is a test.";
        String expected = "This is a" + EOL + 
                          "    test.";
        
        StringBuffer sb = new StringBuffer();
        new HelpFormatter().renderWrappedText(sb, width, padding, text);
        assertEquals("single line padded text", expected, sb.toString());
    }

    @Test
    public void testRenderWrappedTextSingleLinePadded2()
    {
        // single line padded text 2
        int width = 53;
        int padding = 24;
        String text = "  -p,--period <PERIOD>  PERIOD is time duration of form " +
                      "DATE[-DATE] where DATE has form YYYY[MM[DD]]";
        String expected = "  -p,--period <PERIOD>  PERIOD is time duration of" + EOL +
                          "                        form DATE[-DATE] where DATE" + EOL +
                          "                        has form YYYY[MM[DD]]";
        
        StringBuffer sb = new StringBuffer();
        new HelpFormatter().renderWrappedText(sb, width, padding, text);
        assertEquals("single line padded text 2", expected, sb.toString());
    }

    @Test
    public void testRenderWrappedTextMultiLine()
    {
        // multi line text
        int width = 16;
        int padding = 0;
        String expected = "aaaa aaaa aaaa" + EOL +
                      "aaaaaa" + EOL +
                      "aaaaa";

        StringBuffer sb = new StringBuffer();
        new HelpFormatter().renderWrappedText(sb, width, padding, expected);
        assertEquals("multi line text", expected, sb.toString());
    }

    @Test
    public void testRenderWrappedTextMultiLinePadded()
    {
        // multi-line padded text
        int width = 16;
        int padding = 4;
        String text = "aaaa aaaa aaaa" + EOL +
                      "aaaaaa" + EOL +
                      "aaaaa";
        String expected = "aaaa aaaa aaaa" + EOL +
                          "    aaaaaa" + EOL +
                          "    aaaaa";
        
        StringBuffer sb = new StringBuffer();
        new HelpFormatter().renderWrappedText(sb, width, padding, text);
        assertEquals("multi-line padded text", expected, sb.toString());
    }

    @Test
    public void testPrintOptions() throws Exception
    {
        StringBuffer sb = new StringBuffer();
        HelpFormatter hf = new HelpFormatter();
        final int leftPad = 1;
        final int descPad = 3;
        final String lpad = hf.createPadding(leftPad);
        final String dpad = hf.createPadding(descPad);
        Options options = null;
        String expected = null;

        options = new Options().addOption("a", false, "aaaa aaaa aaaa aaaa aaaa");
        expected = lpad + "-a" + dpad + "aaaa aaaa aaaa aaaa aaaa";
        hf.renderOptions(sb, 60, options, leftPad, descPad);
        assertEquals("simple non-wrapped option", expected, sb.toString());

        int nextLineTabStop = leftPad + descPad + "-a".length();
        expected = lpad + "-a" + dpad + "aaaa aaaa aaaa" + EOL +
                   hf.createPadding(nextLineTabStop) + "aaaa aaaa";
        sb.setLength(0);
        hf.renderOptions(sb, nextLineTabStop + 17, options, leftPad, descPad);
        assertEquals("simple wrapped option", expected, sb.toString());


        options = new Options().addOption("a", "aaa", false, "dddd dddd dddd dddd");
        expected = lpad + "-a,--aaa" + dpad + "dddd dddd dddd dddd";
        sb.setLength(0);
        hf.renderOptions(sb, 60, options, leftPad, descPad);
        assertEquals("long non-wrapped option", expected, sb.toString());

        nextLineTabStop = leftPad + descPad + "-a,--aaa".length();
        expected = lpad + "-a,--aaa" + dpad + "dddd dddd" + EOL +
                   hf.createPadding(nextLineTabStop) + "dddd dddd";
        sb.setLength(0);
        hf.renderOptions(sb, 25, options, leftPad, descPad);
        assertEquals("long wrapped option", expected, sb.toString());

        options = new Options().
                addOption("a", "aaa", false, "dddd dddd dddd dddd").
                addOption("b", false, "feeee eeee eeee eeee");
        expected = lpad + "-a,--aaa" + dpad + "dddd dddd" + EOL +
                   hf.createPadding(nextLineTabStop) + "dddd dddd" + EOL +
                   lpad + "-b      " + dpad + "feeee eeee" + EOL +
                   hf.createPadding(nextLineTabStop) + "eeee eeee";
        sb.setLength(0);
        hf.renderOptions(sb, 25, options, leftPad, descPad);
        assertEquals("multiple wrapped options", expected, sb.toString());
    }

    @Test
    public void testPrintHelpWithEmptySyntax()
    {
        HelpFormatter formatter = new HelpFormatter();
        try
        {
            formatter.printHelp(null, new Options());
            fail("null command line syntax should be rejected");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }

        try
        {
            formatter.printHelp("", new Options());
            fail("empty command line syntax should be rejected");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }

    @Test
    public void testAutomaticUsage() throws Exception
    {
        HelpFormatter hf = new HelpFormatter();
        Options options = null;
        String expected = "usage: app [-a]";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(out);

        options = new Options().addOption("a", false, "aaaa aaaa aaaa aaaa aaaa");
        hf.printUsage(pw, 60, "app", options);
        pw.flush();
        assertEquals("simple auto usage", expected, out.toString().trim());
        out.reset();

        expected = "usage: app [-a] [-b]";
        options = new Options().addOption("a", false, "aaaa aaaa aaaa aaaa aaaa")
                .addOption("b", false, "bbb");
        hf.printUsage(pw, 60, "app", options);
        pw.flush();
        assertEquals("simple auto usage", expected, out.toString().trim());
        out.reset();
    }

    // This test ensures the options are properly sorted
    // See https://issues.apache.org/jira/browse/CLI-131
    @Test
    public void testPrintUsage()
    {
        Option optionA = new Option("a", "first");
        Option optionB = new Option("b", "second");
        Option optionC = new Option("c", "third");
        Options opts = new Options();
        opts.addOption(optionA);
        opts.addOption(optionB);
        opts.addOption(optionC);
        HelpFormatter helpFormatter = new HelpFormatter();
        ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(bytesOut);
        helpFormatter.printUsage(printWriter, 80, "app", opts);
        printWriter.close();
        assertEquals("usage: app [-a] [-b] [-c]" + EOL, bytesOut.toString());
    }

    // uses the test for CLI-131 to implement CLI-155
    @Test
    public void testPrintSortedUsage()
    {
        Options opts = new Options();
        opts.addOption(new Option("a", "first"));
        opts.addOption(new Option("b", "second"));
        opts.addOption(new Option("c", "third"));

        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.setOptionComparator(new Comparator<Option>()
        {
            public int compare(Option opt1, Option opt2)
            {
                // reverses the functionality of the default comparator
                return opt2.getKey().compareToIgnoreCase(opt1.getKey());
            }
        });

        StringWriter out = new StringWriter();
        helpFormatter.printUsage(new PrintWriter(out), 80, "app", opts);

        assertEquals("usage: app [-c] [-b] [-a]" + EOL, out.toString());
    }

    @Test
    public void testPrintSortedUsageWithNullComparator()
    {
        Options opts = new Options();
        opts.addOption(new Option("c", "first"));
        opts.addOption(new Option("b", "second"));
        opts.addOption(new Option("a", "third"));

        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.setOptionComparator(null);

        StringWriter out = new StringWriter();
        helpFormatter.printUsage(new PrintWriter(out), 80, "app", opts);

        assertEquals("usage: app [-c] [-b] [-a]" + EOL, out.toString());
    }

    @Test
    public void testPrintOptionGroupUsage()
    {
        OptionGroup group = new OptionGroup();
        group.addOption(Option.builder("a").build());
        group.addOption(Option.builder("b").build());
        group.addOption(Option.builder("c").build());

        Options options = new Options();
        options.addOptionGroup(group);

        StringWriter out = new StringWriter();

        HelpFormatter formatter = new HelpFormatter();
        formatter.printUsage(new PrintWriter(out), 80, "app", options);

        assertEquals("usage: app [-a | -b | -c]" + EOL, out.toString());
    }

    @Test
    public void testPrintRequiredOptionGroupUsage()
    {
        OptionGroup group = new OptionGroup();
        group.addOption(Option.builder("a").build());
        group.addOption(Option.builder("b").build());
        group.addOption(Option.builder("c").build());
        group.setRequired(true);

        Options options = new Options();
        options.addOptionGroup(group);

        StringWriter out = new StringWriter();

        HelpFormatter formatter = new HelpFormatter();
        formatter.printUsage(new PrintWriter(out), 80, "app", options);

        assertEquals("usage: app -a | -b | -c" + EOL, out.toString());
    }

    @Test
    public void testPrintOptionWithEmptyArgNameUsage()
    {
        Option option = new Option("f", true, null);
        option.setArgName("");
        option.setRequired(true);

        Options options = new Options();
        options.addOption(option);

        StringWriter out = new StringWriter();

        HelpFormatter formatter = new HelpFormatter();
        formatter.printUsage(new PrintWriter(out), 80, "app", options);

        assertEquals("usage: app -f" + EOL, out.toString());
    }

    @Test
    public void testDefaultArgName()
    {
        Option option = Option.builder("f").hasArg().required(true).build();
        
        Options options = new Options();
        options.addOption(option);
        
        StringWriter out = new StringWriter();

        HelpFormatter formatter = new HelpFormatter();
        formatter.setArgName("argument");
        formatter.printUsage(new PrintWriter(out), 80, "app", options);

        assertEquals("usage: app -f <argument>" + EOL, out.toString());
    }

    @Test
    public void testRtrim()
    {
        HelpFormatter formatter = new HelpFormatter();

        assertEquals(null, formatter.rtrim(null));
        assertEquals("", formatter.rtrim(""));
        assertEquals("  foo", formatter.rtrim("  foo  "));
    }

    @Test
    public void testAccessors()
    {
        HelpFormatter formatter = new HelpFormatter();

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
    public void testHeaderStartingWithLineSeparator()
    {
        // related to Bugzilla #21215
        Options options = new Options();
        HelpFormatter formatter = new HelpFormatter();
        String header = EOL + "Header";
        String footer = "Footer";
        StringWriter out = new StringWriter();
        formatter.printHelp(new PrintWriter(out), 80, "foobar", header, options, 2, 2, footer, true);
        assertEquals(
                "usage: foobar" + EOL +
                "" + EOL +
                "Header" + EOL +
                "" + EOL +
                "Footer" + EOL
                , out.toString());
    }

    @Test
    public void testIndentedHeaderAndFooter()
    {
        // related to CLI-207
        Options options = new Options();
        HelpFormatter formatter = new HelpFormatter();
        String header = "  Header1\n  Header2";
        String footer = "  Footer1\n  Footer2";
        StringWriter out = new StringWriter();
        formatter.printHelp(new PrintWriter(out), 80, "foobar", header, options, 2, 2, footer, true);

        assertEquals(
                "usage: foobar" + EOL +
                "  Header1" + EOL +
                "  Header2" + EOL +
                "" + EOL +
                "  Footer1" + EOL +
                "  Footer2" + EOL
                , out.toString());
    }

    @Test
    public void testOptionWithoutShortFormat()
    {
        // related to Bugzilla #19383 (CLI-67)
        Options options = new Options();
        options.addOption(new Option("a", "aaa", false, "aaaaaaa"));
        options.addOption(new Option(null, "bbb", false, "bbbbbbb"));
        options.addOption(new Option("c", null, false, "ccccccc"));

        HelpFormatter formatter = new HelpFormatter();
        StringWriter out = new StringWriter();
        formatter.printHelp(new PrintWriter(out), 80, "foobar", "", options, 2, 2, "", true);
        assertEquals(
                "usage: foobar [-a] [--bbb] [-c]" + EOL +
                "  -a,--aaa  aaaaaaa" + EOL +
                "     --bbb  bbbbbbb" + EOL +
                "  -c        ccccccc" + EOL
                , out.toString());
    }
    
    @Test
    public void testOptionWithoutShortFormat2()
    {
        // related to Bugzilla #27635 (CLI-26)
        Option help = new Option("h", "help", false, "print this message");
        Option version = new Option("v", "version", false, "print version information");
        Option newRun = new Option("n", "new", false, "Create NLT cache entries only for new items");
        Option trackerRun = new Option("t", "tracker", false, "Create NLT cache entries only for tracker items");
        
        Option timeLimit = Option.builder("l")
                                 .longOpt("limit")
                                 .hasArg()
                                 .valueSeparator()
                                 .desc("Set time limit for execution, in mintues")
                                 .build();
        
        Option age = Option.builder("a").longOpt("age")
                                        .hasArg()
                                        .valueSeparator()
                                        .desc("Age (in days) of cache item before being recomputed")
                                        .build();
        
        Option server = Option.builder("s").longOpt("server")
                                           .hasArg()
                                           .valueSeparator()
                                           .desc("The NLT server address")
                                           .build();
        
        Option numResults = Option.builder("r").longOpt("results")
                                               .hasArg()
                                               .valueSeparator()
                                               .desc("Number of results per item")
                                               .build();
        
        Option configFile = Option.builder().longOpt("config")
                                            .hasArg()
                                            .valueSeparator()
                                            .desc("Use the specified configuration file")
                                            .build();
        
        Options mOptions = new Options();
        mOptions.addOption(help);
        mOptions.addOption(version);
        mOptions.addOption(newRun);
        mOptions.addOption(trackerRun);
        mOptions.addOption(timeLimit);
        mOptions.addOption(age);
        mOptions.addOption(server);
        mOptions.addOption(numResults);
        mOptions.addOption(configFile);
        
        HelpFormatter formatter = new HelpFormatter();
        final String EOL = System.getProperty("line.separator");
        StringWriter out = new StringWriter();
        formatter.printHelp(new PrintWriter(out),80,"commandline","header",mOptions,2,2,"footer",true);
        assertEquals(
                "usage: commandline [-a <arg>] [--config <arg>] [-h] [-l <arg>] [-n] [-r <arg>]" + EOL +
                "       [-s <arg>] [-t] [-v]" + EOL +
                "header"+EOL+
                "  -a,--age <arg>      Age (in days) of cache item before being recomputed"+EOL+
                "     --config <arg>   Use the specified configuration file"+EOL+
                "  -h,--help           print this message"+EOL+
                "  -l,--limit <arg>    Set time limit for execution, in mintues"+EOL+
                "  -n,--new            Create NLT cache entries only for new items"+EOL+
                "  -r,--results <arg>  Number of results per item"+EOL+
                "  -s,--server <arg>   The NLT server address"+EOL+
                "  -t,--tracker        Create NLT cache entries only for tracker items"+EOL+
                "  -v,--version        print version information"+EOL+
                "footer"+EOL
                ,out.toString());
    }
    
    @Test
    public void testHelpWithLongOptSeparator() throws Exception
    {
        Options options = new Options();
        options.addOption( "f", true, "the file" );
        options.addOption(Option.builder("s").longOpt("size").desc("the size").hasArg().argName("SIZE").build());
        options.addOption(Option.builder().longOpt("age").desc("the age").hasArg().build());
        
        HelpFormatter formatter = new HelpFormatter();
        assertEquals(HelpFormatter.DEFAULT_LONG_OPT_SEPARATOR, formatter.getLongOptSeparator());
        formatter.setLongOptSeparator("=");
        assertEquals("=", formatter.getLongOptSeparator());
        
        StringWriter out = new StringWriter();

        formatter.printHelp(new PrintWriter(out), 80, "create", "header", options, 2, 2, "footer");

        assertEquals(
                "usage: create" + EOL +
                "header" + EOL +
                "     --age=<arg>    the age" + EOL +
                "  -f <arg>          the file" + EOL +
                "  -s,--size=<SIZE>  the size" + EOL +
                "footer" + EOL,
                out.toString());
    }

    @Test
    public void testUsageWithLongOptSeparator() throws Exception
    {
        Options options = new Options();
        options.addOption( "f", true, "the file" );
        options.addOption(Option.builder("s").longOpt("size").desc("the size").hasArg().argName("SIZE").build());
        options.addOption(Option.builder().longOpt("age").desc("the age").hasArg().build());
        
        HelpFormatter formatter = new HelpFormatter();
        formatter.setLongOptSeparator("=");
        
        StringWriter out = new StringWriter();
        
        formatter.printUsage(new PrintWriter(out), 80, "create", options);
        
        assertEquals("usage: create [--age=<arg>] [-f <arg>] [-s <SIZE>]", out.toString().trim());
    }
}
