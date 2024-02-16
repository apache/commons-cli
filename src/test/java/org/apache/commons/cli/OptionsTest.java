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
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        OptionBuilder.withLongOpt("version");
        options.addOption(OptionBuilder.create());
        OptionBuilder.withLongOpt("verbose");
        options.addOption(OptionBuilder.create());

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
    public void testAddOptions() {
        final Options options = new Options();

        final OptionGroup group1 = new OptionGroup();
        group1.addOption(Option.builder("a").build());
        group1.addOption(Option.builder("b").build());

        options.addOptionGroup(group1);

        options.addOption(Option.builder("X").build());
        options.addOption(Option.builder("y").build());

        final Options underTest = new Options();
        underTest.addOptions(options);

        assertEquals(options.getOptionGroups(), underTest.getOptionGroups());
        assertArrayEquals(options.getOptions().toArray(), underTest.getOptions().toArray());
    }

    @Test
    public void testAddOptions2X() {
        final Options options = new Options();

        final OptionGroup group1 = new OptionGroup();
        group1.addOption(Option.builder("a").build());
        group1.addOption(Option.builder("b").build());

        options.addOptionGroup(group1);

        options.addOption(Option.builder("X").build());
        options.addOption(Option.builder("y").build());

        assertThrows(IllegalArgumentException.class, () -> options.addOptions(options));
    }

    @Test
    public void testAddConflictingOptions() {
        final Options options1 = new Options();
        final OptionGroup group1 = new OptionGroup();
        group1.addOption(Option.builder("a").build());
        group1.addOption(Option.builder("b").build());
        options1.addOptionGroup(group1);
        options1.addOption(Option.builder("x").build());
        options1.addOption(Option.builder("y").build());

        final Options options2 = new Options();
        final OptionGroup group2 = new OptionGroup();
        group2.addOption(Option.builder("x").type(Integer.class).build());
        group2.addOption(Option.builder("b").type(Integer.class).build());
        options2.addOptionGroup(group2);
        options2.addOption(Option.builder("c").build());

        assertThrows(IllegalArgumentException.class, () -> options1.addOptions(options2));
    }

    @Test
    public void testAddNonConflictingOptions() {
        final Options options1 = new Options();
        final OptionGroup group1 = new OptionGroup();
        group1.addOption(Option.builder("a").build());
        group1.addOption(Option.builder("b").build());
        options1.addOptionGroup(group1);
        options1.addOption(Option.builder("x").build());
        options1.addOption(Option.builder("y").build());

        final Options options2 = new Options();
        final OptionGroup group2 = new OptionGroup();
        group2.addOption(Option.builder("c").type(Integer.class).build());
        group2.addOption(Option.builder("d").type(Integer.class).build());
        options2.addOptionGroup(group2);
        options1.addOption(Option.builder("e").build());
        options1.addOption(Option.builder("f").build());

        Options underTest = new Options();
        underTest.addOptions(options1);
        underTest.addOptions(options2);

        List<OptionGroup> expected = Arrays.asList(group1, group2);
        assertTrue(expected.size() == underTest.getOptionGroups().size() && expected.containsAll(underTest.getOptionGroups()));
        Set<Option> expectOpt = new HashSet<>();
        expectOpt.addAll(options1.getOptions());
        expectOpt.addAll(options2.getOptions());
        assertEquals(8, expectOpt.size());
        assertTrue(expectOpt.size() == underTest.getOptions().size() && expectOpt.containsAll(underTest.getOptions()));
    }

    @Test
    public void testHelpOptions() {
        OptionBuilder.withLongOpt("long-only1");
        final Option longOnly1 = OptionBuilder.create();
        OptionBuilder.withLongOpt("long-only2");
        final Option longOnly2 = OptionBuilder.create();
        final Option shortOnly1 = OptionBuilder.create("1");
        final Option shortOnly2 = OptionBuilder.create("2");
        OptionBuilder.withLongOpt("bothA");
        final Option bothA = OptionBuilder.create("a");
        OptionBuilder.withLongOpt("bothB");
        final Option bothB = OptionBuilder.create("b");

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
        OptionBuilder.isRequired();
        options.addOption(OptionBuilder.create("f"));
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
        OptionBuilder.isRequired();
        options.addOption(OptionBuilder.create("f"));
        OptionBuilder.isRequired();
        options.addOption(OptionBuilder.create("x"));
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
