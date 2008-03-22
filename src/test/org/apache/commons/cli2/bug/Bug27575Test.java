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
package org.apache.commons.cli2.bug;

import java.util.Iterator;

import junit.framework.TestCase;

import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.builder.PatternBuilder;
import org.apache.commons.cli2.option.GroupImpl;

public class Bug27575Test extends TestCase {

	public void testRequiredOptions(){
		PatternBuilder builder = new PatternBuilder();
		builder.withPattern("hc!<");
		Option option = builder.create();
		assertTrue(option instanceof GroupImpl);

		GroupImpl group = (GroupImpl)option;
		Iterator i = group.getOptions().iterator();
		assertEquals("[-h]",i.next().toString());
		assertEquals("-c <arg>",i.next().toString());
		assertFalse(i.hasNext());
	}
}