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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.apache.commons.cli2.Option;

/**
 * A CommandLine implementation using the Preferences API, useful when
 * constructing a complex DefaultingCommandLine
 *
 * This implementation uses the children of a single preference node to populate
 * the CommandLine.  Options are keyed from their preferred name and presence in
 * the Preferences object is taken as presence in the CommandLine.  Argument
 * values are taken from the Preference value and are optionally separated using
 * the separator char defined, at construction time.  Switch values can be
 * specified using a simple value of <code>true</code> or <code>false</code>;
 * obviously this means that Switches with Arguments are not supported by this
 * implementation.
 *
 * @see java.util.prefs.Preferences
 * @see org.apache.commons.cli2.commandline.DefaultingCommandLine
 * @see org.apache.commons.cli2.Option#getPreferredName() 
 */
public class PreferencesCommandLine extends CommandLineImpl {
	
	private static final char NUL = '\0';
	private final Preferences preferences;
	private final Option root;
	private final char separator;
	
	/**
     * Creates a new PreferencesCommandLine using the specified root Option and
     * Preferences node.  Argument values will be separated using the char 0.
     * 
	 * @param root the CommandLine's root Option
	 * @param preferences the Preferences node to get values from
	 */
	public PreferencesCommandLine(final Option root, final Preferences preferences){
		this(root,preferences,NUL);
	}
	
    /**
     * Creates a new PreferencesCommandLine using the specified root Option,
     * Preferences node and value separator.
     * 
     * @param root the CommandLine's root Option
     * @param preferences the Preferences node to get values from
     * @param separator the character to split argument values
     */
	public PreferencesCommandLine(final Option root, final Preferences preferences, final char separator){
		this.root = root;
		this.preferences = preferences;
		this.separator = separator;
	}
	
	public boolean hasOption(Option option) {
		if(option==null){
			return false;
		}
		else{
			try {
				return Arrays.asList(preferences.keys()).contains(option.getPreferredName());
			} catch (BackingStoreException e) {
				return false;
			}
		}
	}

	public Option getOption(String trigger) {
		return root.findOption(trigger);
	}

	public List getValues(final Option option, final List defaultValues) {
		final String value = preferences.get(option.getPreferredName(),null);
		
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
		final String value = preferences.get(option.getPreferredName(),null);
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
		return preferences.get(property, defaultValue);
	}

	public Set getProperties() {
		try {
			return new HashSet(Arrays.asList(preferences.keys()));
		} catch (BackingStoreException e) {
			return Collections.EMPTY_SET;
		}
	}

	public List getOptions() {
		try {
			final List options = new ArrayList();
			final Iterator keys = Arrays.asList(preferences.keys()).iterator();
			while (keys.hasNext()) {
				final String trigger = (String) keys.next();
				final Option option = root.findOption(trigger);
				if (option != null) {
					options.add(option);
				}
			}
			return Collections.unmodifiableList(options);
		} catch (BackingStoreException e) {
			return Collections.EMPTY_LIST;
		}
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
