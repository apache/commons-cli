/**
 * Copyright 2003-2005 The Apache Software Foundation
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
package org.apache.commons.cli2.option;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.apache.commons.cli2.Argument;
import org.apache.commons.cli2.DisplaySetting;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.HelpLine;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.Parent;
import org.apache.commons.cli2.WriteableCommandLine;
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.CommandBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.WriteableCommandLineImpl;
import org.apache.commons.cli2.resource.ResourceConstants;
import org.apache.commons.cli2.resource.ResourceHelper;

/**
 * @author Rob Oxspring
 */
public class ParentTest
    extends ParentTestCase {
    public static final Argument COMPLEX_ARGUMENT =
        new ArgumentBuilder().withName("username").withMinimum(1).withMaximum(1).create();
    public static final Option COMPLEX_CHILD_SSL =
        new DefaultOptionBuilder().withLongName("ssl").withShortName("s").create();
    public static final Option COMPLEX_CHILD_BASIC =
        new DefaultOptionBuilder().withLongName("basic").withShortName("b").create();
    public static final Option COMPLEX_CHILD_DIGEST =
        new DefaultOptionBuilder().withLongName("digest").withShortName("d").create();
    public static final Group COMPLEX_CHILDREN =
        new GroupBuilder().withName("login-opts").withOption(COMPLEX_CHILD_BASIC)
                          .withOption(COMPLEX_CHILD_DIGEST).withOption(COMPLEX_CHILD_SSL).create();

    public static Parent buildLibParent() {
        final Argument argument = ArgumentTest.buildPathArgument();

        return new DefaultOption("-", "--", false, "--lib", "Specifies library search path", null,
                                 null, false, argument, null, 'l');
    }

    public static Parent buildKParent() {
        final Group children = GroupTest.buildApacheCommandGroup();

        return new DefaultOption("-", "--", false, "-k", "desc", null, null, false, null, children,
                                 'k');
    }

    public static Parent buildComplexParent() {
        return new CommandBuilder().withName("login").withName("lo").withName("l")
                                   .withArgument(COMPLEX_ARGUMENT).withChildren(COMPLEX_CHILDREN)
                                   .create();
    }

    /* (non-Javadoc)
     * @see org.apache.commons.cli2.ParentTestCase#testProcessParent()
     */
    public void testProcessParent()
        throws OptionException {
        final Parent option = buildKParent();
        final List args = list("-k", "start");
        final WriteableCommandLine commandLine = commandLine(option, args);
        final ListIterator iterator = args.listIterator();
        option.processParent(commandLine, iterator);

        assertEquals("start", iterator.next());
        assertFalse(iterator.hasNext());
        assertTrue(commandLine.hasOption(option));
        assertTrue(commandLine.hasOption("-k"));
        assertTrue(commandLine.getValues(option).isEmpty());
    }

    /* (non-Javadoc)
     * @see org.apache.commons.cli2.OptionTestCase#testCanProcess()
     */
    public void testCanProcess() {
        final Parent option = buildKParent();
        assertTrue(option.canProcess(new WriteableCommandLineImpl(option, null), "-k"));
    }

    public void testCanProcess_BadMatch() {
        final Parent option = buildKParent();
        assertFalse(option.canProcess(new WriteableCommandLineImpl(option, null), "-K"));
    }

    public void testCanProcess_ContractedArgument() {
        final Parent option = buildLibParent();
        assertTrue(option.canProcess(new WriteableCommandLineImpl(option, null), "--lib=/usr/lib"));
    }

    /* (non-Javadoc)
     * @see org.apache.commons.cli2.OptionTestCase#testPrefixes()
     */
    public void testPrefixes() {
        final Parent option = buildKParent();
        assertContentsEqual(list("-", "--"), option.getPrefixes());
    }

    /* (non-Javadoc)
     * @see org.apache.commons.cli2.OptionTestCase#testProcess()
     */
    public void testProcess()
        throws OptionException {
        final Parent option = CommandTest.buildStartCommand();
        final List args = list("start");
        final WriteableCommandLine commandLine = commandLine(option, args);
        final ListIterator iterator = args.listIterator();
        option.process(commandLine, iterator);

        assertFalse(iterator.hasNext());
        assertTrue(commandLine.hasOption(option));
        assertTrue(commandLine.hasOption("start"));
        assertFalse(commandLine.hasOption("stop"));
        assertTrue(commandLine.getValues(option).isEmpty());
    }

    public void testProcess_NoMatch()
        throws OptionException {
        final Parent option = CommandTest.buildStartCommand();
        final List args = list("whatever");
        final WriteableCommandLine commandLine = commandLine(option, args);
        final ListIterator iterator = args.listIterator();

        try {
            option.process(commandLine, iterator);
            fail("unexpected token not thrown");
        } catch (OptionException exp) {
            OptionException e =
                new OptionException(option, ResourceConstants.UNEXPECTED_TOKEN, "whatever");
            assertEquals("wrong exception message", e.getMessage(), exp.getMessage());
        }
    }

    public void testProcess_Children()
        throws OptionException {
        final Parent option = buildKParent();
        final List args = list("-k", "start");
        final WriteableCommandLine commandLine = commandLine(option, args);
        final ListIterator iterator = args.listIterator();
        option.process(commandLine, iterator);

        assertNull(option.findOption("whatever"));
        assertNotNull(option.findOption("start"));

        assertFalse(iterator.hasNext());
        assertTrue(commandLine.hasOption(option));
        assertTrue(commandLine.hasOption("-k"));
        assertTrue(commandLine.hasOption("start"));
        assertFalse(commandLine.hasOption("stop"));
        assertTrue(commandLine.getValues(option).isEmpty());
    }

    public void testProcess_Argument()
        throws OptionException {
        final Parent option = buildLibParent();
        final List args = list("--lib=C:\\WINDOWS;C:\\WINNT;C:\\");
        final WriteableCommandLine commandLine = commandLine(option, args);
        final ListIterator iterator = args.listIterator();
        option.process(commandLine, iterator);

        assertFalse(iterator.hasNext());
        assertTrue(commandLine.hasOption(option));
        assertTrue(commandLine.hasOption("--lib"));
        assertContentsEqual(list("C:\\WINDOWS", "C:\\WINNT", "C:\\"), commandLine.getValues(option));
    }

    /* (non-Javadoc)
     * @see org.apache.commons.cli2.OptionTestCase#testTriggers()
     */
    public void testTriggers() {
        final Parent option = buildKParent();
        assertContentsEqual(list("-k"), option.getTriggers());
    }

    /* (non-Javadoc)
     * @see org.apache.commons.cli2.OptionTestCase#testValidate()
     */
    public void testValidate()
        throws OptionException {
        final Parent option = CommandTest.buildStartCommand();
        final WriteableCommandLine commandLine = commandLine(option, list());

        option.validate(commandLine);

        commandLine.addOption(option);

        option.validate(commandLine);
    }

    public void testValidate_Children()
        throws OptionException {
        final Parent option = buildKParent();
        final WriteableCommandLine commandLine = commandLine(option, list());

        option.validate(commandLine);
        commandLine.addOption(option);

        try {
            option.validate(commandLine);
            fail("Missing a command");
        } catch (OptionException moe) {
            assertNotNull(moe.getOption());
            assertNotSame(option, moe.getOption());
        }
    }

    public void testValidate_Argument()
        throws OptionException {
        final Command option = CommandTest.buildLoginCommand();
        final WriteableCommandLine commandLine = commandLine(option, list());

        option.validate(commandLine);

        commandLine.addOption(option);

        try {
            option.validate(commandLine);
            fail("Missing a value");
        } catch (OptionException moe) {
            assertSame(option, moe.getOption());
        }
    }

    /* (non-Javadoc)
     * @see org.apache.commons.cli2.OptionTestCase#testAppendUsage()
     */
    public void testAppendUsage() {
        final Option option = buildComplexParent();
        final StringBuffer buffer = new StringBuffer();
        final Set settings = new HashSet(DisplaySetting.ALL);
        settings.remove(DisplaySetting.DISPLAY_GROUP_OUTER);
        option.appendUsage(buffer, settings, null);

        assertEquals("[login (l,lo) <username> [login-opts (--basic (-b)|--digest (-d)|--ssl (-s))]]",
                     buffer.toString());
    }

    public void testAppendUsage_NoArguments() {
        final Option option = buildComplexParent();
        final StringBuffer buffer = new StringBuffer();
        final Set settings = new HashSet(DisplaySetting.ALL);
        settings.remove(DisplaySetting.DISPLAY_PARENT_ARGUMENT);
        settings.remove(DisplaySetting.DISPLAY_GROUP_OUTER);
        option.appendUsage(buffer, settings, null);

        assertEquals("[login (l,lo) [login-opts (--basic (-b)|--digest (-d)|--ssl (-s))]]",
                     buffer.toString());
    }

    public void testAppendUsage_NoChildren() {
        final Option option = buildComplexParent();
        final StringBuffer buffer = new StringBuffer();
        final Set settings = new HashSet(DisplaySetting.ALL);
        settings.remove(DisplaySetting.DISPLAY_PARENT_CHILDREN);
        option.appendUsage(buffer, settings, null);

        assertEquals("[login (l,lo) <username>]", buffer.toString());
    }

    public void testAppendUsage_NoArgumentsOrChildren() {
        final Option option = buildComplexParent();
        final StringBuffer buffer = new StringBuffer();
        final Set settings = new HashSet(DisplaySetting.ALL);
        settings.remove(DisplaySetting.DISPLAY_PARENT_CHILDREN);
        settings.remove(DisplaySetting.DISPLAY_PARENT_ARGUMENT);
        option.appendUsage(buffer, settings, null);

        assertEquals("[login (l,lo)]", buffer.toString());
    }

    /* (non-Javadoc)
     * @see org.apache.commons.cli2.OptionTestCase#testGetPreferredName()
     */
    public void testGetPreferredName() {
        final Option option = buildLibParent();
        assertEquals("--lib", option.getPreferredName());
    }

    /* (non-Javadoc)
     * @see org.apache.commons.cli2.OptionTestCase#testGetDescription()
     */
    public void testGetDescription() {
        final Option option = buildLibParent();
        assertEquals("Specifies library search path", option.getDescription());
    }

    /* (non-Javadoc)
     * @see org.apache.commons.cli2.OptionTestCase#testHelpLines()
     */
    public void testHelpLines() {
        final Option option = buildComplexParent();
        final List lines = option.helpLines(0, DisplaySetting.ALL, null);
        final Iterator i = lines.iterator();

        final HelpLine line1 = (HelpLine) i.next();
        assertEquals(0, line1.getIndent());
        assertEquals(option, line1.getOption());

        final HelpLine line2 = (HelpLine) i.next();
        assertEquals(1, line2.getIndent());
        assertEquals(COMPLEX_ARGUMENT, line2.getOption());

        final HelpLine line3 = (HelpLine) i.next();
        assertEquals(1, line3.getIndent());
        assertEquals(COMPLEX_CHILDREN, line3.getOption());

        final HelpLine line4 = (HelpLine) i.next();
        assertEquals(2, line4.getIndent());
        assertEquals(COMPLEX_CHILD_BASIC, line4.getOption());

        final HelpLine line5 = (HelpLine) i.next();
        assertEquals(2, line5.getIndent());
        assertEquals(COMPLEX_CHILD_DIGEST, line5.getOption());

        final HelpLine line6 = (HelpLine) i.next();
        assertEquals(2, line6.getIndent());
        assertEquals(COMPLEX_CHILD_SSL, line6.getOption());

        assertFalse(i.hasNext());
    }

    public void testHelpLines_NoArgument() {
        final Option option = buildComplexParent();
        final Set settings = new HashSet(DisplaySetting.ALL);
        settings.remove(DisplaySetting.DISPLAY_PARENT_ARGUMENT);

        final List lines = option.helpLines(0, settings, null);
        final Iterator i = lines.iterator();

        final HelpLine line1 = (HelpLine) i.next();
        assertEquals(0, line1.getIndent());
        assertEquals(option, line1.getOption());

        final HelpLine line3 = (HelpLine) i.next();
        assertEquals(1, line3.getIndent());
        assertEquals(COMPLEX_CHILDREN, line3.getOption());

        final HelpLine line4 = (HelpLine) i.next();
        assertEquals(2, line4.getIndent());
        assertEquals(COMPLEX_CHILD_BASIC, line4.getOption());

        final HelpLine line5 = (HelpLine) i.next();
        assertEquals(2, line5.getIndent());
        assertEquals(COMPLEX_CHILD_DIGEST, line5.getOption());

        final HelpLine line6 = (HelpLine) i.next();
        assertEquals(2, line6.getIndent());
        assertEquals(COMPLEX_CHILD_SSL, line6.getOption());

        assertFalse(i.hasNext());
    }

    public void testHelpLines_NoChildren() {
        final Option option = buildComplexParent();
        final Set settings = new HashSet(DisplaySetting.ALL);
        settings.remove(DisplaySetting.DISPLAY_PARENT_CHILDREN);

        final List lines = option.helpLines(0, settings, null);
        final Iterator i = lines.iterator();

        final HelpLine line1 = (HelpLine) i.next();
        assertEquals(0, line1.getIndent());
        assertEquals(option, line1.getOption());

        final HelpLine line2 = (HelpLine) i.next();
        assertEquals(1, line2.getIndent());
        assertEquals(COMPLEX_ARGUMENT, line2.getOption());

        assertFalse(i.hasNext());
    }

    public void testNullPreferredName() {
        try {
        	new CommandBuilder().create();
        } catch (IllegalStateException exp) {
        	assertEquals(ResourceHelper.getResourceHelper().getMessage(ResourceConstants.OPTION_NO_NAME), exp.getMessage());
        }
    }

    public void testRequired() {
    	Command cmd = new CommandBuilder().withRequired(true).withName("blah").create();
    	assertTrue("cmd is not required", cmd.isRequired());
    	assertEquals("id is incorrect", 0, cmd.getId());
    }

    public void testID() {
    	Command cmd = new CommandBuilder().withId('c').withName("blah").create();
    	assertEquals("id is incorrect", 'c', cmd.getId());
    }

    public void testGetId() {
        assertEquals('h', DefaultOptionTest.buildHelpOption().getId());
        assertEquals('X', DefaultOptionTest.buildXOption().getId());
        assertEquals(0, CommandTest.buildStartCommand().getId());
    }
}
