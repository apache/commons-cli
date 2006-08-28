/**
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
package org.apache.commons.cli2;

import org.apache.commons.cli2.option.ArgumentTest;

/**
 * @author Rob Oxspring
 */
public abstract class WriteableCommandLineTestCase extends CommandLineTestCase {
	
	private WriteableCommandLine writeable;
	
	protected abstract WriteableCommandLine createWriteableCommandLine();
	
	/* (non-Javadoc)
	 * @see org.apache.commons.cli2.CommandLineTest#createCommandLine()
	 */
	protected final CommandLine createCommandLine() {
		final WriteableCommandLine cl = createWriteableCommandLine();
		cl.addOption(present);
		cl.addProperty("present","present property");
		cl.addSwitch(bool,true);
		cl.addValue(present,"present value");
		cl.addOption(multiple);
		cl.addValue(multiple,"value 1");
		cl.addValue(multiple,"value 2");
		cl.addValue(multiple,"value 3");
		return cl;
	}
	
	/*
	 * @see CommandLineTest#setUp()
	 */
	public void setUp() throws Exception {
		super.setUp();
		writeable = createWriteableCommandLine();
	}
	public final void testAddOption() {
		assertFalse(writeable.hasOption(present));
		writeable.addOption(present);
		assertTrue(writeable.hasOption(present));
	}
	public final void testAddValue() {
		assertFalse(writeable.hasOption(present));
		assertTrue(writeable.getValues(present).isEmpty());
		writeable.addValue(present,"value");
		assertContentsEqual(list("value"),writeable.getValues(present));
		
		// most options shouldn't appear due to adding values
		assertFalse(writeable.hasOption(present));
		
		final Argument arg = ArgumentTest.buildHostArgument();
		
		assertFalse(writeable.hasOption(arg));
		assertTrue(writeable.getValues(arg).isEmpty());
		writeable.addValue(arg,"value");
		assertContentsEqual(list("value"),writeable.getValues(arg));
		
		// Arguments should force the option present
		assertTrue(writeable.hasOption(arg));
	}
	public final void testAddSwitch() {
		assertFalse(writeable.hasOption(present));
		assertNull(writeable.getSwitch(present));
		writeable.addSwitch(present,true);
		assertEquals(Boolean.TRUE,writeable.getSwitch(present));
		assertTrue(writeable.hasOption(present));
	}
	public final void testAddProperty() {
		assertNull(writeable.getProperty("present"));
		writeable.addProperty("present","present value");
		assertEquals("present value",writeable.getProperty("present"));
	}
	public final void testLooksLikeOption() {
		//TODO Implement looksLikeOption().
	}
}
