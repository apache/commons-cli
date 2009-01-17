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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import junit.framework.TestCase;

public class BugCLI162Test extends TestCase {

    private Options options;

    public void setUp() {
        options = new Options();
        options.addOption("h", "help", false, "This is a looooong description");
    }

    public void testInfiniteLoop() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(20);
        try {
            formatter.printHelp("app", options); // hang & crash
        } catch(RuntimeException re) {
            assertTrue(re.getMessage().startsWith("Text too long for line - throwing exception to avoid infinite loop [CLI-162]: "));
        }
    }

}
