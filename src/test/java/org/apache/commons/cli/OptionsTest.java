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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

@SuppressWarnings("deprecation") // tests some deprecated classes
public class OptionsTest {
    @Test
    public void testDuplicateLong() {
        final Options opts = new Options();
        opts.addOption("a", "--a", false, "toggle -a");
        opts.addOption("a", "--a", false, "toggle -a*");
        assertEquals("last one in wins", "toggle -a*", opts.getOption("a").getDescription());
    }

    @Test
    public void testDuplicateSimple() {
        final Options opts = new Options();
        opts.addOption("a", false, "toggle -a");
        opts.addOption("a", true, "toggle -a*");

        assertEquals("last one in wins", "toggle -a*", opts.getOption("a").getDescription());
    }

    @Test
    public void testGetMatchingOpts() {
        final Options options = new Options();
        options.addOption(OptionBuilder.withLongOpt("version").create());
        options.addOption(OptionBuilder.withLongOpt("verbose").create());

        assertTrue(options.getMatchingOptions("foo").isEmpty());
        assertEquals(1, options.getMatchingOptions("version").size());
        assertEquals(2, options.getMatchingOptions("ver").size());
    }

    @Test
    public void testGetOptionsGroups() {
        final Options options = new Options();

        final OptionGroup group1 = new OptionGroup();
        group1.addOption(OptionBuilder.create('a'));
        group1.addOption(OptionBuilder.create('b'));

        final OptionGroup group2 = new OptionGroup();
        group2.addOption(OptionBuilder.create('x'));
        group2.addOption(OptionBuilder.create('y'));

        options.addOptionGroup(group1);
        options.addOptionGroup(group2);

        assertNotNull(options.getOptionGroups());
        assertEquals(2, options.getOptionGroups().size());
    }

    @Test
    public void testHelpOptions() {
        final Option longOnly1 = OptionBuilder.withLongOpt("long-only1").create();
        final Option longOnly2 = OptionBuilder.withLongOpt("long-only2").create();
        final Option shortOnly1 = OptionBuilder.create("1");
        final Option shortOnly2 = OptionBuilder.create("2");
        final Option bothA = OptionBuilder.withLongOpt("bothA").create("a");
        final Option bothB = OptionBuilder.withLongOpt("bothB").create("b");

        final Options options = new Options();
        options.addOption(longOnly1);
        options.addOption(longOnly2);
        options.addOption(shortOnly1);
        options.addOption(shortOnly2);
        options.addOption(bothA);
        options.addOption(bothB);

        final Collection<Option> allOptions = new ArrayList<>();
        allOptions.add(longOnly1);
        allOptions.add(longOnly2);
        allOptions.add(shortOnly1);
        allOptions.add(shortOnly2);
        allOptions.add(bothA);
        allOptions.add(bothB);

        final Collection<Option> helpOptions = options.helpOptions();

        assertTrue("Everything in all should be in help", helpOptions.containsAll(allOptions));
        assertTrue("Everything in help should be in all", allOptions.containsAll(helpOptions));
    }

    @Test
    public void testLong() {
        final Options opts = new Options();

        opts.addOption("a", "--a", false, "toggle -a");
        opts.addOption("b", "--b", true, "set -b");

        assertTrue(opts.hasOption("a"));
        assertTrue(opts.hasOption("b"));
    }

    @Test
    public void testMissingOptionException() throws ParseException {
        final Options options = new Options();
        options.addOption(OptionBuilder.isRequired().create("f"));
        try {
            new PosixParser().parse(options, new String[0]);
            fail("Expected MissingOptionException to be thrown");
        } catch (final MissingOptionException e) {
            assertEquals("Missing required option: f", e.getMessage());
        }
    }

    @Test
    public void testMissingOptionsException() throws ParseException {
        final Options options = new Options();
        options.addOption(OptionBuilder.isRequired().create("f"));
        options.addOption(OptionBuilder.isRequired().create("x"));
        try {
            new PosixParser().parse(options, new String[0]);
            fail("Expected MissingOptionException to be thrown");
        } catch (final MissingOptionException e) {
            assertEquals("Missing required options: f, x", e.getMessage());
        }
    }

    @Test
    public void testSimple() {
        final Options opts = new Options();

        opts.addOption("a", false, "toggle -a");
        opts.addOption("b", true, "toggle -b");

        assertTrue(opts.hasOption("a"));
        assertTrue(opts.hasOption("b"));
    }

    @Test
    public void testToString() {
        final Options options = new Options();
        options.addOption("f", "foo", true, "Foo");
        options.addOption("b", "bar", false, "Bar");

        final String s = options.toString();
        assertNotNull("null string returned", s);
        assertTrue("foo option missing", s.toLowerCase().contains("foo"));
        assertTrue("bar option missing", s.toLowerCase().contains("bar"));
    }
}
