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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@SuppressWarnings("deprecation") // tests some deprecated classes
public class ValueTest {

    private static final Option NULL_OPTION = null;
    private static final String NULL_STRING = null;

    protected static Stream<CommandLineParser> parsers() {
        return Stream.of(new DefaultParser(), new PosixParser());
    }

    private final Options opts = new Options();

    private CommandLine cl;

    @BeforeEach
    public void setUp() throws Exception {
        opts.addOption("a", false, "toggle -a");
        opts.addOption("b", true, "set -b");
        opts.addOption("c", "c", false, "toggle -c");
        opts.addOption("d", "d", true, "set -d");

        opts.addOption(OptionBuilder.hasOptionalArg().create('e'));
        opts.addOption(OptionBuilder.hasOptionalArg().withLongOpt("fish").create());
        opts.addOption(OptionBuilder.hasOptionalArgs().withLongOpt("gravy").create());
        opts.addOption(OptionBuilder.hasOptionalArgs(2).withLongOpt("hide").create());
        opts.addOption(OptionBuilder.hasOptionalArgs(2).create('i'));
        opts.addOption(OptionBuilder.hasOptionalArgs().create('j'));
        opts.addOption(Option.builder().option("v").hasArg().valueSeparator().build());

        final String[] args = { "-a", "-b", "foo", "--c", "--d", "bar" };

        cl = new PosixParser().parse(opts, args);
    }

    @Test
    public void testLongNoArg() {
        assertTrue(cl.hasOption("c"));
        assertNull(cl.getOptionValue("c"));
    }

    @Test
    public void testLongNoArgWithOption() {
        assertTrue(cl.hasOption(opts.getOption("c")));
        assertNull(cl.getOptionValue(opts.getOption("c")));
    }

    @ParameterizedTest
    @MethodSource("parsers")
    public void testLongOptionalArgValue(final CommandLineParser parser) throws Exception {
        final String[] args = { "--fish", "face" };
        final CommandLine cmd = parser.parse(opts, args);
        assertTrue(cmd.hasOption("fish"));
        assertEquals("face", cmd.getOptionValue("fish"));
    }

    @ParameterizedTest
    @MethodSource("parsers")
    public void testLongOptionalArgValues(final CommandLineParser parser) throws Exception {
        final String[] args = { "--gravy", "gold", "garden" };
        final CommandLine cmd = parser.parse(opts, args);
        assertNull(cmd.getOptionValues(NULL_OPTION));
        assertNull(cmd.getOptionValues(NULL_STRING));
        assertTrue(cmd.hasOption("gravy"));
        assertEquals("gold", cmd.getOptionValue("gravy"));
        assertEquals("gold", cmd.getOptionValues("gravy")[0]);
        assertEquals("garden", cmd.getOptionValues("gravy")[1]);
        assertEquals(cmd.getArgs().length, 0);
    }

    @ParameterizedTest
    @MethodSource("parsers")
    public void testLongOptionalArgValuesWithOption(final CommandLineParser parser) throws Exception {
        final String[] args = { "--gravy", "gold", "garden" };
        final CommandLine cmd = parser.parse(opts, args);
        assertNull(cmd.getOptionValues(NULL_OPTION));
        assertNull(cmd.getOptionValues(NULL_STRING));
        assertTrue(cmd.hasOption(opts.getOption("gravy")));
        assertEquals("gold", cmd.getOptionValue(opts.getOption("gravy")));
        assertEquals("gold", cmd.getOptionValues(opts.getOption("gravy"))[0]);
        assertEquals("garden", cmd.getOptionValues(opts.getOption("gravy"))[1]);
        assertEquals(cmd.getArgs().length, 0);
    }

    @ParameterizedTest
    @MethodSource("parsers")
    public void testLongOptionalArgValueWithOption(final CommandLineParser parser) throws Exception {
        final String[] args = { "--fish", "face" };
        final CommandLine cmd = parser.parse(opts, args);
        assertTrue(cmd.hasOption(opts.getOption("fish")));
        assertEquals("face", cmd.getOptionValue(opts.getOption("fish")));
    }

    @ParameterizedTest
    @MethodSource("parsers")
    public void testLongOptionalNArgValues(final CommandLineParser parser) throws Exception {
        final String[] args = { "--hide", "house", "hair", "head" };
        final CommandLine cmd = parser.parse(opts, args);
        assertTrue(cmd.hasOption("hide"));
        assertEquals("house", cmd.getOptionValue("hide"));
        assertEquals("house", cmd.getOptionValues("hide")[0]);
        assertEquals("hair", cmd.getOptionValues("hide")[1]);
        assertEquals(cmd.getArgs().length, 1);
        assertEquals("head", cmd.getArgs()[0]);
    }

    @ParameterizedTest
    @MethodSource("parsers")
    public void testLongOptionalNArgValuesWithOption(final CommandLineParser parser) throws Exception {
        final CommandLine cmd = parser.parse(opts, new String[] { "--hide", "house", "hair", "head" });
        assertNull(cmd.getOptionValues(NULL_OPTION));
        assertNull(cmd.getOptionValues(NULL_STRING));
        assertTrue(cmd.hasOption(opts.getOption("hide")));
        assertEquals("house", cmd.getOptionValue(opts.getOption("hide")));
        assertEquals("house", cmd.getOptionValues(opts.getOption("hide"))[0]);
        assertEquals("hair", cmd.getOptionValues(opts.getOption("hide"))[1]);
        assertEquals(cmd.getArgs().length, 1);
        assertEquals("head", cmd.getArgs()[0]);
    }

    @ParameterizedTest
    @MethodSource("parsers")
    public void testLongOptionalNoValue(final CommandLineParser parser) throws Exception {
        final String[] args = { "--fish" };
        final CommandLine cmd = parser.parse(opts, args);
        assertTrue(cmd.hasOption("fish"));
        assertNull(cmd.getOptionValue("fish"));
    }

    @ParameterizedTest
    @MethodSource("parsers")
    public void testLongOptionalNoValueWithOption(final CommandLineParser parser) throws Exception {
        final String[] args = { "--fish" };
        final CommandLine cmd = parser.parse(opts, args);
        assertTrue(cmd.hasOption(opts.getOption("fish")));
        assertNull(cmd.getOptionValue(opts.getOption("fish")));
    }

    @Test
    public void testLongWithArg() {
        assertTrue(cl.hasOption("d"));
        assertNotNull(cl.getOptionValue("d"));
        assertEquals(cl.getOptionValue("d"), "bar");
    }

    @Test
    public void testLongWithArgWithOption() {
        assertTrue(cl.hasOption(opts.getOption("d")));
        assertNotNull(cl.getOptionValue(opts.getOption("d")));
        assertEquals(cl.getOptionValue(opts.getOption("d")), "bar");
    }

    @Test
    public void testShortNoArg() {
        assertTrue(cl.hasOption("a"));
        assertNull(cl.getOptionValue("a"));
    }

    @Test
    public void testShortNoArgWithOption() {
        assertTrue(cl.hasOption(opts.getOption("a")));
        assertNull(cl.getOptionValue(opts.getOption("a")));
    }

    @ParameterizedTest
    @MethodSource("parsers")
    public void testShortOptionalArgNoValue(final CommandLineParser parser) throws Exception {
        final String[] args = { "-e" };
        final CommandLine cmd = parser.parse(opts, args);
        assertTrue(cmd.hasOption("e"));
        assertNull(cmd.getOptionValue("e"));
    }

    @ParameterizedTest
    @MethodSource("parsers")
    public void testShortOptionalArgNoValueWithOption(final CommandLineParser parser) throws Exception {
        final String[] args = { "-e" };
        final CommandLine cmd = parser.parse(opts, args);
        assertTrue(cmd.hasOption(opts.getOption("e")));
        assertNull(cmd.getOptionValue(opts.getOption("e")));
    }

    @ParameterizedTest
    @MethodSource("parsers")
    public void testShortOptionalArgValue(final CommandLineParser parser) throws Exception {
        final String[] args = { "-e", "everything" };
        final CommandLine cmd = parser.parse(opts, args);
        assertTrue(cmd.hasOption("e"));
        assertEquals("everything", cmd.getOptionValue("e"));
    }

    @ParameterizedTest
    @MethodSource("parsers")
    public void testShortOptionalArgValues(final CommandLineParser parser) throws Exception {
        final String[] args = { "-j", "ink", "idea" };
        final CommandLine cmd = parser.parse(opts, args);
        assertTrue(cmd.hasOption("j"));
        assertEquals("ink", cmd.getOptionValue("j"));
        assertEquals("ink", cmd.getOptionValues("j")[0]);
        assertEquals("idea", cmd.getOptionValues("j")[1]);
        assertEquals(cmd.getArgs().length, 0);
    }

    @ParameterizedTest
    @MethodSource("parsers")
    public void testShortOptionalArgValuesWithOption(final CommandLineParser parser) throws Exception {
        final String[] args = { "-j", "ink", "idea" };
        final CommandLine cmd = parser.parse(opts, args);
        assertNull(cmd.getOptionValues(NULL_OPTION));
        assertNull(cmd.getOptionValues(NULL_STRING));
        assertTrue(cmd.hasOption(opts.getOption("j")));
        assertEquals("ink", cmd.getOptionValue(opts.getOption("j")));
        assertEquals("ink", cmd.getOptionValues(opts.getOption("j"))[0]);
        assertEquals("idea", cmd.getOptionValues(opts.getOption("j"))[1]);
        assertEquals(cmd.getArgs().length, 0);
    }

    @ParameterizedTest
    @MethodSource("parsers")
    public void testShortOptionalArgValueWithOption(final CommandLineParser parser) throws Exception {
        final String[] args = { "-e", "everything" };
        final CommandLine cmd = parser.parse(opts, args);
        assertNull(cmd.getOptionValues(NULL_OPTION));
        assertNull(cmd.getOptionValues(NULL_STRING));
        assertTrue(cmd.hasOption(opts.getOption("e")));
        assertEquals("everything", cmd.getOptionValue(opts.getOption("e")));
    }

    @ParameterizedTest
    @MethodSource("parsers")
    public void testShortOptionalNArgValues(final CommandLineParser parser) throws Exception {
        final String[] args = { "-i", "ink", "idea", "isotope", "ice" };
        final CommandLine cmd = parser.parse(opts, args);
        assertNull(cmd.getOptionValues(NULL_OPTION));
        assertNull(cmd.getOptionValues(NULL_STRING));
        assertTrue(cmd.hasOption("i"));
        assertEquals("ink", cmd.getOptionValue("i"));
        assertEquals("ink", cmd.getOptionValues("i")[0]);
        assertEquals("idea", cmd.getOptionValues("i")[1]);
        assertEquals(cmd.getArgs().length, 2);
        assertEquals("isotope", cmd.getArgs()[0]);
        assertEquals("ice", cmd.getArgs()[1]);
    }

    @Test
    public void testShortOptionalNArgValuesSeparated() throws Exception {
        final String[] args = { "-v=ink", "-v=idea", "-v=isotope", "-v=ice" };
        final CommandLineParser parser = new DefaultParser();
        final CommandLine cmd = parser.parse(opts, args);
        assertNull(cmd.getOptionValues(NULL_OPTION));
        assertNull(cmd.getOptionValues(NULL_STRING));
        assertTrue(cmd.hasOption("v"));
        assertEquals("ink", cmd.getOptionValue("v"));
        assertEquals("ink", cmd.getOptionValues("v")[0]);
        assertEquals("idea", cmd.getOptionValues("v")[1]);
        assertEquals("isotope", cmd.getOptionValues("v")[2]);
        assertEquals("ice", cmd.getOptionValues("v")[3]);
    }

    @Test
    public void testShortOptionalNArgValuesWithOption() throws Exception {
        final String[] args = { "-i", "ink", "idea", "isotope", "ice" };
        final CommandLineParser parser = new PosixParser();
        final CommandLine cmd = parser.parse(opts, args);
        assertNull(cmd.getOptionValues(NULL_OPTION));
        assertNull(cmd.getOptionValues(NULL_STRING));
        assertTrue(cmd.hasOption("i"));
        assertEquals("ink", cmd.getOptionValue(opts.getOption("i")));
        assertEquals("ink", cmd.getOptionValues(opts.getOption("i"))[0]);
        assertEquals("idea", cmd.getOptionValues(opts.getOption("i"))[1]);
        assertEquals(cmd.getArgs().length, 2);
        assertEquals("isotope", cmd.getArgs()[0]);
        assertEquals("ice", cmd.getArgs()[1]);
    }

    @Test
    public void testShortWithArg() {
        assertTrue(cl.hasOption("b"));
        assertNotNull(cl.getOptionValue("b"));
        assertEquals(cl.getOptionValue("b"), "foo");
    }

    @Test
    public void testShortWithArgWithOption() {
        assertTrue(cl.hasOption(opts.getOption("b")));
        assertNotNull(cl.getOptionValue(opts.getOption("b")));
        assertEquals(cl.getOptionValue(opts.getOption("b")), "foo");
    }
}
