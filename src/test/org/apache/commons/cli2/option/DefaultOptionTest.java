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
package org.apache.commons.cli2.option;

import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.apache.commons.cli2.DisplaySetting;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.Parent;
import org.apache.commons.cli2.WriteableCommandLine;

/**
 * @author roberto
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class DefaultOptionTest extends ParentTestCase {

    public static DefaultOption buildHelpOption() {
        final Set aliases = new HashSet(list("-h", "-?"));
        return new DefaultOption(
            "-",
            "--",
            true,
            "--help",
            "Displays the help",
            aliases,
            aliases,
            false,
            null,
            null,
            'h');
    }

    public static DefaultOption buildXOption() {
        return new DefaultOption(
            "-",
            "--",
            true,
            "-X",
            "This is needed",
            null,
            null,
            true,
            null,
            null,
            'X');
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.cli2.ParentTestCase#testProcessParent()
     */
    public void testProcessParent() throws OptionException {
        final DefaultOption option = buildHelpOption();
        final List args = list("--help");
        final WriteableCommandLine commandLine = commandLine(option, args);
        final ListIterator iterator = args.listIterator();
        option.processParent(commandLine, iterator);

        assertFalse(iterator.hasNext());
        assertTrue(commandLine.hasOption(option));
        assertTrue(commandLine.hasOption("--help"));
        assertTrue(commandLine.hasOption("-?"));
        assertTrue(commandLine.getValues(option).isEmpty());
    }

    public void testProcessParent_Burst() throws OptionException {
        final DefaultOption option = buildHelpOption();
        final List args = list("-help");
        final WriteableCommandLine commandLine = commandLine(option, args);
        final ListIterator iterator = args.listIterator();
        option.processParent(commandLine, iterator);

        assertEquals("-elp", iterator.next());
        assertFalse(iterator.hasNext());
        assertTrue(commandLine.hasOption(option));
        assertTrue(commandLine.hasOption("--help"));
        assertTrue(commandLine.hasOption("-?"));
        assertTrue(commandLine.getValues(option).isEmpty());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.cli2.OptionTestCase#testCanProcess()
     */
    public void testCanProcess() {
        final DefaultOption option = buildHelpOption();
        assertTrue(option.canProcess("-?"));
    }

    public void testCanProcess_BadMatch() {
        final DefaultOption option = buildHelpOption();
        assertFalse(option.canProcess("-H"));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.cli2.OptionTestCase#testPrefixes()
     */
    public void testPrefixes() {
        final DefaultOption option = buildHelpOption();
        assertContentsEqual(list("-", "--"), option.getPrefixes());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.cli2.OptionTestCase#testProcess()
     */
    public void testProcess() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.cli2.OptionTestCase#testTriggers()
     */
    public void testTriggers() {
        final DefaultOption option = buildHelpOption();
        assertContentsEqual(list("-?", "-h", "--help"), option.getTriggers());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.cli2.OptionTestCase#testValidate()
     */
    public void testValidate() throws OptionException {
        final Parent option = buildXOption();
        final WriteableCommandLine commandLine = commandLine(option, list());

        try {
            option.validate(commandLine);
            fail("Missing an option");
        }
        catch (OptionException moe) {
            assertSame(option, moe.getOption());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.cli2.OptionTestCase#testAppendUsage()
     */
    public void testAppendUsage() {
        final Option option = buildHelpOption();
        final StringBuffer buffer = new StringBuffer();
        option.appendUsage(buffer, DisplaySetting.ALL, null);

        assertEquals("[--help (-?,-h)]", buffer.toString());
    }

    public void testAppendUsage_NoOptional() {
        final Option option = buildHelpOption();
        final StringBuffer buffer = new StringBuffer();
        final Set settings = new HashSet(DisplaySetting.ALL);
        settings.remove(DisplaySetting.DISPLAY_OPTIONAL);
        option.appendUsage(buffer, settings, null);

        assertEquals("--help (-?,-h)", buffer.toString());
    }

    public void testAppendUsage_NoAlias() {
        final Option option = buildHelpOption();
        final StringBuffer buffer = new StringBuffer();
        final Set settings = new HashSet(DisplaySetting.ALL);
        settings.remove(DisplaySetting.DISPLAY_ALIASES);
        option.appendUsage(buffer, settings, null);

        assertEquals("[--help]", buffer.toString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.cli2.OptionTestCase#testGetPreferredName()
     */
    public void testGetPreferredName() {
        final Option option = buildHelpOption();
        assertEquals("--help", option.getPreferredName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.cli2.OptionTestCase#testGetDescription()
     */
    public void testGetDescription() {
        final Option option = buildHelpOption();
        assertEquals("Displays the help", option.getDescription());
    }
    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.cli2.OptionTestCase#testHelpLines()
     */
    public void testHelpLines() {
        // TODO Auto-generated method stub
    }
}
