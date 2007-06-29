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

import junit.framework.TestCase;

/**
 * @author brianegge
 */
public class OptionTest extends TestCase {

   private static class TestOption extends Option {
        public TestOption(String opt, boolean hasArg, String description) throws IllegalArgumentException {
            super(opt, hasArg, description);
        }
        public boolean addValue(String value) {
            addValueForProcessing(value);
            return true;
        }
   }

   public void testClear() {
       TestOption option = new TestOption("x", true, "");
       assertEquals(0, option.getValuesList().size());
       option.addValue("a");
       assertEquals(1, option.getValuesList().size());
       option.clearValues();
       assertEquals(0, option.getValuesList().size());
   }

    // See http://issues.apache.org/jira/browse/CLI-21
    public void testClone() throws CloneNotSupportedException {
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

    private static class DefaultOption extends Option {

        private final String defaultValue;

        public DefaultOption(String opt, String description, String defaultValue) throws IllegalArgumentException {
            super(opt, true, description);
            this.defaultValue = defaultValue;
        }

        public String getValue() {
            return super.getValue() != null ? super.getValue() : defaultValue;
        }
    }

    public void testSubclass() throws CloneNotSupportedException {
        Option option = new DefaultOption("f", "file", "myfile.txt");
        Option clone = (Option) option.clone();
        assertEquals("myfile.txt", clone.getValue());
        assertEquals(DefaultOption.class, clone.getClass());
    }

}
