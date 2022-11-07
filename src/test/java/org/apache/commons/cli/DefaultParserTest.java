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

import org.junit.Before;
import org.junit.Test;

public class DefaultParserTest extends ParserTestCase {

    @Override
    @Before
    public void setUp() {
        super.setUp();
        parser = new DefaultParser();
    }

    @Test
    public void testBuilder() {
        parser = DefaultParser.builder()
                .setStripLeadingAndTrailingQuotes(false)
                .setAllowPartialMatching(false)
                .build();
        assertEquals(DefaultParser.class, parser.getClass());
    }

    @Test
    public void testLongOptionQuoteHandlingWithoutStrip() throws Exception {
        parser = DefaultParser.builder().setStripLeadingAndTrailingQuotes(false).build();
        final String[] args = {"--bfile", "\"quoted string\""};

        final CommandLine cl = parser.parse(options, args);

        assertEquals("Confirm --bfile \"arg\" keeps quotes",  "\"quoted string\"", cl.getOptionValue("b"));
    }

    @Test
    public void testLongOptionQuoteHandlingWithStrip() throws Exception {
        parser = DefaultParser.builder().setStripLeadingAndTrailingQuotes(true).build();
        final String[] args = {"--bfile", "\"quoted string\""};

        final CommandLine cl = parser.parse(options, args);

        assertEquals("Confirm --bfile \"arg\" strips quotes",  "quoted string", cl.getOptionValue("b"));
    }

    @Override
    @Test
    public void testLongOptionWithEqualsQuoteHandling() throws Exception {
        final String[] args = {"--bfile=\"quoted string\""};

        final CommandLine cl = parser.parse(options, args);

        assertEquals("Confirm --bfile=\"arg\" strips quotes",  "\"quoted string\"", cl.getOptionValue("b"));
    }

    @Test
    public void testLongOptionWithEqualsQuoteHandlingWithoutStrip() throws Exception {
        parser = DefaultParser.builder().setStripLeadingAndTrailingQuotes(false).build();
        final String[] args = {"--bfile=\"quoted string\""};

        final CommandLine cl = parser.parse(options, args);

        assertEquals("Confirm --bfile=\"arg\" keeps quotes",  "\"quoted string\"", cl.getOptionValue("b"));
    }

    @Test
    public void testLongOptionWithEqualsQuoteHandlingWithStrip() throws Exception {
        parser = DefaultParser.builder().setStripLeadingAndTrailingQuotes(true).build();
        final String[] args = {"--bfile=\"quoted string\""};

        final CommandLine cl = parser.parse(options, args);

        assertEquals("Confirm --bfile=\"arg\" strips quotes",  "quoted string", cl.getOptionValue("b"));
    }

    @Override
    @Test
    public void testShortOptionConcatenatedQuoteHandling() throws Exception {
        final String[] args = {"-b\"quoted string\""};

        final CommandLine cl = parser.parse(options, args);

        //This is behavior is not consistent with the other parsers, but is required for backwards compatibility
        assertEquals("Confirm -b\"arg\" keeps quotes",  "\"quoted string\"", cl.getOptionValue("b"));
    }

    @Test
    public void testShortOptionQuoteHandlingWithoutStrip() throws Exception {
        parser = DefaultParser.builder().setStripLeadingAndTrailingQuotes(false).build();
        final String[] args = {"-b", "\"quoted string\""};

        final CommandLine cl = parser.parse(options, args);

        assertEquals("Confirm -b \"arg\" keeps quotes",  "\"quoted string\"", cl.getOptionValue("b"));
    }

    @Test
    public void testShortOptionQuoteHandlingWithStrip() throws Exception {
        parser = DefaultParser.builder().setStripLeadingAndTrailingQuotes(true).build();
        final String[] args = {"-b", "\"quoted string\""};

        final CommandLine cl = parser.parse(options, args);

        assertEquals("Confirm -b \"arg\" strips quotes",  "quoted string", cl.getOptionValue("b"));
    }
}
