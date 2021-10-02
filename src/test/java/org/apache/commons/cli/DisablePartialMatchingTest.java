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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DisablePartialMatchingTest {
    @Test
    public void testDisablePartialMatching() throws Exception {
        final CommandLineParser parser = new DefaultParser(false);

        final Options options = new Options();

        options.addOption(new Option("d", "debug", false, "Turn on debug."));
        options.addOption(new Option("e", "extract", false, "Turn on extract."));
        options.addOption(new Option("o", "option", true, "Turn on option with argument."));

        final CommandLine line = parser.parse(options, new String[] {"-de", "--option=foobar"});

        assertTrue("There should be an option debug in any case...", line.hasOption("debug"));
        assertTrue("There should be an extract option because partial matching is off", line.hasOption("extract"));
        assertTrue("There should be an option option with a argument value", line.hasOption("option"));
    }

    @Test
    public void testRegularPartialMatching() throws Exception {
        final CommandLineParser parser = new DefaultParser();

        final Options options = new Options();

        options.addOption(new Option("d", "debug", false, "Turn on debug."));
        options.addOption(new Option("e", "extract", false, "Turn on extract."));
        options.addOption(new Option("o", "option", true, "Turn on option with argument."));

        final CommandLine line = parser.parse(options, new String[] {"-de", "--option=foobar"});

        assertTrue("There should be an option debug in any case...", line.hasOption("debug"));
        assertFalse("There should not be an extract option because partial matching only selects debug", line.hasOption("extract"));
        assertTrue("There should be an option option with a argument value", line.hasOption("option"));
    }
}
