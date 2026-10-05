/*
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      https://www.apache.org/licenses/LICENSE-2.0

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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junitpioneer.jupiter.DefaultLocale;

/**
 * Tests for standard Converters.
 */
public class ConverterTests {

    // A class whose static initializer has an observable side effect.
    public static class AClassWithAStaticInitializer {

        static {
            classInitializerRan = true;
        }
    }

    // A class without a default constructor.
    public class AClassWithoutADefaultConstructor {

        public AClassWithoutADefaultConstructor(final int i) {
        }
    }

    // A Plugin used by the end-to-end public-API test; kept separate so it is not initialized by another test first.
    public static class ApiPluginImpl implements Plugin {

        static {
            apiPluginInitializerRan = true;
        }
    }

    // Marker type a caller might validate a resolved Class against before touching it.
    public interface Plugin {
    }

    // A Plugin whose static initializer has an observable side effect.
    public static class PluginImpl implements Plugin {

        static {
            pluginInitializerRan = true;
        }
    }

    // Set by the static initializer of AClassWithAStaticInitializer; readable without initializing that class.
    private static boolean classInitializerRan;

    // Set by the static initializer of PluginImpl; readable without initializing that class.
    private static boolean pluginInitializerRan;

    // Set by the static initializer of ApiPluginImpl; readable without initializing that class.
    private static boolean apiPluginInitializerRan;

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
    void testClass() throws Exception {
        assertNotNull(Converter.CLASS.apply(this.getClass().getName()), this.getClass().getName());
        assertNotNull(Converter.CLASS.apply(this.getClass().getCanonicalName()), this.getClass().getCanonicalName());
        assertThrows(ClassNotFoundException.class, () -> Converter.CLASS.apply(this.getClass().getSimpleName()), this.getClass().getSimpleName());
        assertNotNull(Converter.CLASS.apply(this.getClass().getTypeName()), this.getClass().getTypeName());
        assertThrows(ClassNotFoundException.class, () -> Converter.CLASS.apply("foo.bar"));
        assertNotNull(Converter.CLASS.apply(AClassWithoutADefaultConstructor.class.getName()));
    }

    @Test
    void testClassDoesNotInitialize() throws Exception {
        final Class<?> cls = Converter.CLASS.apply(AClassWithAStaticInitializer.class.getName());
        assertFalse(classInitializerRan);
        assertEquals(AClassWithAStaticInitializer.class, cls);
        cls.getConstructor().newInstance();
        assertTrue(classInitializerRan);
    }

    @Test
    void testClassNotInitializedThroughPublicApi() throws Exception {
        // What an application using the public API actually does: declare a Class-typed option via a pattern,
        // parse argv, query the value, then gate it. Parsing and querying must not run the named class's
        // static initializer; only the application deciding to instantiate it should.
        final Options options = PatternOptionBuilder.parsePattern("c+");
        final CommandLine line = new DefaultParser().parse(options, new String[] {"-c", ApiPluginImpl.class.getName()});
        final Class<?> cls = line.getParsedOptionValue("c");
        assertEquals(ApiPluginImpl.class, cls);
        // The isAssignableFrom gate a caller uses to reject non-plugins runs without initializing the class.
        assertTrue(Plugin.class.isAssignableFrom(cls));
        assertFalse(apiPluginInitializerRan);
        // Only when the application instantiates the class does its static initializer run.
        final Object instance = cls.getConstructor().newInstance();
        assertTrue(instance instanceof Plugin);
        assertTrue(apiPluginInitializerRan);
    }

    @Test
    void testClassValidatedBeforeInitialization() throws Exception {
        // The caller pattern this enables: resolve the option, gate it with isAssignableFrom, then instantiate.
        // The assignability check must not run the class's static initializer; only newInstance() should.
        final Class<?> cls = Converter.CLASS.apply(PluginImpl.class.getName());
        assertTrue(Plugin.class.isAssignableFrom(cls));
        assertFalse(pluginInitializerRan);
        final Object instance = cls.getConstructor().newInstance();
        assertTrue(instance instanceof Plugin);
        assertTrue(pluginInitializerRan);
    }

    @Test
    void testDate() throws Exception {
        assertThrows(java.text.ParseException.class, () -> Converter.DATE.apply("whatever"));
        /*
         * Dates calculated from strings are dependent upon configuration and environment settings for the machine on which the test is running. To avoid this
         * problem, convert the time into a string and then unparse that using the converter. This produces strings that always match the correct time zone.
         */
        final Date expected = new Date(1023400137000L);
        final DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        final String formatted = dateFormat.format(expected);
        assertEquals(expected, Converter.DATE.apply(formatted));
        assertThrows(java.text.ParseException.class, () -> Converter.DATE.apply("Jun 06 17:48:57 EDT 2002"));
    }

    @Test
    @DefaultLocale(language = "de", country = "DE")
    void testDateLocaleDe() throws Exception {
        final Date expected = new Date(1023400137000L);
        final String formatted = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").format(expected);
        assertEquals(expected, Converter.DATE.apply(formatted));
    }

    @Test
    @DefaultLocale(language = "de", country = "DE")
    void testDateLocaleDeEnglishInput() throws Exception {
        // Date.toString() always emits English month/day names, so the converter must still parse
        // them when the default locale is not English.
        final Date expected = new Date(1023400137000L);
        final String formatted = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH).format(expected);
        assertEquals(expected, Converter.DATE.apply(formatted));
    }

    @Test
    void testDateRejectsInvalid() {
        // A lenient SimpleDateFormat rolls "Feb 30" over to March 1; the converter must reject
        // out-of-range fields instead of silently returning a wrong Date.
        assertThrows(java.text.ParseException.class, () -> Converter.DATE.apply("Fri Feb 30 12:00:00 UTC 2024"));
        assertThrows(java.text.ParseException.class, () -> Converter.DATE.apply("Mon Jan 32 00:00:00 UTC 2024"));
    }

    @Test
    void testDateRejectsTrailingText() throws Exception {
        final Date expected = new Date(1023400137000L);
        final String formatted = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").format(expected);
        assertEquals(expected, Converter.DATE.apply(formatted));
        assertThrows(java.text.ParseException.class, () -> Converter.DATE.apply(formatted + " trailing"));
    }

    @Test
    @DefaultLocale(language = "de", country = "DE")
    void testDateRejectsTrailingTextLocaleDe() throws Exception {
        // Trailing text must be rejected even when a non-English default locale parses the date, and
        // the reported error position should point past the parsed date rather than at index 0.
        final Date expected = new Date(1023400137000L);
        final String formatted = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").format(expected);
        assertEquals(expected, Converter.DATE.apply(formatted));
        final java.text.ParseException e = assertThrows(java.text.ParseException.class, () -> Converter.DATE.apply(formatted + " trailing"));
        assertEquals(formatted.length(), e.getErrorOffset());
    }

    @Test
    void testFile() throws Exception {
        final URL url = this.getClass().getClassLoader().getResource("./org/apache/commons/cli/existing-readable.file");
        final String fileName = url.toString().substring("file:".length());
        assertNotNull(Converter.FILE.apply(fileName));
    }

    @ParameterizedTest
    @MethodSource("numberTestParameters")
    void testNumber(final String str, final Number expected) throws Exception {
        if (expected != null) {
            assertEquals(expected, Converter.NUMBER.apply(str));
        } else {
            assertThrows(NumberFormatException.class, () -> Converter.NUMBER.apply(str));
        }
    }

    @Test
    void testObject() throws Exception {
        assertNotNull(Converter.OBJECT.apply(this.getClass().getName()), this.getClass().getName());
        assertNotNull(Converter.OBJECT.apply(this.getClass().getCanonicalName()), this.getClass().getCanonicalName());
        assertThrows(ClassNotFoundException.class, () -> Converter.OBJECT.apply(this.getClass().getSimpleName()), this.getClass().getSimpleName());
        assertNotNull(Converter.OBJECT.apply(this.getClass().getTypeName()), this.getClass().getTypeName());
        assertThrows(ClassNotFoundException.class, () -> Converter.OBJECT.apply("foo.bar"));
        assertThrows(NoSuchMethodException.class, () -> Converter.OBJECT.apply(AClassWithoutADefaultConstructor.class.getName()));
    }

    @Test
    void testUrl() throws Exception {
        assertEquals(new URL("http://apache.org"), Converter.URL.apply("http://apache.org"));
        assertThrows(java.net.MalformedURLException.class, () -> Converter.URL.apply("foo.bar"));
    }
}
