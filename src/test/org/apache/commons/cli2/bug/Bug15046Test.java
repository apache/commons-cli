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
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.Parser;

/**
 * @author John Keyes
 */
public class Bug15046Test extends TestCase {

    public Bug15046Test(String name) {
        super(name);
    }

    public void testParamNamedAsOption() throws Exception {
        final String[] CLI_ARGS = new String[] { "-z", "c" };

        DefaultOptionBuilder obuilder = new DefaultOptionBuilder();
        ArgumentBuilder abuilder = new ArgumentBuilder();

        Option option =
            obuilder
                .withShortName("z")
                .withLongName("timezone")
                .withDescription("affected option")
                .withArgument(abuilder.withName("timezone").create())
                .create();

        GroupBuilder gbuilder = new GroupBuilder();
        Group options =
            gbuilder.withName("bug15046").withOption(option).create();

        Parser parser = new Parser();
        parser.setGroup(options);
        CommandLine line = parser.parse(CLI_ARGS);

        assertEquals("c", line.getValue("-z"));

        Option c =
            obuilder
                .withShortName("c")
                .withLongName("conflict")
                .withDescription("conflicting option")
                .withArgument(abuilder.withName("conflict").create())
                .create();

        options =
            gbuilder
                .withName("bug15046")
                .withOption(option)
                .withOption(c)
                .create();

        parser.setGroup(options);
        line = parser.parse(CLI_ARGS);

        assertEquals("c", line.getValue("-z"));
    }
}
