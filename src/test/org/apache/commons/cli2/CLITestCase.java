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
package org.apache.commons.cli2;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

public abstract class CLITestCase extends TestCase {

	public static List list() {
	    return Collections.EMPTY_LIST;
	}

	public static List list(final Object args[]) {
	    return new LinkedList(Arrays.asList(args));
	}

	public static List list(final Object arg0) {
	    return list(new Object[] { arg0 });
	}

	public static List list(final Object arg0, final Object arg1) {
	    return list(new Object[] { arg0, arg1 });
	}

	public static List list(final Object arg0, final Object arg1, final Object arg2) {
	    return list(new Object[] { arg0, arg1, arg2 });
	}

	public static List list(final Object arg0, final Object arg1, final Object arg2, final Object arg3) {
	    return list(new Object[] { arg0, arg1, arg2, arg3 });
	}

	public static List list(final Object arg0, final Object arg1, final Object arg2, final Object arg3, final Object arg4) {
	    return list(new Object[] { arg0, arg1, arg2, arg3, arg4 });
	}

	public static List list(final Object arg0, final Object arg1, final Object arg2, final Object arg3, final Object arg4, final Object arg5) {
	    return list(new Object[] { arg0, arg1, arg2, arg3, arg4, arg5 });
	}

	public static void assertListContentsEqual(final List expected, final List found) {
	
	    final Iterator e = expected.iterator();
	    final Iterator f = found.iterator();
	
	    while (e.hasNext() && f.hasNext()) {
	        assertEquals(e.next(), f.next());
	    }
	
	    if (e.hasNext()) {
	        fail("Expected more elements");
	    }
	
	    if (f.hasNext()) {
	        fail("Found more elements");
	    }
	}

	public static void assertContentsEqual(final Collection expected, final Collection found) {
	    assertTrue(expected.containsAll(found));
	    assertTrue(found.containsAll(expected));
	    assertEquals(expected.size(), found.size());
	}
}
