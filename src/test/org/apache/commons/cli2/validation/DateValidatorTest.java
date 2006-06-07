/*
 * Copyright 2003-2005 The Apache Software Foundation
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

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.cli2.resource.ResourceConstants;
import org.apache.commons.cli2.resource.ResourceHelper;

/**
 * JUnit test case for DateValidator.
 *
 * @author Rob Oxspring
 * @author John Keyes
 */
public class DateValidatorTest
    extends TestCase {
    private static final ResourceHelper resources = ResourceHelper.getResourceHelper();
    public static final DateFormat D_M_YY = new SimpleDateFormat("d/M/yy");
    public static final DateFormat YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");
    private List formats = Arrays.asList(new Object[] { D_M_YY, YYYY_MM_DD });

    public void testSingleFormatValidate()
        throws InvalidArgumentException {
        final Object[] array = new Object[] { "23/12/03" };
        final List list = Arrays.asList(array);
        final Validator validator = new DateValidator(D_M_YY);

        validator.validate(list);

        final Iterator i = list.iterator();
        assertEquals("2003-12-23", YYYY_MM_DD.format((Date) i.next()));
        assertFalse(i.hasNext());
    }

    public void testDefaultDateFormatValidate()
        throws InvalidArgumentException {
        final Object[] array = new Object[] { "23-Dec-2003" };
        final List list = Arrays.asList(array);
        final Validator validator = new DateValidator( new SimpleDateFormat("dd-MMM-yyyy") );

        validator.validate(list);

        final Iterator i = list.iterator();
        // CLI-40: For some reason, the YYYY_MM_DD object gets quite 
        // confused here and returns 2003-12-22. If we make a new one 
        // there is no problem.
        assertEquals("2003-12-23", new SimpleDateFormat("yyyy-MM-dd").format((Date) i.next()));
        assertFalse(i.hasNext());
    }

    public void testDefaultTimeFormatValidate()
        throws InvalidArgumentException {
        final Object[] array = new Object[] { "18:00:00" };
        final List list = Arrays.asList(array);
        final Validator validator = new DateValidator( new SimpleDateFormat("HH:mm:ss") );

        validator.validate(list);

        final Iterator i = list.iterator();
        final DateFormat df = new SimpleDateFormat("HH:mm:ss");
        assertEquals("18:00:00", df.format((Date) i.next()));
        assertFalse(i.hasNext());
    }

    public void testDefaultDateTimeFormatValidate()
        throws InvalidArgumentException {
        final Object[] array = new Object[] { "23-Jan-2003 18:00:00" };
        final List list = Arrays.asList(array);
        final Validator validator = new DateValidator( new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss") );

        validator.validate(list);

        final Iterator i = list.iterator();
        final DateFormat df = new SimpleDateFormat("yyyy/M/dd HH:mm:ss");
        assertEquals("2003/1/23 18:00:00", df.format((Date) i.next()));
        assertFalse(i.hasNext());
    }

    public void testDefaultValidator()
        throws InvalidArgumentException {
        final Object[] array = new Object[] { "23/01/03 18:00" };
        final List list = Arrays.asList(array);
        final Validator validator = new DateValidator(new SimpleDateFormat("dd/MM/yy HH:mm"));

        validator.validate(list);

        final Iterator i = list.iterator();
        final DateFormat df = new SimpleDateFormat("yyyy/M/dd HH:mm:ss");
        assertEquals("2003/1/23 18:00:00", df.format((Date) i.next()));
        assertFalse(i.hasNext());
    }

    public void testValidate()
        throws InvalidArgumentException {
        final Object[] array = new Object[] { "23/12/03", "2002-10-12" };
        final List list = Arrays.asList(array);
        final Validator validator = new DateValidator(formats);

        validator.validate(list);

        final Iterator i = list.iterator();
        assertEquals("2003-12-23", YYYY_MM_DD.format((Date) i.next()));
        assertEquals("2002-10-12", YYYY_MM_DD.format((Date) i.next()));
        assertFalse(i.hasNext());
    }

    public void testMinimumBounds()
        throws InvalidArgumentException {
        final DateValidator validator = new DateValidator(formats);
        final Calendar cal = Calendar.getInstance();

        {
            final Object[] array = new Object[] { "23/12/03", "2002-10-12" };
            final List list = Arrays.asList(array);
            cal.set(2002, 1, 12);

            final Date min = cal.getTime();
            validator.setMinimum(min);
            assertTrue("maximum bound is set", validator.getMaximum() == null);
            assertEquals("minimum bound is incorrect", min, validator.getMinimum());
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
            } catch (final InvalidArgumentException exp) {
                assertEquals(resources.getMessage(ResourceConstants.DATEVALIDATOR_DATE_OUTOFRANGE,
                                                  new Object[] { "2002-10-12" }), exp.getMessage());
            }
        }
    }

    public void testFormats()
        throws InvalidArgumentException {
        final DateValidator validator = new DateValidator(formats);
        assertEquals("date format is incorrect", ((SimpleDateFormat) formats.get(0)).toPattern(),
                     ((SimpleDateFormat) validator.getFormats()[0]).toPattern());
        assertEquals("date format is incorrect", ((SimpleDateFormat) formats.get(1)).toPattern(),
                     ((SimpleDateFormat) validator.getFormats()[1]).toPattern());
    }

    public void testMaximumBounds()
        throws InvalidArgumentException {
        final DateValidator validator = new DateValidator(formats);
        final Calendar cal = Calendar.getInstance();

        {
            final Object[] array = new Object[] { "23/12/03", "2002-10-12" };
            final List list = Arrays.asList(array);
            cal.set(2004, 1, 12);

            final Date max = cal.getTime();
            validator.setMaximum(max);
            assertTrue("minimum bound is set", validator.getMinimum() == null);
            assertEquals("maximum bound is incorrect", max, validator.getMaximum());
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
            } catch (final InvalidArgumentException exp) {
                assertEquals(resources.getMessage(ResourceConstants.DATEVALIDATOR_DATE_OUTOFRANGE,
                                                  new Object[] { "2004-10-12" }), exp.getMessage());
            }
        }
    }

    public static Test suite() {
        Test result = new TestSuite(DateValidatorTest.class); // default behavior
        result = new TimeZoneTestSuite("EST", result); // ensure it runs in EST timezone

        return result;
    }
}
