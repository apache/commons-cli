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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

/**
 * Abstract test case testing common parser features.
 */
public abstract class ParserTestCase {
    protected CommandLineParser parser;

    protected Options options;

    @SuppressWarnings("deprecation")
    private CommandLine parse(final CommandLineParser parser, final Options opts, final String[] args, final Properties properties) throws ParseException {
        if (parser instanceof Parser) {
            return ((Parser) parser).parse(opts, args, properties);
        }
        if (parser instanceof DefaultParser) {
            return ((DefaultParser) parser).parse(opts, args, properties);
        }
        throw new UnsupportedOperationException("Default options not supported by this parser");
    }

    @Before
    public void setUp() {
        //@formatter:off
        options = new Options()
            .addOption("a", "enable-a", false, "turn [a] on or off")
            .addOption("b", "bfile", true, "set the value of [b]")
            .addOption("c", "copt", false, "turn [c] on or off");
        //@formatter:on
    }

    @Test
    public void testAmbiguousLongWithoutEqualSingleDash() throws Exception {
        final String[] args = {"-b", "-foobar"};

        final Options options = new Options();
        options.addOption(OptionBuilder.withLongOpt("foo").hasOptionalArg().create('f'));
        options.addOption(OptionBuilder.withLongOpt("bar").hasOptionalArg().create('b'));

        final CommandLine cl = parser.parse(options, args);

        assertTrue(cl.hasOption("b"));
        assertTrue(cl.hasOption("f"));
        assertEquals("bar", cl.getOptionValue("foo"));
    }

    @Test
    public void testAmbiguousPartialLongOption1() throws Exception {
        final String[] args = {"--ver"};

        final Options options = new Options();
        options.addOption(OptionBuilder.withLongOpt("version").create());
        options.addOption(OptionBuilder.withLongOpt("verbose").create());

        boolean caught = false;

        try {
            parser.parse(options, args);
        } catch (final AmbiguousOptionException e) {
            caught = true;
            assertEquals("Partial option", "--ver", e.getOption());
            assertNotNull("Matching options null", e.getMatchingOptions());
            assertEquals("Matching options size", 2, e.getMatchingOptions().size());
        }

        assertTrue("Confirm MissingArgumentException caught", caught);
    }

    @Test
    public void testAmbiguousPartialLongOption2() throws Exception {
        final String[] args = {"-ver"};

        final Options options = new Options();
        options.addOption(OptionBuilder.withLongOpt("version").create());
        options.addOption(OptionBuilder.withLongOpt("verbose").create());

        boolean caught = false;

        try {
            parser.parse(options, args);
        } catch (final AmbiguousOptionException e) {
            caught = true;
            assertEquals("Partial option", "-ver", e.getOption());
            assertNotNull("Matching options null", e.getMatchingOptions());
            assertEquals("Matching options size", 2, e.getMatchingOptions().size());
        }

        assertTrue("Confirm MissingArgumentException caught", caught);
    }

    @Test
    public void testAmbiguousPartialLongOption3() throws Exception {
        final String[] args = {"--ver=1"};

        final Options options = new Options();
        options.addOption(OptionBuilder.withLongOpt("version").create());
        options.addOption(OptionBuilder.withLongOpt("verbose").hasOptionalArg().create());

        boolean caught = false;

        try {
            parser.parse(options, args);
        } catch (final AmbiguousOptionException e) {
            caught = true;
            assertEquals("Partial option", "--ver", e.getOption());
            assertNotNull("Matching options null", e.getMatchingOptions());
            assertEquals("Matching options size", 2, e.getMatchingOptions().size());
        }

        assertTrue("Confirm MissingArgumentException caught", caught);
    }

    @Test
    public void testAmbiguousPartialLongOption4() throws Exception {
        final String[] args = {"-ver=1"};

        final Options options = new Options();
        options.addOption(OptionBuilder.withLongOpt("version").create());
        options.addOption(OptionBuilder.withLongOpt("verbose").hasOptionalArg().create());

        boolean caught = false;

        try {
            parser.parse(options, args);
        } catch (final AmbiguousOptionException e) {
            caught = true;
            assertEquals("Partial option", "-ver", e.getOption());
            assertNotNull("Matching options null", e.getMatchingOptions());
            assertEquals("Matching options size", 2, e.getMatchingOptions().size());
        }

        assertTrue("Confirm MissingArgumentException caught", caught);
    }

    @Test
    public void testArgumentStartingWithHyphen() throws Exception {
        final String[] args = {"-b", "-foo"};

        final CommandLine cl = parser.parse(options, args);
        assertEquals("-foo", cl.getOptionValue("b"));
    }

    @Test
    public void testBursting() throws Exception {
        final String[] args = {"-acbtoast", "foo", "bar"};

        final CommandLine cl = parser.parse(options, args);

        assertTrue("Confirm -a is set", cl.hasOption("a"));
        assertTrue("Confirm -b is set", cl.hasOption("b"));
        assertTrue("Confirm -c is set", cl.hasOption("c"));
        assertEquals("Confirm arg of -b", "toast", cl.getOptionValue("b"));
        assertEquals("Confirm size of extra args", 2, cl.getArgList().size());
    }

    @Test
    public void testDoubleDash1() throws Exception {
        final String[] args = {"--copt", "--", "-b", "toast"};

        final CommandLine cl = parser.parse(options, args);

        assertTrue("Confirm -c is set", cl.hasOption("c"));
        assertFalse("Confirm -b is not set", cl.hasOption("b"));
        assertEquals("Confirm 2 extra args: " + cl.getArgList().size(), 2, cl.getArgList().size());
    }

    @Test
    public void testDoubleDash2() throws Exception {
        final Options options = new Options();
        options.addOption(OptionBuilder.hasArg().create('n'));
        options.addOption(OptionBuilder.create('m'));

        try {
            parser.parse(options, new String[] {"-n", "--", "-m"});
            fail("MissingArgumentException not thrown for option -n");
        } catch (final MissingArgumentException e) {
            assertNotNull("option null", e.getOption());
            assertEquals("n", e.getOption().getOpt());
        }
    }

    @Test
    public void testLongOptionQuoteHandling() throws Exception {
        final String[] args = {"--bfile", "\"quoted string\""};

        final CommandLine cl = parser.parse(options, args);

        assertEquals("Confirm --bfile \"arg\" strips quotes",  "quoted string", cl.getOptionValue("b"));
    }

    @Test
    public void testLongOptionWithEqualsQuoteHandling() throws Exception {
        final String[] args = {"--bfile=\"quoted string\""};

        final CommandLine cl = parser.parse(options, args);

        assertEquals("Confirm --bfile=\"arg\" strips quotes",  "quoted string", cl.getOptionValue("b"));
    }

    @Test
    public void testLongWithEqualDoubleDash() throws Exception {
        final String[] args = {"--foo=bar"};

        final Options options = new Options();
        options.addOption(OptionBuilder.withLongOpt("foo").hasArg().create('f'));

        final CommandLine cl = parser.parse(options, args);

        assertEquals("bar", cl.getOptionValue("foo"));
    }

    @Test
    public void testLongWithEqualSingleDash() throws Exception {
        final String[] args = {"-foo=bar"};

        final Options options = new Options();
        options.addOption(OptionBuilder.withLongOpt("foo").hasArg().create('f'));

        final CommandLine cl = parser.parse(options, args);

        assertEquals("bar", cl.getOptionValue("foo"));
    }

    @Test
    public void testLongWithoutEqualDoubleDash() throws Exception {
        final String[] args = {"--foobar"};

        final Options options = new Options();
        options.addOption(OptionBuilder.withLongOpt("foo").hasArg().create('f'));

        final CommandLine cl = parser.parse(options, args, true);

        assertFalse(cl.hasOption("foo")); // foo isn't expected to be recognized with a double dash
    }

    @Test
    public void testLongWithoutEqualSingleDash() throws Exception {
        final String[] args = {"-foobar"};

        final Options options = new Options();
        options.addOption(OptionBuilder.withLongOpt("foo").hasArg().create('f'));

        final CommandLine cl = parser.parse(options, args);

        assertEquals("bar", cl.getOptionValue("foo"));
    }

    @Test
    public void testLongWithUnexpectedArgument1() throws Exception {
        final String[] args = {"--foo=bar"};

        final Options options = new Options();
        options.addOption(OptionBuilder.withLongOpt("foo").create('f'));

        try {
            parser.parse(options, args);
        } catch (final UnrecognizedOptionException e) {
            assertEquals("--foo=bar", e.getOption());
            return;
        }

        fail("UnrecognizedOptionException not thrown");
    }

    @Test
    public void testLongWithUnexpectedArgument2() throws Exception {
        final String[] args = {"-foobar"};

        final Options options = new Options();
        options.addOption(OptionBuilder.withLongOpt("foo").create('f'));

        try {
            parser.parse(options, args);
        } catch (final UnrecognizedOptionException e) {
            assertEquals("-foobar", e.getOption());
            return;
        }

        fail("UnrecognizedOptionException not thrown");
    }

    @Test
    public void testMissingArg() throws Exception {
        final String[] args = {"-b"};

        boolean caught = false;

        try {
            parser.parse(options, args);
        } catch (final MissingArgumentException e) {
            caught = true;
            assertEquals("option missing an argument", "b", e.getOption().getOpt());
        }

        assertTrue("Confirm MissingArgumentException caught", caught);
    }

    @Test
    public void testMissingArgWithBursting() throws Exception {
        final String[] args = {"-acb"};

        boolean caught = false;

        try {
            parser.parse(options, args);
        } catch (final MissingArgumentException e) {
            caught = true;
            assertEquals("option missing an argument", "b", e.getOption().getOpt());
        }

        assertTrue("Confirm MissingArgumentException caught", caught);
    }

    @Test
    public void testMissingRequiredGroup() throws Exception {
        final OptionGroup group = new OptionGroup();
        group.addOption(OptionBuilder.create("a"));
        group.addOption(OptionBuilder.create("b"));
        group.setRequired(true);

        final Options options = new Options();
        options.addOptionGroup(group);
        options.addOption(OptionBuilder.isRequired().create("c"));

        try {
            parser.parse(options, new String[] {"-c"});
            fail("MissingOptionException not thrown");
        } catch (final MissingOptionException e) {
            assertEquals(1, e.getMissingOptions().size());
            assertTrue(e.getMissingOptions().get(0) instanceof OptionGroup);
        } catch (final ParseException e) {
            fail("Expected to catch MissingOptionException");
        }
    }

    @Test
    public void testMissingRequiredOption() {
        final String[] args = {"-a"};

        final Options options = new Options();
        options.addOption("a", "enable-a", false, null);
        options.addOption(OptionBuilder.withLongOpt("bfile").hasArg().isRequired().create('b'));

        try {
            parser.parse(options, args);
            fail("exception should have been thrown");
        } catch (final MissingOptionException e) {
            assertEquals("Incorrect exception message", "Missing required option: b", e.getMessage());
            assertTrue(e.getMissingOptions().contains("b"));
        } catch (final ParseException e) {
            fail("expected to catch MissingOptionException");
        }
    }

    @Test
    public void testMissingRequiredOptions() {
        final String[] args = {"-a"};

        final Options options = new Options();
        options.addOption("a", "enable-a", false, null);
        options.addOption(OptionBuilder.withLongOpt("bfile").hasArg().isRequired().create('b'));
        options.addOption(OptionBuilder.withLongOpt("cfile").hasArg().isRequired().create('c'));

        try {
            parser.parse(options, args);
            fail("exception should have been thrown");
        } catch (final MissingOptionException e) {
            assertEquals("Incorrect exception message", "Missing required options: b, c", e.getMessage());
            assertTrue(e.getMissingOptions().contains("b"));
            assertTrue(e.getMissingOptions().contains("c"));
        } catch (final ParseException e) {
            fail("expected to catch MissingOptionException");
        }
    }

    @Test
    public void testMultiple() throws Exception {
        final String[] args = {"-c", "foobar", "-b", "toast"};

        CommandLine cl = parser.parse(options, args, true);
        assertTrue("Confirm -c is set", cl.hasOption("c"));
        assertEquals("Confirm  3 extra args: " + cl.getArgList().size(), 3, cl.getArgList().size());

        cl = parser.parse(options, cl.getArgs());

        assertFalse("Confirm -c is not set", cl.hasOption("c"));
        assertTrue("Confirm -b is set", cl.hasOption("b"));
        assertEquals("Confirm arg of -b", "toast", cl.getOptionValue("b"));
        assertEquals("Confirm  1 extra arg: " + cl.getArgList().size(), 1, cl.getArgList().size());
        assertEquals("Confirm  value of extra arg: " + cl.getArgList().get(0), "foobar", cl.getArgList().get(0));
    }

    @Test
    public void testMultipleWithLong() throws Exception {
        final String[] args = {"--copt", "foobar", "--bfile", "toast"};

        CommandLine cl = parser.parse(options, args, true);
        assertTrue("Confirm -c is set", cl.hasOption("c"));
        assertEquals("Confirm  3 extra args: " + cl.getArgList().size(), 3, cl.getArgList().size());

        cl = parser.parse(options, cl.getArgs());

        assertFalse("Confirm -c is not set", cl.hasOption("c"));
        assertTrue("Confirm -b is set", cl.hasOption("b"));
        assertEquals("Confirm arg of -b", "toast", cl.getOptionValue("b"));
        assertEquals("Confirm  1 extra arg: " + cl.getArgList().size(), 1, cl.getArgList().size());
        assertEquals("Confirm  value of extra arg: " + cl.getArgList().get(0), "foobar", cl.getArgList().get(0));
    }

    @Test
    public void testNegativeArgument() throws Exception {
        final String[] args = {"-b", "-1"};

        final CommandLine cl = parser.parse(options, args);
        assertEquals("-1", cl.getOptionValue("b"));
    }

    @Test
    public void testNegativeOption() throws Exception {
        final String[] args = {"-b", "-1"};

        options.addOption("1", false, null);

        final CommandLine cl = parser.parse(options, args);
        assertEquals("-1", cl.getOptionValue("b"));
    }

    @Test
    public void testOptionAndRequiredOption() throws Exception {
        final String[] args = {"-a", "-b", "file"};

        final Options options = new Options();
        options.addOption("a", "enable-a", false, null);
        options.addOption(OptionBuilder.withLongOpt("bfile").hasArg().isRequired().create('b'));

        final CommandLine cl = parser.parse(options, args);

        assertTrue("Confirm -a is set", cl.hasOption("a"));
        assertTrue("Confirm -b is set", cl.hasOption("b"));
        assertEquals("Confirm arg of -b", "file", cl.getOptionValue("b"));
        assertTrue("Confirm NO of extra args", cl.getArgList().isEmpty());
    }

    @Test
    public void testOptionGroup() throws Exception {
        final OptionGroup group = new OptionGroup();
        group.addOption(OptionBuilder.create("a"));
        group.addOption(OptionBuilder.create("b"));

        final Options options = new Options();
        options.addOptionGroup(group);

        parser.parse(options, new String[] {"-b"});

        assertEquals("selected option", "b", group.getSelected());
    }

    @Test
    public void testOptionGroupLong() throws Exception {
        final OptionGroup group = new OptionGroup();
        group.addOption(OptionBuilder.withLongOpt("foo").create());
        group.addOption(OptionBuilder.withLongOpt("bar").create());

        final Options options = new Options();
        options.addOptionGroup(group);

        final CommandLine cl = parser.parse(options, new String[] {"--bar"});

        assertTrue(cl.hasOption("bar"));
        assertEquals("selected option", "bar", group.getSelected());
    }

    @Test
    public void testPartialLongOptionSingleDash() throws Exception {
        final String[] args = {"-ver"};

        final Options options = new Options();
        options.addOption(OptionBuilder.withLongOpt("version").create());
        options.addOption(OptionBuilder.hasArg().create('v'));

        final CommandLine cl = parser.parse(options, args);

        assertTrue("Confirm --version is set", cl.hasOption("version"));
        assertFalse("Confirm -v is not set", cl.hasOption("v"));
    }

    @Test
    public void testPropertiesOption1() throws Exception {
        final String[] args = {"-Jsource=1.5", "-J", "target", "1.5", "foo"};

        final Options options = new Options();
        options.addOption(OptionBuilder.withValueSeparator().hasArgs(2).create('J'));

        final CommandLine cl = parser.parse(options, args);

        final List<String> values = Arrays.asList(cl.getOptionValues("J"));
        assertNotNull("null values", values);
        assertEquals("number of values", 4, values.size());
        assertEquals("value 1", "source", values.get(0));
        assertEquals("value 2", "1.5", values.get(1));
        assertEquals("value 3", "target", values.get(2));
        assertEquals("value 4", "1.5", values.get(3));

        final List<?> argsleft = cl.getArgList();
        assertEquals("Should be 1 arg left", 1, argsleft.size());
        assertEquals("Expecting foo", "foo", argsleft.get(0));
    }

    @Test
    public void testPropertiesOption2() throws Exception {
        final String[] args = {"-Dparam1", "-Dparam2=value2", "-D"};

        final Options options = new Options();
        options.addOption(OptionBuilder.withValueSeparator().hasOptionalArgs(2).create('D'));

        final CommandLine cl = parser.parse(options, args);

        final Properties props = cl.getOptionProperties("D");
        assertNotNull("null properties", props);
        assertEquals("number of properties in " + props, 2, props.size());
        assertEquals("property 1", "true", props.getProperty("param1"));
        assertEquals("property 2", "value2", props.getProperty("param2"));

        final List<?> argsleft = cl.getArgList();
        assertEquals("Should be no arg left", 0, argsleft.size());
    }

    @Test
    public void testPropertyOptionFlags() throws Exception {
        final Options opts = new Options();
        opts.addOption("a", false, "toggle -a");
        opts.addOption("c", "c", false, "toggle -c");
        opts.addOption(OptionBuilder.hasOptionalArg().create('e'));

        Properties properties = new Properties();
        properties.setProperty("a", "true");
        properties.setProperty("c", "yes");
        properties.setProperty("e", "1");

        CommandLine cmd = parse(parser, opts, null, properties);
        assertTrue(cmd.hasOption("a"));
        assertTrue(cmd.hasOption("c"));
        assertTrue(cmd.hasOption("e"));

        properties = new Properties();
        properties.setProperty("a", "false");
        properties.setProperty("c", "no");
        properties.setProperty("e", "0");

        cmd = parse(parser, opts, null, properties);
        assertFalse(cmd.hasOption("a"));
        assertFalse(cmd.hasOption("c"));
        assertTrue(cmd.hasOption("e")); // this option accepts an argument

        properties = new Properties();
        properties.setProperty("a", "TRUE");
        properties.setProperty("c", "nO");
        properties.setProperty("e", "TrUe");

        cmd = parse(parser, opts, null, properties);
        assertTrue(cmd.hasOption("a"));
        assertFalse(cmd.hasOption("c"));
        assertTrue(cmd.hasOption("e"));

        properties = new Properties();
        properties.setProperty("a", "just a string");
        properties.setProperty("e", "");

        cmd = parse(parser, opts, null, properties);
        assertFalse(cmd.hasOption("a"));
        assertFalse(cmd.hasOption("c"));
        assertTrue(cmd.hasOption("e"));

        properties = new Properties();
        properties.setProperty("a", "0");
        properties.setProperty("c", "1");

        cmd = parse(parser, opts, null, properties);
        assertFalse(cmd.hasOption("a"));
        assertTrue(cmd.hasOption("c"));
    }

    @Test
    public void testPropertyOptionGroup() throws Exception {
        final Options opts = new Options();

        final OptionGroup group1 = new OptionGroup();
        group1.addOption(new Option("a", null));
        group1.addOption(new Option("b", null));
        opts.addOptionGroup(group1);

        final OptionGroup group2 = new OptionGroup();
        group2.addOption(new Option("x", null));
        group2.addOption(new Option("y", null));
        opts.addOptionGroup(group2);

        final String[] args = {"-a"};

        final Properties properties = new Properties();
        properties.put("b", "true");
        properties.put("x", "true");

        final CommandLine cmd = parse(parser, opts, args, properties);

        assertTrue(cmd.hasOption("a"));
        assertFalse(cmd.hasOption("b"));
        assertTrue(cmd.hasOption("x"));
        assertFalse(cmd.hasOption("y"));
    }

    @Test
    public void testPropertyOptionMultipleValues() throws Exception {
        final Options opts = new Options();
        opts.addOption(OptionBuilder.hasArgs().withValueSeparator(',').create('k'));

        final Properties properties = new Properties();
        properties.setProperty("k", "one,two");

        final String[] values = {"one", "two"};

        final CommandLine cmd = parse(parser, opts, null, properties);
        assertTrue(cmd.hasOption("k"));
        assertArrayEquals(values, cmd.getOptionValues('k'));
    }

    @Test
    public void testPropertyOptionRequired() throws Exception {
        final Options opts = new Options();
        opts.addOption(OptionBuilder.isRequired().create("f"));

        final Properties properties = new Properties();
        properties.setProperty("f", "true");

        final CommandLine cmd = parse(parser, opts, null, properties);
        assertTrue(cmd.hasOption("f"));
    }

    @Test
    public void testPropertyOptionSingularValue() throws Exception {
        final Options opts = new Options();
        opts.addOption(OptionBuilder.hasOptionalArgs(2).withLongOpt("hide").create());

        final Properties properties = new Properties();
        properties.setProperty("hide", "seek");

        final CommandLine cmd = parse(parser, opts, null, properties);
        assertTrue(cmd.hasOption("hide"));
        assertEquals("seek", cmd.getOptionValue("hide"));
        assertFalse(cmd.hasOption("fake"));
    }

    @Test
    public void testPropertyOptionUnexpected() throws Exception {
        final Options opts = new Options();

        final Properties properties = new Properties();
        properties.setProperty("f", "true");

        try {
            parse(parser, opts, null, properties);
            fail("UnrecognizedOptionException expected");
        } catch (final UnrecognizedOptionException e) {
            // expected
        }
    }

    @Test
    public void testPropertyOverrideValues() throws Exception {
        final Options opts = new Options();
        opts.addOption(OptionBuilder.hasOptionalArgs(2).create('i'));
        opts.addOption(OptionBuilder.hasOptionalArgs().create('j'));

        final String[] args = {"-j", "found", "-i", "ink"};

        final Properties properties = new Properties();
        properties.setProperty("j", "seek");

        final CommandLine cmd = parse(parser, opts, args, properties);
        assertTrue(cmd.hasOption("j"));
        assertEquals("found", cmd.getOptionValue("j"));
        assertTrue(cmd.hasOption("i"));
        assertEquals("ink", cmd.getOptionValue("i"));
        assertFalse(cmd.hasOption("fake"));
    }

    @Test
    public void testReuseOptionsTwice() throws Exception {
        final Options opts = new Options();
        opts.addOption(OptionBuilder.isRequired().create('v'));

        // first parsing
        parser.parse(opts, new String[] {"-v"});

        try {
            // second parsing, with the same Options instance and an invalid command line
            parser.parse(opts, new String[0]);
            fail("MissingOptionException not thrown");
        } catch (final MissingOptionException e) {
            // expected
        }
    }

    @Test
    public void testShortOptionConcatenatedQuoteHandling() throws Exception {
        final String[] args = {"-b\"quoted string\""};

        final CommandLine cl = parser.parse(options, args);

        assertEquals("Confirm -b\"arg\" strips quotes",  "quoted string", cl.getOptionValue("b"));
    }

    @Test
    public void testShortOptionQuoteHandling() throws Exception {
        final String[] args = {"-b", "\"quoted string\""};

        final CommandLine cl = parser.parse(options, args);

        assertEquals("Confirm -b \"arg\" strips quotes",  "quoted string", cl.getOptionValue("b"));
    }

    @Test
    public void testShortWithEqual() throws Exception {
        final String[] args = {"-f=bar"};

        final Options options = new Options();
        options.addOption(OptionBuilder.withLongOpt("foo").hasArg().create('f'));

        final CommandLine cl = parser.parse(options, args);

        assertEquals("bar", cl.getOptionValue("foo"));
    }

    @Test
    public void testShortWithoutEqual() throws Exception {
        final String[] args = {"-fbar"};

        final Options options = new Options();
        options.addOption(OptionBuilder.withLongOpt("foo").hasArg().create('f'));

        final CommandLine cl = parser.parse(options, args);

        assertEquals("bar", cl.getOptionValue("foo"));
    }

    @Test
    public void testShortWithUnexpectedArgument() throws Exception {
        final String[] args = {"-f=bar"};

        final Options options = new Options();
        options.addOption(OptionBuilder.withLongOpt("foo").create('f'));

        try {
            parser.parse(options, args);
        } catch (final UnrecognizedOptionException e) {
            assertEquals("-f=bar", e.getOption());
            return;
        }

        fail("UnrecognizedOptionException not thrown");
    }

    @Test
    public void testSimpleLong() throws Exception {
        final String[] args = {"--enable-a", "--bfile", "toast", "foo", "bar"};

        final CommandLine cl = parser.parse(options, args);

        assertTrue("Confirm -a is set", cl.hasOption("a"));
        assertTrue("Confirm -b is set", cl.hasOption("b"));
        assertEquals("Confirm arg of -b", "toast", cl.getOptionValue("b"));
        assertEquals("Confirm arg of --bfile", "toast", cl.getOptionValue("bfile"));
        assertEquals("Confirm size of extra args", 2, cl.getArgList().size());
    }

    @Test
    public void testSimpleShort() throws Exception {
        final String[] args = {"-a", "-b", "toast", "foo", "bar"};

        final CommandLine cl = parser.parse(options, args);

        assertTrue("Confirm -a is set", cl.hasOption("a"));
        assertTrue("Confirm -b is set", cl.hasOption("b"));
        assertEquals("Confirm arg of -b", "toast", cl.getOptionValue("b"));
        assertEquals("Confirm size of extra args", 2, cl.getArgList().size());
    }

    @Test
    public void testSingleDash() throws Exception {
        final String[] args = {"--copt", "-b", "-", "-a", "-"};

        final CommandLine cl = parser.parse(options, args);

        assertTrue("Confirm -a is set", cl.hasOption("a"));
        assertTrue("Confirm -b is set", cl.hasOption("b"));
        assertEquals("Confirm arg of -b", "-", cl.getOptionValue("b"));
        assertEquals("Confirm 1 extra arg: " + cl.getArgList().size(), 1, cl.getArgList().size());
        assertEquals("Confirm value of extra arg: " + cl.getArgList().get(0), "-", cl.getArgList().get(0));
    }

    @Test
    public void testStopAtExpectedArg() throws Exception {
        final String[] args = {"-b", "foo"};

        final CommandLine cl = parser.parse(options, args, true);

        assertTrue("Confirm -b is set", cl.hasOption('b'));
        assertEquals("Confirm -b is set", "foo", cl.getOptionValue('b'));
        assertTrue("Confirm no extra args: " + cl.getArgList().size(), cl.getArgList().isEmpty());
    }

    @Test
    public void testStopAtNonOptionLong() throws Exception {
        final String[] args = {"--zop==1", "-abtoast", "--b=bar"};

        final CommandLine cl = parser.parse(options, args, true);

        assertFalse("Confirm -a is not set", cl.hasOption("a"));
        assertFalse("Confirm -b is not set", cl.hasOption("b"));
        assertEquals("Confirm  3 extra args: " + cl.getArgList().size(), 3, cl.getArgList().size());
    }

    @Test
    public void testStopAtNonOptionShort() throws Exception {
        final String[] args = {"-z", "-a", "-btoast"};

        final CommandLine cl = parser.parse(options, args, true);
        assertFalse("Confirm -a is not set", cl.hasOption("a"));
        assertEquals("Confirm  3 extra args: " + cl.getArgList().size(), 3, cl.getArgList().size());
    }

    @Test
    public void testStopAtUnexpectedArg() throws Exception {
        final String[] args = {"-c", "foober", "-b", "toast"};

        final CommandLine cl = parser.parse(options, args, true);
        assertTrue("Confirm -c is set", cl.hasOption("c"));
        assertEquals("Confirm  3 extra args: " + cl.getArgList().size(), 3, cl.getArgList().size());
    }

    @Test
    public void testStopBursting() throws Exception {
        final String[] args = {"-azc"};

        final CommandLine cl = parser.parse(options, args, true);
        assertTrue("Confirm -a is set", cl.hasOption("a"));
        assertFalse("Confirm -c is not set", cl.hasOption("c"));

        assertEquals("Confirm  1 extra arg: " + cl.getArgList().size(), 1, cl.getArgList().size());
        assertTrue(cl.getArgList().contains("zc"));
    }

    @Test
    public void testStopBursting2() throws Exception {
        final String[] args = {"-c", "foobar", "-btoast"};

        CommandLine cl = parser.parse(options, args, true);
        assertTrue("Confirm -c is set", cl.hasOption("c"));
        assertEquals("Confirm  2 extra args: " + cl.getArgList().size(), 2, cl.getArgList().size());

        cl = parser.parse(options, cl.getArgs());

        assertFalse("Confirm -c is not set", cl.hasOption("c"));
        assertTrue("Confirm -b is set", cl.hasOption("b"));
        assertEquals("Confirm arg of -b", "toast", cl.getOptionValue("b"));
        assertEquals("Confirm  1 extra arg: " + cl.getArgList().size(), 1, cl.getArgList().size());
        assertEquals("Confirm  value of extra arg: " + cl.getArgList().get(0), "foobar", cl.getArgList().get(0));
    }

    @Test
    public void testUnambiguousPartialLongOption1() throws Exception {
        final String[] args = {"--ver"};

        final Options options = new Options();
        options.addOption(OptionBuilder.withLongOpt("version").create());
        options.addOption(OptionBuilder.withLongOpt("help").create());

        final CommandLine cl = parser.parse(options, args);

        assertTrue("Confirm --version is set", cl.hasOption("version"));
    }

    @Test
    public void testUnambiguousPartialLongOption2() throws Exception {
        final String[] args = {"-ver"};

        final Options options = new Options();
        options.addOption(OptionBuilder.withLongOpt("version").create());
        options.addOption(OptionBuilder.withLongOpt("help").create());

        final CommandLine cl = parser.parse(options, args);

        assertTrue("Confirm --version is set", cl.hasOption("version"));
    }

    @Test
    public void testUnambiguousPartialLongOption3() throws Exception {
        final String[] args = {"--ver=1"};

        final Options options = new Options();
        options.addOption(OptionBuilder.withLongOpt("verbose").hasOptionalArg().create());
        options.addOption(OptionBuilder.withLongOpt("help").create());

        final CommandLine cl = parser.parse(options, args);

        assertTrue("Confirm --verbose is set", cl.hasOption("verbose"));
        assertEquals("1", cl.getOptionValue("verbose"));
    }

    @Test
    public void testUnambiguousPartialLongOption4() throws Exception {
        final String[] args = {"-ver=1"};

        final Options options = new Options();
        options.addOption(OptionBuilder.withLongOpt("verbose").hasOptionalArg().create());
        options.addOption(OptionBuilder.withLongOpt("help").create());

        final CommandLine cl = parser.parse(options, args);

        assertTrue("Confirm --verbose is set", cl.hasOption("verbose"));
        assertEquals("1", cl.getOptionValue("verbose"));
    }

    @Test
    public void testUnlimitedArgs() throws Exception {
        final String[] args = {"-e", "one", "two", "-f", "alpha"};

        final Options options = new Options();
        options.addOption(OptionBuilder.hasArgs().create("e"));
        options.addOption(OptionBuilder.hasArgs().create("f"));

        final CommandLine cl = parser.parse(options, args);

        assertTrue("Confirm -e is set", cl.hasOption("e"));
        assertEquals("number of arg for -e", 2, cl.getOptionValues("e").length);
        assertTrue("Confirm -f is set", cl.hasOption("f"));
        assertEquals("number of arg for -f", 1, cl.getOptionValues("f").length);
    }

    @Test
    public void testUnrecognizedOption() throws Exception {
        final String[] args = {"-a", "-d", "-b", "toast", "foo", "bar"};

        try {
            parser.parse(options, args);
            fail("UnrecognizedOptionException wasn't thrown");
        } catch (final UnrecognizedOptionException e) {
            assertEquals("-d", e.getOption());
        }
    }

    @Test
    public void testUnrecognizedOptionWithBursting() throws Exception {
        final String[] args = {"-adbtoast", "foo", "bar"};

        try {
            parser.parse(options, args);
            fail("UnrecognizedOptionException wasn't thrown");
        } catch (final UnrecognizedOptionException e) {
            assertEquals("-adbtoast", e.getOption());
        }
    }

    @Test
    public void testWithRequiredOption() throws Exception {
        final String[] args = {"-b", "file"};

        final Options options = new Options();
        options.addOption("a", "enable-a", false, null);
        options.addOption(OptionBuilder.withLongOpt("bfile").hasArg().isRequired().create('b'));

        final CommandLine cl = parser.parse(options, args);

        assertFalse("Confirm -a is NOT set", cl.hasOption("a"));
        assertTrue("Confirm -b is set", cl.hasOption("b"));
        assertEquals("Confirm arg of -b", "file", cl.getOptionValue("b"));
        assertTrue("Confirm NO of extra args", cl.getArgList().isEmpty());
    }

}
