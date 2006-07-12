/**
 * Copyright 2006 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import org.apache.commons.cli2.Argument;
import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.Parser;

import junit.framework.TestCase;

/**
 * http://issues.apache.org/jira/browse/CLI-80
 */
public class BugCLI80Test extends TestCase {

    public void testBug() {
        final String optName = "option";

        Argument arg = new ArgumentBuilder().withName(optName)
                                            .withMaximum(1)
                                            .create();

        Option option = new DefaultOptionBuilder().withArgument(arg)
                                                  .withDescription("singular option")
                                                  .withLongName(optName)
                                                  .withShortName("o")
                                                  .create();

        Group group = new GroupBuilder().withOption(option).create();

        Parser p = new Parser();
        p.setGroup(group);

        CommandLine cl = p.parseAndHelp( new String[] { "-o", "yes" } );
        assertNotNull("Couldn't parse valid commandLine", cl);

        assertEquals("Couldn't look up value by short name", "yes", cl.getValue("-o") );

        try {
            cl = p.parse( new String[] { "-o", "yes", "-o", "jam" } );
            fail("Parsed invalid commandLine");
        } catch(OptionException e) {
          // ok
        }
    }

}
