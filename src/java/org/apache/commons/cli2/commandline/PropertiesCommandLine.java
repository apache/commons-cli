/**
 * Copyright 2004 The Apache Software Foundation
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.cli2.Option;

/**
 * A CommandLine implementation using a java Properties instance, useful for
 * constructing a complex DefaultingCommandLine
 *
 * Options are keyed from their property name and presence in the Properties
 * instance is taken as presence in the CommandLine.  Argument values are taken
 * from the property value and are optionally separated using the separator
 * char, defined at construction time.  Switch values can be specified using a
 * simple value of <code>true</code> or <code>false</code>; obviously this means
 * that Switches with Arguments are not supported by this implementation.
 *
 * @see java.util.Properties
 * @see org.apache.commons.cli2.commandline.DefaultingCommandLine
 * @see org.apache.commons.cli2.Option#getPreferredName() 
 */
public class PropertiesCommandLine extends CommandLineImpl {
	
	private static final char NUL = '\0';
	private final Properties properties;
	private final Option root;
	private final char separator;
	
    /**
     * Creates a new PropertiesCommandLine using the specified root Option,
     * Properties instance.  The character 0 is used as the value separator.
     *
     * @param root the CommandLine's root Option
     * @param properties the Properties instance to get values from
     */
	public PropertiesCommandLine(final Option root, final Properties properties){
		this(root,properties,NUL);
	}
	
    /**
     * Creates a new PropertiesCommandLine using the specified root Option,
     * Properties instance and value separator.
     *
     * @param root the CommandLine's root Option
     * @param properties the Properties instance to get values from
     * @param separator the character to split argument values
     */
	public PropertiesCommandLine(final Option root, final Properties properties, final char separator){
		this.root = root;
		this.properties = properties;
		this.separator = separator;
	}
	

	public boolean hasOption(Option option) {
		if(option==null){
			return false;
		}
		else{
			return properties.containsKey(option.getPreferredName());
		}
	}

	public Option getOption(String trigger) {
		return root.findOption(trigger);
	}

	public List getValues(final Option option, final List defaultValues) {
		final String value = properties.getProperty(option.getPreferredName());
		
		if(value==null){
			return defaultValues;
		}
		else if(separator>NUL){
			final List values = new ArrayList();
			final StringTokenizer tokens = new StringTokenizer(value,String.valueOf(separator));
			
			while(tokens.hasMoreTokens()){
				values.add(tokens.nextToken());
			}
			
			return values;
		}
		else{
			return Collections.singletonList(value);
		}
	}

	public Boolean getSwitch(final Option option, final Boolean defaultValue) {
		final String value = properties.getProperty(option.getPreferredName());
		if("true".equals(value)){
			return Boolean.TRUE;
		}
		else if("false".equals(value)){
			return Boolean.FALSE;
		}
		else{
			return defaultValue;
		}
	}
	
	public String getProperty(final String property, final String defaultValue) {
		return properties.getProperty(property,defaultValue);
	}

	public Set getProperties() {
		return properties.keySet();
	}

	public List getOptions() {
		final List options = new ArrayList();
		final Iterator keys = properties.keySet().iterator();
		while(keys.hasNext()){
			final String trigger = (String)keys.next();
			final Option option = root.findOption(trigger);
			if(option!=null){
				options.add(option);
			}
		}
		return Collections.unmodifiableList(options);
	}

	public Set getOptionTriggers() {
		final Set triggers = new HashSet();
		final Iterator options = getOptions().iterator();
		while(options.hasNext()){
			final Option option = (Option)options.next();
			triggers.addAll(option.getTriggers());
		}
		return Collections.unmodifiableSet(triggers);
	}
}
