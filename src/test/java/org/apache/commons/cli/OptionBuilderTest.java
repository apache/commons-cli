/*
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      https://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package org.apache.commons.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

@SuppressWarnings("deprecation") // OptionBuilder is marked deprecated
class OptionBuilderTest {

    @Test
    void testBaseOptionCharOpt() {
        OptionBuilder.withDescription("option description");
        final Option base = OptionBuilder.create('o');
        assertEquals("o", base.getOpt());
        assertEquals("option description", base.getDescription());
        assertFalse(base.hasArg());
    }

    @Test
    void testBaseOptionStringOpt() {
        OptionBuilder.withDescription("option description");
        final Option base = OptionBuilder.create("o");
        assertEquals("o", base.getOpt());
        assertEquals("option description", base.getDescription());
        assertFalse(base.hasArg());
    }

    @Test
    void testBuilderIsResettedAlways() {
        OptionBuilder.withDescription("JUnit");
        assertThrows(IllegalArgumentException.class, () -> OptionBuilder.create('"'));
        assertNull(OptionBuilder.create('x').getDescription(), "we inherited a description");
        assertThrows(IllegalStateException.class, (Executable) OptionBuilder::create);
        assertNull(OptionBuilder.create('x').getDescription(), "we inherited a description");
    }

    @Test
    void testCompleteOption() {
        OptionBuilder.withLongOpt("simple option");
        OptionBuilder.hasArg();
        OptionBuilder.isRequired();
        OptionBuilder.hasArgs();
        OptionBuilder.withType(Float.class);
        OptionBuilder.withDescription("this is a simple option");
        final Option simple = OptionBuilder.create('s');
        assertEquals("s", simple.getOpt());
        assertEquals("simple option", simple.getLongOpt());
        assertEquals("this is a simple option", simple.getDescription());
        assertEquals(simple.getType(), Float.class);
        assertTrue(simple.hasArg());
        assertTrue(simple.isRequired());
        assertTrue(simple.hasArgs());
    }

    @Test
    void testCreateIncompleteOption() {
        assertThrows(IllegalStateException.class, (Executable) OptionBuilder::create);
        // implicitly reset the builder
        OptionBuilder.create("opt");
    }

    @Test
    void testIllegalOptions() {
        OptionBuilder.withDescription("option description");
        // bad single character option
        assertThrows(IllegalArgumentException.class, () -> OptionBuilder.create('"'));
        // bad character in option string
        assertThrows(IllegalArgumentException.class, () -> OptionBuilder.create("opt`"));
        // valid option
        OptionBuilder.create("opt");
    }

    @Test
    void testOptionArgNumbers() {
        OptionBuilder.withDescription("option description");
        OptionBuilder.hasArgs(2);
        final Option opt = OptionBuilder.create('o');
        assertEquals(2, opt.getArgs());
    }

    @Test
    void testSpecialOptChars() throws Exception {
        OptionBuilder.withDescription("help options");
        // '?'
        final Option opt1 = OptionBuilder.create('?');
        assertEquals("?", opt1.getOpt());
        OptionBuilder.withDescription("read from stdin");
        // '@'
        final Option opt2 = OptionBuilder.create('@');
        assertEquals("@", opt2.getOpt());
        // ' '
        assertThrows(IllegalArgumentException.class, () -> OptionBuilder.create(' '));
    }

    @Test
    void testTwoCompleteOptions() {
        OptionBuilder.withLongOpt("simple option");
        OptionBuilder.hasArg();
        OptionBuilder.isRequired();
        OptionBuilder.hasArgs();
        OptionBuilder.withType(Float.class);
        OptionBuilder.withDescription("this is a simple option");
        Option simple = OptionBuilder.create('s');
        assertEquals("s", simple.getOpt());
        assertEquals("simple option", simple.getLongOpt());
        assertEquals("this is a simple option", simple.getDescription());
        assertEquals(simple.getType(), Float.class);
        assertTrue(simple.hasArg());
        assertTrue(simple.isRequired());
        assertTrue(simple.hasArgs());
        OptionBuilder.withLongOpt("dimple option");
        OptionBuilder.hasArg();
        OptionBuilder.withDescription("this is a dimple option");
        simple = OptionBuilder.create('d');
        assertEquals("d", simple.getOpt());
        assertEquals("dimple option", simple.getLongOpt());
        assertEquals("this is a dimple option", simple.getDescription());
        assertEquals(String.class, simple.getType());
        assertTrue(simple.hasArg());
        assertFalse(simple.isRequired());
        assertFalse(simple.hasArgs());
    }
}
