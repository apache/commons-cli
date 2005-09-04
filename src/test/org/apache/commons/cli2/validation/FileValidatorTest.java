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
package org.apache.commons.cli2.validation;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

/**
 * JUnit test case for the FileValidator.
 *
 * @author Rob Oxspring
 * @author John Keyes
 */
public class FileValidatorTest extends TestCase {

    public void testValidate() throws InvalidArgumentException {
        final Object[] array = new Object[] { "src", "project.xml", "veryunlikelyfilename"};
        final List list = Arrays.asList(array);
        final FileValidator validator = new FileValidator();

        validator.validate(list);

        final Iterator i = list.iterator();
        assertEquals(new File("src"), i.next());
        assertEquals(new File("project.xml"), i.next());
        assertEquals(new File("veryunlikelyfilename"), i.next());
        assertFalse(i.hasNext());
    }

    public void testValidate_Directory() {
        final Object[] array = new Object[] { "src", "project.xml"};
        final List list = Arrays.asList(array);
        final FileValidator validator = FileValidator.getExistingDirectoryInstance();

        assertTrue("is a directory validator", validator.isDirectory());
        assertFalse("is not a file validator", validator.isFile());
        assertTrue("is an existing file validator", validator.isExisting());
        assertFalse("is not a hidden file validator", validator.isHidden());

        try{
            validator.validate(list);
            fail("InvalidArgumentException");
        }
        catch(InvalidArgumentException e){
            assertEquals("project.xml",e.getMessage());
        }
    }

    public void testValidate_ReadableFile() {
        final Object[] array = new Object[] { "src/test/data/readable.txt", "src/test/data/notreadable.txt"};
        final List list = Arrays.asList(array);
        final FileValidator validator = FileValidator.getExistingFileInstance();
        validator.setReadable(true);

        assertFalse("is not a directory validator", validator.isDirectory());
        assertTrue("is a file validator", validator.isFile());
        assertTrue("is an existing file validator", validator.isExisting());
        assertFalse("is not a hidden file validator", validator.isHidden());
        assertTrue("is a readable file validator", validator.isReadable());
        assertFalse("is not a writable file validator", validator.isWritable());

        try{
            validator.validate(list);
            fail("InvalidArgumentException");
        }
        catch(InvalidArgumentException e){
            assertEquals("src/test/data/notreadable.txt",e.getMessage());
        }
    }

    public void testValidate_WritableFile() {
        final Object[] array = new Object[] { "src/test/data/writable.txt", "src/test/data/readable.txt"};
        final List list = Arrays.asList(array);
        final FileValidator validator = FileValidator.getExistingFileInstance();
        validator.setWritable(true);

        assertFalse("is not a directory validator", validator.isDirectory());
        assertTrue("is a file validator", validator.isFile());
        assertTrue("is an existing file validator", validator.isExisting());
        assertFalse("is not a hidden file validator", validator.isHidden());
        assertTrue("is not a readable file validator", validator.isReadable());
        assertFalse("is a writable file validator", validator.isWritable());

        try{
            validator.validate(list);
            fail("InvalidArgumentException");
        }
        catch(InvalidArgumentException e){
            assertEquals("src/test/data/readable.txt",e.getMessage());
        }
    }

    public void testValidate_HiddenFile() throws InvalidArgumentException {
        final Object[] array = new Object[] { "src/test/data/hidden.txt", "src"};
        final List list = Arrays.asList(array);
        final FileValidator validator = FileValidator.getExistingFileInstance();
        validator.setHidden(true);

        assertFalse("is not a directory validator", validator.isDirectory());
        assertTrue("is a file validator", validator.isFile());
        assertTrue("is an existing file validator", validator.isExisting());
        assertTrue("is a hidden file validator", validator.isHidden());

        try{
            validator.validate(list);
            fail("InvalidArgumentException");
        }
        catch(InvalidArgumentException e){
            assertEquals("src",e.getMessage());
        }
    }

    public void testValidate_Existing() {
        final Object[] array = new Object[] { "project.xml", "veryunlikelyfilename"};
        final List list = Arrays.asList(array);
        final FileValidator validator = FileValidator.getExistingInstance();

        assertFalse("is not a directory validator", validator.isDirectory());
        assertFalse("is not a file validator", validator.isFile());
        assertTrue("is an existing file validator", validator.isExisting());
        assertFalse("is not a hidden file validator", validator.isHidden());

        try{
            validator.validate(list);
            fail("InvalidArgumentException");
        }
        catch(InvalidArgumentException e){
            assertEquals("veryunlikelyfilename",e.getMessage());
        }
    }

    public void testValidate_File() {
        final Object[] array = new Object[] { "project.xml", "src"};
        final List list = Arrays.asList(array);
        final Validator validator = FileValidator.getExistingFileInstance();

        try{
            validator.validate(list);
            fail("InvalidArgumentException");
        }
        catch(InvalidArgumentException e){
            assertEquals("src",e.getMessage());
        }
    }
}
