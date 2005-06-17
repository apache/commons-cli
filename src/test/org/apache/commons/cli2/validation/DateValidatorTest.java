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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

/**
 * @author Rob Oxspring
 * @author John Keyes
 */
public class DateValidatorTest extends TestCase {
    public static final DateFormat D_M_YY = new SimpleDateFormat("d/M/yy");
    public static final DateFormat YYYY_MM_YY =
        new SimpleDateFormat("yyyy-MM-dd");
    private List formats = Arrays.asList(new Object[] { D_M_YY, YYYY_MM_YY });

    public void testValidate() throws InvalidArgumentException {
        final Object[] array = new Object[] { "23/12/03", "2002-10-12" };
        final List list = Arrays.asList(array);
        final Validator validator = new DateValidator(formats);

        validator.validate(list);

        final Iterator i = list.iterator();
        assertEquals("2003-12-23", YYYY_MM_YY.format((Date)i.next()));
        assertEquals("2002-10-12", YYYY_MM_YY.format((Date)i.next()));
        assertFalse(i.hasNext());
    }

    public void testMinimumBounds() throws InvalidArgumentException {
        final DateValidator validator = new DateValidator(formats);
        final Calendar cal = Calendar.getInstance();

        {
            final Object[] array = new Object[] { "23/12/03", "2002-10-12" };
            final List list = Arrays.asList(array);
            cal.set(2002, 1, 12);
            final Date min = cal.getTime();
            validator.setMinimum(min);
            validator.validate(list);
        }

        {
            final Object[] array = new Object[] { "23/12/03", "2002-10-12" };
            final List list = Arrays.asList(array);
            cal.set(2003, 1, 12);
            final Date min = cal.getTime();
            validator.setMinimum(min);

            try {
                validator.validate(list);
                fail("minimum out of bounds exception not caught");
            }
            catch (final InvalidArgumentException exp) {
                assertEquals("Out of range: 2002-10-12", exp.getMessage());
            }
        }
    }

    public void testMaximumBounds() throws InvalidArgumentException {
        final DateValidator validator = new DateValidator(formats);
        final Calendar cal = Calendar.getInstance();

        {
            final Object[] array = new Object[] { "23/12/03", "2002-10-12" };
            final List list = Arrays.asList(array);
            cal.set(2004, 1, 12);
            final Date max = cal.getTime();
            validator.setMaximum(max);
            validator.validate(list);
        }

        {
            final Object[] array = new Object[] { "23/12/03", "2004-10-12" };
            final List list = Arrays.asList(array);
            cal.set(2004, 1, 12);
            final Date max = cal.getTime();
            validator.setMaximum(max);

            try {
                validator.validate(list);
                fail("maximum out of bounds exception not caught");
            }
            catch (final InvalidArgumentException exp) {
                assertEquals("Out of range: 2004-10-12", exp.getMessage());
            }
        }
    }

}
