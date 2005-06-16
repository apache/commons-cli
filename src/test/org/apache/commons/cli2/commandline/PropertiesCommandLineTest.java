/**
 * Copyright 2004-2005 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
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

import java.util.Properties;

import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.CommandLineTestCase;

/**
 * @author Rob Oxspring
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class PropertiesCommandLineTest extends CommandLineTestCase {
	
	private Properties props = null;
	
	/* (non-Javadoc)
	 * @see org.apache.commons.cli2.CommandLineTest#createCommandLine()
	 */
	protected CommandLine createCommandLine() {
		props = new Properties();
		props.setProperty("--present","present value");
		props.setProperty("--alsopresent","");
		props.setProperty("--multiple","value 1|value 2|value 3");
		props.setProperty("--bool","true");
		
		props.setProperty("present","present property");
		return new PropertiesCommandLine(root,props,'|');
	}
	
	public void testToMakeEclipseSpotTheTestCase(){
		// nothing to test
	}
}
