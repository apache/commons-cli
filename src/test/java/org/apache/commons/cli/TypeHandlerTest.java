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

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

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

    private static Stream<Arguments> createValueTestParameters() {
        // forse the PatternOptionBuilder to load / modify the TypeHandler table.
        final Class<?> ignore = PatternOptionBuilder.FILES_VALUE;
        // reset the type handler table.
        TypeHandler.resetConverters();
        final List<Arguments> lst = new ArrayList<>();

        try {
            lst.add(Arguments.of(Instantiable.class.getName(), PatternOptionBuilder.CLASS_VALUE, Instantiable.class));
            lst.add(Arguments.of("what ever", PatternOptionBuilder.CLASS_VALUE, ParseException.class));

            lst.add(Arguments.of("what ever", PatternOptionBuilder.DATE_VALUE, ParseException.class));
            lst.add(Arguments.of("Thu Jun 06 17:48:57 EDT 2002", PatternOptionBuilder.DATE_VALUE,
                    new Date(1023400137000L)));
            lst.add(Arguments.of("Jun 06 17:48:57 EDT 2002", PatternOptionBuilder.DATE_VALUE, ParseException.class));

            lst.add(Arguments.of("non-existing.file", PatternOptionBuilder.EXISTING_FILE_VALUE, ParseException.class));

            lst.add(Arguments.of("some-file.txt", PatternOptionBuilder.FILE_VALUE, new File("some-file.txt")));

            lst.add(Arguments.of("some-path.txt", Path.class, new File("some-path.txt").toPath()));

            // the PatternOptionBUilder.FILES_VALUE is not registered so it should just return the string
            lst.add(Arguments.of("some.files", PatternOptionBuilder.FILES_VALUE, "some.files"));

            lst.add(Arguments.of("just-a-string", Integer.class, ParseException.class));
            lst.add(Arguments.of("5", Integer.class, 5));
            lst.add(Arguments.of("5.5", Integer.class, ParseException.class));
            lst.add(Arguments.of(Long.toString(Long.MAX_VALUE), Integer.class, ParseException.class));

            lst.add(Arguments.of("just-a-string", Long.class, ParseException.class));
            lst.add(Arguments.of("5", Long.class, 5L));
            lst.add(Arguments.of("5.5", Long.class, ParseException.class));

            lst.add(Arguments.of("just-a-string", Short.class, ParseException.class));
            lst.add(Arguments.of("5", Short.class, (short) 5));
            lst.add(Arguments.of("5.5", Short.class, ParseException.class));
            lst.add(Arguments.of(Integer.toString(Integer.MAX_VALUE), Short.class, ParseException.class));

            lst.add(Arguments.of("just-a-string", Byte.class, ParseException.class));
            lst.add(Arguments.of("5", Byte.class, (byte) 5));
            lst.add(Arguments.of("5.5", Byte.class, ParseException.class));
            lst.add(Arguments.of(Short.toString(Short.MAX_VALUE), Byte.class, ParseException.class));

            lst.add(Arguments.of("just-a-string", Character.class, 'j'));
            lst.add(Arguments.of("5", Character.class, '5'));
            lst.add(Arguments.of("5.5", Character.class, '5'));
            lst.add(Arguments.of("\\u0124", Character.class, Character.toChars(0x0124)[0]));

            lst.add(Arguments.of("just-a-string", Double.class, ParseException.class));
            lst.add(Arguments.of("5", Double.class, 5d));
            lst.add(Arguments.of("5.5", Double.class, 5.5));

            lst.add(Arguments.of("just-a-string", Float.class, ParseException.class));
            lst.add(Arguments.of("5", Float.class, 5f));
            lst.add(Arguments.of("5.5", Float.class, 5.5f));
            lst.add(Arguments.of(Double.toString(Double.MAX_VALUE), Float.class, Float.POSITIVE_INFINITY));

            lst.add(Arguments.of("just-a-string", BigInteger.class, ParseException.class));
            lst.add(Arguments.of("5", BigInteger.class, new BigInteger("5")));
            lst.add(Arguments.of("5.5", BigInteger.class, ParseException.class));

            lst.add(Arguments.of("just-a-string", BigDecimal.class, ParseException.class));
            lst.add(Arguments.of("5", BigDecimal.class, new BigDecimal("5")));
            lst.add(Arguments.of("5.5", BigDecimal.class, new BigDecimal(5.5)));

            lst.add(Arguments.of("1.5", PatternOptionBuilder.NUMBER_VALUE, Double.valueOf(1.5)));
            lst.add(Arguments.of("15", PatternOptionBuilder.NUMBER_VALUE, Long.valueOf(15)));
            lst.add(Arguments.of("not a number", PatternOptionBuilder.NUMBER_VALUE, ParseException.class));

            lst.add(Arguments.of(Instantiable.class.getName(), PatternOptionBuilder.OBJECT_VALUE, new Instantiable()));
            lst.add(Arguments.of(NotInstantiable.class.getName(), PatternOptionBuilder.OBJECT_VALUE,
                    ParseException.class));
            lst.add(Arguments.of("unknown", PatternOptionBuilder.OBJECT_VALUE, ParseException.class));

            lst.add(Arguments.of("String", PatternOptionBuilder.STRING_VALUE, "String"));

            final String urlString = "https://commons.apache.org";
            lst.add(Arguments.of(urlString, PatternOptionBuilder.URL_VALUE, new URL(urlString)));
            lst.add(Arguments.of("Malformed-url", PatternOptionBuilder.URL_VALUE, ParseException.class));

            return lst.stream();

        } catch (final MalformedURLException e) {
            throw new RuntimeException(e);
        }

    }

    @SuppressWarnings("unchecked")
    @ParameterizedTest(name = "{0} as {1}")
    @MethodSource("createValueTestParameters")
    public void createValueTests(final String str, final Class<?> type, final Object expected) throws Exception {
        if (expected instanceof Class<?> && Throwable.class.isAssignableFrom((Class<?>) expected)) {
            assertThrows((Class<Throwable>) expected, () -> TypeHandler.createValue(str, type));
        } else {
            assertEquals(expected, TypeHandler.createValue(str, type));
        }
    }

    @Test
    public void testCreateValueExistingFile() throws Exception {
        try (FileInputStream result = TypeHandler.createValue(
                "src/test/resources/org/apache/commons/cli/existing-readable.file",
                PatternOptionBuilder.EXISTING_FILE_VALUE)) {
            assertNotNull(result);
        }
    }

    @Test
    public void testNoConverters() {
        assertEquals(Converter.NUMBER, TypeHandler.getConverter(Number.class));
        try {
            TypeHandler.noConverters();
            assertEquals(Converter.DEFAULT, TypeHandler.getConverter(Number.class));
        } finally {
            TypeHandler.resetConverters();
            assertEquals(Converter.NUMBER, TypeHandler.getConverter(Number.class));
        }
    }

    /* proof of equality for later tests */
    @Test
    public void testnstantiableEquals() {
        assertEquals(new Instantiable(), new Instantiable());
    }

    @Test
    public void testRegister() {
        assertEquals(Converter.DEFAULT, TypeHandler.getConverter(NotInstantiable.class));
        try {
            TypeHandler.register(NotInstantiable.class, Converter.DATE);
            assertEquals(Converter.DATE, TypeHandler.getConverter(NotInstantiable.class));
        } finally {
            TypeHandler.register(NotInstantiable.class, null);
            assertEquals(Converter.DEFAULT, TypeHandler.getConverter(NotInstantiable.class));
        }
    }

    @Test
    public void testResetConverters() {
        assertEquals(Converter.DEFAULT, TypeHandler.getConverter(NotInstantiable.class));
        try {
            TypeHandler.register(NotInstantiable.class, Converter.DATE);
            assertEquals(Converter.DATE, TypeHandler.getConverter(NotInstantiable.class));
            TypeHandler.resetConverters();
            assertEquals(Converter.DEFAULT, TypeHandler.getConverter(NotInstantiable.class));
            assertEquals(Converter.DEFAULT, TypeHandler.getConverter(NotInstantiable.class));
        } finally {
            TypeHandler.register(NotInstantiable.class, null);
        }
    }
}
