/*
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package org.apache.commons.cli;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("deprecation") // tests some deprecated classes
public class OptionGroupTest {
    private Options options;
    private final Parser parser = new PosixParser();

    @Before
    public void setUp() {
        final Option file = new Option("f", "file", false, "file to process");
        final Option dir = new Option("d", "directory", false, "directory to process");
        final OptionGroup group = new OptionGroup();
        group.addOption(file);
        group.addOption(dir);
        options = new Options().addOptionGroup(group);

        final Option section = new Option("s", "section", false, "section to process");
        final Option chapter = new Option("c", "chapter", false, "chapter to process");
        final OptionGroup group2 = new OptionGroup();
        group2.addOption(section);
        group2.addOption(chapter);

        options.addOptionGroup(group2);

        final Option importOpt = new Option(null, "import", false, "section to process");
        final Option exportOpt = new Option(null, "export", false, "chapter to process");
        final OptionGroup group3 = new OptionGroup();
        group3.addOption(importOpt);
        group3.addOption(exportOpt);
        options.addOptionGroup(group3);

        options.addOption("r", "revision", false, "revision number");
    }

    @Test
    public void testGetNames() {
        final OptionGroup group = new OptionGroup();
        group.addOption(OptionBuilder.create('a'));
        group.addOption(OptionBuilder.create('b'));

        assertNotNull("null names", group.getNames());
        assertEquals(2, group.getNames().size());
        assertTrue(group.getNames().contains("a"));
        assertTrue(group.getNames().contains("b"));
    }

    @Test
    public void testNoOptionsExtraArgs() throws Exception {
        final String[] args = {"arg1", "arg2"};

        final CommandLine cl = parser.parse(options, args);

        assertFalse("Confirm -r is NOT set", cl.hasOption("r"));
        assertFalse("Confirm -f is NOT set", cl.hasOption("f"));
        assertFalse("Confirm -d is NOT set", cl.hasOption("d"));
        assertFalse("Confirm -s is NOT set", cl.hasOption("s"));
        assertFalse("Confirm -c is NOT set", cl.hasOption("c"));
        assertEquals("Confirm TWO extra args", 2, cl.getArgList().size());
    }

    @Test
    public void testSingleLongOption() throws Exception {
        final String[] args = {"--file"};

        final CommandLine cl = parser.parse(options, args);

        assertFalse("Confirm -r is NOT set", cl.hasOption("r"));
        assertTrue("Confirm -f is set", cl.hasOption("f"));
        assertFalse("Confirm -d is NOT set", cl.hasOption("d"));
        assertFalse("Confirm -s is NOT set", cl.hasOption("s"));
        assertFalse("Confirm -c is NOT set", cl.hasOption("c"));
        assertTrue("Confirm no extra args", cl.getArgList().isEmpty());
    }

    @Test
    public void testSingleOption() throws Exception {
        final String[] args = {"-r"};

        final CommandLine cl = parser.parse(options, args);

        assertTrue("Confirm -r is set", cl.hasOption("r"));
        assertFalse("Confirm -f is NOT set", cl.hasOption("f"));
        assertFalse("Confirm -d is NOT set", cl.hasOption("d"));
        assertFalse("Confirm -s is NOT set", cl.hasOption("s"));
        assertFalse("Confirm -c is NOT set", cl.hasOption("c"));
        assertTrue("Confirm no extra args", cl.getArgList().isEmpty());
    }

    @Test
    public void testSingleOptionFromGroup() throws Exception {
        final String[] args = {"-f"};

        final CommandLine cl = parser.parse(options, args);

        assertFalse("Confirm -r is NOT set", cl.hasOption("r"));
        assertTrue("Confirm -f is set", cl.hasOption("f"));
        assertFalse("Confirm -d is NOT set", cl.hasOption("d"));
        assertFalse("Confirm -s is NOT set", cl.hasOption("s"));
        assertFalse("Confirm -c is NOT set", cl.hasOption("c"));
        assertTrue("Confirm no extra args", cl.getArgList().isEmpty());
    }

    @Test
    public void testToString() {
        final OptionGroup group1 = new OptionGroup();
        group1.addOption(new Option(null, "foo", false, "Foo"));
        group1.addOption(new Option(null, "bar", false, "Bar"));

        if (!"[--bar Bar, --foo Foo]".equals(group1.toString())) {
            assertEquals("[--foo Foo, --bar Bar]", group1.toString());
        }

        final OptionGroup group2 = new OptionGroup();
        group2.addOption(new Option("f", "foo", false, "Foo"));
        group2.addOption(new Option("b", "bar", false, "Bar"));

        if (!"[-b Bar, -f Foo]".equals(group2.toString())) {
            assertEquals("[-f Foo, -b Bar]", group2.toString());
        }
    }

    @Test
    public void testTwoLongOptionsFromGroup() throws Exception {
        final String[] args = {"--file", "--directory"};

        try {
            parser.parse(options, args);
            fail("two arguments from group not allowed");
        } catch (final AlreadySelectedException e) {
            assertNotNull("null option group", e.getOptionGroup());
            assertEquals("selected option", "f", e.getOptionGroup().getSelected());
            assertEquals("option", "d", e.getOption().getOpt());
        }
    }

    @Test
    public void testTwoOptionsFromDifferentGroup() throws Exception {
        final String[] args = {"-f", "-s"};

        final CommandLine cl = parser.parse(options, args);
        assertFalse("Confirm -r is NOT set", cl.hasOption("r"));
        assertTrue("Confirm -f is set", cl.hasOption("f"));
        assertFalse("Confirm -d is NOT set", cl.hasOption("d"));
        assertTrue("Confirm -s is set", cl.hasOption("s"));
        assertFalse("Confirm -c is NOT set", cl.hasOption("c"));
        assertTrue("Confirm NO extra args", cl.getArgList().isEmpty());
    }

    @Test
    public void testTwoOptionsFromGroup() throws Exception {
        final String[] args = {"-f", "-d"};

        try {
            parser.parse(options, args);
            fail("two arguments from group not allowed");
        } catch (final AlreadySelectedException e) {
            assertNotNull("null option group", e.getOptionGroup());
            assertEquals("selected option", "f", e.getOptionGroup().getSelected());
            assertEquals("option", "d", e.getOption().getOpt());
        }
    }

    @Test
    public void testTwoOptionsFromGroupWithProperties() throws Exception {
        final String[] args = {"-f"};

        final Properties properties = new Properties();
        properties.put("d", "true");

        final CommandLine cl = parser.parse(options, args, properties);
        assertTrue(cl.hasOption("f"));
        assertFalse(cl.hasOption("d"));
    }

    @Test
    public void testTwoValidLongOptions() throws Exception {
        final String[] args = {"--revision", "--file"};

        final CommandLine cl = parser.parse(options, args);

        assertTrue("Confirm -r is set", cl.hasOption("r"));
        assertTrue("Confirm -f is set", cl.hasOption("f"));
        assertFalse("Confirm -d is NOT set", cl.hasOption("d"));
        assertFalse("Confirm -s is NOT set", cl.hasOption("s"));
        assertFalse("Confirm -c is NOT set", cl.hasOption("c"));
        assertTrue("Confirm no extra args", cl.getArgList().isEmpty());
    }

    @Test
    public void testTwoValidOptions() throws Exception {
        final String[] args = {"-r", "-f"};

        final CommandLine cl = parser.parse(options, args);

        assertTrue("Confirm -r is set", cl.hasOption("r"));
        assertTrue("Confirm -f is set", cl.hasOption("f"));
        assertFalse("Confirm -d is NOT set", cl.hasOption("d"));
        assertFalse("Confirm -s is NOT set", cl.hasOption("s"));
        assertFalse("Confirm -c is NOT set", cl.hasOption("c"));
        assertTrue("Confirm no extra args", cl.getArgList().isEmpty());
    }

    @Test
    public void testValidLongOnlyOptions() throws Exception {
        final CommandLine cl1 = parser.parse(options, new String[] {"--export"});
        assertTrue("Confirm --export is set", cl1.hasOption("export"));

        final CommandLine cl2 = parser.parse(options, new String[] {"--import"});
        assertTrue("Confirm --import is set", cl2.hasOption("import"));
    }
}
