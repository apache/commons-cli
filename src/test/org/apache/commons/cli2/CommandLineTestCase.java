/*
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
package org.apache.commons.cli2;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.Parser;
import org.apache.commons.cli2.option.ArgumentTest;
import org.apache.commons.cli2.option.CommandTest;
import org.apache.commons.cli2.option.DefaultOptionTest;
import org.apache.commons.cli2.option.OptionTestCase;
import org.apache.commons.cli2.option.PropertyOption;
import org.apache.commons.cli2.option.SwitchTest;
import org.apache.commons.cli2.resource.ResourceConstants;
import org.apache.commons.cli2.resource.ResourceHelper;

public abstract class CommandLineTestCase
    extends CLITestCase {
    private static final ResourceHelper resources = ResourceHelper.getResourceHelper();
    public final Option present =
        new DefaultOptionBuilder().withLongName("present").withLongName("alsopresent").create();
    public final Option missing = new DefaultOptionBuilder().withLongName("missing").create();
    public final Option multiple = new DefaultOptionBuilder().withLongName("multiple").create();
    public final Option bool = new DefaultOptionBuilder().withLongName("bool").create();
    public final Option root =
        new GroupBuilder().withOption(present).withOption(missing).withOption(multiple)
                          .withOption(bool).create();
    private CommandLine commandLine;

    protected abstract CommandLine createCommandLine();

    /*
     * @see TestCase#setUp()
     */
    public void setUp()
        throws Exception {
        super.setUp();
        commandLine = createCommandLine();
    }

    /*
     * Class to test for boolean hasOption(String)
     */
    public final void testHasOptionString() {
        assertTrue(commandLine.hasOption("--present"));
        assertTrue(commandLine.hasOption("--alsopresent"));
        assertFalse(commandLine.hasOption("--missing"));
    }

    /*
     * Class to test for boolean hasOption(Option)
     */
    public final void testHasOptionOption() {
        assertTrue(commandLine.hasOption(present));
        assertFalse(commandLine.hasOption(missing));
    }

    public final void testGetOption() {
        assertSame(present, commandLine.getOption("--present"));
        assertSame(present, commandLine.getOption("--alsopresent"));

        //TODO decide whether the following assertion is valid
        //assertSame(missing,commandLine.getOption("--missing"));
    }

    /*
     * Class to test for List getValues(String)
     */
    public final void testGetValuesString() {
        assertListContentsEqual(list("present value"), commandLine.getValues("--present"));
        assertListContentsEqual(list("value 1", "value 2", "value 3"),
                                commandLine.getValues("--multiple"));
        assertTrue(commandLine.getValues("--missing").isEmpty());
    }

    /*
     * Class to test for List getValues(String, List)
     */
    public final void testGetValuesStringList() {
        assertListContentsEqual(list("present value"), commandLine.getValues("--present", null));
        assertListContentsEqual(list("present value"), commandLine.getValues("--alsopresent", null));
        assertSame(commandLine.getValues("--missing", Collections.EMPTY_LIST),
                   Collections.EMPTY_LIST);

        final List def = Collections.singletonList("default value");
        assertSame(def, commandLine.getValues("--missing", def));
    }

    /*
     * Class to test for List getValues(Option)
     */
    public final void testGetValuesOption() {
        assertListContentsEqual(list("present value"), commandLine.getValues(present));
        assertTrue(commandLine.getValues(missing).isEmpty());
    }

    /*
     * Class to test for List getValues(Option, List)
     */
    public final void testGetValuesOptionList() {
        assertListContentsEqual(list("present value"), commandLine.getValues(present));
        assertSame(commandLine.getValues(missing, Collections.EMPTY_LIST), Collections.EMPTY_LIST);

        final List defs = Collections.singletonList("custom default");
        assertSame(defs, commandLine.getValues(missing, defs));
    }

    /*
     * Class to test for Object getValue(String)
     */
    public final void testGetValueString() {
        assertEquals("present value", commandLine.getValue("--present"));
        assertEquals("present value", commandLine.getValue("--alsopresent"));
        assertNull(commandLine.getValue("--missing"));

        try {
            commandLine.getValue("--multiple");
            fail("expected IllegalStateException");
        } catch (IllegalStateException e) {
            assertEquals(resources.getMessage(ResourceConstants.ARGUMENT_TOO_MANY_VALUES),
                         e.getMessage());
        }
    }

    /*
     * Class to test for Object getValue(String, Object)
     */
    public final void testGetValueStringObject() {
        assertEquals("present value", commandLine.getValue("--present", "default value"));
        assertEquals("present value", commandLine.getValue("--alsopresent", "default value"));
        assertEquals("default value", commandLine.getValue("--missing", "default value"));

        try {
            commandLine.getValue("--multiple");
            fail("expected IllegalStateException");
        } catch (IllegalStateException e) {
            assertEquals(resources.getMessage(ResourceConstants.ARGUMENT_TOO_MANY_VALUES),
                         e.getMessage());
        }
    }

    /*
     * Class to test for Object getValue(Option)
     */
    public final void testGetValueOption() {
        assertEquals("present value", commandLine.getValue(present));
        assertNull(commandLine.getValue(missing));

        try {
            commandLine.getValue(multiple);
            fail("expected IllegalStateException");
        } catch (IllegalStateException e) {
            assertEquals(resources.getMessage(ResourceConstants.ARGUMENT_TOO_MANY_VALUES),
                         e.getMessage());
        }
    }

    /*
     * Class to test for Object getValue(Option, Object)
     */
    public final void testGetValueOptionObject() {
        assertEquals("present value", commandLine.getValue(present, "default value"));
        assertEquals("default value", commandLine.getValue(missing, "default value"));

        try {
            commandLine.getValue(multiple);
            fail("expected IllegalStateException");
        } catch (IllegalStateException e) {
            assertEquals(resources.getMessage(ResourceConstants.ARGUMENT_TOO_MANY_VALUES),
                         e.getMessage());
        }
    }

    /*
     * Class to test for Boolean getSwitch(String)
     */
    public final void testGetSwitchString() {
        assertEquals(Boolean.TRUE, commandLine.getSwitch("--bool"));
        assertNull(commandLine.getSwitch("--missing"));
    }

    /*
     * Class to test for Boolean getSwitch(String, Boolean)
     */
    public final void testGetSwitchStringBoolean() {
        assertEquals(Boolean.TRUE, commandLine.getSwitch("--bool", Boolean.FALSE));
        assertEquals(Boolean.FALSE, commandLine.getSwitch("--missing", Boolean.FALSE));
    }

    /*
     * Class to test for Boolean getSwitch(Option)
     */
    public final void testGetSwitchOption() {
        assertEquals(Boolean.TRUE, commandLine.getSwitch(bool));
        assertNull(commandLine.getSwitch(missing));
    }

    /*
     * Class to test for Boolean getSwitch(Option, Boolean)
     */
    public final void testGetSwitchOptionBoolean() {
        assertEquals(Boolean.TRUE, commandLine.getSwitch(bool, Boolean.FALSE));
        assertEquals(Boolean.FALSE, commandLine.getSwitch(missing, Boolean.FALSE));
    }

    /*
     * Class to test for String getProperty(String)
     */
    public final void testGetPropertyString() {
        assertEquals("present property", commandLine.getProperty("present"));
        assertNull(commandLine.getProperty("missing"));
    }

    /*
     * Class to test for String getProperty(String, String)
     */
    public final void testGetPropertyStringString() {
        assertEquals("present property", commandLine.getProperty("present", "default property"));
        assertEquals("default property", commandLine.getProperty("missing", "default property"));
    }

    public final void testGetProperties() {
        assertTrue(commandLine.getProperties().containsAll(list("present")));
    }

    /*
     * Class to test for int getOptionCount(String)
     */
    public final void testGetOptionCountString() {
        // one option, one switch
        assertTrue(1 <= commandLine.getOptionCount("--present"));
        assertTrue(1 <= commandLine.getOptionCount("--bool"));
        assertEquals(0, commandLine.getOptionCount("--missing"));
    }

    /*
     * Class to test for int getOptionCount(Option)
     */
    public final void testGetOptionCountOption() {
        // one option, one switch
        assertTrue(1 <= commandLine.getOptionCount(present));
        assertTrue(1 <= commandLine.getOptionCount(bool));
        assertEquals(0, commandLine.getOptionCount(missing));
    }

    public final void testGetOptions() {
        //TODO Implement getOptions().
    }

    public final void testGetOptionTriggers() {
        //TODO Implement getOptionTriggers().
    }

    // OLD TESTS FOLLOW
    public final void testProperties() {
        final Option option = new PropertyOption();
        final List args = CLITestCase.list();
        final WriteableCommandLine writeable = OptionTestCase.commandLine(option, args);

        assertTrue(writeable.getProperties().isEmpty());

        writeable.addProperty("myprop", "myval");
        assertEquals(1, writeable.getProperties().size());
        assertEquals("myval", writeable.getProperty("myprop"));

        writeable.addProperty("myprop", "myval2");
        assertEquals(1, writeable.getProperties().size());
        assertEquals("myval2", writeable.getProperty("myprop"));

        writeable.addProperty("myprop2", "myval3");
        assertEquals(2, writeable.getProperties().size());
        assertEquals("myval3", writeable.getProperty("myprop2"));
    }

    public final void testOptions() {
        final Option option = new PropertyOption();
        final List args = CLITestCase.list();
        final WriteableCommandLine writeable = OptionTestCase.commandLine(option, args);

        final Option start = CommandTest.buildStartCommand();

        assertFalse(writeable.hasOption(start));
        assertFalse(writeable.hasOption("start"));
        assertFalse(writeable.hasOption("go"));

        writeable.addOption(start);

        assertTrue(writeable.hasOption(start));
        assertTrue(writeable.hasOption("start"));
        assertTrue(writeable.hasOption("go"));
    }

    public final void testValues() {
        final Option option = new PropertyOption();
        final List args = CLITestCase.list();
        final WriteableCommandLine writeable = OptionTestCase.commandLine(option, args);

        final Option start = CommandTest.buildStartCommand();

        assertNull(writeable.getValue(start));
        assertTrue(writeable.getValues(start).isEmpty());

        writeable.addOption(start);

        assertTrue(writeable.getValues(start).isEmpty());

        writeable.addValue(start, "file1");

        assertEquals("file1", writeable.getValue(start));
        assertEquals("file1", writeable.getValue("start"));
        assertEquals("file1", writeable.getValue("go"));
        assertEquals(1, writeable.getValues(start).size());
        assertEquals(1, writeable.getValues("start").size());
        assertEquals(1, writeable.getValues("go").size());
        assertTrue(writeable.getValues(start).contains("file1"));
        assertTrue(writeable.getValues("start").contains("file1"));
        assertTrue(writeable.getValues("go").contains("file1"));

        writeable.addValue(start, "file2");

        try {
            writeable.getValue(start);
            fail("Cannot get single value if multiple are present");
        } catch (IllegalStateException ise) {
            assertEquals(resources.getMessage(ResourceConstants.ARGUMENT_TOO_MANY_VALUES),
                         ise.getMessage());
        }

        try {
            writeable.getValue("start");
            fail("Cannot get single value if multiple are present");
        } catch (IllegalStateException ise) {
            assertEquals(resources.getMessage(ResourceConstants.ARGUMENT_TOO_MANY_VALUES),
                         ise.getMessage());
        }

        writeable.getValues(start).add("file3");
    }

    public final void testSwitches() {
        final Option option = new PropertyOption();
        final List args = CLITestCase.list();
        final WriteableCommandLine writeable = OptionTestCase.commandLine(option, args);

        final Option start = CommandTest.buildStartCommand();

        assertNull(writeable.getSwitch(start));
        assertNull(writeable.getSwitch("start"));
        assertNull(writeable.getSwitch("go"));

        writeable.addSwitch(start, true);

        try {
            writeable.addSwitch(start, false);
            fail("Switch cannot be changed");
        } catch (IllegalStateException ise) {
            assertEquals(resources.getMessage(ResourceConstants.SWITCH_ALREADY_SET),
                         ise.getMessage());
        }
    }

    public final void testSwitches_True() {
        final Option option = new PropertyOption();
        final List args = CLITestCase.list();
        final WriteableCommandLine writeable = OptionTestCase.commandLine(option, args);

        final Option start = CommandTest.buildStartCommand();

        writeable.addSwitch(start, true);
        assertSame(Boolean.TRUE, writeable.getSwitch(start));
    }

    public final void testSwitches_False() {
        final Option option = new PropertyOption();
        final List args = CLITestCase.list();
        final WriteableCommandLine writeable = OptionTestCase.commandLine(option, args);

        final Option start = CommandTest.buildStartCommand();

        writeable.addSwitch(start, false);
        assertSame(Boolean.FALSE, writeable.getSwitch(start));
    }

    //    public final void testLooksLikeOption() {
    //        final Option option = new PropertyOption();
    //        final List args = OptionTestCase.list();
    //        final WriteableCommandLine commandLine =
    //            OptionTestCase.commandLine(option, args);
    //
    //        assertTrue(commandLine.looksLikeOption("-D"));
    //        assertFalse(commandLine.looksLikeOption("--help"));
    //        assertFalse(commandLine.looksLikeOption("+display"));
    //        assertFalse(commandLine.looksLikeOption("myprefix"));
    //        assertFalse(commandLine.looksLikeOption("myprefix2"));
    //        assertFalse(commandLine.looksLikeOption("myprefference"));
    //        assertFalse(commandLine.looksLikeOption("/SCANDISK"));
    //        assertFalse(commandLine.looksLikeOption("update"));
    //    }
    public final void testGetOptions_Order()
        throws OptionException {
        final Option help = DefaultOptionTest.buildHelpOption();
        final Option login = CommandTest.buildLoginCommand();
        final Option targets = ArgumentTest.buildTargetsArgument();

        final Group group =
            new GroupBuilder().withOption(help).withOption(login).withOption(targets).create();

        final Parser parser = new Parser();
        parser.setGroup(group);

        final CommandLine cl =
            parser.parse(new String[] { "login", "rob", "--help", "target1", "target2" });

        final Iterator i = cl.getOptions().iterator();

        assertSame(login, i.next());
        assertSame(help, i.next());
        assertSame(targets, i.next());
        assertSame(targets, i.next());
        assertFalse(i.hasNext());
    }

    public final void testGetOptionCount()
        throws OptionException {
        final Option help = DefaultOptionTest.buildHelpOption();
        final Option login = CommandTest.buildLoginCommand();
        final Option targets = ArgumentTest.buildTargetsArgument();
        final Option display = SwitchTest.buildDisplaySwitch();

        final Group group =
            new GroupBuilder().withOption(help).withOption(login).withOption(targets)
                              .withOption(display).create();

        final Parser parser = new Parser();
        parser.setGroup(group);

        final CommandLine cl =
            parser.parse(new String[] {
                             "--help", "login", "rob", "+display", "--help", "--help", "target1",
                             "target2"
                         });

        assertEquals(1, cl.getOptionCount(login));
        assertEquals(3, cl.getOptionCount(help));
        assertEquals(2, cl.getOptionCount(targets));
        assertEquals(1, cl.getOptionCount(display));
    }

    public final void testGetOptionCount_Strings()
        throws OptionException {
        final Option help = DefaultOptionTest.buildHelpOption();
        final Option login = CommandTest.buildLoginCommand();
        final Option targets = ArgumentTest.buildTargetsArgument();
        final Option display = SwitchTest.buildDisplaySwitch();

        final Group group =
            new GroupBuilder().withOption(help).withOption(login).withOption(targets)
                              .withOption(display).create();

        final Parser parser = new Parser();
        parser.setGroup(group);

        final CommandLine cl =
            parser.parse(new String[] {
                             "--help", "login", "rob", "+display", "--help", "--help", "target1",
                             "target2"
                         });

        assertEquals(1, cl.getOptionCount("login"));
        assertEquals(3, cl.getOptionCount("-?"));
        assertEquals(1, cl.getOptionCount("+display"));
    }

    public final void testOptionAsArgument()
        throws OptionException {
        final Option p = new DefaultOptionBuilder().withShortName("p").create();
        final Argument argument = new ArgumentBuilder().create();
        final Option withArgument =
            new DefaultOptionBuilder().withShortName("attr").withArgument(argument).create();

        final Group group = new GroupBuilder().withOption(p).withOption(withArgument).create();

        final Parser parser = new Parser();
        parser.setGroup(group);

        final CommandLine cl = parser.parse(new String[] { "-p", "-attr", "p" });

        assertEquals(1, cl.getOptionCount("-p"));
        assertTrue(cl.hasOption("-p"));
        assertTrue(cl.hasOption("-attr"));
        assertTrue(cl.getValue("-attr").equals("p"));
    }
}
