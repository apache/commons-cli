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
package org.apache.commons.cli2;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.Parser;

/**
 * @author Rob Oxspring
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class PrecedenceTest extends TestCase {
    private final String[] args = new String[] { "-file" };

    public void testSimple() throws OptionException {
        final DefaultOptionBuilder oBuilder = new DefaultOptionBuilder();

        final Group options =
            new GroupBuilder()
                .withOption(oBuilder.withShortName("file").create())
                .create();

        final CommandLine cl = buildCommandLine(options, args);
        assertEquals(new String[] { "-file" }, cl);
    }

    public void testArgument() throws OptionException {
        final DefaultOptionBuilder oBuilder = new DefaultOptionBuilder();
        final ArgumentBuilder aBuilder = new ArgumentBuilder();

        final Group options =
            new GroupBuilder()
                .withOption(
                    oBuilder
                        .withShortName("f")
                        .withArgument(aBuilder.create())
                        .create())
                .create();

        final CommandLine cl = buildCommandLine(options, args);
        assertEquals(new String[] { "-f" }, cl);
    }

    public void testBurst() throws OptionException {
        final DefaultOptionBuilder oBuilder = new DefaultOptionBuilder();
        final GroupBuilder gBuilder = new GroupBuilder();
        final Group options =
            gBuilder
                .withOption(oBuilder.withShortName("f").create())
                .withOption(oBuilder.withShortName("i").create())
                .withOption(oBuilder.withShortName("l").create())
                .withOption(oBuilder.withShortName("e").create())
                .create();

        final CommandLine cl = buildCommandLine(options, args);
        assertEquals(new String[] { "-f", "-i", "-l", "-e" }, cl);
    }

    public void testChildren() throws OptionException {
        final DefaultOptionBuilder oBuilder = new DefaultOptionBuilder();
        final GroupBuilder gBuilder = new GroupBuilder();

        final Group children =
            gBuilder
                .withOption(oBuilder.withShortName("i").create())
                .withOption(oBuilder.withShortName("l").create())
                .withOption(oBuilder.withShortName("e").create())
                .create();
        final Group options =
            gBuilder
                .withOption(
                    oBuilder
                        .withShortName("f")
                        .withChildren(children)
                        .create())
                .create();

        final CommandLine cl = buildCommandLine(options, args);
        assertEquals(new String[] { "-f", "-i", "-l", "-e" }, cl);
    }

    public void XtestSimpleVsArgument() throws OptionException {
        final DefaultOptionBuilder oBuilder = new DefaultOptionBuilder();
        final GroupBuilder gBuilder = new GroupBuilder();
        final ArgumentBuilder aBuilder = new ArgumentBuilder();

        final Group options =
            gBuilder
                .withOption(oBuilder.withShortName("file").create())
                .withOption(
                    oBuilder
                        .withShortName("f")
                        .withArgument(aBuilder.create())
                        .create())
                .create();

        final CommandLine cl = buildCommandLine(options, args);
        assertEquals(new String[] { "-f" }, cl);
    }

    public void XtestSimpleVsBurst() throws OptionException {
        final DefaultOptionBuilder oBuilder = new DefaultOptionBuilder();
        final GroupBuilder gBuilder = new GroupBuilder();
        final Group options =
            gBuilder
                .withOption(oBuilder.withShortName("file").create())
                .withOption(oBuilder.withShortName("f").create())
                .withOption(oBuilder.withShortName("i").create())
                .withOption(oBuilder.withShortName("l").create())
                .withOption(oBuilder.withShortName("e").create())
                .create();

        final CommandLine cl = buildCommandLine(options, args);
        assertEquals(new String[] { "-f", "-i", "-l", "-e" }, cl);
    }

    public void XtestSimpleVsChildren() throws OptionException {
        final DefaultOptionBuilder oBuilder = new DefaultOptionBuilder();
        final GroupBuilder gBuilder = new GroupBuilder();

        final Group children =
            gBuilder
                .withOption(
                    oBuilder.withShortName("i").withLongName("ci").create())
                .withOption(
                    oBuilder.withShortName("l").withLongName("cl").create())
                .withOption(
                    oBuilder.withShortName("e").withLongName("ce").create())
                .create();

        final Group options =
            gBuilder
                .withOption(oBuilder.withShortName("file").create())
                .withOption(
                    oBuilder
                        .withShortName("f")
                        .withChildren(children)
                        .create())
                .create();

        final CommandLine cl = buildCommandLine(options, args);
        assertEquals(
            new String[] { "-f", "-i", "--ci", "-l", "--cl", "-e", "--ce" },
            cl);
    }

    public void testArgumentVsBurst() throws OptionException {
        final DefaultOptionBuilder oBuilder = new DefaultOptionBuilder();
        final GroupBuilder gBuilder = new GroupBuilder();
        final ArgumentBuilder aBuilder = new ArgumentBuilder();

        final Group options =
            gBuilder
                .withOption(
                    oBuilder
                        .withShortName("f")
                        .withArgument(aBuilder.create())
                        .create())
                .withOption(oBuilder.withShortName("i").create())
                .withOption(oBuilder.withShortName("l").create())
                .withOption(oBuilder.withShortName("e").create())
                .create();

        final CommandLine cl = buildCommandLine(options, args);
        assertEquals(new String[] { "-f" }, cl);
    }

    public void testArgumentVsChildren() throws OptionException {
        final DefaultOptionBuilder oBuilder = new DefaultOptionBuilder();
        final GroupBuilder gBuilder = new GroupBuilder();
        final ArgumentBuilder aBuilder = new ArgumentBuilder();

        final Group children =
            gBuilder
                .withOption(oBuilder.withShortName("i").create())
                .withOption(oBuilder.withShortName("l").create())
                .withOption(oBuilder.withShortName("e").create())
                .create();
        final Group options =
            gBuilder
                .withOption(
                    oBuilder
                        .withShortName("f")
                        .withChildren(children)
                        .withArgument(aBuilder.create())
                        .create())
                .create();

        final CommandLine cl = buildCommandLine(options, args);
        assertEquals(new String[] { "-f" }, cl);
    }

    public void testBurstVsChildren() throws OptionException {
        final DefaultOptionBuilder oBuilder = new DefaultOptionBuilder();
        final GroupBuilder gBuilder = new GroupBuilder();

        final Group children =
            gBuilder
                .withOption(
                    oBuilder.withShortName("i").withLongName("ci").create())
                .withOption(
                    oBuilder.withShortName("l").withLongName("cl").create())
                .withOption(
                    oBuilder.withShortName("e").withLongName("ce").create())
                .create();

        final Group options =
            gBuilder
                .withOption(
                    oBuilder
                        .withShortName("f")
                        .withChildren(children)
                        .create())
                .withOption(
                    oBuilder.withShortName("i").withLongName("bi").create())
                .withOption(
                    oBuilder.withShortName("l").withLongName("bl").create())
                .withOption(
                    oBuilder.withShortName("e").withLongName("be").create())
                .create();

        final CommandLine cl = buildCommandLine(options, args);
        assertEquals(
            new String[] { "-f", "-i", "--ci", "-l", "--cl", "-e", "--ce" },
            cl);
    }

    public void XtestSimpleVsArgumentVsBurst() throws OptionException {
        final DefaultOptionBuilder oBuilder = new DefaultOptionBuilder();
        final GroupBuilder gBuilder = new GroupBuilder();
        final ArgumentBuilder aBuilder = new ArgumentBuilder();

        final Group options =
            gBuilder
                .withOption(oBuilder.withShortName("file").create())
                .withOption(
                    oBuilder
                        .withShortName("f")
                        .withArgument(aBuilder.create())
                        .create())
                .withOption(oBuilder.withShortName("i").create())
                .withOption(oBuilder.withShortName("l").create())
                .withOption(oBuilder.withShortName("e").create())
                .create();

        final CommandLine cl = buildCommandLine(options, args);
        assertEquals(new String[] { "-f" }, cl);
    }

    public void XtestSimpleVsArgumentVsChildren() throws OptionException {
        final DefaultOptionBuilder oBuilder = new DefaultOptionBuilder();
        final GroupBuilder gBuilder = new GroupBuilder();
        final ArgumentBuilder aBuilder = new ArgumentBuilder();

        final Group children =
            gBuilder
                .withOption(
                    oBuilder.withShortName("i").withLongName("ci").create())
                .withOption(
                    oBuilder.withShortName("l").withLongName("cl").create())
                .withOption(
                    oBuilder.withShortName("e").withLongName("ce").create())
                .create();

        final Group options =
            gBuilder
                .withOption(oBuilder.withShortName("file").create())
                .withOption(
                    oBuilder
                        .withShortName("f")
                        .withChildren(children)
                        .withArgument(aBuilder.create())
                        .create())
                .create();

        final CommandLine cl = buildCommandLine(options, args);
        assertEquals(new String[] { "-f" }, cl);
    }

    public void XtestSimpleVsBurstVsChildren() throws OptionException {
        final DefaultOptionBuilder oBuilder = new DefaultOptionBuilder();
        final GroupBuilder gBuilder = new GroupBuilder();

        final Group children =
            gBuilder
                .withOption(
                    oBuilder.withShortName("i").withLongName("ci").create())
                .withOption(
                    oBuilder.withShortName("l").withLongName("cl").create())
                .withOption(
                    oBuilder.withShortName("e").withLongName("ce").create())
                .create();

        final Group options =
            gBuilder
                .withOption(oBuilder.withShortName("file").create())
                .withOption(
                    oBuilder
                        .withShortName("f")
                        .withChildren(children)
                        .create())
                .withOption(oBuilder.withShortName("i").create())
                .withOption(oBuilder.withShortName("l").create())
                .withOption(oBuilder.withShortName("e").create())
                .create();

        final CommandLine cl = buildCommandLine(options, args);
        assertEquals(new String[] { "-f", "-i", "-l", "-e" }, cl);
    }

    public void testArgumentVsBurstVsChildren() throws OptionException {
        final DefaultOptionBuilder oBuilder = new DefaultOptionBuilder();
        final GroupBuilder gBuilder = new GroupBuilder();
        final ArgumentBuilder aBuilder = new ArgumentBuilder();

        final Group children =
            gBuilder
                .withOption(
                    oBuilder.withShortName("i").withLongName("ci").create())
                .withOption(
                    oBuilder.withShortName("l").withLongName("cl").create())
                .withOption(
                    oBuilder.withShortName("e").withLongName("ce").create())
                .create();

        final Group options =
            gBuilder
                .withOption(
                    oBuilder
                        .withShortName("f")
                        .withChildren(children)
                        .withArgument(aBuilder.create())
                        .create())
                .withOption(oBuilder.withShortName("i").create())
                .withOption(oBuilder.withShortName("l").create())
                .withOption(oBuilder.withShortName("e").create())
                .create();

        final CommandLine cl = buildCommandLine(options, args);
        assertEquals(new String[] { "-f" }, cl);
    }

    public void XtestSimpleVsArgumentVsBurstVsChildren()
        throws OptionException {
        final DefaultOptionBuilder oBuilder = new DefaultOptionBuilder();
        final GroupBuilder gBuilder = new GroupBuilder();
        final ArgumentBuilder aBuilder = new ArgumentBuilder();

        final Group children =
            gBuilder
                .withOption(
                    oBuilder.withShortName("i").withLongName("ci").create())
                .withOption(
                    oBuilder.withShortName("l").withLongName("cl").create())
                .withOption(
                    oBuilder.withShortName("e").withLongName("ce").create())
                .create();

        final Group options =
            gBuilder
                .withOption(oBuilder.withShortName("file").create())
                .withOption(
                    oBuilder
                        .withShortName("f")
                        .withChildren(children)
                        .withArgument(aBuilder.create())
                        .create())
                .withOption(oBuilder.withShortName("i").create())
                .withOption(oBuilder.withShortName("l").create())
                .withOption(oBuilder.withShortName("e").create())
                .create();

        final CommandLine cl = buildCommandLine(options, args);
        assertEquals(new String[] { "-f" }, cl);
    }

    public CommandLine buildCommandLine(final Group group, final String[] args)
        throws OptionException {
        Parser p = new Parser();
        p.setGroup(group);
        return p.parse(args);
    }

    public void assertEquals(final String options[], final CommandLine line) {
        final List expected = Arrays.asList(options);
        final Set actual = line.getOptionTriggers();

        //System.out.println(getName() + ": " + actual);

        assertTrue(expected.containsAll(actual));
        assertTrue(actual.containsAll(expected));
    }
}
