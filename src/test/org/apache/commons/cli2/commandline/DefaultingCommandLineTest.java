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

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.CommandLineTestCase;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.WriteableCommandLine;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;

/**
 * @author Rob Oxspring
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class DefaultingCommandLineTest
    extends CommandLineTestCase {
    private CommandLine first;
    private CommandLine second;
    private Option inFirst = new DefaultOptionBuilder().withLongName("infirst").create();
    private Option inBoth = new DefaultOptionBuilder().withLongName("inboth").create();
    private Option inSecond = new DefaultOptionBuilder().withLongName("insecond").create();

    /* (non-Javadoc)
     * @see org.apache.commons.cli2.CommandLineTest#createCommandLine()
     */
    protected final CommandLine createCommandLine() {
        final WriteableCommandLine writeable = new WriteableCommandLineImpl(root, new ArrayList());
        writeable.addOption(present);
        writeable.addProperty("present", "present property");
        writeable.addSwitch(bool, true);
        writeable.addValue(present, "present value");
        writeable.addOption(multiple);
        writeable.addValue(multiple, "value 1");
        writeable.addValue(multiple, "value 2");
        writeable.addValue(multiple, "value 3");

        final DefaultingCommandLine defaults = new DefaultingCommandLine();
        defaults.appendCommandLine(writeable);

        return defaults;
    }

    public void setUp()
        throws Exception {
        super.setUp();

        WriteableCommandLine writeable;

        writeable = new WriteableCommandLineImpl(root, new ArrayList());
        writeable.addOption(inFirst);
        writeable.addOption(inBoth);
        writeable.addProperty("infirst", "infirst first value");
        writeable.addProperty("inboth", "inboth first value");
        writeable.addSwitch(inFirst, true);
        writeable.addSwitch(inBoth, true);
        writeable.addValue(inFirst, "infirst first value 1");
        writeable.addValue(inFirst, "infirst first value 2");
        writeable.addValue(inBoth, "inboth first value 1");
        writeable.addValue(inBoth, "inboth first value 2");
        first = writeable;

        writeable = new WriteableCommandLineImpl(root, new ArrayList());
        writeable.addOption(inSecond);
        writeable.addOption(inBoth);
        writeable.addProperty("insecond", "insecond second value");
        writeable.addProperty("inboth", "inboth second value");
        writeable.addSwitch(inSecond, true);
        writeable.addSwitch(inBoth, true);
        writeable.addValue(inSecond, "insecond second value 1");
        writeable.addValue(inSecond, "insecond second value 2");
        writeable.addValue(inBoth, "inboth second value 1");
        writeable.addValue(inBoth, "inboth second value 2");
        second = writeable;
    }

    public final void testAppendCommandLine() {
        final DefaultingCommandLine defaults = new DefaultingCommandLine();
        Iterator i;

        i = defaults.commandLines();
        assertFalse(i.hasNext());

        defaults.appendCommandLine(first);
        i = defaults.commandLines();
        assertSame(first, i.next());
        assertFalse(i.hasNext());

        defaults.appendCommandLine(second);
        i = defaults.commandLines();
        assertSame(first, i.next());
        assertSame(second, i.next());
        assertFalse(i.hasNext());
    }

    public final void testInsertCommandLine() {
        final DefaultingCommandLine defaults = new DefaultingCommandLine();
        Iterator i;

        i = defaults.commandLines();
        assertFalse(i.hasNext());

        defaults.insertCommandLine(0, first);
        i = defaults.commandLines();
        assertSame(first, i.next());
        assertFalse(i.hasNext());

        defaults.insertCommandLine(0, second);
        i = defaults.commandLines();
        assertSame(second, i.next());
        assertSame(first, i.next());
        assertFalse(i.hasNext());
    }
}
