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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.Test;

/**
 * Demonstrates inconsistencies in parsing Java property-style options.
 */
public class BugCLI312Test {
    @Test
    public void testNoOptionValues() {
        final Option o1 = Option.builder("A").build();
        final Option o2 = Option.builder().option("D").longOpt("define").numberOfArgs(2).valueSeparator('=').build();
        final Options options = new Options().addOption(o1).addOption(o2);

        final CommandLineParser parser = new DefaultParser();

        assertThrows(MissingArgumentException.class, () -> parser.parse(options, "-D -A".split(" ")));
    }

    @Test
    public void testPropertyStyleOption_withGetOptionProperties() throws ParseException {
        final Option o1 = Option.builder().option("D").longOpt("define").numberOfArgs(2).valueSeparator('=').build();

        final Options options = new Options();
        options.addOption(o1);

        final CommandLineParser parser = new DefaultParser();

        final CommandLine cl = parser.parse(options, "-Dv -Dw=1 -D x=2 -D y -D z=3 other".split(" "));
        assertArrayEquals(new String[] {"v", "w", "1", "x", "2", "y", "z", "3"}, cl.getOptionValues('D'));

        final Properties properties = cl.getOptionProperties("D");
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

                switch (defineOptionsFound) {
                case 1:
                    assertArrayEquals(new String[] {"v"}, o.getValues());
                    break;
                case 2:
                    assertArrayEquals(new String[] {"w", "1"}, o.getValues());
                    break;
                case 3:
                    assertArrayEquals(new String[] {"x", "2"}, o.getValues());
                    break;
                case 4:
                    assertArrayEquals(new String[] {"y"}, o.getValues());
                    break;
                case 5:
                    assertArrayEquals(new String[] {"z", "3"}, o.getValues());
                    break;
                default:
                    fail("Didn't expect " + defineOptionsFound + " occurrences of -D");
                    break;
                }
            }
        }
        assertEquals("other", cl.getArgList().get(0));
    }
}
