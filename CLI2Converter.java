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
package org.apache.commons.cli;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.validation.InvalidArgumentException;
import org.apache.commons.cli2.validation.Validator;

/**
 * A utility class for converting data structures version 1 to 
 * version 2 Option instances.
 */
public class CLI2Converter {
	
	private CLI2Converter(){
		// prevent creation of static utility class 
	}
	
	/**
	 * Creates a version 2 Option instance from a version 1 Option instance.
	 * 
	 * @param option1 the version 1 Option to convert
	 * @return a version 2 Option  
	 */
	public static Option option(final org.apache.commons.cli.Option option1){
		
		final DefaultOptionBuilder obuilder = new DefaultOptionBuilder();
		obuilder.withRequired(option1.isRequired());
		
		final String shortName = option1.getOpt();
		if(shortName!=null && !" ".equals(shortName)){
			obuilder.withShortName(shortName);
		}
		
		final String longName = option1.getLongOpt();
		if(longName!=null){
			obuilder.withLongName(longName);
		}
		obuilder.withId(option1.getId());
		
		final String description = option1.getDescription();
		if(description!=null){
			obuilder.withDescription(description);
		}
		
		if(option1.hasArg()){
			final ArgumentBuilder abuilder = new ArgumentBuilder();
			final String argName = option1.getArgName();
			abuilder.withName(argName);
			abuilder.withMaximum(option1.getArgs());
			if(option1.hasValueSeparator()){
				abuilder.withSubsequentSeparator(option1.getValueSeparator());
			}
			if(option1.hasOptionalArg()){
				abuilder.withMinimum(0);
			}
			else{
				//TODO check what non-optional arg means
				abuilder.withMinimum(option1.getArgs());
			}
			
			final Object type = option1.getType();
			if(type!=null){
				abuilder.withValidator(new TypeHandlerValidator(type));
			}
			
			obuilder.withArgument(abuilder.create());
		}
		
		return obuilder.create();
	}
	
	/**
	 * Creates a version 2 Group instance from a version 1 OptionGroup instance.
	 * 
	 * @param optionGroup1 the version 1 OptionGroup to convert
	 * @return a version 2 Group
	 */
	public static Group group(final OptionGroup optionGroup1){
		
		final GroupBuilder gbuilder = new GroupBuilder();
		
		for(final Iterator i = optionGroup1.getOptions().iterator();i.hasNext();){
			final org.apache.commons.cli.Option option1 = (org.apache.commons.cli.Option)i.next();
			final Option option2 = option(option1);
			gbuilder.withOption(option2);
		}
		
		gbuilder.withMaximum(1);
		
		if(optionGroup1.isRequired()){
			gbuilder.withMinimum(1);
		}
		
		return gbuilder.create();
	}
	
	/**
	 * Creates a version 2 Group instance from a version 1 Options instance.
	 * 
	 * @param options1 the version 1 Options to convert
	 * @return a version 2 Group
	 */
	public static Group group(final Options options1){
		
		final GroupBuilder gbuilder = new GroupBuilder();
		
		final Set optionGroups = new HashSet();
		
		for(final Iterator i = options1.getOptionGroups().iterator();i.hasNext();){
			final OptionGroup optionGroup1 = (OptionGroup)i.next();
			Group group = group(optionGroup1);
			gbuilder.withOption(group);
			optionGroups.add(optionGroup1);
		}
		
		for(final Iterator i = options1.getOptions().iterator();i.hasNext();){
			final org.apache.commons.cli.Option option1 = (org.apache.commons.cli.Option)i.next();
			if(!optionInAGroup(option1,optionGroups)){
				final Option option2 = option(option1);
				gbuilder.withOption(option2);
			}
		}
		
		return gbuilder.create();
	}

	private static boolean optionInAGroup(final org.apache.commons.cli.Option option1, final Set optionGroups) {
		for (Iterator i = optionGroups.iterator(); i.hasNext();) {
			OptionGroup group = (OptionGroup) i.next();
			if(group.getOptions().contains(option1)){
				return true;
			}
		}
		return false;
	}
}

class TypeHandlerValidator implements Validator{
	
	private final Object type;
	
	/**
     * Creates a new Validator using the TypeHandler class.
     * 
     * @see TypeHandler
	 * @param type The required type for valid elements
	 */
	public TypeHandlerValidator(final Object type){
		this.type = type;
	}
	
	/* (non-Javadoc)
	 * @see org.apache.commons.cli2.validation.Validator#validate(java.util.List)
	 */
	public void validate(final List values) throws InvalidArgumentException {
		final ListIterator i = values.listIterator();
		while(i.hasNext()){
			final String value = (String)i.next();
			final Object converted = TypeHandler.createValue(value,type);
			if(converted==null){
				throw new InvalidArgumentException("Unable to understand value: " + value);
			}
			i.set(converted);
		}
	}
}