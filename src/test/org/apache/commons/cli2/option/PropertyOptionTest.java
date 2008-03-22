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
package org.apache.commons.cli2.option;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.apache.commons.cli2.DisplaySetting;
import org.apache.commons.cli2.HelpLine;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.WriteableCommandLine;
import org.apache.commons.cli2.commandline.WriteableCommandLineImpl;

/**
 * @author Rob Oxspring
 */
public class PropertyOptionTest extends OptionTestCase {

    /*
     * (non-Javadoc)
     *
     * @see org.apache.commons.cli2.OptionTestCase#testCanProcess()
     */
    public void testCanProcess() {
        final Option option = new PropertyOption();
        assertTrue(option.canProcess(new WriteableCommandLineImpl(option,null), "-Dmyprop=myval"));
    }

    public void testCanProcess_Null() {
        final Option option = new PropertyOption();
        assertFalse(option.canProcess(new WriteableCommandLineImpl(option,null), (String) null));
    }

    public void testCanProcess_TooShort() {
        final Option option = new PropertyOption();
        assertFalse(option.canProcess(new WriteableCommandLineImpl(option,null), "-D"));
    }

    public void testCanProcess_BadMatch() {
        final Option option = new PropertyOption();
        assertFalse(option.canProcess(new WriteableCommandLineImpl(option,null),"-dump"));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.commons.cli2.OptionTestCase#testPrefixes()
     */
    public void testPrefixes() {
        final Option option = new PropertyOption();
        assertContentsEqual(list("-D"), option.getPrefixes());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.commons.cli2.OptionTestCase#testProcess()
     */
    public void testProcess() throws OptionException {
        final Option option = new PropertyOption();
        final List args = list("-Dmyprop=myvalue");
        final WriteableCommandLine commandLine = commandLine(option, args);
        final ListIterator iterator = args.listIterator();

        option.process(commandLine, iterator);
        assertEquals("myvalue", commandLine.getProperty("myprop"));
        assertFalse(iterator.hasNext());
        assertEquals(1, commandLine.getProperties().size());
    }

    public void testProcess_UnexpectedOptionException() {
        final Option option = new PropertyOption();
        final List args = list("--help");
        final WriteableCommandLine commandLine = commandLine(option, args);
        final ListIterator iterator = args.listIterator();

        try {
            option.process(commandLine, iterator);
            fail("UnexpectedOption");
        }
        catch (final OptionException uoe) {
            assertEquals(option, uoe.getOption());
            assertEquals(
                "Unexpected --help while processing -Dproperty=value",
                uoe.getMessage());
        }
    }

    public void testProcess_BadPropertyException() throws OptionException {
        final Option option = new PropertyOption();
        final List args = list("-Dmyprop");
        final WriteableCommandLine commandLine = commandLine(option, args);
        final ListIterator iterator = args.listIterator();

        option.process(commandLine, iterator);

        assertEquals("true", commandLine.getProperty("myprop"));
    }

    public void testProcess_SetToEmpty() throws OptionException {
        final Option option = new PropertyOption();
        final List args = list("-Dmyprop=");
        final WriteableCommandLine commandLine = commandLine(option, args);
        final ListIterator iterator = args.listIterator();

        option.process(commandLine, iterator);
        assertEquals("", commandLine.getProperty("myprop"));
        assertFalse(iterator.hasNext());
        assertEquals(1, commandLine.getProperties().size());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.commons.cli2.OptionTestCase#testTriggers()
     */
    public void testTriggers() {
        final Option option = new PropertyOption();

        assertContentsEqual(list("-D"), option.getTriggers());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.commons.cli2.OptionTestCase#testValidate()
     */
    public void testValidate() throws OptionException {
        final Option option = new PropertyOption();
        final List args = list("-Dproperty=value");
        final WriteableCommandLine commandLine = commandLine(option, args);
        final ListIterator iterator = args.listIterator();

        option.process(commandLine, iterator);

        option.validate(commandLine);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.commons.cli2.OptionTestCase#testAppendUsage()
     */
    public void testAppendUsage() {
        final Option option = new PropertyOption();
        final StringBuffer buffer = new StringBuffer();
        option.appendUsage(buffer, DisplaySetting.ALL, null);

        assertEquals("-D<property>=<value>", buffer.toString());
    }

    public void testAppendUsage_Hidden() {
        final Option option = new PropertyOption();
        final StringBuffer buffer = new StringBuffer();
        final Set settings = new HashSet(DisplaySetting.ALL);
        settings.remove(DisplaySetting.DISPLAY_PROPERTY_OPTION);
        option.appendUsage(buffer, settings, null);

        assertEquals("", buffer.toString());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.commons.cli2.OptionTestCase#testGetPreferredName()
     */
    public void testGetPreferredName() {
        final Option option = new PropertyOption();
        assertEquals("-D", option.getPreferredName());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.commons.cli2.OptionTestCase#testGetDescription()
     */
    public void testGetDescription() {
        final Option option = new PropertyOption();
        assertEquals(
            "Passes properties and values to the application",
            option.getDescription());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.commons.cli2.OptionTestCase#testHelpLines()
     */
    public void testHelpLines() {
        final Option option = new PropertyOption();
        final List lines = option.helpLines(0, DisplaySetting.ALL, null);
        final Iterator i = lines.iterator();

        final HelpLine line1 = (HelpLine)i.next();
        assertEquals(0, line1.getIndent());
        assertEquals(option, line1.getOption());

        assertFalse(i.hasNext());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.commons.cli2.OptionTestCase#testHelpLines()
     */
    public void testHelpLines_NoDisplay() {
        final Option option = new PropertyOption();
        final Set settings = new HashSet(DisplaySetting.ALL);
        settings.remove(DisplaySetting.DISPLAY_PROPERTY_OPTION);
        final List lines = option.helpLines(0, settings, null);
        final Iterator i = lines.iterator();

        assertFalse(i.hasNext());
    }
}
