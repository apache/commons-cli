/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.cli.bug;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.junit.Test;

/**
 * Demonstrates inconsistencies in parsing Java property-style options.
 */
public class BugCLI312Test {
    @Test
    public void testPropertyStyleOption_withGetOptionProperties() throws ParseException {
        final Option o1 = Option.builder().option("D").longOpt("define").numberOfArgs(2).valueSeparator('=').build();

        final Options options = new Options();
        options.addOption(o1);

        final CommandLineParser parser = new DefaultParser();

        final CommandLine cl = parser.parse(options, "-Dv -Dw=1 -D x=2 -D y -D z=3 other".split(" "));
        assertArrayEquals(new String[] {"v", "w", "1", "x", "2", "y", "z", "3"}, cl.getOptionValues('D'));

        Properties properties = cl.getOptionProperties("D");
        assertEquals("true", properties.getProperty("v"));
        assertEquals("1", properties.getProperty("w"));
        assertEquals("2", properties.getProperty("x"));
        assertEquals("true", properties.getProperty("y"));
        assertEquals("3", properties.getProperty("z"));
        assertEquals(5, properties.size());
        assertEquals("other", cl.getArgList().get(0));
    }

    @Test
    public void testPropertyStyleOption_withGetOptions() throws ParseException {
        final Option o1 = Option.builder().option("D").longOpt("define").numberOfArgs(2).valueSeparator('=').build();

        final Options options = new Options();
        options.addOption(o1);

        final CommandLineParser parser = new DefaultParser();

        final CommandLine cl = parser.parse(options, "-Dv -Dw=1 -D x=2 -D y -D z=3 other".split(" "));
        assertArrayEquals(new String[] {"v", "w", "1", "x", "2", "y", "z", "3"}, cl.getOptionValues('D'));

        int defineOptionsFound = 0;
        for (final Option o : cl.getOptions()) {
            if ("D".equals(o.getOpt())) {
                defineOptionsFound++;

                if (defineOptionsFound == 1) {
                    assertArrayEquals(new String[] {"v"}, o.getValues());
                } else if (defineOptionsFound == 2) {
                    assertArrayEquals(new String[] {"w", "1"}, o.getValues());
                } else if (defineOptionsFound == 3) {
                    assertArrayEquals(new String[] {"x", "2"}, o.getValues());
                } else if (defineOptionsFound == 4) {
                    assertArrayEquals(new String[] {"y"}, o.getValues());
                } else if (defineOptionsFound == 5) {
                    assertArrayEquals(new String[] {"z", "3"}, o.getValues());
                } else {
                    fail("Didn't expect " + defineOptionsFound + " occurrences of -D");
                }
            }
        }
        assertEquals("other", cl.getArgList().get(0));
    }
}
