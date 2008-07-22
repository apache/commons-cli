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
package org.apache.commons.cli2.bug;

import junit.framework.TestCase;

import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.Parser;
import org.apache.commons.cli2.option.PropertyOption;

/**
 * @author brianegge
 */
public class BugCLI126Test extends TestCase {
    public void testMultiplePropertyArgs() throws OptionException {
        PropertyOption conf = new PropertyOption("-P", "Properties for this process", 1);
        PropertyOption env = new PropertyOption("-C", "Properties for child processes", 2);
        GroupBuilder builder = new GroupBuilder();
        Group options = builder.withOption(conf).withOption(env).create();

        Parser parser = new Parser();
        parser.setGroup(options);
        CommandLine line =
            parser.parseAndHelp(
                new String[] {
                    "-Phome=.",
                    "-Chome=/"
                    });
        assertEquals(".", line.getProperty(conf, "home"));
        assertEquals("/", line.getProperty(env, "home"));
    }
}
