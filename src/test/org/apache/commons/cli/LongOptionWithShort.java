/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.cli;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/**
 * <p>
 * This is a collection of tests that test real world
 * applications command lines focusing on options with
 * long and short names.
 * </p>
 */
public class LongOptionWithShort extends TestCase {
    public LongOptionWithShort(String name) {
        super(name);
    }

    public static Test suite() {
        return new TestSuite(LongOptionWithShort.class);
    }

    /**
     *
     */
    public void testLongOptionWithShort() {
        Option help = new Option("h", "help", false, "print this message");
        Option version = new Option("v", "version", false,
                "print version information");
        Option newRun = new Option("n", "new", false,
                "Create NLT cache entries only for new items");
        Option trackerRun = new Option("t", "tracker", false,
                "Create NLT cache entries only for tracker items");

        Option timeLimit = OptionBuilder.withLongOpt("limit").hasArg()
                                        .withValueSeparator()
                                        .withDescription("Set time limit for execution, in mintues")
                                        .create("l");

        Option age = OptionBuilder.withLongOpt("age").hasArg()
                                  .withValueSeparator()
                                  .withDescription("Age (in days) of cache item before being recomputed")
                                  .create("a");

        Option server = OptionBuilder.withLongOpt("server").hasArg()
                                     .withValueSeparator()
                                     .withDescription("The NLT server address")
                                     .create("s");

        Option numResults = OptionBuilder.withLongOpt("results").hasArg()
                                         .withValueSeparator()
                                         .withDescription("Number of results per item")
                                         .create("r");

        Option configFile = OptionBuilder.withLongOpt("file").hasArg()
                                         .withValueSeparator()
                                         .withDescription("Use the specified configuration file")
                                         .create();

        Options options = new Options();
        options.addOption(help);
        options.addOption(version);
        options.addOption(newRun);
        options.addOption(trackerRun);
        options.addOption(timeLimit);
        options.addOption(age);
        options.addOption(server);
        options.addOption(numResults);
        options.addOption(configFile);

        // create the command line parser
        CommandLineParser parser = new PosixParser();

        String[] args = new String[] {
                "-v",
                "-l",
                "10",
                "-age",
                "5",
                "-file",
                "filename"
            };

        try {
            CommandLine line = parser.parse(options, args);
            assertTrue(line.hasOption("v"));
            assertEquals(line.getOptionValue("l"), "10");
            assertEquals(line.getOptionValue("limit"), "10");
            assertEquals(line.getOptionValue("a"), "5");
            assertEquals(line.getOptionValue("age"), "5");
            assertEquals(line.getOptionValue("file"), "filename");
        }
        catch (ParseException exp) {
            fail("Unexpected exception:" + exp.getMessage());
        }
    }
}
