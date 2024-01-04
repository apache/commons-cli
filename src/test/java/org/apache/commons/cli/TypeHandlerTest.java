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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.cli.converters.Converter;
import org.apache.commons.cli.converters.Verifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class TypeHandlerTest {

    /** Used for Class and Object creation tests. */
    public static class Instantiable {

        @Override
        public boolean equals(Object arg0) {
            return arg0 instanceof Instantiable;
        }

        @Override
        public int hashCode() {
            return 1;
        }
    }

    /* proof of equality for later tests */
    @Test
    public void testnstantiableEquals() {
        assertEquals(new Instantiable(), new Instantiable());
    }

    /** Used for Class and Object negative creation tests */
    public static final class NotInstantiable {
        private NotInstantiable() {
        }

    }

    @Test
    public void testRegister() {
        assertEquals(Converter.DEFAULT, TypeHandler.getConverter(NotInstantiable.class));
        assertEquals(Verifier.DEFAULT, TypeHandler.getVerifier(NotInstantiable.class));
        try {
            TypeHandler.register(NotInstantiable.class, Converter.DATE, Verifier.NUMBER);
            assertEquals(Converter.DATE, TypeHandler.getConverter(NotInstantiable.class));
            assertEquals(Verifier.NUMBER, TypeHandler.getVerifier(NotInstantiable.class));
        } finally {
            TypeHandler.register(NotInstantiable.class, null, null);
            assertEquals(Converter.DEFAULT, TypeHandler.getConverter(NotInstantiable.class));
            assertEquals(Verifier.DEFAULT, TypeHandler.getVerifier(NotInstantiable.class));
        }
    }

    @Test
    public void testResetConvertersAndVerifiers() {
        assertEquals(Converter.DEFAULT, TypeHandler.getConverter(NotInstantiable.class));
        assertEquals(Verifier.DEFAULT, TypeHandler.getVerifier(NotInstantiable.class));
        try {
            TypeHandler.register(NotInstantiable.class, Converter.DATE, Verifier.NUMBER);
            assertEquals(Converter.DATE, TypeHandler.getConverter(NotInstantiable.class));
            assertEquals(Verifier.NUMBER, TypeHandler.getVerifier(NotInstantiable.class));
            TypeHandler.resetConverters();
            assertEquals(Converter.DEFAULT, TypeHandler.getConverter(NotInstantiable.class));
            assertEquals(Verifier.NUMBER, TypeHandler.getVerifier(NotInstantiable.class));
            TypeHandler.resetVerifiers();
            assertEquals(Converter.DEFAULT, TypeHandler.getConverter(NotInstantiable.class));
            assertEquals(Verifier.DEFAULT, TypeHandler.getVerifier(NotInstantiable.class));
        } finally {
            TypeHandler.register(NotInstantiable.class, null, null);
        }
    }
    
    @Test
    public void testNoConverters() {
        assertEquals(Converter.NUMBER, TypeHandler.getConverter(Number.class));
        assertEquals(Verifier.NUMBER, TypeHandler.getVerifier(Number.class));
        try {
            TypeHandler.noConverters();
            assertEquals(Converter.DEFAULT, TypeHandler.getConverter(Number.class));
            assertEquals(Verifier.NUMBER, TypeHandler.getVerifier(Number.class));
        } finally {
            TypeHandler.resetConverters();
            assertEquals(Converter.NUMBER, TypeHandler.getConverter(Number.class));
            assertEquals(Verifier.NUMBER, TypeHandler.getVerifier(Number.class));
        }
    }

    @Test
    public void testNoVerifiers() {
        assertEquals(Converter.NUMBER, TypeHandler.getConverter(Number.class));
        assertEquals(Verifier.NUMBER, TypeHandler.getVerifier(Number.class));
        try {
            TypeHandler.noVerifiers();
            assertEquals(Converter.NUMBER, TypeHandler.getConverter(Number.class));
            assertEquals(Verifier.DEFAULT, TypeHandler.getVerifier(Number.class));
        } finally {
            TypeHandler.resetVerifiers();
            assertEquals(Converter.NUMBER, TypeHandler.getConverter(Number.class));
            assertEquals(Verifier.NUMBER, TypeHandler.getVerifier(Number.class));
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

    @SuppressWarnings("unchecked")
    @ParameterizedTest(name = "{0} as {1}")
    @MethodSource("createValueTestParameters")
    public void createValueTests(String str, Class<?> type, Object expected) throws Exception {
        if (expected instanceof Class<?> && Throwable.class.isAssignableFrom((Class<?>) expected)) {
            assertThrows((Class<Throwable>) expected, () -> TypeHandler.createValue(str, type));
        } else {
            assertEquals(expected, TypeHandler.createValue(str, type));
        }
    }

    private static Stream<Arguments> createValueTestParameters() {
        List<Arguments> lst = new ArrayList<>();

        try {
            lst.add(Arguments.of(Instantiable.class.getName(), PatternOptionBuilder.CLASS_VALUE, Instantiable.class));
            lst.add(Arguments.of("what ever", PatternOptionBuilder.CLASS_VALUE, ParseException.class));

            lst.add(Arguments.of("what ever", PatternOptionBuilder.DATE_VALUE, ParseException.class));
            lst.add(Arguments.of("Thu Jun 06 17:48:57 EDT 2002", PatternOptionBuilder.DATE_VALUE,
                    new Date(1023400137000L)));
            lst.add(Arguments.of("Jun 06 17:48:57 EDT 2002", PatternOptionBuilder.DATE_VALUE, ParseException.class));

            lst.add(Arguments.of("non-existing.file", PatternOptionBuilder.EXISTING_FILE_VALUE, ParseException.class));

            lst.add(Arguments.of("some-file.txt", PatternOptionBuilder.FILE_VALUE, new File("some-file.txt")));

            lst.add(Arguments.of("some.files", PatternOptionBuilder.FILES_VALUE, UnsupportedOperationException.class));

            lst.add(Arguments.of("just-a-string", Integer.class, ParseException.class));
            lst.add(Arguments.of("5", Integer.class, 5));
            lst.add(Arguments.of("5.5", Integer.class, ParseException.class));
            lst.add(Arguments.of(Long.valueOf(Long.MAX_VALUE).toString(), Integer.class, ParseException.class));

            lst.add(Arguments.of("just-a-string", Long.class, ParseException.class));
            lst.add(Arguments.of("5", Long.class, 5L));
            lst.add(Arguments.of("5.5", Long.class, ParseException.class));

            lst.add(Arguments.of("just-a-string", Short.class, ParseException.class));
            lst.add(Arguments.of("5", Short.class, (short) 5));
            lst.add(Arguments.of("5.5", Short.class, ParseException.class));
            lst.add(Arguments.of(Integer.valueOf(Integer.MAX_VALUE).toString(), Short.class, ParseException.class));

            lst.add(Arguments.of("just-a-string", Byte.class, ParseException.class));
            lst.add(Arguments.of("5", Byte.class, (byte) 5));
            lst.add(Arguments.of("5.5", Byte.class, ParseException.class));
            lst.add(Arguments.of(Short.valueOf(Short.MAX_VALUE).toString(), Byte.class, ParseException.class));

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
            lst.add(Arguments.of(Double.valueOf(Double.MAX_VALUE).toString(), Float.class, Float.POSITIVE_INFINITY));

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

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

    }
}
