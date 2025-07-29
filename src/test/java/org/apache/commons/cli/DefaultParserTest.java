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

package org.apache.commons.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.commons.cli.DefaultParser.Builder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

class DefaultParserTest extends AbstractParserTestCase {

    static class ExternalArgumentsProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(final ExtensionContext context) {
            return Stream.of(
                    /* Arguments:
                     * 1. test case name
                     * 2. parser
                     * 3. input string
                     * 4. expected option value
                     * 5. checked option
                     * 6. assertion message
                     */
                    Arguments.of(
                            "Long option quote handling DEFAULT behavior",
                            DefaultParser.builder().get(),
                            new String[]{"--bfile", "\"quoted string\""},
                            "quoted string",
                            "b",
                            "Confirm --bfile=\"arg\" strips quotes"
                    ),
                    Arguments.of(
                            "Long option with equals quote handling DEFAULT behavior",
                            DefaultParser.builder().get(),
                            new String[]{"--bfile=\"quoted string\""},
                            "\"quoted string\"",
                            "b",
                            "Confirm --bfile=\"arg\" keeps quotes"
                    ),
                    Arguments.of(
                            "Short option quote handling DEFAULT behavior",
                            DefaultParser.builder().get(),
                            new String[]{"-b", "\"quoted string\""},
                            "quoted string",
                            "b",
                            "Confirm -b\"arg\" strips quotes"
                    ),
                    Arguments.of(
                            "Short option concatenated quote handling DEFAULT behavior",
                            DefaultParser.builder().get(),
                            new String[]{"-b\"quoted string\""},
                            "\"quoted string\"",
                            "b",
                            "Confirm -b\"arg\" keeps quotes"
                    ),
                    Arguments.of(
                            "Long option quote handling WITHOUT strip",
                            DefaultParser.builder().setStripLeadingAndTrailingQuotes(false).get(),
                            new String[]{"--bfile", "\"quoted string\""},
                            "\"quoted string\"",
                            "b",
                            "Confirm --bfile \"arg\" keeps quotes"
                    ),
                    Arguments.of(
                            "Long option with equals quote handling WITHOUT strip",
                            DefaultParser.builder().setStripLeadingAndTrailingQuotes(false).get(),
                            new String[]{"--bfile=\"quoted string\""},
                            "\"quoted string\"",
                            "b",
                            "Confirm --bfile=\"arg\" keeps quotes"
                    ),
                    Arguments.of(
                            "Short option quote handling WITHOUT strip",
                            DefaultParser.builder().setStripLeadingAndTrailingQuotes(false).get(),
                            new String[]{"-b", "\"quoted string\""},
                            "\"quoted string\"",
                            "b",
                            "Confirm -b\"arg\" keeps quotes"
                    ),
                    Arguments.of(
                            "Short option concatenated quote handling WITHOUT strip",
                            DefaultParser.builder().setStripLeadingAndTrailingQuotes(false).get(),
                            new String[]{"-b\"quoted string\""},
                            "\"quoted string\"",
                            "b",
                            "Confirm -b\"arg\" keeps quotes"
                    ),
                    Arguments.of(
                            "Long option quote handling WITH strip",
                            DefaultParser.builder().setStripLeadingAndTrailingQuotes(true).get(),
                            new String[]{"--bfile", "\"quoted string\""},
                            "quoted string",
                            "b",
                            "Confirm --bfile \"arg\" strips quotes"
                    ),
                    Arguments.of(
                            "Long option With Equals Quote Handling WITH Strip",
                            DefaultParser.builder().setStripLeadingAndTrailingQuotes(true).get(),
                            new String[]{"--bfile=\"quoted string\""},
                            "quoted string",
                            "b",
                            "Confirm --bfile=\"arg\" strips quotes"
                    ),
                    Arguments.of(
                            "Short option quote handling WITH strip",
                            DefaultParser.builder().setStripLeadingAndTrailingQuotes(true).get(),
                            new String[]{"-b", "\"quoted string\""},
                            "quoted string",
                            "b",
                            "Confirm -b \"arg\" strips quotes"
                    ),
                    Arguments.of(
                            "Short option concatenated quote handling WITH strip",
                            DefaultParser.builder().setStripLeadingAndTrailingQuotes(true).get(),
                            new String[]{"-b\"quoted string\""},
                            "quoted string",
                            "b",
                            "Confirm -b\"arg\" strips quotes"
                    )
            );
        }
    }

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        parser = new DefaultParser();
    }

    @Test
    void testBuilder() {
        // @formatter:off
        final Builder builder = DefaultParser.builder()
                .setStripLeadingAndTrailingQuotes(false)
                .setAllowPartialMatching(false)
                .setDeprecatedHandler(null);
        // @formatter:on
        parser = builder.build();
        assertEquals(DefaultParser.class, parser.getClass());
        parser = builder.get();
        assertEquals(DefaultParser.class, parser.getClass());
    }

    @Test
    void testDeprecated() throws ParseException {
        final Set<Option> handler = new HashSet<>();
        parser = DefaultParser.builder().setDeprecatedHandler(handler::add).build();
        final Option opt1 = Option.builder().option("d1").deprecated().get();
        // @formatter:off
        final Option opt2 = Option.builder().option("d2").deprecated(DeprecatedAttributes.builder()
        .setForRemoval(true)
        .setSince("1.0")
        .setDescription("Do this instead.").get()).get();
        // @formatter:on
        final Option opt3 = Option.builder().option("a").get();
        // @formatter:off
        final CommandLine cl = parser.parse(new Options()
                .addOption(opt1)
                .addOption(opt2)
                .addOption(opt3),
                new String[] {"-d1", "-d2", "-a"});
        // @formatter:on
        // Trigger handler:
        assertTrue(cl.hasOption(opt1.getOpt()));
        assertTrue(cl.hasOption(opt2.getOpt()));
        assertTrue(cl.hasOption(opt3.getOpt()));
        // Assert handler was triggered
        assertTrue(handler.contains(opt1));
        assertTrue(handler.contains(opt2));
        assertFalse(handler.contains(opt3));
    }

    @Test
    void testLegacyStopAtNonOption() throws ParseException {
        final Option a = Option.builder().option("a").longOpt("first-letter").get();
        final Option b = Option.builder().option("b").longOpt("second-letter").get();
        final Option c = Option.builder().option("c").longOpt("third-letter").get();

        final Options options = new Options();
        options.addOption(a);
        options.addOption(b);
        options.addOption(c);

        final String[] args = {"-a", "-b", "-c", "-d", "arg1", "arg2"}; // -d is rogue option

        final DefaultParser parser = new DefaultParser();

        final CommandLine commandLine = parser.parse(options, args, null, true);
        assertEquals(3, commandLine.getOptions().length);
        assertEquals(3, commandLine.getArgs().length);
        assertTrue(commandLine.getArgList().contains("-d"));
        assertTrue(commandLine.getArgList().contains("arg1"));
        assertTrue(commandLine.getArgList().contains("arg2"));

        final UnrecognizedOptionException e = assertThrows(UnrecognizedOptionException.class, () -> parser.parse(options, args, null, false));
        assertTrue(e.getMessage().contains("-d"));
    }

    @Override
    @Test
    @Disabled("Test case handled in the parameterized tests as \"DEFAULT behavior\"")
    void testLongOptionWithEqualsQuoteHandling() throws Exception {
    }

    @ParameterizedTest(name = "{index}. {0}")
    @ArgumentsSource(ExternalArgumentsProvider.class)
    void testParameterized(final String testName, final CommandLineParser parser, final String[] args, final String expected,
        final String option, final String message) throws Exception {
        final CommandLine cl = parser.parse(options, args);

        assertEquals(expected, cl.getOptionValue(option), message);
    }

    @Test
    void testParseIgnoreHappyPath() throws ParseException {
        final Option a = Option.builder().option("a").longOpt("first-letter").get();
        final Option b = Option.builder().option("b").longOpt("second-letter").get();
        final Option c = Option.builder().option("c").longOpt("third-letter").get();
        final Option d = Option.builder().option("d").longOpt("fourth-letter").get();

        final Options baseOptions = new Options();
        baseOptions.addOption(a);
        baseOptions.addOption(b);
        final Options specificOptions = new Options();
        specificOptions.addOption(a);
        specificOptions.addOption(b);
        specificOptions.addOption(c);
        specificOptions.addOption(d);

        final String[] args = {"-a", "-b", "-c", "-d", "arg1", "arg2"};

        final DefaultParser parser = new DefaultParser();

        final CommandLine baseCommandLine = parser.parse(baseOptions, null, DefaultParser.NonOptionAction.IGNORE, args);
        assertEquals(2, baseCommandLine.getOptions().length);
        assertEquals(2, baseCommandLine.getArgs().length);
        assertTrue(baseCommandLine.hasOption("a"));
        assertTrue(baseCommandLine.hasOption("b"));
        assertFalse(baseCommandLine.hasOption("c"));
        assertFalse(baseCommandLine.hasOption("d"));
        assertFalse(baseCommandLine.getArgList().contains("-a"));
        assertFalse(baseCommandLine.getArgList().contains("-b"));
        assertFalse(baseCommandLine.getArgList().contains("-c"));
        assertFalse(baseCommandLine.getArgList().contains("-d"));
        assertTrue(baseCommandLine.getArgList().contains("arg1"));
        assertTrue(baseCommandLine.getArgList().contains("arg2"));

        final CommandLine specificCommandLine = parser.parse(specificOptions, null, DefaultParser.NonOptionAction.THROW, args);
        assertEquals(4, specificCommandLine.getOptions().length);
        assertEquals(2, specificCommandLine.getArgs().length);
        assertTrue(specificCommandLine.hasOption("a"));
        assertTrue(specificCommandLine.hasOption("b"));
        assertTrue(specificCommandLine.hasOption("c"));
        assertTrue(specificCommandLine.hasOption("d"));
        assertFalse(specificCommandLine.getArgList().contains("-a"));
        assertFalse(specificCommandLine.getArgList().contains("-b"));
        assertFalse(specificCommandLine.getArgList().contains("-c"));
        assertFalse(specificCommandLine.getArgList().contains("-d"));
        assertTrue(specificCommandLine.getArgList().contains("arg1"));
        assertTrue(specificCommandLine.getArgList().contains("arg2"));
    }

    @Test
    void testParseIgnoreNonHappyPath() throws ParseException {
        final Option a = Option.builder().option("a").longOpt("first-letter").get();
        final Option b = Option.builder().option("b").longOpt("second-letter").get();
        final Option c = Option.builder().option("c").longOpt("third-letter").get();

        final Options baseOptions = new Options();
        baseOptions.addOption(a);
        baseOptions.addOption(b);
        final Options specificOptions = new Options();
        specificOptions.addOption(a);
        specificOptions.addOption(b);
        specificOptions.addOption(c);

        final String[] args = {"-a", "-b", "-c", "-d", "arg1", "arg2"}; // -d is rogue option

        final DefaultParser parser = new DefaultParser();

        final CommandLine baseCommandLine = parser.parse(baseOptions, null, DefaultParser.NonOptionAction.IGNORE, args);
        assertEquals(2, baseCommandLine.getOptions().length);
        assertEquals(2, baseCommandLine.getArgs().length);

        final UnrecognizedOptionException e = assertThrows(UnrecognizedOptionException.class,
                () -> parser.parse(specificOptions, null, DefaultParser.NonOptionAction.THROW, args));
        assertTrue(e.getMessage().contains("-d"));
    }

    @Test
    void testParseNullOption() throws ParseException {
        // Edge case
        assertThrows(NullPointerException.class, () -> new DefaultParser().parse(null, null, DefaultParser.NonOptionAction.IGNORE, "-a"));
    }

    @Test
    void testParseSkipHappyPath() throws ParseException {
        final Option a = Option.builder().option("a").longOpt("first-letter").get();
        final Option b = Option.builder().option("b").longOpt("second-letter").get();
        final Option c = Option.builder().option("c").longOpt("third-letter").get();
        final Option d = Option.builder().option("d").longOpt("fourth-letter").get();

        final Options baseOptions = new Options();
        baseOptions.addOption(a);
        baseOptions.addOption(b);
        final Options specificOptions = new Options();
        specificOptions.addOption(a);
        specificOptions.addOption(b);
        specificOptions.addOption(c);
        specificOptions.addOption(d);

        final String[] args = {"-a", "-b", "-c", "-d", "arg1", "arg2"};

        final DefaultParser parser = new DefaultParser();

        final CommandLine baseCommandLine = parser.parse(baseOptions, null, DefaultParser.NonOptionAction.SKIP, args);
        assertEquals(2, baseCommandLine.getOptions().length);
        assertEquals(4, baseCommandLine.getArgs().length);
        assertTrue(baseCommandLine.hasOption("a"));
        assertTrue(baseCommandLine.hasOption("b"));
        assertFalse(baseCommandLine.hasOption("c"));
        assertFalse(baseCommandLine.hasOption("d"));
        assertFalse(baseCommandLine.getArgList().contains("-a"));
        assertFalse(baseCommandLine.getArgList().contains("-b"));
        assertTrue(baseCommandLine.getArgList().contains("-c"));
        assertTrue(baseCommandLine.getArgList().contains("-d"));
        assertTrue(baseCommandLine.getArgList().contains("arg1"));
        assertTrue(baseCommandLine.getArgList().contains("arg2"));

        final CommandLine specificCommandLine = parser.parse(specificOptions, null, DefaultParser.NonOptionAction.THROW, args);
        assertEquals(4, specificCommandLine.getOptions().length);
        assertEquals(2, specificCommandLine.getArgs().length);
        assertTrue(specificCommandLine.hasOption("a"));
        assertTrue(specificCommandLine.hasOption("b"));
        assertTrue(specificCommandLine.hasOption("c"));
        assertTrue(specificCommandLine.hasOption("d"));
        assertFalse(specificCommandLine.getArgList().contains("-a"));
        assertFalse(specificCommandLine.getArgList().contains("-b"));
        assertFalse(specificCommandLine.getArgList().contains("-c"));
        assertFalse(specificCommandLine.getArgList().contains("-d"));
        assertTrue(specificCommandLine.getArgList().contains("arg1"));
        assertTrue(specificCommandLine.getArgList().contains("arg2"));
    }

    @Test
    void testParseSkipNonHappyPath() throws ParseException {
        final Option a = Option.builder().option("a").longOpt("first-letter").get();
        final Option b = Option.builder().option("b").longOpt("second-letter").get();
        final Option c = Option.builder().option("c").longOpt("third-letter").get();

        final Options baseOptions = new Options();
        baseOptions.addOption(a);
        baseOptions.addOption(b);
        final Options specificOptions = new Options();
        specificOptions.addOption(a);
        specificOptions.addOption(b);
        specificOptions.addOption(c);

        final String[] args = {"-a", "-b", "-c", "-d", "arg1", "arg2"}; // -d is rogue option

        final DefaultParser parser = new DefaultParser();

        final CommandLine baseCommandLine = parser.parse(baseOptions, null, DefaultParser.NonOptionAction.SKIP, args);
        assertEquals(2, baseCommandLine.getOptions().length);
        assertEquals(4, baseCommandLine.getArgs().length);

        final UnrecognizedOptionException e = assertThrows(UnrecognizedOptionException.class,
                () -> parser.parse(specificOptions, null, DefaultParser.NonOptionAction.THROW, args));
        assertTrue(e.getMessage().contains("-d"));
    }

    @Override
    @Test
    @Disabled("Test case handled in the parameterized tests as \"DEFAULT behavior\"")
    void testShortOptionConcatenatedQuoteHandling() throws Exception {
    }
}
