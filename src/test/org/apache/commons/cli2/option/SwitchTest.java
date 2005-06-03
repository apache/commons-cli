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
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.apache.commons.cli2.DisplaySetting;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.Parent;
import org.apache.commons.cli2.WriteableCommandLine;
import org.apache.commons.cli2.commandline.WriteableCommandLineImpl;

/**
 * @author Rob Oxspring
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class SwitchTest extends ParentTestCase {

    public static Switch buildDisplaySwitch() {
        final Set aliases = new HashSet();
        aliases.add("d");
        aliases.add("disp");
        return new Switch(
            "+",
            "-",
            "display",
            aliases,
            "Sets whether to display to screen",
            true,
            null,
            null,
            'd',
            null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.cli2.ParentTestCase#testProcessParent()
     */
    public void testProcessParent() throws OptionException {
        final Switch option = buildDisplaySwitch();
        final List args = list("+d");
        final WriteableCommandLine commandLine = commandLine(option, args);
        final ListIterator iterator = args.listIterator();
        option.processParent(commandLine, iterator);

        assertFalse(iterator.hasNext());
        assertTrue(commandLine.hasOption(option));
        assertTrue(commandLine.hasOption("+d"));
        assertTrue(commandLine.hasOption("-display"));
        assertEquals(Boolean.TRUE, commandLine.getSwitch("-d"));
        assertTrue(commandLine.getValues(option).isEmpty());
    }

    public void testProcessParent_Disabled() throws OptionException {
        final Switch option = buildDisplaySwitch();
        final List args = list("-disp");
        final WriteableCommandLine commandLine = commandLine(option, args);
        final ListIterator iterator = args.listIterator();
        option.process(commandLine, iterator);

        assertFalse(iterator.hasNext());
        assertTrue(commandLine.hasOption(option));
        assertTrue(commandLine.hasOption("+d"));
        assertTrue(commandLine.hasOption("-display"));
        assertEquals(Boolean.FALSE, commandLine.getSwitch("-d"));
        assertTrue(commandLine.getValues(option).isEmpty());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.cli2.OptionTestCase#testCanProcess()
     */
    public void testCanProcess() {
        final Switch option = buildDisplaySwitch();
        assertTrue(option.canProcess(new WriteableCommandLineImpl(option,null),"+d"));
    }

    public void testCanProcess_BadMatch() {
        final Switch option = buildDisplaySwitch();
        assertFalse(option.canProcess(new WriteableCommandLineImpl(option,null),"-dont"));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.cli2.OptionTestCase#testPrefixes()
     */
    public void testPrefixes() {
        final Switch option = buildDisplaySwitch();
        assertContentsEqual(list("-", "+"), option.getPrefixes());
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
        final Switch option = buildDisplaySwitch();
        assertContentsEqual(
            list("-d", "+d", "-disp", "+disp", "+display", "-display"),
            option.getTriggers());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.cli2.OptionTestCase#testValidate()
     */
    public void testValidate() {
        final Parent option = buildDisplaySwitch();
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
        final Option option = buildDisplaySwitch();
        final StringBuffer buffer = new StringBuffer();
        option.appendUsage(buffer, DisplaySetting.ALL, null);

        assertEquals(
            "+display|-display (+d|-d,+disp|-disp)",
            buffer.toString());
    }

    public void testAppendUsage_NoAlias() {
        final Option option = buildDisplaySwitch();
        final StringBuffer buffer = new StringBuffer();
        final Set settings = new HashSet(DisplaySetting.ALL);
        settings.remove(DisplaySetting.DISPLAY_ALIASES);
        option.appendUsage(buffer, settings, null);

        assertEquals("+display|-display", buffer.toString());
    }

    public void testAppendUsage_NoDisabled() {
        final Option option = buildDisplaySwitch();
        final StringBuffer buffer = new StringBuffer();
        final Set settings = new HashSet(DisplaySetting.ALL);
        settings.remove(DisplaySetting.DISPLAY_SWITCH_DISABLED);
        option.appendUsage(buffer, settings, null);

        assertEquals("+display (+d,+disp)", buffer.toString());
    }

    public void testAppendUsage_NoEnabled() {
        final Option option = buildDisplaySwitch();
        final StringBuffer buffer = new StringBuffer();
        final Set settings = new HashSet(DisplaySetting.ALL);
        settings.remove(DisplaySetting.DISPLAY_SWITCH_ENABLED);
        option.appendUsage(buffer, settings, null);

        assertEquals("-display (-d,-disp)", buffer.toString());
    }

    public void testAppendUsage_NoDisabledOrEnabled() {
        final Option option = buildDisplaySwitch();
        final StringBuffer buffer = new StringBuffer();
        final Set settings = new HashSet(DisplaySetting.ALL);
        settings.remove(DisplaySetting.DISPLAY_SWITCH_DISABLED);
        settings.remove(DisplaySetting.DISPLAY_SWITCH_ENABLED);
        option.appendUsage(buffer, settings, null);

        assertEquals("+display (+d,+disp)", buffer.toString());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.cli2.OptionTestCase#testGetPreferredName()
     */
    public void testGetPreferredName() {
        final Option option = buildDisplaySwitch();
        assertEquals("+display", option.getPreferredName());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.commons.cli2.OptionTestCase#testGetDescription()
     */
    public void testGetDescription() {
        final Option option = buildDisplaySwitch();
        assertEquals(
            "Sets whether to display to screen",
            option.getDescription());
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
