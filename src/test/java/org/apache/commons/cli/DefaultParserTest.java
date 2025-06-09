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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
    void testSkip() throws ParseException {
        final Options options = new Options();
        options.addOption(Option.builder("a").longOpt("first-letter").build());
        options.addOption(Option.builder("b").longOpt("second-letter").build());

        final DefaultParser parser = DefaultParser.builder().get();
        final CommandLine commandLine = parser.parse(options, new String[] {"-a", "-b", "-c", "-d"}, DefaultParser.UnrecognizedTokensOperation.SKIP);
        assertTrue(commandLine.hasOption("a"));
        assertTrue(commandLine.hasOption("b"));
        assertFalse(commandLine.hasOption("c"));
        assertFalse(commandLine.hasOption("d"));

        assertFalse(commandLine.getArgList().contains("-a"));
        assertFalse(commandLine.getArgList().contains("-b"));
        assertTrue(commandLine.getArgList().contains("-c"));
        assertTrue(commandLine.getArgList().contains("-d"));
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
        final Option opt1 = Option.builder().option("d1").deprecated().build();
        // @formatter:off
        final Option opt2 = Option.builder().option("d2").deprecated(DeprecatedAttributes.builder()
                .setForRemoval(true)
                .setSince("1.0")
                .setDescription("Do this instead.").get()).build();
        // @formatter:on
        final Option opt3 = Option.builder().option("a").build();
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

    @Override
    @Test
    @Disabled("Test case handled in the parameterized tests as \"DEFAULT behavior\"")
    void testShortOptionConcatenatedQuoteHandling() throws Exception {
    }
}
