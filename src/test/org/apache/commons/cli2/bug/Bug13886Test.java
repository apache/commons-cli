/**
 * Copyright 2003-2004 The Apache Software Foundation
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

import junit.framework.TestCase;

import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.Parser;

/**
 * @author John Keyes
 */
public class Bug13886Test extends TestCase {

    public Bug13886Test(final String name) {
        super(name);
    }

    public void testMandatoryGroup() throws Exception {
        final DefaultOptionBuilder obuilder = new DefaultOptionBuilder();
        final GroupBuilder gbuilder = new GroupBuilder();

        final Option a = obuilder.withShortName("a").create();

        final Option b = obuilder.withShortName("b").create();

        final Group options =
            gbuilder
                .withOption(a)
                .withOption(b)
                .withMaximum(1)
                .withMinimum(1)
                .create();

        final Parser parser = new Parser();
        parser.setGroup(options);

        try {
            parser.parse(new String[] {
            });
            fail("Expected MissingOptionException not caught");
        }
        catch (final OptionException exp) {
            assertEquals("Missing option -a|-b", exp.getMessage());
        }

        try {
            parser.parse(new String[] { "-a" });
        }
        catch (final OptionException exp) {
            fail("Unexpected MissingOptionException caught");
        }

        try {
            parser.parse(new String[] { "-b" });
        }
        catch (final OptionException exp) {
            fail("Unexpected MissingOptionException caught");
        }

        try {
            parser.parse(new String[] { "-a", "-b" });
            fail("Expected UnexpectedOptionException not caught");
        }
        catch (final OptionException exp) {
            assertEquals(
                "Unexpected -b while processing -a|-b",
                exp.getMessage());
        }
    }
}
