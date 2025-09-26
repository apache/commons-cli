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

import org.junit.jupiter.api.Test;

class OptionCountTest {
    private static final Option  VERBOSITY = new Option("v", "verbosity: use multiple times for more");
    private static final Options OPTIONS   = new Options().addOption(VERBOSITY);

    @Test
    void testNoSwitch() throws ParseException {
        final CommandLine cmdLine = new DefaultParser().parse(OPTIONS, new String[]{});
        assertEquals(0, cmdLine.getOptionCount(VERBOSITY));
    }

    @Test
    void testOneSwitch() throws ParseException {
        final CommandLine cmdLine = new DefaultParser().parse(OPTIONS, new String[]{"-v"});
        assertEquals(1, cmdLine.getOptionCount(VERBOSITY));
        assertEquals(1, cmdLine.getOptionCount("v"));
        assertEquals(1, cmdLine.getOptionCount('v'));
    }

    @Test
    void testThreeSwitches() throws ParseException {
        final CommandLine cmdLine = new DefaultParser().parse(OPTIONS, new String[]{"-v", "-v", "-v"});
        assertEquals(3, cmdLine.getOptionCount(VERBOSITY));
    }

    @Test
    void testThreeSwitchesCompact() throws ParseException {
        final CommandLine cmdLine = new DefaultParser().parse(OPTIONS, new String[]{"-vvv"});
        assertEquals(3, cmdLine.getOptionCount(VERBOSITY));
    }

    @Test
    void testFiveSwitchesMixed() throws ParseException {
        final CommandLine cmdLine = new DefaultParser().parse(OPTIONS, new String[]{"-v", "-vvv", "-v"});
        assertEquals(5, cmdLine.getOptionCount(VERBOSITY));
    }
}
