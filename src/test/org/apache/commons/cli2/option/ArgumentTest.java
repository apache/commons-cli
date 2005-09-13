/*
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

import java.text.ParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.apache.commons.cli2.Argument;
import org.apache.commons.cli2.DisplaySetting;
import org.apache.commons.cli2.HelpLine;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.WriteableCommandLine;
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.WriteableCommandLineImpl;
import org.apache.commons.cli2.resource.ResourceConstants;
import org.apache.commons.cli2.resource.ResourceHelper;
import org.apache.commons.cli2.validation.DateValidator;
import org.apache.commons.cli2.validation.DateValidatorTest;

/**
 * @author Rob Oxspring
 */
public class ArgumentTest
    extends ArgumentTestCase {
    private ResourceHelper resources = ResourceHelper.getResourceHelper();

    public static Argument buildUsernameArgument() {
        return new ArgumentImpl("username", "The user to connect as", 1, 1, '\0', '\0', null,
                                ArgumentImpl.DEFAULT_CONSUME_REMAINING, null, 0);
    }

    public static Argument buildHostArgument() {
        return new ArgumentImpl("host", "The host name", 2, 3, '\0', ',', null, null, null, 0);
    }

    public static Argument buildPathArgument() {
        return new ArgumentImpl("path", "The place to look for files", 1, Integer.MAX_VALUE, '=',
                                ';', null, ArgumentImpl.DEFAULT_CONSUME_REMAINING, null, 0);
    }

    public static Argument buildDateLimitArgument() {
        return new ArgumentImpl("limit", "the last acceptable date", 0, 1, '=', '\0',
                                new DateValidator(DateValidatorTest.YYYY_MM_YY), null, null, 0);
    }

    public static Argument buildTargetsArgument() {
        return new ArgumentImpl("target", "The targets ant should build", 0, Integer.MAX_VALUE,
                                '\0', ',', null, null, null, 0);
    }

    public static Argument buildSizeArgument() {
        List defaults = new ArrayList();
        defaults.add("10");

        return new ArgumentImpl("size", "The number of units", 1, 1, '\0', '\0', null,
                                ArgumentImpl.DEFAULT_CONSUME_REMAINING, defaults, 0);
    }

    public static Argument buildBoundsArgument() {
        List defaults = new ArrayList();
        defaults.add("5");
        defaults.add("10");

        return new ArgumentImpl("size", "The number of units", 2, 2, '\0', '\0', null,
                                ArgumentImpl.DEFAULT_CONSUME_REMAINING, defaults, 0);
    }

    public void testNew() {
        try {
            new ArgumentImpl("limit", "the last acceptable date", 10, 5, '=', '\0',
                             new DateValidator(DateValidatorTest.YYYY_MM_YY), null, null, 0);
        } catch (IllegalArgumentException e) {
            assertEquals(resources.getMessage("Argument.minimum.exceeds.maximum"), e.getMessage());
        }

        {
            ArgumentImpl arg =
                new ArgumentImpl(null, "the last acceptable date", 5, 5, '=', '\0',
                                 new DateValidator(DateValidatorTest.YYYY_MM_YY), null, null, 0);
            assertEquals("wrong arg name", "arg", arg.getPreferredName());
        }

        {
            List defaults = new ArrayList();

            try {
                new ArgumentImpl(null, "the last acceptable date", 1, 1, '=', '\0',
                                 new DateValidator(DateValidatorTest.YYYY_MM_YY), null, defaults, 0);
            } catch (IllegalArgumentException exp) {
                assertEquals(resources.getMessage("Argument.too.few.defaults"), exp.getMessage());
            }
        }

        try {
            List defaults = new ArrayList();
            defaults.add("1");
            defaults.add("2");

            new ArgumentImpl(null, "the last acceptable date", 1, 1, '=', '\0',
                             new DateValidator(DateValidatorTest.YYYY_MM_YY), null, defaults, 0);
        } catch (IllegalArgumentException exp) {
            assertEquals(resources.getMessage("Argument.too.many.defaults"), exp.getMessage());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.commons.cli2.ArgumentTestCase#testProcessValues()
     */
    public void testProcessValues()
        throws OptionException {
        final Argument option = buildUsernameArgument();
        final List args = list("rob");
        final WriteableCommandLine commandLine = commandLine(option, args);
        final ListIterator iterator = args.listIterator();
        option.processValues(commandLine, iterator, option);

        assertFalse(iterator.hasNext());
        assertTrue(commandLine.hasOption(option));
        assertTrue(commandLine.hasOption("username"));
        assertEquals("rob", commandLine.getValue(option));
    }

    public void testProcessValues_BoundaryQuotes()
        throws OptionException {
        final Argument option = buildUsernameArgument();
        final List args = list("\"rob\"");
        final WriteableCommandLine commandLine = commandLine(option, args);
        final ListIterator iterator = args.listIterator();
        option.processValues(commandLine, iterator, option);

        assertFalse(iterator.hasNext());
        assertTrue(commandLine.hasOption(option));
        assertTrue(commandLine.hasOption("username"));
        assertEquals("rob", commandLine.getValue(option));
    }

    public void testProcessValues_SpareValues()
        throws OptionException {
        final Argument option = buildUsernameArgument();
        final List args = list("rob", "secret");
        final WriteableCommandLine commandLine = commandLine(option, args);
        final ListIterator iterator = args.listIterator();
        option.processValues(commandLine, iterator, option);

        assertTrue(iterator.hasNext());
        assertTrue(commandLine.hasOption(option));
        assertTrue(commandLine.hasOption("username"));
        assertEquals("rob", commandLine.getValue(option));
    }

    public void testProcessValues_Optional() {
        final Argument option = buildTargetsArgument();
        final List args = list();
        final WriteableCommandLine commandLine = commandLine(option, args);
        final ListIterator iterator = args.listIterator();

        try {
            option.processValues(commandLine, iterator, option);
        } catch (final OptionException mve) {
            assertEquals(option, mve.getOption());
            assertEquals("Missing value(s) target [target ...]", mve.getMessage());
        }

        assertFalse(iterator.hasNext());
        assertFalse(commandLine.hasOption(option));
        assertFalse(commandLine.hasOption("username"));
        assertTrue(commandLine.getValues(option).isEmpty());
    }

    public void testProcessValues_Multiple()
        throws OptionException {
        final Argument option = buildTargetsArgument();
        final List args = list("compile", "test", "docs");
        final WriteableCommandLine commandLine = commandLine(option, args);
        final ListIterator iterator = args.listIterator();
        option.processValues(commandLine, iterator, option);

        assertFalse(iterator.hasNext());
        assertTrue(commandLine.hasOption(option));
        assertTrue(commandLine.hasOption("target"));
        assertFalse(commandLine.getValues(option).isEmpty());
        assertListContentsEqual(args, commandLine.getValues(option));
    }

    public void testProcessValues_Contracted()
        throws OptionException {
        final Argument option = buildTargetsArgument();
        final List args = list("compile,test,javadoc", "checkstyle,jdepend");
        final WriteableCommandLine commandLine = commandLine(option, args);
        final ListIterator iterator = args.listIterator();
        option.processValues(commandLine, iterator, option);

        assertFalse(iterator.hasNext());
        assertTrue(commandLine.hasOption(option));
        assertTrue(commandLine.hasOption("target"));
        assertListContentsEqual(list("compile", "test", "javadoc", "checkstyle", "jdepend"),
                                commandLine.getValues(option));
    }

    public void testProcessValues_ContractedTooFew() {
        final Argument option = buildHostArgument();
        final List args = list("box1");
        final WriteableCommandLine commandLine = commandLine(option, args);
        final ListIterator iterator = args.listIterator();

        try {
            option.processValues(commandLine, iterator, option);
            option.validate(commandLine);
            fail("Expected MissingValueException");
        } catch (OptionException mve) {
            assertSame(option, mve.getOption());
        }
    }

    public void testProcessValues_ContractedTooMany() {
        final Argument option = buildHostArgument();
        final List args = list("box1,box2,box3,box4");
        final WriteableCommandLine commandLine = commandLine(option, args);
        final ListIterator iterator = args.listIterator();

        try {
            option.processValues(commandLine, iterator, option);
            option.validate(commandLine);
            fail("Expected MissingValueException");
        } catch (OptionException mve) {
            assertSame(option, mve.getOption());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.commons.cli2.OptionTestCase#testCanProcess()
     */
    public void testCanProcess() {
        final Argument option = buildTargetsArgument();
        assertTrue(option.canProcess(new WriteableCommandLineImpl(option, null), "any value"));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.commons.cli2.OptionTestCase#testPrefixes()
     */
    public void testPrefixes() {
        final Argument option = buildTargetsArgument();
        assertTrue(option.getPrefixes().isEmpty());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.commons.cli2.OptionTestCase#testProcess()
     */
    public void testProcess()
        throws OptionException {
        final Argument option = buildPathArgument();
        final List args = list("-path=/lib;/usr/lib;/usr/local/lib");
        final WriteableCommandLine commandLine = commandLine(option, args);
        final ListIterator iterator = args.listIterator();
        option.process(commandLine, iterator);

        assertFalse(iterator.hasNext());
        assertTrue(commandLine.hasOption(option));
        assertTrue(commandLine.hasOption("path"));
        assertListContentsEqual(list("-path=/lib", "/usr/lib", "/usr/local/lib"),
                                commandLine.getValues(option));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.commons.cli2.OptionTestCase#testTriggers()
     */
    public void testTriggers() {
        final Argument option = buildTargetsArgument();
        assertTrue(option.getTriggers().isEmpty());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.commons.cli2.OptionTestCase#testValidate()
     */
    public void testValidate()
        throws OptionException {
        final Argument option = buildUsernameArgument();
        final WriteableCommandLine commandLine = commandLine(option, list());

        commandLine.addValue(option, "rob");

        option.validate(commandLine);
    }

    public void testValidate_Minimum() {
        final Argument option = buildUsernameArgument();
        final WriteableCommandLine commandLine = commandLine(option, list());

        try {
            option.validate(commandLine);
            fail("UnexpectedValue");
        } catch (OptionException mve) {
            assertEquals(option, mve.getOption());
        }
    }

    public void testRequired() {
        {
            final Argument arg = buildBoundsArgument();

            assertTrue("not required", arg.isRequired());
        }

        {
            final Argument arg = buildTargetsArgument();

            assertFalse("should not be required", arg.isRequired());
        }
    }

    public void testValidate_Maximum() {
        final Argument option = buildUsernameArgument();
        final WriteableCommandLine commandLine = commandLine(option, list());

        commandLine.addValue(option, "rob");
        commandLine.addValue(option, "oxspring");

        try {
            option.validate(commandLine);
            fail("UnexpectedValue");
        } catch (OptionException uve) {
            assertEquals(option, uve.getOption());
        }
    }

    public void testValidate_Validator()
        throws OptionException, ParseException {
        final Argument option = buildDateLimitArgument();
        final WriteableCommandLine commandLine = commandLine(option, list());

        commandLine.addValue(option, "2004-01-01");

        option.validate(commandLine, option);
        assertContentsEqual(Arrays.asList(new Object[] {
                                              DateValidatorTest.YYYY_MM_YY.parse("2004-01-01")
                                          }), commandLine.getValues(option));
    }

    public void testValidate_ValidatorInvalidDate()
        throws OptionException, ParseException {
        final Argument option = buildDateLimitArgument();
        final WriteableCommandLine commandLine = commandLine(option, list());

        commandLine.addValue(option, "12-12-2004");

        try {
            option.validate(commandLine, option);
        } catch (OptionException exp) {
            OptionException e =
                new OptionException(option, ResourceConstants.ARGUMENT_UNEXPECTED_VALUE,
                                    "12-12-2004");
            assertEquals("wrong exception message", e.getMessage(), exp.getMessage());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.commons.cli2.OptionTestCase#testAppendUsage()
     */
    public void testAppendUsage() {
        final Option option = buildUsernameArgument();
        final StringBuffer buffer = new StringBuffer();
        option.appendUsage(buffer, DisplaySetting.ALL, null);

        assertEquals("<username>", buffer.toString());
    }

    public void testAppendUsage_Infinite() {
        final Option option = buildTargetsArgument();
        final StringBuffer buffer = new StringBuffer();
        option.appendUsage(buffer, DisplaySetting.ALL, null);

        assertEquals("[<target1> [<target2> ...]]", buffer.toString());
    }

    public void testAppendUsage_InfiniteNoOptional() {
        final Option option = buildTargetsArgument();
        final StringBuffer buffer = new StringBuffer();
        final Set settings = new HashSet(DisplaySetting.ALL);
        settings.remove(DisplaySetting.DISPLAY_OPTIONAL);
        option.appendUsage(buffer, settings, null);

        assertEquals("<target1> [<target2> ...]", buffer.toString());
    }

    public void testAppendUsage_InfiniteNoNumbering() {
        final Option option = buildTargetsArgument();
        final StringBuffer buffer = new StringBuffer();
        final Set settings = new HashSet(DisplaySetting.ALL);
        settings.remove(DisplaySetting.DISPLAY_ARGUMENT_NUMBERED);
        option.appendUsage(buffer, settings, null);

        assertEquals("[<target> [<target> ...]]", buffer.toString());
    }

    public void testAppendUsage_Minimum() {
        final Option option = buildHostArgument();
        final StringBuffer buffer = new StringBuffer();
        option.appendUsage(buffer, DisplaySetting.ALL, null);

        assertEquals("<host1> <host2> [<host3>]", buffer.toString());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.commons.cli2.OptionTestCase#testGetPreferredName()
     */
    public void testGetPreferredName() {
        final Option option = buildPathArgument();
        assertEquals("path", option.getPreferredName());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.commons.cli2.OptionTestCase#testGetDescription()
     */
    public void testGetDescription() {
        final Option option = buildHostArgument();
        assertEquals("The host name", option.getDescription());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.apache.commons.cli2.OptionTestCase#testHelpLines()
     */
    public void testHelpLines() {
        final Option option = buildHostArgument();
        final List lines = option.helpLines(0, DisplaySetting.ALL, null);
        final Iterator i = lines.iterator();

        final HelpLine line1 = (HelpLine) i.next();
        assertEquals(0, line1.getIndent());
        assertEquals(option, line1.getOption());

        assertFalse(i.hasNext());
    }

    public void testCanProcess_ConsumeRemaining() {
        final Option option = buildUsernameArgument();

        assertTrue(option.canProcess(new WriteableCommandLineImpl(option, null), "--"));
    }

    public void testProcess_ConsumeRemaining()
        throws OptionException {
        final Option option = buildPathArgument();
        final List args = list("options", "--", "--ignored", "-Dprop=val");
        final WriteableCommandLine commandLine = commandLine(option, args);
        final ListIterator iterator = args.listIterator();

        option.process(commandLine, iterator);

        final List values = commandLine.getValues(option);
        assertTrue(values.contains("options"));
        assertTrue(values.contains("--ignored"));
        assertTrue(values.contains("-Dprop=val"));
        assertEquals(3, values.size());
        assertFalse(iterator.hasNext());
    }

    public void testProcess_ConsumeNothing() {
        final Option option = buildPathArgument();
        final List args = list("--");
        final WriteableCommandLine commandLine = commandLine(option, args);
        final ListIterator iterator = args.listIterator();

        try {
            option.process(commandLine, iterator);
            option.validate(commandLine);
            fail("Missing Value!");
        } catch (OptionException mve) {
            assertEquals(option, mve.getOption());
            assertEquals("Missing value(s) path [path ...]", mve.getMessage());
        }

        assertTrue(commandLine.getValues(option).isEmpty());
        assertFalse(iterator.hasNext());
    }

    //    public void testProcess_DefinedDefaultValue() throws OptionException {
    //        final Option size = buildSizeArgument();
    //        final List args = list();
    //        final WriteableCommandLine commandLine = commandLine(size, args);
    //        final ListIterator iterator = args.listIterator();
    //
    //        size.process(commandLine, iterator);
    //
    //        assertEquals("10", commandLine.getValue(size));
    //    }
    //
    //    public void testProcess_DefinedDefaultValues() throws OptionException {
    //        final Option bounds = buildBoundsArgument();
    //        final List args = list();
    //        final WriteableCommandLine commandLine = commandLine(bounds, args);
    //        final ListIterator iterator = args.listIterator();
    //
    //        bounds.process(commandLine, iterator);
    //
    //        List values = new ArrayList();
    //        values.add("5");
    //        values.add("10");
    //        assertEquals(values, commandLine.getValues(bounds));
    //    }
    public void testProcess_InterrogatedDefaultValue()
        throws OptionException {
        final Option size = buildSizeArgument();
        final List args = list();
        final WriteableCommandLine commandLine = commandLine(size, args);
        final ListIterator iterator = args.listIterator();

        size.process(commandLine, iterator);

        assertEquals(new Integer(20), commandLine.getValue(size, new Integer(20)));
    }

    public void testTooFewDefaults() {
        List defaults = new ArrayList();
        defaults.add("5");

        try {
            new ArgumentImpl("size", "The number of units", 2, 2, '\0', '\0', null,
                             ArgumentImpl.DEFAULT_CONSUME_REMAINING, defaults, 0);
        } catch (IllegalArgumentException exp) {
            assertEquals("wrong exception message",
                         ResourceHelper.getResourceHelper().getMessage(ResourceConstants.ARGUMENT_TOO_FEW_DEFAULTS),
                         exp.getMessage());
        }
    }

    public void testTooManyDefaults() {
        List defaults = new ArrayList();
        defaults.add("5");
        defaults.add("10");
        defaults.add("15");

        try {
            new ArgumentImpl("size", "The number of units", 2, 2, '\0', '\0', null,
                             ArgumentImpl.DEFAULT_CONSUME_REMAINING, defaults, 0);
        } catch (IllegalArgumentException exp) {
            assertEquals("wrong exception message",
                         ResourceHelper.getResourceHelper().getMessage(ResourceConstants.ARGUMENT_TOO_MANY_DEFAULTS),
                         exp.getMessage());
        }
    }

    public void testProcess_InterrogatedDefaultValues()
        throws OptionException {
        final Option bounds = buildBoundsArgument();
        final List args = list();
        final WriteableCommandLine commandLine = commandLine(bounds, args);
        final ListIterator iterator = args.listIterator();

        bounds.process(commandLine, iterator);

        List values = new ArrayList();
        values.add("50");
        values.add("100");
        assertEquals(values, commandLine.getValues(bounds, values));
    }

    public void testProcess_StripBoundaryQuotes()
        throws OptionException {
        final Option bounds = buildBoundsArgument();
        final List args = list();
        final WriteableCommandLine commandLine = commandLine(bounds, args);
        final ListIterator iterator = args.listIterator();

        bounds.process(commandLine, iterator);

        List values = new ArrayList();
        values.add("50\"");
        values.add("\"100");
        assertEquals(values, commandLine.getValues(bounds, values));
    }

    public void testSourceDestArgument() {
        final ArgumentBuilder abuilder = new ArgumentBuilder();
        final GroupBuilder gbuilder = new GroupBuilder();
        final Argument inputfiles =
            abuilder.withName("input").withMinimum(0).withMaximum(0).create();
        final Argument bad_outputfile =
            abuilder.withName("output").withMinimum(1).withMaximum(2).create();

        try {
            final Argument targets = new SourceDestArgument(inputfiles, bad_outputfile);
        } catch (final IllegalArgumentException exp) {
            assertEquals("wrong exception message",
                         ResourceHelper.getResourceHelper().getMessage(ResourceConstants.SOURCE_DEST_MUST_ENFORCE_VALUES),
                         exp.getMessage());
        }

        final Argument outputfile =
            abuilder.withName("output").withMinimum(1).withMaximum(1).create();

        final Argument targets = new SourceDestArgument(inputfiles, outputfile);
        final StringBuffer buffer = new StringBuffer("test content");
        targets.appendUsage(buffer, Collections.EMPTY_SET, null);

        assertTrue("buffer not added", buffer.toString().startsWith("test content"));
        assertFalse("space added", buffer.charAt(12) == ' ');
    }
}
