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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

@SuppressWarnings("deprecation") // OptionBuilder is marked deprecated
public class OptionBuilderTest {
    @Test
    public void testBaseOptionCharOpt() {
        final Option base = OptionBuilder.withDescription("option description").create('o');

        assertEquals("o", base.getOpt());
        assertEquals("option description", base.getDescription());
        assertFalse(base.hasArg());
    }

    @Test
    public void testBaseOptionStringOpt() {
        final Option base = OptionBuilder.withDescription("option description").create("o");

        assertEquals("o", base.getOpt());
        assertEquals("option description", base.getDescription());
        assertFalse(base.hasArg());
    }

    @Test
    public void testBuilderIsResettedAlways() {
        try {
            OptionBuilder.withDescription("JUnit").create('"');
            fail("IllegalArgumentException expected");
        } catch (final IllegalArgumentException e) {
            // expected
        }
        assertNull("we inherited a description", OptionBuilder.create('x').getDescription());

        try {
            OptionBuilder.withDescription("JUnit").create();
            fail("IllegalArgumentException expected");
        } catch (final IllegalArgumentException e) {
            // expected
        }
        assertNull("we inherited a description", OptionBuilder.create('x').getDescription());
    }

    @Test
    public void testCompleteOption() {
        //@formatter:off
        final Option simple = OptionBuilder.withLongOpt("simple option")
                                     .hasArg()
                                     .isRequired()
                                     .hasArgs()
                                     .withType(Float.class)
                                     .withDescription("this is a simple option")
                                     .create('s');
        //@formatter:on

        assertEquals("s", simple.getOpt());
        assertEquals("simple option", simple.getLongOpt());
        assertEquals("this is a simple option", simple.getDescription());
        assertEquals(simple.getType(), Float.class);
        assertTrue(simple.hasArg());
        assertTrue(simple.isRequired());
        assertTrue(simple.hasArgs());
    }

    @Test
    public void testCreateIncompleteOption() {
        try {
            OptionBuilder.hasArg().create();
            fail("Incomplete option should be rejected");
        } catch (final IllegalArgumentException e) {
            // expected

            // implicitly reset the builder
            OptionBuilder.create("opt");
        }
    }

    @Test
    public void testIllegalOptions() {
        // bad single character option
        try {
            OptionBuilder.withDescription("option description").create('"');
            fail("IllegalArgumentException not caught");
        } catch (final IllegalArgumentException exp) {
            // success
        }

        // bad character in option string
        try {
            OptionBuilder.create("opt`");
            fail("IllegalArgumentException not caught");
        } catch (final IllegalArgumentException exp) {
            // success
        }

        // valid option
        try {
            OptionBuilder.create("opt");
            // success
        } catch (final IllegalArgumentException exp) {
            fail("IllegalArgumentException caught");
        }
    }

    @Test
    public void testOptionArgNumbers() {
        //@formatter:off
        final Option opt = OptionBuilder.withDescription("option description")
                                  .hasArgs(2)
                                  .create('o');
        //@formatter:on
        assertEquals(2, opt.getArgs());
    }

    @Test
    public void testSpecialOptChars() throws Exception {
        // '?'
        final Option opt1 = OptionBuilder.withDescription("help options").create('?');
        assertEquals("?", opt1.getOpt());

        // '@'
        final Option opt2 = OptionBuilder.withDescription("read from stdin").create('@');
        assertEquals("@", opt2.getOpt());

        // ' '
        try {
            OptionBuilder.create(' ');
            fail("IllegalArgumentException not caught");
        } catch (final IllegalArgumentException e) {
            // success
        }
    }

    @Test
    public void testTwoCompleteOptions() {
        //@formatter:off
        Option simple = OptionBuilder.withLongOpt("simple option")
                                     .hasArg()
                                     .isRequired()
                                     .hasArgs()
                                     .withType(Float.class)
                                     .withDescription("this is a simple option")
                                     .create('s');
        //@formatter:on

        assertEquals("s", simple.getOpt());
        assertEquals("simple option", simple.getLongOpt());
        assertEquals("this is a simple option", simple.getDescription());
        assertEquals(simple.getType(), Float.class);
        assertTrue(simple.hasArg());
        assertTrue(simple.isRequired());
        assertTrue(simple.hasArgs());

        //@formatter:off
        simple = OptionBuilder.withLongOpt("dimple option")
                              .hasArg()
                              .withDescription("this is a dimple option")
                              .create('d');
        //@formatter:on

        assertEquals("d", simple.getOpt());
        assertEquals("dimple option", simple.getLongOpt());
        assertEquals("this is a dimple option", simple.getDescription());
        assertEquals(String.class, simple.getType());
        assertTrue(simple.hasArg());
        assertFalse(simple.isRequired());
        assertFalse(simple.hasArgs());
    }
}
