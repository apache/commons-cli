/*
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package org.apache.commons.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import org.junit.jupiter.api.Test;

/**
 * Test case for the PatternOptionBuilder class.
 */
@SuppressWarnings("deprecation") // tests some deprecated classes
public class PatternOptionBuilderTest {

    @Test
    public void testClassPattern() throws Exception {
        final Options options = PatternOptionBuilder.parsePattern("c+d+");
        final CommandLineParser parser = new PosixParser();
        final CommandLine line = parser.parse(options, new String[] {"-c", "java.util.Calendar", "-d", "System.DateTime"});

        assertEquals(Calendar.class, line.getOptionObject("c"), "c value");
        assertNull(line.getOptionObject("d"), "d value");
    }

    @Test
    public void testEmptyPattern() {
        final Options options = PatternOptionBuilder.parsePattern("");
        assertTrue(options.getOptions().isEmpty());
    }

    @Test
    public void testExistingFilePattern() throws Exception {
        final Options options = PatternOptionBuilder.parsePattern("g<");
        final CommandLineParser parser = new PosixParser();
        final CommandLine line = parser.parse(options, new String[] {"-g", "src/test/resources/org/apache/commons/cli/existing-readable.file"});

        final Object parsedReadableFileStream = line.getOptionObject("g");

        assertNotNull(parsedReadableFileStream, "option g not parsed");
        assertTrue(parsedReadableFileStream instanceof FileInputStream, "option g not FileInputStream");
    }

    @Test
    public void testExistingFilePatternFileNotExist() throws Exception {
        final Options options = PatternOptionBuilder.parsePattern("f<");
        final CommandLineParser parser = new PosixParser();
        final CommandLine line = parser.parse(options, new String[] {"-f", "non-existing.file"});

        assertNull(line.getOptionObject("f"), "option f parsed");
    }

    @Test
    public void testNumberPattern() throws Exception {
        final Options options = PatternOptionBuilder.parsePattern("n%d%x%");
        final CommandLineParser parser = new PosixParser();
        // 3,5 fails validation.
        //assertThrows(ParseException.class, () -> parser.parse(options, new String[] {"-n", "1", "-d", "2.1", "-x", "3,5"}));

        final CommandLine line = parser.parse(options, new String[] {"-n", "1", "-d", "2.1", "-x", "3,5"});
        assertEquals(Long.class, line.getOptionObject("n").getClass(), "n object class");
        assertEquals(Long.valueOf(1), line.getOptionObject("n"), "n value");

        assertEquals(Double.class, line.getOptionObject("d").getClass(), "d object class");
        assertEquals(Double.valueOf(2.1), line.getOptionObject("d"), "d value");

        assertNull(line.getOptionObject("x"), "x object");
    }

    @Test
    public void testObjectPattern() throws Exception {
        final Options options = PatternOptionBuilder.parsePattern("o@i@n@");
        final CommandLineParser parser = new PosixParser();
        final CommandLine line = parser.parse(options, new String[] {"-o", "java.lang.String", "-i", "java.util.Calendar", "-n", "System.DateTime"});

        assertEquals("", line.getOptionObject("o"), "o value");
        assertNull(line.getOptionObject("i"), "i value");
        assertNull(line.getOptionObject("n"), "n value");
    }

    @Test
    public void testRequiredOption() throws Exception {
        final Options options = PatternOptionBuilder.parsePattern("!n%m%");
        final CommandLineParser parser = new PosixParser();

        try {
            parser.parse(options, new String[] {""});
            fail("MissingOptionException wasn't thrown");
        } catch (final MissingOptionException e) {
            assertEquals(1, e.getMissingOptions().size());
            assertTrue(e.getMissingOptions().contains("n"));
        }
    }

    @Test
    public void testSimplePattern() throws Exception {
        /*
         * Dates calculated from strings are dependent upon configuration and environment settings for the
         * machine on which the test is running.  To avoid this problem, convert the time into a string
         * and then unparse that using the converter.  This produces strings that always match the correct
         * time zone.
         */
        final Options options = PatternOptionBuilder.parsePattern("a:b@cde>f+n%t/m*z#");
        final Date expectedDate = new Date(1023400137000L);
        final DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        final String[] args = {"-c", "-a", "foo", "-b", "java.util.Vector", "-e", "build.xml", "-f", "java.util.Calendar", "-n", "4.5", "-t",
            "https://commons.apache.org", "-z", dateFormat.format(expectedDate), "-m", "test*"};

        final CommandLineParser parser = new PosixParser();
        final CommandLine line = parser.parse(options, args);

        assertEquals("foo", line.getOptionValue("a"), "flag a");
        assertEquals("foo", line.getOptionObject("a"), "string flag a");
        assertEquals(new Vector<>(), line.getOptionObject("b"), "object flag b");
        assertTrue(line.hasOption("c"), "boolean true flag c");
        assertFalse(line.hasOption("d"), "boolean false flag d");
        assertEquals(new File("build.xml"), line.getOptionObject("e"), "file flag e");
        assertEquals(Calendar.class, line.getOptionObject("f"), "class flag f");
        assertEquals(Double.valueOf(4.5), line.getOptionObject("n"), "number flag n");
        assertEquals(new URL("https://commons.apache.org"), line.getOptionObject("t"), "url flag t");

        // tests the char methods of CommandLine that delegate to the String methods
        assertEquals("foo", line.getOptionValue('a'), "flag a");
        assertEquals("foo", line.getOptionObject('a'), "string flag a");
        assertEquals(new Vector<>(), line.getOptionObject('b'), "object flag b");
        assertTrue(line.hasOption('c'), "boolean true flag c");
        assertFalse(line.hasOption('d'), "boolean false flag d");
        assertEquals(new File("build.xml"), line.getOptionObject('e'), "file flag e");
        assertEquals(Calendar.class, line.getOptionObject('f'), "class flag f");
        assertEquals(Double.valueOf(4.5), line.getOptionObject('n'), "number flag n");
        assertEquals(new URL("https://commons.apache.org"), line.getOptionObject('t'), "url flag t");

        // FILES NOT SUPPORTED YET
        assertThrows(UnsupportedOperationException.class, () -> line.getOptionObject('m'));

        assertEquals(expectedDate, line.getOptionObject('z'), "date flag z");

    }

    @Test
    public void testUntypedPattern() throws Exception {
        final Options options = PatternOptionBuilder.parsePattern("abc");
        final CommandLineParser parser = new PosixParser();
        final CommandLine line = parser.parse(options, new String[] {"-abc"});

        assertTrue(line.hasOption('a'));
        assertNull(line.getOptionObject('a'), "value a");
        assertTrue(line.hasOption('b'));
        assertNull(line.getOptionObject('b'), "value b");
        assertTrue(line.hasOption('c'));
        assertNull(line.getOptionObject('c'), "value c");
    }

    @Test
    public void testURLPattern() throws Exception {
        final Options options = PatternOptionBuilder.parsePattern("u/v/");
        final CommandLineParser parser = new PosixParser();
        final CommandLine line = parser.parse(options, new String[] {"-u", "https://commons.apache.org", "-v", "foo://commons.apache.org"});

        assertEquals(new URL("https://commons.apache.org"), line.getOptionObject("u"), "u value");
        assertNull(line.getOptionObject("v"), "v value");
    }
}
