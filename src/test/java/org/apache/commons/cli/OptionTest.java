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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class OptionTest
{
    private static class TestOption extends Option
    {
        private static final long serialVersionUID = 1L;

        public TestOption(String opt, boolean hasArg, String description) throws IllegalArgumentException
        {
            super(opt, hasArg, description);
        }

        @Override
        public boolean addValue(String value)
        {
            addValueForProcessing(value);
            return true;
        }
    }

    @Test
    public void testClear()
    {
        TestOption option = new TestOption("x", true, "");
        assertEquals(0, option.getValuesList().size());
        option.addValue("a");
        assertEquals(1, option.getValuesList().size());
        option.clearValues();
        assertEquals(0, option.getValuesList().size());
    }

    // See http://issues.apache.org/jira/browse/CLI-21
    @Test
    public void testClone()
    {
        TestOption a = new TestOption("a", true, "");
        TestOption b = (TestOption) a.clone();
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

    private static class DefaultOption extends Option
    {
        private static final long serialVersionUID = 1L;

        private final String defaultValue;

        public DefaultOption(String opt, String description, String defaultValue) throws IllegalArgumentException
        {
            super(opt, true, description);
            this.defaultValue = defaultValue;
        }

        @Override
        public String getValue()
        {
            return super.getValue() != null ? super.getValue() : defaultValue;
        }
    }

    @Test
    public void testSubclass()
    {
        Option option = new DefaultOption("f", "file", "myfile.txt");
        Option clone = (Option) option.clone();
        assertEquals("myfile.txt", clone.getValue());
        assertEquals(DefaultOption.class, clone.getClass());
    }

    @Test
    public void testHasArgName()
    {
        Option option = new Option("f", null);

        option.setArgName(null);
        assertFalse(option.hasArgName());

        option.setArgName("");
        assertFalse(option.hasArgName());

        option.setArgName("file");
        assertTrue(option.hasArgName());
    }

    @Test
    public void testHasArgs()
    {
        Option option = new Option("f", null);

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
    public void testGetValue()
    {
        Option option = new Option("f", null);
        option.setArgs(Option.UNLIMITED_VALUES);

        assertEquals("default", option.getValue("default"));
        assertEquals(null, option.getValue(0));

        option.addValueForProcessing("foo");
        
        assertEquals("foo", option.getValue());
        assertEquals("foo", option.getValue(0));
        assertEquals("foo", option.getValue("default"));
    }
    
    @Test
    public void testBuilderMethods()
    {
        char defaultSeparator = (char) 0;

        checkOption(Option.builder("a").desc("desc").build(),
            "a", "desc", null, Option.UNINITIALIZED, null, false, false, defaultSeparator, String.class);
        checkOption(Option.builder("a").desc("desc").build(),
            "a", "desc", null, Option.UNINITIALIZED, null, false, false, defaultSeparator, String.class);
        checkOption(Option.builder("a").desc("desc").longOpt("aaa").build(),
            "a", "desc", "aaa", Option.UNINITIALIZED, null, false, false, defaultSeparator, String.class);
        checkOption(Option.builder("a").desc("desc").hasArg(true).build(),
            "a", "desc", null, 1, null, false, false, defaultSeparator, String.class);
        checkOption(Option.builder("a").desc("desc").hasArg(false).build(),
            "a", "desc", null, Option.UNINITIALIZED, null, false, false, defaultSeparator, String.class);
        checkOption(Option.builder("a").desc("desc").hasArg(true).build(),
            "a", "desc", null, 1, null, false, false, defaultSeparator, String.class);
        checkOption(Option.builder("a").desc("desc").numberOfArgs(3).build(),
            "a", "desc", null, 3, null, false, false, defaultSeparator, String.class);
        checkOption(Option.builder("a").desc("desc").required(true).build(),
            "a", "desc", null, Option.UNINITIALIZED, null, true, false, defaultSeparator, String.class);
        checkOption(Option.builder("a").desc("desc").required(false).build(),
            "a", "desc", null, Option.UNINITIALIZED, null, false, false, defaultSeparator, String.class);

        checkOption(Option.builder("a").desc("desc").argName("arg1").build(),
            "a", "desc", null, Option.UNINITIALIZED, "arg1", false, false, defaultSeparator, String.class);
        checkOption(Option.builder("a").desc("desc").optionalArg(false).build(),
            "a", "desc", null, Option.UNINITIALIZED, null, false, false, defaultSeparator, String.class);
        checkOption(Option.builder("a").desc("desc").optionalArg(true).build(),
            "a", "desc", null, Option.UNINITIALIZED, null, false, true, defaultSeparator, String.class);
        checkOption(Option.builder("a").desc("desc").valueSeparator(':').build(),
            "a", "desc", null, Option.UNINITIALIZED, null, false, false, ':', String.class);
        checkOption(Option.builder("a").desc("desc").type(Integer.class).build(),
            "a", "desc", null, Option.UNINITIALIZED, null, false, false, defaultSeparator, Integer.class);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testBuilderInsufficientParams1()
    {
        Option.builder().desc("desc").build();
    }

    @Test(expected=IllegalArgumentException.class)
    public void testBuilderInsufficientParams2()
    {
        Option.builder(null).desc("desc").build();
    }

    private static void checkOption(Option option, String opt, String description, String longOpt, int numArgs,
                                    String argName,  boolean required, boolean optionalArg,
                                    char valueSeparator, Class<?> cls)
    {
        assertEquals(opt, option.getOpt());
        assertEquals(description, option.getDescription());
        assertEquals(longOpt, option.getLongOpt());
        assertEquals(numArgs, option.getArgs());
        assertEquals(argName, option.getArgName());
        assertEquals(required, option.isRequired());

        assertEquals(optionalArg, option.hasOptionalArg());
        assertEquals(valueSeparator, option.getValueSeparator());
        assertEquals(cls,  option.getType());
    }
    
}
