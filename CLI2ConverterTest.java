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

import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.builder.PatternBuilder;
import org.apache.commons.cli2.option.GroupImpl;

import junit.framework.TestCase;

public class CLI2ConverterTest extends TestCase {
	
	private Options aceOptions;
	private OptionGroup aceOptionGroup;
	private Options abcdeOptions;
	private Options pattern;
	
	private Option a;
	private Option bbb;
	private Option c;
	private Option ddd;
	private Option e;
	private Option f;
	private Option g;
	private Option h;
	private Option i;
	
	public void setUp(){
		a = OptionBuilder.withDescription("A description").create('a');
		bbb = OptionBuilder.create("bbb");
		c = OptionBuilder.withLongOpt("ccc").create('c');
		ddd = OptionBuilder.withLongOpt("ddd").create();
		e = OptionBuilder.isRequired(true).create("e");
		f = OptionBuilder.hasArg().withArgName("argument").create('f');
		g = OptionBuilder.hasArgs(5).create('g');
		h = OptionBuilder.hasOptionalArg().create('h');
		i = OptionBuilder.hasOptionalArgs(5).create('i');

		aceOptions = new Options();
		aceOptions.addOption(OptionBuilder.create('a'));
		aceOptions.addOption(OptionBuilder.create('c'));
		aceOptions.addOption(OptionBuilder.create('e'));
		
		aceOptionGroup = new OptionGroup();
		aceOptionGroup.addOption(OptionBuilder.create('a'));
		aceOptionGroup.addOption(OptionBuilder.create('c'));
		aceOptionGroup.addOption(OptionBuilder.create('e'));
		
		abcdeOptions = new Options();
		abcdeOptions.addOption(OptionBuilder.create('d'));
		OptionGroup og = new OptionGroup();
		og.addOption(OptionBuilder.create('a'));
		og.addOption(OptionBuilder.create('c'));
		og.addOption(OptionBuilder.create('e'));
		abcdeOptions.addOptionGroup(og);
		abcdeOptions.addOption(OptionBuilder.create('b'));
		
		pattern = PatternOptionBuilder.parsePattern("a%ce:");
	}
	
	/*
	 * Class to test for Option option(Option)
	 */
	public void testOption() {
		assertTrue(e.isRequired());
		
		assertEquals("A description",a.getDescription());
		assertEquals('a',a.getId());
		assertEquals("[-a]",CLI2Converter.option(a).toString());
		assertEquals("[-bbb]",CLI2Converter.option(bbb).toString());
		assertEquals("[-c (--ccc)]",CLI2Converter.option(c).toString());
		assertEquals("[--ddd]",CLI2Converter.option(ddd).toString());
		assertEquals("-e",CLI2Converter.option(e).toString());
		assertEquals("[-f <argument>]",CLI2Converter.option(f).toString());
		assertEquals("[-g <arg1> <arg2> <arg3> <arg4> <arg5>]",CLI2Converter.option(g).toString());
		assertEquals("[-h [<arg>]]",CLI2Converter.option(h).toString());
		assertEquals("[-i [<arg1> [<arg2> [<arg3> [<arg4> [<arg5>]]]]]]",CLI2Converter.option(i).toString());
	}
	/*
	 * Class to test for Group group(OptionGroup)
	 */
	public void testGroupOptionGroup() {
		GroupImpl group;
		
		group = (GroupImpl)CLI2Converter.group(aceOptionGroup);
		assertEquals("[-a|-c|-e]",group.toString());
		assertEquals(0,group.getMinimum());
		assertEquals(1,group.getMaximum());
		
		aceOptionGroup.setRequired(true);
		group = (GroupImpl)CLI2Converter.group(aceOptionGroup);
		assertEquals("-a|-c|-e",group.toString());
		assertEquals(1,group.getMinimum());
		assertEquals(1,group.getMaximum());
	}
	/*
	 * Class to test for Group group(Options)
	 */
	public void testGroupOptions() {
		
		GroupImpl group;
		
		group = (GroupImpl)CLI2Converter.group(aceOptions);
		assertEquals("[-a|-c|-e]",group.toString());
		assertEquals(0,group.getMinimum());
		assertEquals(Integer.MAX_VALUE,group.getMaximum());
		
		group = (GroupImpl)CLI2Converter.group(abcdeOptions);
		
		assertEquals("[-a|-c|-e|-d|-b]",group.toString());
		assertEquals(0,group.getMinimum());
		assertEquals(Integer.MAX_VALUE,group.getMaximum());
		
		group = (GroupImpl)CLI2Converter.group(pattern);
		assertEquals("[-a <arg>|-c|-e <arg>]",group.toString());
		assertEquals(0,group.getMinimum());
		assertEquals(Integer.MAX_VALUE,group.getMaximum());
	}
}
