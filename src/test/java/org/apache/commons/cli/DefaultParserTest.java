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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * TODO Needs a rework using JUnit parameterized tests.
 */
public class DefaultParserTest extends AbstractParserTestCase {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        parser = new DefaultParser();
    }

    @Test
    public void testBuilder() {
        // @formatter:off
        parser = DefaultParser.builder()
                .setStripLeadingAndTrailingQuotes(false)
                .setAllowPartialMatching(false)
                .setDeprecatedHandler(null)
                .build();
        // @formatter:on
        assertEquals(DefaultParser.class, parser.getClass());
    }

    @Test
    public void testDeprecated() throws ParseException {
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

    @Test
    public void testLongOptionQuoteHandlingWithoutStrip() throws Exception {
        parser = DefaultParser.builder().setStripLeadingAndTrailingQuotes(false).build();
        final String[] args = {"--bfile", "\"quoted string\""};

        final CommandLine cl = parser.parse(options, args);

        assertEquals("\"quoted string\"", cl.getOptionValue("b"), "Confirm --bfile \"arg\" keeps quotes");
    }

    @Test
    public void testLongOptionQuoteHandlingWithStrip() throws Exception {
        parser = DefaultParser.builder().setStripLeadingAndTrailingQuotes(true).build();
        final String[] args = {"--bfile", "\"quoted string\""};

        final CommandLine cl = parser.parse(options, args);

        assertEquals("quoted string", cl.getOptionValue("b"), "Confirm --bfile \"arg\" strips quotes");
    }

    @Override
    @Test
    public void testLongOptionWithEqualsQuoteHandling() throws Exception {
        final String[] args = {"--bfile=\"quoted string\""};

        final CommandLine cl = parser.parse(options, args);

        assertEquals("\"quoted string\"", cl.getOptionValue("b"), "Confirm --bfile=\"arg\" strips quotes");
    }

    @Test
    public void testLongOptionWithEqualsQuoteHandlingWithoutStrip() throws Exception {
        parser = DefaultParser.builder().setStripLeadingAndTrailingQuotes(false).build();
        final String[] args = {"--bfile=\"quoted string\""};

        final CommandLine cl = parser.parse(options, args);

        assertEquals("\"quoted string\"", cl.getOptionValue("b"), "Confirm --bfile=\"arg\" keeps quotes");
    }

    @Test
    public void testLongOptionWithEqualsQuoteHandlingWithStrip() throws Exception {
        parser = DefaultParser.builder().setStripLeadingAndTrailingQuotes(true).build();
        final String[] args = {"--bfile=\"quoted string\""};

        final CommandLine cl = parser.parse(options, args);

        assertEquals("quoted string", cl.getOptionValue("b"), "Confirm --bfile=\"arg\" strips quotes");
    }

    @Override
    @Test
    public void testShortOptionConcatenatedQuoteHandling() throws Exception {
        final String[] args = {"-b\"quoted string\""};

        final CommandLine cl = parser.parse(options, args);

        //This is behavior is not consistent with the other parsers, but is required for backwards compatibility
        assertEquals("\"quoted string\"", cl.getOptionValue("b"), "Confirm -b\"arg\" keeps quotes");
    }

    @Test
    public void testShortOptionQuoteHandlingWithoutStrip() throws Exception {
        parser = DefaultParser.builder().setStripLeadingAndTrailingQuotes(false).build();
        final String[] args = {"-b", "\"quoted string\""};

        final CommandLine cl = parser.parse(options, args);

        assertEquals("\"quoted string\"", cl.getOptionValue("b"), "Confirm -b \"arg\" keeps quotes");
    }

    @Test
    public void testShortOptionQuoteHandlingWithStrip() throws Exception {
        parser = DefaultParser.builder().setStripLeadingAndTrailingQuotes(true).build();
        final String[] args = {"-b", "\"quoted string\""};

        final CommandLine cl = parser.parse(options, args);

        assertEquals("quoted string", cl.getOptionValue("b"), "Confirm -b \"arg\" strips quotes");
    }
}
