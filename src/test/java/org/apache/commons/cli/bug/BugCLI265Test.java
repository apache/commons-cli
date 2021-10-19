/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.cli.bug;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.junit.Before;
import org.junit.Test;

/**
 * Test for CLI-265.
 * <p>
 * The issue is that a short option with an optional value will use whatever comes next as value.
 */
public class BugCLI265Test {

    private DefaultParser parser;
    private Options options;

    @Before
    public void setUp() {
        parser = new DefaultParser();

        final Option optionT1 = Option.builder("t1").hasArg().numberOfArgs(1).optionalArg(true).argName("t1_path").build();
        final Option optionA = Option.builder("a").hasArg(false).build();
        final Option optionB = Option.builder("b").hasArg(false).build();
        final Option optionLast = Option.builder("last").hasArg(false).build();

        options = new Options().addOption(optionT1).addOption(optionA).addOption(optionB).addOption(optionLast);
    }

    @Test
    public void shouldParseConcatenatedShortOptions() throws Exception {
        final String[] concatenatedShortOptions = {"-t1", "-ab"};

        final CommandLine commandLine = parser.parse(options, concatenatedShortOptions);

        assertTrue(commandLine.hasOption("t1"));
        assertNull(commandLine.getOptionValue("t1"));
        assertTrue(commandLine.hasOption("a"));
        assertTrue(commandLine.hasOption("b"));
        assertFalse(commandLine.hasOption("last"));
    }

    @Test
    public void shouldParseShortOptionWithoutValue() throws Exception {
        final String[] twoShortOptions = {"-t1", "-last"};

        final CommandLine commandLine = parser.parse(options, twoShortOptions);

        assertTrue(commandLine.hasOption("t1"));
        assertNotEquals("Second option has been used as value for first option", "-last", commandLine.getOptionValue("t1"));
        assertTrue("Second option has not been detected", commandLine.hasOption("last"));
    }

    @Test
    public void shouldParseShortOptionWithValue() throws Exception {
        final String[] shortOptionWithValue = {"-t1", "path/to/my/db"};

        final CommandLine commandLine = parser.parse(options, shortOptionWithValue);

        assertEquals("path/to/my/db", commandLine.getOptionValue("t1"));
        assertFalse(commandLine.hasOption("last"));
    }
}
