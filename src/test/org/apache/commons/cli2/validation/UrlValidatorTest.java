/**
 * Copyright 2004 The Apache Software Foundation
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

public class UrlValidatorTest extends TestCase {
    
    public void testValidate() throws InvalidArgumentException, MalformedURLException {
        final Object[] array = new Object[] { "http://www.apache.org/", "file:///etc"};
        final List list = Arrays.asList(array);
        final Validator validator = new UrlValidator();

        validator.validate(list);

        final Iterator i = list.iterator();
        assertEquals(new URL("http://www.apache.org/"), i.next());
        assertEquals(new URL("file:///etc"), i.next());
        assertFalse(i.hasNext());
    }

    public void testMalformedURL() throws InvalidArgumentException, MalformedURLException {
        final Object[] array = new Object[] { "www.apache.org"};
        final List list = Arrays.asList(array);
        final Validator validator = new UrlValidator();

        try {
            validator.validate(list);
        }
        catch(InvalidArgumentException e){
            assertEquals("Cannot understand url: www.apache.org",e.getMessage());
        }
        
    }

    public void testBadProtocol() {
        {
            final Object[] array = new Object[] { "http://www.apache.org/", "file:///etc"};
            final List list = Arrays.asList(array);
            final UrlValidator validator = new UrlValidator();
            validator.setProtocol("http");
    
            assertEquals("incorrect protocol", "http", validator.getProtocol());
            try{
                validator.validate(list);
                fail("Expected InvalidArgumentException");
            }
            catch(InvalidArgumentException e){
                assertEquals("file:///etc",e.getMessage());
            }
        }
        {
            final Object[] array = new Object[] { "http://www.apache.org/", "file:///etc"};
            final List list = Arrays.asList(array);
            final UrlValidator validator = new UrlValidator("http");
    
            try{
                validator.validate(list);
                fail("Expected InvalidArgumentException");
            }
            catch(InvalidArgumentException e){
                assertEquals("file:///etc",e.getMessage());
            }
        }
    }
}
