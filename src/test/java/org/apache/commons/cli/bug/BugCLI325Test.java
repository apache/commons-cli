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

package org.apache.commons.cli.bug;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Test;

public class BugCLI325Test {

    @Test
    public void testCli325() throws ParseException {
        // @formatter:off
        final Option option = Option.builder("x")
                .hasArgs()
                .valueSeparator()
                .desc("Multiple arg option with value separator.")
                .build();
        // @formatter:on
        final String[] args = {"-x", "A=a", "B=b"};
        final CommandLine cmdLine = DefaultParser.builder().build().parse(new Options().addOption(option), args);
        final Properties props = cmdLine.getOptionProperties(option);
        assertEquals(2, props.size());
        assertEquals("a", props.get("A"));
        assertEquals("b", props.get("B"));
    }
}
