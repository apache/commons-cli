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
public class Bug13935Test extends TestCase {

    public Bug13935Test(final String name) {
        super(name);
    }

    public void testRequiredGroup() throws Exception {
        final DefaultOptionBuilder obuilder = new DefaultOptionBuilder();
        final ArgumentBuilder abuilder = new ArgumentBuilder();
        final GroupBuilder gbuilder = new GroupBuilder();

        final Option testOption =
            obuilder
                .withShortName("a")
                .withArgument(abuilder.withName("quoted string").create())
                .create();

        final Group options = gbuilder.withOption(testOption).create();

        final Parser parser = new Parser();
        parser.setGroup(options);

        final CommandLine cmdLine =
            parser.parse(new String[] { "-a", "\"two tokens\"" });

        assertTrue(cmdLine.hasOption("-a"));
        assertEquals("two tokens", cmdLine.getValue("-a"));
    }
}
