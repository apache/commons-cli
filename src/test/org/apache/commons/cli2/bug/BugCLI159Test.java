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
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.Parser;

/**
 * Inconsistent handling of minimum and maximum constraints for groups and their
 * child groups.
 *
 * @author Oliver Heger
 * @version $Id$
 */
public class BugCLI159Test extends TestCase
{
    /** The parent group. */
    private Group parent;

    /** The child group. */
    private Group child;

    /** The parser. */
    private Parser parser;

    /**
     * Creates some test options, including a group with a child group.
     *
     * @param childGroupRequired a flag whether the child group is required
     */
    private void setUpOptions(boolean childGroupRequired)
    {
        final DefaultOptionBuilder obuilder = new DefaultOptionBuilder();
        final GroupBuilder gbuilder = new GroupBuilder();
        Option parentOpt = obuilder.withLongName("parent").withShortName("p")
                .create();
        Option childOpt1 = obuilder.withLongName("child").withShortName("c")
                .create();
        Option childOpt2 = obuilder.withLongName("sub").withShortName("s")
                .create();
        Option childOpt3 = obuilder.withLongName("test").withShortName("t")
                .create();
        child = gbuilder.withName("childOptions").withOption(childOpt1)
                .withOption(childOpt2).withOption(childOpt3).withMinimum(2)
                .withRequired(childGroupRequired).create();
        parent = gbuilder.withName("options").withOption(parentOpt).withOption(
                child).withMinimum(0).create();
        parser = new Parser();
        parser.setGroup(parent);
    }

    /**
     * Tests whether the child group can be omitted.
     */
    public void testNoChildGroup() throws OptionException
    {
        setUpOptions(false);
        CommandLine cl = parser.parse(new String[] {
            "--parent"
        });
        assertNotNull("No command line parsed", cl);
        assertFalse("Child group found", cl.hasOption(child));
    }

    /**
     * Tests whether a required child groupd can be omitted.
     */
    public void testNoChildGroupRequired()
    {
        setUpOptions(true);
        try
        {
            parser.parse(new String[] {
                "--parent"
            });
            fail("Missing child group not detected!");
        }
        catch (OptionException oex)
        {
            // ok
        }
    }

    /**
     * Tests parsing an empty command line. Because the parent group is optional
     * this should be possible.
     */
    public void testNoOptions() throws OptionException
    {
        setUpOptions(false);
        CommandLine cl = parser.parse(new String[0]);
        assertFalse("Found parent option", cl.hasOption("--parent"));
        assertFalse("Found child option", cl.hasOption("--child"));
    }

    /**
     * Tests parsing a command line with options of the child group.
     */
    public void testWithChildOptions() throws OptionException
    {
        setUpOptions(false);
        CommandLine cl = parser.parse(new String[] {
            "-ct"
        });
        assertTrue("child option not found", cl.hasOption("--child"));
        assertTrue("test option not found", cl.hasOption("--test"));
    }

    /**
     * Tests a command line containing options of the child group, but the
     * minimum constraint is violated.
     */
    public void testWithChildOptionsMissing()
    {
        setUpOptions(false);
        try
        {
            parser.parse(new String[] {
                    "--parent", "--sub"
            });
            fail("Missing options of child group not detected!");
        }
        catch (OptionException oex)
        {
            // ok
        }
    }

    /**
     * Tests whether the root group is always validated.
     */
    public void testRequiredRootGroup()
    {
        setUpOptions(false);
        parser.setGroup(child);
        try
        {
            parser.parse(new String[] {
                "--test"
            });
            fail("Missing options not detected!");
        }
        catch (OptionException oex)
        {
            // ok
        }
    }
}
