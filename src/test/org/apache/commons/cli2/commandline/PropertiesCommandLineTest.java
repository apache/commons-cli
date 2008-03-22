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
package org.apache.commons.cli2.commandline;

import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.CommandLineTestCase;

/**
 * @author Rob Oxspring
 */
public class PropertiesCommandLineTest
    extends CommandLineTestCase {
    private Properties props = null;

    protected CommandLine createCommandLine() {
        props = new Properties();
        props.setProperty("--present", "present value");
        props.setProperty("--alsopresent", "");
        props.setProperty("--multiple", "value 1|value 2|value 3");
        props.setProperty("--bool", "true");

        props.setProperty("present", "present property");
    	return new PropertiesCommandLine(root, props, '|');
    }

    protected CommandLine createCommandLineNoSep() {
        props = new Properties();
        props.setProperty("--present", "present value");
        props.setProperty("--alsopresent", "");
        props.setProperty("--multiple", "value 1|value 2|value 3");
        props.setProperty("--bool", "false");

        props.setProperty("present", "present property");
    	return new PropertiesCommandLine(root, props);
    }

    public void testPropertyValues() {
        // nothing to test
    	CommandLine cmdline = createCommandLine();

    	assertEquals("wrong value", "present value", cmdline.getValue("--present"));
    	assertEquals("wrong value", "present value", cmdline.getValue("--alsopresent"));
    	assertEquals("wrong # of values", 3, cmdline.getValues("--multiple").size());
    	assertEquals("wrong value 1", "value 1", cmdline.getValues("--multiple").get(0));
    	assertEquals("wrong value 2", "value 2", cmdline.getValues("--multiple").get(1));
    	assertEquals("wrong value 3", "value 3", cmdline.getValues("--multiple").get(2));
    }

    public void testNoSeparator() {
        // nothing to test
    	CommandLine cmdline = createCommandLineNoSep();

    	assertEquals("wrong value", "present value", cmdline.getValue("--present"));
    	assertEquals("wrong value", "present value", cmdline.getValue("--alsopresent"));
    	assertEquals("wrong # of values", 1, cmdline.getValues("--multiple").size());
    	assertEquals("wrong value", "value 1|value 2|value 3", cmdline.getValue("--multiple"));
    	assertFalse("expected a false", cmdline.getSwitch("--bool").booleanValue());
    }

    public void testNullOption() {
        // nothing to test
    	CommandLine cmdline = createCommandLine();

    	assertFalse("should not find null option", cmdline.hasOption((String) null));
    	assertTrue("expected a true", cmdline.getSwitch("--bool").booleanValue());
    }

    public void testPropertyTriggers() {
        // nothing to test
    	CommandLine cmdline = createCommandLine();

    	Set triggers = cmdline.getOptionTriggers();
        Iterator iter = triggers.iterator();
        assertEquals("wrong # of triggers", 4, triggers.size());
        assertTrue("cannot find trigger", triggers.contains("--bool"));
        assertTrue("cannot find trigger", triggers.contains("--present"));
        assertTrue("cannot find trigger", triggers.contains("--multiple"));
        assertTrue("cannot find trigger", triggers.contains("--alsopresent"));

    	assertFalse("should not find null option", cmdline.hasOption((String) null));
    	assertTrue("expected a true", cmdline.getSwitch("--bool").booleanValue());
    }
}
