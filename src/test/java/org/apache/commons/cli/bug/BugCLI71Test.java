/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.cli.bug;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("deprecation") // tests some deprecated classes
class BugCLI71Test {
    private Options options;
    private CommandLineParser parser;

    @BeforeEach
    public void setUp() {
        options = new Options();

        final Option algorithm = new Option("a", "algo", true, "the algorithm which it to perform executing");
        algorithm.setArgName("algorithm name");
        options.addOption(algorithm);

        final Option key = new Option("k", "key", true, "the key the setted algorithm uses to process");
        algorithm.setArgName("value");
        options.addOption(key);

        parser = new PosixParser();
    }

    @Test
    void testBasic() throws Exception {
        final String[] args = {"-a", "Caesar", "-k", "A"};
        final CommandLine line = parser.parse(options, args);
        assertEquals("Caesar", line.getOptionValue("a"));
        assertEquals("A", line.getOptionValue("k"));
    }

    @Test
    void testGetsDefaultIfOptional() throws Exception {
        final String[] args = {"-k", "-a", "Caesar"};
        options.getOption("k").setOptionalArg(true);
        final CommandLine line = parser.parse(options, args);

        assertEquals("Caesar", line.getOptionValue("a"));
        assertEquals("a", line.getOptionValue('k', "a"));
    }

    @Test
    void testLackOfError() throws Exception {
        final String[] args = { "-k", "-a", "Caesar" };
        final MissingArgumentException e = assertThrows(MissingArgumentException.class, () -> parser.parse(options, args));
        assertEquals("k", e.getOption().getOpt(), "option missing an argument");
    }

    @Test
    void testMistakenArgument() throws Exception {
        String[] args = {"-a", "Caesar", "-k", "A"};
        CommandLine line = parser.parse(options, args);
        args = new String[] {"-a", "Caesar", "-k", "a"};
        line = parser.parse(options, args);
        assertEquals("Caesar", line.getOptionValue("a"));
        assertEquals("a", line.getOptionValue("k"));
    }

}
