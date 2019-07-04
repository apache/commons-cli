/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.cli;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TypeHandlerTest
{

    @Test
    public void testCreateValueString()
        throws Exception
    {
        assertEquals("String", TypeHandler.createValue("String", PatternOptionBuilder.STRING_VALUE));
    }

    @Test(expected = ParseException.class)
    public void testCreateValueObject_unknownClass()
        throws Exception
    {
        TypeHandler.createValue("unknown", PatternOptionBuilder.OBJECT_VALUE);
    }

    @Test(expected = ParseException.class)
    public void testCreateValueObject_notInstantiableClass()
        throws Exception
    {
        TypeHandler.createValue(NotInstantiable.class.getName(), PatternOptionBuilder.OBJECT_VALUE);
    }

    @Test
    public void testCreateValueObject_InstantiableClass()
        throws Exception
    {
        Object result = TypeHandler.createValue(Instantiable.class.getName(), PatternOptionBuilder.OBJECT_VALUE);
        assertTrue(result instanceof Instantiable);
    }

    @Test(expected = ParseException.class)
    public void testCreateValueNumber_noNumber()
        throws Exception
    {
        TypeHandler.createValue("not a number", PatternOptionBuilder.NUMBER_VALUE);
    }

    @Test
    public void testCreateValueNumber_Double()
        throws Exception
    {
        assertEquals(1.5d, TypeHandler.createValue("1.5", PatternOptionBuilder.NUMBER_VALUE));
    }

    @Test
    public void testCreateValueNumber_Long()
        throws Exception
    {
        assertEquals(Long.valueOf(15), TypeHandler.createValue("15", PatternOptionBuilder.NUMBER_VALUE));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCreateValueDate()
        throws Exception
    {
        TypeHandler.createValue("what ever", PatternOptionBuilder.DATE_VALUE);
    }

    @Test(expected = ParseException.class)
    public void testCreateValueClass_notFound()
        throws Exception
    {
        TypeHandler.createValue("what ever", PatternOptionBuilder.CLASS_VALUE);
    }

    @Test
    public void testCreateValueClass()
        throws Exception
    {
        Object clazz = TypeHandler.createValue(Instantiable.class.getName(), PatternOptionBuilder.CLASS_VALUE);
        assertEquals(Instantiable.class, clazz);
    }

    @Test
    public void testCreateValueFile()
            throws Exception
    {
        File result = TypeHandler.createValue("some-file.txt", PatternOptionBuilder.FILE_VALUE);
        assertEquals("some-file.txt", result.getName());
    }

    @Test
    public void testCreateValueExistingFile()
            throws Exception
    {
        FileInputStream result = TypeHandler.createValue("src/test/resources/existing-readable.file", PatternOptionBuilder.EXISTING_FILE_VALUE);
        assertNotNull(result);
    }

    @Test(expected = ParseException.class)
    public void testCreateValueExistingFile_nonExistingFile()
            throws Exception
    {
        TypeHandler.createValue("non-existing.file", PatternOptionBuilder.EXISTING_FILE_VALUE);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testCreateValueFiles()
            throws Exception
    {
        TypeHandler.createValue("some.files", PatternOptionBuilder.FILES_VALUE);
    }

    @Test
    public void testCreateValueURL()
            throws Exception
    {
        String urlString = "https://commons.apache.org";
        URL result = TypeHandler.createValue(urlString, PatternOptionBuilder.URL_VALUE);
        assertEquals(urlString, result.toString());
    }

    @Test(expected = ParseException.class)
    public void testCreateValueURL_malformed()
            throws Exception
    {
        TypeHandler.createValue("malformed-url", PatternOptionBuilder.URL_VALUE);
    }

    @Test(expected = ParseException.class)
    public void testCreateValueInteger_failure()
            throws Exception
    {
        TypeHandler.createValue("just-a-string", Integer.class);
    }

    public static class Instantiable
    {
    }

    public static class NotInstantiable
    {
        private NotInstantiable() {}
    }
}
