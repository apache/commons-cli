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

package org.apache.commons.cli.bug;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * https://issues.apache.org/jira/browse/CLI-148
 */
@SuppressWarnings("deprecation") // tests some deprecated classes
class BugCLI148Test {
    private Options options;

    @BeforeEach
    public void setUp() throws Exception {
        options = new Options();
        options.addOption(OptionBuilder.hasArg().create('t'));
        options.addOption(OptionBuilder.hasArg().create('s'));
    }

    @Test
    void testWorkaround1() throws Exception {
        final CommandLineParser parser = new PosixParser();
        final String[] args = {"-t-something"};

        final CommandLine commandLine = parser.parse(options, args);
        assertEquals("-something", commandLine.getOptionValue('t'));
    }

    @Test
    void testWorkaround2() throws Exception {
        final CommandLineParser parser = new PosixParser();
        final String[] args = {"-t", "\"-something\""};

        final CommandLine commandLine = parser.parse(options, args);
        assertEquals("-something", commandLine.getOptionValue('t'));
    }
}
