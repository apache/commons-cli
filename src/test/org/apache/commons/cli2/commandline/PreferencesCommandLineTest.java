/*
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

import java.util.prefs.Preferences;

import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.CommandLineTestCase;

/**
 * @author Rob Oxspring
 */
public class PreferencesCommandLineTest extends CommandLineTestCase {
	
	/* (non-Javadoc)
	 * @see org.apache.commons.cli2.CommandLineTest#createCommandLine()
	 */
	protected CommandLine createCommandLine() {
		// TODO Auto-generated method stub
		final Preferences props = Preferences.userNodeForPackage(PreferencesCommandLineTest.class);
		props.put("--present","present value");
		props.put("--alsopresent","");
		props.put("--multiple","value 1|value 2|value 3");
		props.put("--bool","true");
		
		props.put("present","present property");
		
		return new PreferencesCommandLine(root,props,'|');
	}
	
	public void testToMakeEclipseSpotTheTestCase(){
		// nothing to test
	}
}
