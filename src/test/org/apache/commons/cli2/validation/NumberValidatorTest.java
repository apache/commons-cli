/**
 * Copyright 2003-2004 The Apache Software Foundation
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

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

public class NumberValidatorTest extends TestCase {
    
    public void testValidate_Number() throws InvalidArgumentException {
        final NumberFormat format = NumberFormat.getNumberInstance();
        
        final Object[] array = 
            new Object[] { 
                format.format(1d), 
                format.format(1.07d), 
                format.format(-.45d)};
        
        final List list = Arrays.asList(array);
        final Validator validator = NumberValidator.getNumberInstance();

        validator.validate(list);

        final Iterator i = list.iterator();
        assertEquals(1d, ((Number)i.next()).doubleValue(), 0.0001);
        assertEquals(1.07d, ((Number)i.next()).doubleValue(), 0.0001);
        assertEquals(-.45d, ((Number)i.next()).doubleValue(), 0.0001);
        assertFalse(i.hasNext());
    }

    public void testValidate_Currency() throws InvalidArgumentException {
        NumberFormat format = NumberFormat.getCurrencyInstance();
        final Object[] array =
            new Object[] {
                format.format(1d),
                format.format(1.07),
                format.format(-0.45)};
        final List list = Arrays.asList(array);
        final Validator validator = NumberValidator.getCurrencyInstance();

        validator.validate(list);

        final Iterator i = list.iterator();
        assertEquals(1d, ((Number)i.next()).doubleValue(), 0.0001);
        assertEquals(1.07d, ((Number)i.next()).doubleValue(), 0.0001);
        assertEquals(-.45d, ((Number)i.next()).doubleValue(), 0.0001);
        assertFalse(i.hasNext());
    }

    public void testValidate_Percent() throws InvalidArgumentException {
        final NumberFormat format = NumberFormat.getPercentInstance();
        
        final Object[] array 
            = new Object[] { 
                format.format(.01), 
                format.format(1.07),
                format.format(-.45),
                format.format(0.001) };
        final List list = Arrays.asList(array);
        final Validator validator = NumberValidator.getPercentInstance();

        validator.validate(list);

        final Iterator i = list.iterator();
        assertEquals(0.01d, ((Number)i.next()).doubleValue(), 0.0001);
        assertEquals(1.07d, ((Number)i.next()).doubleValue(), 0.0001);
        assertEquals(-.45d, ((Number)i.next()).doubleValue(), 0.0001);
        assertEquals(0.00001d, ((Number)i.next()).doubleValue(), 0.0001);
        assertFalse(i.hasNext());
    }

    public void testValidate_Integer() throws InvalidArgumentException {
        final Object[] array = new Object[] { "1", "107", "-45" };
        final List list = Arrays.asList(array);
        final Validator validator = NumberValidator.getIntegerInstance();

        validator.validate(list);

        final Iterator i = list.iterator();
        assertEquals(1d, ((Number)i.next()).doubleValue(), 0.0001);
        assertEquals(107d, ((Number)i.next()).doubleValue(), 0.0001);
        assertEquals(-45d, ((Number)i.next()).doubleValue(), 0.0001);
        assertFalse(i.hasNext());
    }

    public void testValidate_ExcessChars() {
        final Object[] array = new Object[] { "10DowningStreet"};
        final List list = Arrays.asList(array);
        final Validator validator = NumberValidator.getIntegerInstance();

        try{
            validator.validate(list);
            fail("InvalidArgumentException");
        }
        catch(InvalidArgumentException e){
            assertEquals("10DowningStreet",e.getMessage());
        }
    }

    public void testValidate_Maximum() {
        final Object[] array = new Object[] { "1", "107" };
        final List list = Arrays.asList(array);
        final NumberValidator validator = NumberValidator.getIntegerInstance();
        validator.setMaximum(new Integer(100));

        try {
            validator.validate(list);
            fail("107 too big");
        }
        catch (InvalidArgumentException ive) {
            assertEquals("Out of range: 107", ive.getMessage());
        }
    }

    public void testValidate_Minimum() {
        final Object[] array = new Object[] { "107", "1" };
        final List list = Arrays.asList(array);
        final NumberValidator validator = NumberValidator.getIntegerInstance();
        validator.setMinimum(new Integer(100));

        try {
            validator.validate(list);
            fail("1 too small");
        }
        catch (InvalidArgumentException ive) {
            assertEquals("Out of range: 1", ive.getMessage());
        }
    }
}
