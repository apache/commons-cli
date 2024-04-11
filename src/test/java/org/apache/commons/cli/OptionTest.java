/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.jupiter.api.Test;

public class OptionTest {

    private static final class DefaultOption extends Option {
        private static final long serialVersionUID = 1L;

        private final String defaultValue;

        DefaultOption(final String opt, final String description, final String defaultValue) throws IllegalArgumentException {
            super(opt, true, description);
            this.defaultValue = defaultValue;
        }

        @Override
        public String getValue() {
            return super.getValue() != null ? super.getValue() : defaultValue;
        }
    }

    private static final class TestOption extends Option {
        private static final long serialVersionUID = 1L;

        TestOption(final String opt, final boolean hasArg, final String description) throws IllegalArgumentException {
            super(opt, hasArg, description);
        }

        @Override
        public boolean addValue(final String value) {
            processValue(value);
            return true;
        }
    }

    private static void checkOption(final Option option, final String opt, final String description, final String longOpt, final int numArgs,
            final String argName, final boolean required, final boolean optionalArg, final char valueSeparator, final Class<?> cls, final String deprecatedDesc,
            final Boolean deprecatedForRemoval, final String deprecatedSince) {
        assertEquals(opt, option.getOpt());
        assertEquals(description, option.getDescription());
        assertEquals(longOpt, option.getLongOpt());
        assertEquals(numArgs, option.getArgs());
        assertEquals(argName, option.getArgName());
        assertEquals(required, option.isRequired());

        assertEquals(optionalArg, option.hasOptionalArg());
        assertEquals(numArgs > 0, option.hasArg());
        assertEquals(numArgs > 0, option.acceptsArg());
        assertEquals(valueSeparator, option.getValueSeparator());
        assertEquals(cls, option.getType());
        if (deprecatedDesc != null) {
            assertEquals(deprecatedDesc, option.getDeprecated().getDescription());
        }
        if (deprecatedForRemoval != null) {
            assertEquals(deprecatedForRemoval, option.getDeprecated().isForRemoval());
        }
        if (deprecatedSince != null) {
            assertEquals(deprecatedSince, option.getDeprecated().getSince());
        }
    }

    private Option roundTrip(final Option o) throws IOException, ClassNotFoundException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        final ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        final ObjectInputStream ois = new ObjectInputStream(bais);
        return (Option) ois.readObject();
    }

    @Test
    public void testAddValue() {
        final Option option = new Option("f", null);
        assertThrows(UnsupportedOperationException.class, () -> option.addValue(""));
        assertThrows(IllegalArgumentException.class, () -> option.processValue(""));
    }

    @Test
    public void testBuilderEmpty() {
        assertThrows(IllegalArgumentException.class, () -> Option.builder().build());
    }

    @Test
    public void testBuilderInsufficientParams1() {
        assertThrows(IllegalArgumentException.class, () -> Option.builder().desc("desc").build());
    }

    @Test
    public void testBuilderInsufficientParams2() {
        assertThrows(IllegalArgumentException.class, () -> Option.builder(null).desc("desc").build());
    }

    @Test
    public void testBuilderInvalidOptionName0() {
        assertThrows(IllegalArgumentException.class, () -> Option.builder().option(null).build());
        assertThrows(IllegalArgumentException.class, () -> Option.builder().option(""));
        assertThrows(IllegalArgumentException.class, () -> Option.builder().option(" "));
    }

    @Test
    public void testBuilderInvalidOptionName1() {
        assertThrows(IllegalArgumentException.class, () -> Option.builder().option("invalid?"));
    }

    @Test
    public void testBuilderInvalidOptionName2() {
        assertThrows(IllegalArgumentException.class, () -> Option.builder().option("invalid@"));
    }

    @Test
    public void testBuilderInvalidOptionName3() {
        assertThrows(IllegalArgumentException.class, () -> Option.builder("invalid?"));
    }

    @Test
    public void testBuilderInvalidOptionName4() {
        assertThrows(IllegalArgumentException.class, () -> Option.builder("invalid@"));
    }

    @Test
    public void testBuilderMethods() {
        final char defaultSeparator = (char) 0;

        checkOption(Option.builder("a").desc("desc").build(), "a", "desc", null, Option.UNINITIALIZED, null, false, false, defaultSeparator, String.class, null,
                null, null);
        checkOption(Option.builder("a").desc("desc").build(), "a", "desc", null, Option.UNINITIALIZED, null, false, false, defaultSeparator, String.class, null,
                null, null);
        checkOption(Option.builder("a").desc("desc").longOpt("aaa").build(), "a", "desc", "aaa", Option.UNINITIALIZED, null, false, false, defaultSeparator,
                String.class, null, null, null);
        checkOption(Option.builder("a").desc("desc").hasArg(true).build(), "a", "desc", null, 1, null, false, false, defaultSeparator, String.class, null, null,
                null);
        checkOption(Option.builder("a").desc("desc").hasArg(false).build(), "a", "desc", null, Option.UNINITIALIZED, null, false, false, defaultSeparator,
                String.class, null, null, null);
        checkOption(Option.builder("a").desc("desc").hasArg(true).build(), "a", "desc", null, 1, null, false, false, defaultSeparator, String.class, null, null,
                null);
        checkOption(Option.builder("a").desc("desc").numberOfArgs(3).build(), "a", "desc", null, 3, null, false, false, defaultSeparator, String.class, null,
                null, null);
        checkOption(Option.builder("a").desc("desc").required(true).build(), "a", "desc", null, Option.UNINITIALIZED, null, true, false, defaultSeparator,
                String.class, null, null, null);
        checkOption(Option.builder("a").desc("desc").required(false).build(), "a", "desc", null, Option.UNINITIALIZED, null, false, false, defaultSeparator,
                String.class, null, null, null);

        checkOption(Option.builder("a").desc("desc").argName("arg1").build(), "a", "desc", null, Option.UNINITIALIZED, "arg1", false, false, defaultSeparator,
                String.class, null, null, null);
        checkOption(Option.builder("a").desc("desc").optionalArg(false).build(), "a", "desc", null, Option.UNINITIALIZED, null, false, false, defaultSeparator,
                String.class, null, null, null);
        checkOption(Option.builder("a").desc("desc").optionalArg(true).build(), "a", "desc", null, 1, null, false, true, defaultSeparator, String.class, null,
                null, null);
        checkOption(Option.builder("a").desc("desc").valueSeparator(':').build(), "a", "desc", null, Option.UNINITIALIZED, null, false, false, ':',
                String.class, null, null, null);
        checkOption(Option.builder("a").desc("desc").type(Integer.class).build(), "a", "desc", null, Option.UNINITIALIZED, null, false, false, defaultSeparator,
                Integer.class, null, null, null);
        checkOption(Option.builder("a").desc("desc").type(null).build(), "a", "desc", null, Option.UNINITIALIZED, null, false, false, defaultSeparator,
                String.class, null, null, null);
        checkOption(Option.builder().option("a").desc("desc").type(Integer.class).build(), "a", "desc", null, Option.UNINITIALIZED, null, false, false,
                defaultSeparator, Integer.class, null, null, null);
        // Deprecated
        checkOption(Option.builder().option("a").desc("desc").type(Integer.class).deprecated().build(), "a", "desc", null, Option.UNINITIALIZED, null, false,
                false, defaultSeparator, Integer.class, "", false, "");
        checkOption(Option.builder().option("a").desc("desc").type(Integer.class).deprecated(DeprecatedAttributes.builder().get()).build(), "a", "desc", null,
                Option.UNINITIALIZED, null, false, false, defaultSeparator, Integer.class, "", false, "");
        checkOption(Option.builder().option("a").desc("desc").type(Integer.class).deprecated(DeprecatedAttributes.builder().setDescription("X").get()).build(),
                "a", "desc", null, Option.UNINITIALIZED, null, false, false, defaultSeparator, Integer.class, "X", false, "");
        checkOption(
                Option.builder().option("a").desc("desc").type(Integer.class)
                        .deprecated(DeprecatedAttributes.builder().setDescription("X").setForRemoval(true).get()).build(),
                "a", "desc", null, Option.UNINITIALIZED, null, false, false, defaultSeparator, Integer.class, "X", true, "");
        checkOption(
                Option.builder().option("a").desc("desc").type(Integer.class)
                        .deprecated(DeprecatedAttributes.builder().setDescription("X").setForRemoval(true).setSince("2.0").get()).build(),
                "a", "desc", null, Option.UNINITIALIZED, null, false, false, defaultSeparator, Integer.class, "X", true, "2.0");
    }

    @Test
    public void testClear() {
        final TestOption option = new TestOption("x", true, "");
        assertEquals(0, option.getValuesList().size());
        option.addValue("a");
        assertEquals(1, option.getValuesList().size());
        option.clearValues();
        assertEquals(0, option.getValuesList().size());
    }

    // See https://issues.apache.org/jira/browse/CLI-21
    @Test
    public void testClone() {
        final TestOption a = new TestOption("a", true, "");
        final TestOption b = (TestOption) a.clone();
        assertEquals(a, b);
        assertNotSame(a, b);
        a.setDescription("a");
        assertEquals("", b.getDescription());
        b.setArgs(2);
        b.addValue("b1");
        b.addValue("b2");
        assertEquals(1, a.getArgs());
        assertEquals(0, a.getValuesList().size());
        assertEquals(2, b.getValues().length);
    }

    @Test
    public void testEquals() {
        final Option option1a = new Option("1", null);
        final Option option1b = new Option("1", null);
        final Option option2 = new Option("2", null);
        assertEquals(option1a, option1a);
        assertEquals(option1a, option1b);
        assertEquals(option1b, option1a);
        assertNotEquals(option1a, option2);
        assertNotEquals(option1b, option2);
        assertNotEquals(option2, option1a);
        assertNotEquals(option2, "");
    }

    @Test
    public void testGetValue() {
        final Option option = new Option("f", null);
        option.setArgs(Option.UNLIMITED_VALUES);

        assertEquals("default", option.getValue("default"));
        assertNull(option.getValue(0));

        option.processValue("foo");

        assertEquals("foo", option.getValue());
        assertEquals("foo", option.getValue(0));
        assertEquals("foo", option.getValue("default"));
    }

    @Test
    public void testHasArgName() {
        final Option option = new Option("f", null);

        option.setArgName(null);
        assertFalse(option.hasArgName());

        option.setArgName("");
        assertFalse(option.hasArgName());

        option.setArgName("file");
        assertTrue(option.hasArgName());
    }

    @Test
    public void testHasArgs() {
        final Option option = new Option("f", null);

        option.setArgs(0);
        assertFalse(option.hasArgs());

        option.setArgs(1);
        assertFalse(option.hasArgs());

        option.setArgs(10);
        assertTrue(option.hasArgs());

        option.setArgs(Option.UNLIMITED_VALUES);
        assertTrue(option.hasArgs());

        option.setArgs(Option.UNINITIALIZED);
        assertFalse(option.hasArgs());
    }

    @Test
    public void testHashCode() {
        assertNotEquals(Option.builder("test").build().hashCode(), Option.builder("test2").build().hashCode());
        assertNotEquals(Option.builder("test").build().hashCode(), Option.builder().longOpt("test").build().hashCode());
        assertNotEquals(Option.builder("test").build().hashCode(), Option.builder("test").longOpt("long test").build().hashCode());
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        final Option option = Option.builder("o").type(TypeHandlerTest.Instantiable.class).build();
        assertEquals(Converter.DEFAULT, option.getConverter());
        Option roundtrip = roundTrip(option);
        assertEquals(Converter.DEFAULT, roundtrip.getConverter());
        // verify unregistered class converters and verifiers get reset to default.
        // converters are NOT Serializable, use a serialization proxy if you want that.
        option.setConverter(Converter.DATE);
        roundtrip = roundTrip(option);
        assertEquals(Converter.DEFAULT, roundtrip.getConverter());
        // verify registered class converters and verifiers do not get reset to default.
        // converters are NOT Serializable, use a serialization proxy if you want that.
        // verify earlier values still set.
        assertEquals(Converter.DATE, option.getConverter());
        roundtrip = roundTrip(option);
        assertEquals(Converter.DEFAULT, roundtrip.getConverter());
    }

    @Test
    public void testSubclass() {
        final Option option = new DefaultOption("f", "file", "myfile.txt");
        final Option clone = (Option) option.clone();
        assertEquals("myfile.txt", clone.getValue());
        assertEquals(DefaultOption.class, clone.getClass());
    }

    @Test
    public void testTypeClass() {
        final Option option = new Option("f", null);
        assertEquals(String.class, option.getType());
        option.setType(CharSequence.class);
        assertEquals(CharSequence.class, option.getType());
    }

    @Test
    public void testTypeObject() {
        final Option option = new Option("f", null);
        assertEquals(String.class, option.getType());
        @SuppressWarnings("cast")
        final Object type = CharSequence.class; // Do NOT remove cast
        option.setType(type);
        assertEquals(CharSequence.class, option.getType());
    }
}
