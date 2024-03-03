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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Tests for standard Converters.
 */
public class ConverterTests {

    // A class without a default constructor.
    public class AClassWithoutADefaultConstructor {
        public AClassWithoutADefaultConstructor(final int i) {
        }
    }

    private static Stream<Arguments> numberTestParameters() {
        final List<Arguments> lst = new ArrayList<>();

        lst.add(Arguments.of("123", Long.valueOf("123")));
        lst.add(Arguments.of("12.3", Double.valueOf("12.3")));
        lst.add(Arguments.of("-123", Long.valueOf("-123")));
        lst.add(Arguments.of("-12.3", Double.valueOf("-12.3")));
        lst.add(Arguments.of(".3", Double.valueOf("0.3")));
        lst.add(Arguments.of("-.3", Double.valueOf("-0.3")));
        lst.add(Arguments.of("0x5F", null));
        lst.add(Arguments.of("2,3", null));
        lst.add(Arguments.of("1.2.3", null));

        return lst.stream();
    }

    @Test
    public void classTests() throws Exception {

        assertNotNull(Converter.CLASS.apply(this.getClass().getName()), this.getClass().getName());
        assertNotNull(Converter.CLASS.apply(this.getClass().getCanonicalName()), this.getClass().getCanonicalName());
        assertThrows(ClassNotFoundException.class, () -> Converter.CLASS.apply(this.getClass().getSimpleName()),
                this.getClass().getSimpleName());
        assertNotNull(Converter.CLASS.apply(this.getClass().getTypeName()), this.getClass().getTypeName());

        assertThrows(ClassNotFoundException.class, () -> Converter.CLASS.apply("foo.bar"));
        assertNotNull(Converter.CLASS.apply(AClassWithoutADefaultConstructor.class.getName()));
    }

    @Test
    public void dateTests() throws Exception {
        assertThrows(java.text.ParseException.class, () -> Converter.DATE.apply("whatever"));

        /*
         * Dates calculated from strings are dependent upon configuration and environment settings for the
         * machine on which the test is running.  To avoid this problem, convert the time into a string
         * and then unparse that using the converter.  This produces strings that always match the correct
         * time zone.
         */
        final Date expected = new Date(1023400137000L);
        final DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        final String formatted = dateFormat.format(expected);
        assertEquals(expected, Converter.DATE.apply(formatted));

        assertThrows(java.text.ParseException.class, () -> Converter.DATE.apply("Jun 06 17:48:57 EDT 2002"));
    }

    @Test
    public void fileTests() throws Exception {
        final URL url = this.getClass().getClassLoader().getResource("./org/apache/commons/cli/existing-readable.file");
        final String fileName = url.toString().substring("file:".length());
        assertNotNull(Converter.FILE.apply(fileName));
    }

    @ParameterizedTest
    @MethodSource("numberTestParameters")
    public void numberTests(final String str, final Number expected) throws Exception {
        if (expected != null) {
            assertEquals(expected, Converter.NUMBER.apply(str));
        } else {
            assertThrows(NumberFormatException.class, () -> Converter.NUMBER.apply(str));
        }
    }

    @Test
    public void objectTests() throws Exception {
        assertNotNull(Converter.OBJECT.apply(this.getClass().getName()), this.getClass().getName());
        assertNotNull(Converter.OBJECT.apply(this.getClass().getCanonicalName()), this.getClass().getCanonicalName());
        assertThrows(ClassNotFoundException.class, () -> Converter.OBJECT.apply(this.getClass().getSimpleName()),
                this.getClass().getSimpleName());
        assertNotNull(Converter.OBJECT.apply(this.getClass().getTypeName()), this.getClass().getTypeName());

        assertThrows(ClassNotFoundException.class, () -> Converter.OBJECT.apply("foo.bar"));
        assertThrows(NoSuchMethodException.class, () -> Converter.OBJECT.apply(AClassWithoutADefaultConstructor.class.getName()));
    }

    @Test
    public void urlTests() throws Exception {
        assertEquals(new URL("http://apache.org"), Converter.URL.apply("http://apache.org"));
        assertThrows(java.net.MalformedURLException.class, () -> Converter.URL.apply("foo.bar"));
    }
}
