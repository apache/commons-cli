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
import java.text.ParsePosition;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

/**
 * @author John Keyes
 * 
 * @see java.text.DateFormat
 */
public class DateValidator implements Validator {

    /** an array of permitted DateFormats */
    private final DateFormat[] formats;

    /** minimum Date allowed i.e: a valid date occurs later than this date */
    private Date minimum;

    /** maximum Date allowed i.e: a valid date occurs earlier than this date */
    private Date maximum;

    /**
     * Creates a Validator for dates.
     * 
     * @return DateValidator a Validator for dates
     */
    public static DateValidator getDateInstance() {
        return new DateValidator(DateFormat.getDateInstance());
    }

    /**
     * Creates a Validator for times.
     * 
     * @return DateValidator a Validator for times
     */
    public static DateValidator getTimeInstance() {
        return new DateValidator(DateFormat.getTimeInstance());
    }

    /**
     * Creates a Validator for date/times
     * 
     * @return DateValidator a Validator for date/times
     */
    public static DateValidator getDateTimeInstance() {
        return new DateValidator(DateFormat.getDateTimeInstance());
    }

    /**
     * Creates a Validator for the default date/time format
     */
    public DateValidator() {
        this(DateFormat.getInstance());
    }

    /**
     * Creates a Validator for the specified DateFormat.
     * 
     * @param format
     *            a DateFormat which dates must conform to
     */
    public DateValidator(final DateFormat format) {
        this.formats = new DateFormat[] { format };
    }

    /**
     * Creates a Validator for the List of specified DateFormats.
     * 
     * @param formats
     *            a List of DateFormats which dates must conform to
     */
    public DateValidator(final List formats) {
        this.formats =
            (DateFormat[])formats.toArray(new DateFormat[formats.size()]);
    }

    /**
     * Validate each String value in the specified List against this instances
     * permitted DateFormats.
     * 
     * If a value is valid then it's <code>String</code> value in the list is
     * replaced with it's <code>Date</code> value.
     * 
     * @see org.apache.commons.cli2.validation.Validator#validate(java.util.List)
     */
    public void validate(final List values) throws InvalidArgumentException {

        // for each value
        for (final ListIterator i = values.listIterator(); i.hasNext();) {

            final String value = (String)i.next();

            Date date = null;

            // create a resuable ParsePosition instance
            final ParsePosition pp = new ParsePosition(0);

            // for each permitted DateFormat
            for (int f = 0; f < this.formats.length && date == null; ++f) {

                // reset the parse position
                pp.setIndex(0);

                // TODO: should we call setLenient(false) on
                //       each DateFormat or allow the user
                //       to specify the parsing used
                date = this.formats[f].parse(value, pp);

                // if the wrong number of characters have been parsed
                if (pp.getIndex() < value.length()) {
                    date = null;
                }
            }

            // if date has not been set throw an InvalidArgumentException
            if (date == null) {
                throw new InvalidArgumentException(value);
            }

            // if the date is outside the bounds
            if (isDateEarlier(date) || isDateLater(date)) {
                throw new InvalidArgumentException("Out of range: " + value);
            }

            // replace the value in the list with the actual Date
            i.set(date);
        }
    }

    /**
     * Returns the maximum date permitted.
     * 
     * @return Date the maximum date permitted. If no maximum date has been
     *         specified then return <code>null</code>.
     */
    public Date getMaximum() {
        return maximum;
    }

    /**
     * Sets the maximum Date to the specified value.
     * 
     * @param maximum
     *            the maximum Date permitted
     */
    public void setMaximum(final Date maximum) {
        this.maximum = maximum;
    }

    /**
     * Returns the minimum date permitted.
     * 
     * @return Date the minimum date permitted. If no minimum date has been
     *         specified then return <code>null</code>.
     */
    public Date getMinimum() {
        return minimum;
    }

    /**
     * Sets the minimum Date to the specified value.
     * 
     * @param minimum
     *            the minimum Date permitted
     */
    public void setMinimum(Date minimum) {
        this.minimum = minimum;
    }

    /**
     * Returns whether the specified Date is later than the maximum date.
     * 
     * @param date
     *            the Date to evaluate
     * 
     * @return boolean whether <code>date</code> is earlier than the maximum
     *         date
     */
    private boolean isDateLater(Date date) {
        return maximum != null && date.getTime() > maximum.getTime();
    }

    /**
     * Returns whether the specified Date is earlier than the minimum date.
     * 
     * @param date
     *            the Date to evaluate
     * 
     * @return boolean whether <code>date</code> is earlier than the minimum
     *         date
     */
    private boolean isDateEarlier(Date date) {
        return minimum != null && date.getTime() < minimum.getTime();
    }
}
