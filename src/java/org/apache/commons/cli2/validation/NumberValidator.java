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

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.List;
import java.util.ListIterator;

/**
 * The <code>NumberValidator</code> validates the string argument
 * values are numbers.  If the value is a number, the string value in
 * the {@link java.util.List} of values is replaced with the
 * {@link java.lang.Number} instance.
 *
 * A maximum and minimum value can also be specified using 
 * the {@link #setMaximum setMaximum}, and the 
 * {@link #setMinimum setMinimum} methods.
 *
 * The following example shows how to limit the valid values
 * for the age attribute to integers less than 100.
 *
 * <pre>
 * ...
 * ArgumentBuilder builder = new ArgumentBuilder();
 * NumberValidator validator = NumberValidator.getIntegerInstance();
 * validator.setMaximum(new Integer(100));
 * 
 * Argument age = 
 *     builder.withName("age");
 *            .withValidator(validator);
 * </pre>
 * 
 * @author Rob Oxspring
 * @author John Keyes
 */
public class NumberValidator implements Validator {

    /**
     * Returns a <code>NumberValidator</code> for a currency format 
     * for the current default locale.
     * @return a <code>NumberValidator</code> for a currency format 
     * for the current default locale.
     */
    public static NumberValidator getCurrencyInstance() {
        return new NumberValidator(NumberFormat.getCurrencyInstance());
    }

    /**
     * Returns a <code>NumberValidator</code> for an integer number format 
     * for the current default locale.
     * @return a <code>NumberValidator</code> for an integer number format 
     * for the current default locale.
     */
    public static NumberValidator getIntegerInstance() {
        final NumberFormat format = NumberFormat.getNumberInstance();
        format.setParseIntegerOnly(true);
        return new NumberValidator(format);
    }

    /**
     * Returns a <code>NumberValidator</code> for a percentage format 
     * for the current default locale.
     * @return a <code>NumberValidator</code> for a percentage format 
     * for the current default locale.
     */
    public static NumberValidator getPercentInstance() {
        return new NumberValidator(NumberFormat.getPercentInstance());
    }

    /**
     * Returns a <code>NumberValidator</code> for a general-purpose 
     * number format for the current default locale.
     * @returns a <code>NumberValidator</code> for a general-purpose 
     * number format for the current default locale.
     */
    public static NumberValidator getNumberInstance() {
        return new NumberValidator(NumberFormat.getNumberInstance());
    }

    /** the <code>NumberFormat</code> being used. */
    private NumberFormat format;

    /** the lower bound for argument values. */
    private Number minimum = null;
    
    /** the upper bound for argument values */
    private Number maximum = null;

    /**
     * Creates a new NumberValidator.
     */
    public NumberValidator() {
        this(NumberFormat.getInstance());
    }

    /**
     * Creates a new NumberValidator based on the specified NumberFormat
     * @param format the format of numbers to accept
     */
    public NumberValidator(final NumberFormat format) {
        this.format = format;
    }

    /**
     * Validate the list of values against the list of permitted values.
     * If a value is valid, replace the string in the <code>values</code>
     * {@link java.util.List} with the {@link java.lang.Number} instance.
     * 
     * @see org.apache.commons.cli2.validation.Validator#validate(java.util.List)
     */
    public void validate(final List values) throws InvalidArgumentException {
        for (final ListIterator i = values.listIterator(); i.hasNext();) {
            final String value = (String)i.next();

            final ParsePosition pp = new ParsePosition(0);
            final Number number = format.parse(value, pp);
            if (pp.getIndex() < value.length()) {
                throw new InvalidArgumentException(value);
            }

            if ((minimum != null
                && number.doubleValue() < minimum.doubleValue())
                || (maximum != null
                    && number.doubleValue() > maximum.doubleValue())) {
                throw new InvalidArgumentException("Out of range: " + value);
            }

            i.set(number);
        }
    }

    /**
     * Return the format being used to validate argument values against.
     *
     * @return the format being used to validate argument values against.
     */
    public NumberFormat getFormat() {
        return format;
    }

    /**
     * Specify the format being used to validate argument values against.
     *
     * @param format the format being used to validate argument values against.
     */
    public void setFormat(NumberFormat format) {
        this.format = format;
    }
    
    /**
     * Return the maximum value allowed for an argument value.
     *
     * @return the maximum value allowed for an argument value.
     */
    public Number getMaximum() {
        return maximum;
    }

    /**
     * Specify the maximum value allowed for an argument value.
     *
     * @param maximum the maximum value allowed for an argument value.
     */
    public void setMaximum(Number maximum) {
        this.maximum = maximum;
    }

    /**
     * Return the minimum value allowed for an argument value.
     *
     * @return the minimum value allowed for an argument value.
     */
    public Number getMinimum() {
        return minimum;
    }

    /**
     * Specify the minimum value allowed for an argument value.
     *
     * @param minimum the minimum value allowed for an argument value.
     */
    public void setMinimum(Number minimum) {
        this.minimum = minimum;
    }
}
