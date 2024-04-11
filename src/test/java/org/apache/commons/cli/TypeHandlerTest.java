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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class TypeHandlerTest {

    /** Used for Class and Object creation tests. */
    public static class Instantiable {

        @Override
        public boolean equals(final Object arg0) {
            return arg0 instanceof Instantiable;
        }

        @Override
        public int hashCode() {
            return 1;
        }
    }

    /** Used for Class and Object negative creation tests */
    public static final class NotInstantiable {
        private NotInstantiable() {
        }

    }

    /** Always returns the same Path. */
    private static final Converter<Path, InvalidPathException> PATH_CONVERTER = s -> Paths.get("foo");

    private static Stream<Date> createDateFixtures() {
        return Stream.of(Date.from(Instant.EPOCH), Date.from(Instant.ofEpochSecond(0)), Date.from(Instant.ofEpochSecond(40_000)));

    }

    private static Stream<Arguments> createValueTestParameters() throws MalformedURLException {
        // force the PatternOptionBuilder to load / modify the TypeHandler table.
        @SuppressWarnings("unused")
        final Class<?> loadStatic = PatternOptionBuilder.FILES_VALUE;
        // reset the type handler table.
        // TypeHandler.resetConverters();
        final List<Arguments> list = new ArrayList<>();

        /*
         * Dates calculated from strings are dependent upon configuration and environment settings for the machine on which the test is running. To avoid this
         * problem, convert the time into a string and then unparse that using the converter. This produces strings that always match the correct time zone.
         */
        final Date date = new Date(1023400137000L);
        final DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");

        list.add(Arguments.of(Instantiable.class.getName(), PatternOptionBuilder.CLASS_VALUE, Instantiable.class));
        list.add(Arguments.of("what ever", PatternOptionBuilder.CLASS_VALUE, ParseException.class));

        list.add(Arguments.of("what ever", PatternOptionBuilder.DATE_VALUE, ParseException.class));
        list.add(Arguments.of(dateFormat.format(date), PatternOptionBuilder.DATE_VALUE, date));
        list.add(Arguments.of("Jun 06 17:48:57 EDT 2002", PatternOptionBuilder.DATE_VALUE, ParseException.class));

        list.add(Arguments.of("non-existing.file", PatternOptionBuilder.EXISTING_FILE_VALUE, ParseException.class));

        list.add(Arguments.of("some-file.txt", PatternOptionBuilder.FILE_VALUE, new File("some-file.txt")));

        list.add(Arguments.of("some-path.txt", Path.class, new File("some-path.txt").toPath()));

        // the PatternOptionBuilder.FILES_VALUE is not registered so it should just return the string
        list.add(Arguments.of("some.files", PatternOptionBuilder.FILES_VALUE, "some.files"));

        list.add(Arguments.of("just-a-string", Integer.class, ParseException.class));
        list.add(Arguments.of("5", Integer.class, 5));
        list.add(Arguments.of("5.5", Integer.class, ParseException.class));
        list.add(Arguments.of(Long.toString(Long.MAX_VALUE), Integer.class, ParseException.class));

        list.add(Arguments.of("just-a-string", Long.class, ParseException.class));
        list.add(Arguments.of("5", Long.class, 5L));
        list.add(Arguments.of("5.5", Long.class, ParseException.class));

        list.add(Arguments.of("just-a-string", Short.class, ParseException.class));
        list.add(Arguments.of("5", Short.class, (short) 5));
        list.add(Arguments.of("5.5", Short.class, ParseException.class));
        list.add(Arguments.of(Integer.toString(Integer.MAX_VALUE), Short.class, ParseException.class));

        list.add(Arguments.of("just-a-string", Byte.class, ParseException.class));
        list.add(Arguments.of("5", Byte.class, (byte) 5));
        list.add(Arguments.of("5.5", Byte.class, ParseException.class));
        list.add(Arguments.of(Short.toString(Short.MAX_VALUE), Byte.class, ParseException.class));

        list.add(Arguments.of("just-a-string", Character.class, 'j'));
        list.add(Arguments.of("5", Character.class, '5'));
        list.add(Arguments.of("5.5", Character.class, '5'));
        list.add(Arguments.of("\\u0124", Character.class, Character.toChars(0x0124)[0]));

        list.add(Arguments.of("just-a-string", Double.class, ParseException.class));
        list.add(Arguments.of("5", Double.class, 5d));
        list.add(Arguments.of("5.5", Double.class, 5.5));

        list.add(Arguments.of("just-a-string", Float.class, ParseException.class));
        list.add(Arguments.of("5", Float.class, 5f));
        list.add(Arguments.of("5.5", Float.class, 5.5f));
        list.add(Arguments.of(Double.toString(Double.MAX_VALUE), Float.class, Float.POSITIVE_INFINITY));

        list.add(Arguments.of("just-a-string", BigInteger.class, ParseException.class));
        list.add(Arguments.of("5", BigInteger.class, new BigInteger("5")));
        list.add(Arguments.of("5.5", BigInteger.class, ParseException.class));

        list.add(Arguments.of("just-a-string", BigDecimal.class, ParseException.class));
        list.add(Arguments.of("5", BigDecimal.class, new BigDecimal("5")));
        list.add(Arguments.of("5.5", BigDecimal.class, new BigDecimal(5.5)));

        list.add(Arguments.of("1.5", PatternOptionBuilder.NUMBER_VALUE, Double.valueOf(1.5)));
        list.add(Arguments.of("15", PatternOptionBuilder.NUMBER_VALUE, Long.valueOf(15)));
        list.add(Arguments.of("not a number", PatternOptionBuilder.NUMBER_VALUE, ParseException.class));

        list.add(Arguments.of(Instantiable.class.getName(), PatternOptionBuilder.OBJECT_VALUE, new Instantiable()));
        list.add(Arguments.of(NotInstantiable.class.getName(), PatternOptionBuilder.OBJECT_VALUE, ParseException.class));
        list.add(Arguments.of("unknown", PatternOptionBuilder.OBJECT_VALUE, ParseException.class));

        list.add(Arguments.of("String", PatternOptionBuilder.STRING_VALUE, "String"));

        final String urlString = "https://commons.apache.org";
        list.add(Arguments.of(urlString, PatternOptionBuilder.URL_VALUE, new URL(urlString)));
        list.add(Arguments.of("Malformed-url", PatternOptionBuilder.URL_VALUE, ParseException.class));

        return list.stream();

    }

    @Test
    public void testCreateClass() throws ParseException {
        final Class<?> cls = getClass();
        assertEquals(cls, TypeHandler.createClass(cls.getName()));
    }

    @ParameterizedTest
    @MethodSource("createDateFixtures")
    public void testCreateDate(final Date date) {
        assertEquals(date, TypeHandler.createDate(date.toString()));
    }

    @Test
    public void testCreateFile() {
        final File file = new File("").getAbsoluteFile();
        assertEquals(file, TypeHandler.createFile(file.toString()));
    }

    @Test
    public void testCreateFiles() {
        assertThrows(UnsupportedOperationException.class, () -> TypeHandler.createFiles(null));
    }

    @Test
    public void testCreateNumber() throws ParseException {
        assertEquals(0L, TypeHandler.createNumber("0"));
        assertEquals(0d, TypeHandler.createNumber("0.0"));
    }

    @Test
    public void testCreateObject() throws ParseException {
        assertTrue(TypeHandler.createObject(Date.class.getName()) instanceof Date);
    }

    @Test
    public void testCreateURL() throws ParseException, MalformedURLException {
        final URL file = Paths.get("").toAbsolutePath().toUri().toURL();
        assertEquals(file, TypeHandler.createURL(file.toString()));
    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest(name = "{0} as {1}")
    @MethodSource("createValueTestParameters")
    public void testCreateValue(final String str, final Class<?> type, final Object expected) throws Exception {
        @SuppressWarnings("cast")
        final Object objectApiTest = type; // KEEP this cast
        if (expected instanceof Class<?> && Throwable.class.isAssignableFrom((Class<?>) expected)) {
            assertThrows((Class<Throwable>) expected, () -> TypeHandler.createValue(str, type));
            assertThrows((Class<Throwable>) expected, () -> TypeHandler.createValue(str, objectApiTest));
        } else {
            assertEquals(expected, TypeHandler.createValue(str, type));
            assertEquals(expected, TypeHandler.createValue(str, objectApiTest));
        }
    }

    @Test
    public void testCreateValueExistingFile() throws Exception {
        try (FileInputStream result = TypeHandler.createValue("src/test/resources/org/apache/commons/cli/existing-readable.file",
                PatternOptionBuilder.EXISTING_FILE_VALUE)) {
            assertNotNull(result);
        }
    }

    /* proof of equality for later tests */
    @Test
    public void testnstantiableEquals() {
        assertEquals(new Instantiable(), new Instantiable());
    }

    @Test
    public void testOpenFile() throws ParseException, IOException {
        try (FileInputStream fis = TypeHandler.openFile("src/test/resources/org/apache/commons/cli/existing-readable.file")) {
            IOUtils.consume(fis);
        }
    }

    @Test
    public void testRegister() {
        final Map<Class<?>, Converter<?, ? extends Throwable>> map = TypeHandler.createDefaultMap();
        final TypeHandler typeHandler = new TypeHandler(map);
        assertEquals(Converter.PATH, typeHandler.getConverter(Path.class));
        try {
            map.put(Path.class, PATH_CONVERTER);
            assertEquals(PATH_CONVERTER, typeHandler.getConverter(Path.class));
        } finally {
            map.remove(Path.class);
            assertEquals(Converter.DEFAULT, typeHandler.getConverter(Path.class));
        }
    }

}
