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
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.Parser;
import org.apache.commons.cli2.option.DefaultOption;

import java.util.List;

/**
 * ArgumentBuilder.withMaximum causes parse errors: Unexpeced <value> while processing options
 *
 * @author David Biesack
 * @author brianegge
 */
public class BugCLI145Test extends TestCase {
    public void testWithMaximum() {
        final DefaultOptionBuilder obuilder = new DefaultOptionBuilder();
        final ArgumentBuilder abuilder = new ArgumentBuilder();
        final GroupBuilder gbuilder = new GroupBuilder();
        DefaultOption aOption = obuilder//
                .withShortName("a")
                .withLongName("a")
                .withArgument(abuilder
                        .withName("a")
                        .withDefault("10")
                        .create())
                .create();
        DefaultOption bOption = obuilder
                .withShortName("b")
                .withLongName("b")
                .withArgument(abuilder
                        .withName("b")
                        .withMinimum(2)
                        .withMaximum(4)
                        .withDefault("100")
                        .withDefault("1000")
                        .withDefault("10000")
                        .withDefault("1000000")
                        .create())
                .create();
        Group options = gbuilder
                .withName("options")
                .withOption(aOption)
                .withOption(bOption)
                .create();
        Parser parser = new Parser();
        parser.setHelpTrigger("--help");
        parser.setGroup(options);
        CommandLine cl = parser.parseAndHelp("-a 0 -b 1 2 3 4".split(" "));
        assertNotNull(cl);
        int a = Integer.parseInt(cl.getValue(aOption).toString());
        List b = cl.getValues(bOption);
        assertEquals(0, a);
        assertEquals(4, b.size());
    }

    public void testWithMaximumUsingDefaultValues() {
        final DefaultOptionBuilder obuilder = new DefaultOptionBuilder();
        final ArgumentBuilder abuilder = new ArgumentBuilder();
        final GroupBuilder gbuilder = new GroupBuilder();
        DefaultOption aOption = obuilder//
                .withShortName("a")
                .withLongName("a")
                .withArgument(abuilder
                        .withName("a")
                        .withDefault("10")
                        .create())
                .create();
        DefaultOption bOption = obuilder
                .withShortName("b")
                .withLongName("b")
                .withArgument(abuilder
                        .withName("b")
                        .withMinimum(2)
                        .withMaximum(4)
                        .withDefault("100")
                        .withDefault("1000")
                        .withDefault("10000")
                        .create())
                .create();
        Group options = gbuilder
                .withName("options")
                .withOption(aOption)
                .withOption(bOption)
                .create();
        Parser parser = new Parser();
        parser.setHelpTrigger("--help");
        parser.setGroup(options);
        CommandLine cl = parser.parseAndHelp("-a -b".split(" "));
        assertNotNull(cl);
        int a = Integer.parseInt(cl.getValue(aOption).toString());
        List b = cl.getValues(bOption);
        assertEquals(10, a);
        assertEquals(3, b.size());
        assertEquals("10000", b.get(2));
    }
}
