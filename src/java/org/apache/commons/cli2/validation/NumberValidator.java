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
import java.text.ParsePosition;
import java.util.List;
import java.util.ListIterator;

/**
 * A Validator instance that parses Numbers
 */
public class NumberValidator implements Validator {

    /**
     * @return an instance using local currency format
     */
    public static NumberValidator getCurrencyInstance() {
        return new NumberValidator(NumberFormat.getCurrencyInstance());
    }

    /**
     * @return an instance using local integer format
     */
    public static NumberValidator getIntegerInstance() {
        final NumberFormat format = NumberFormat.getNumberInstance();
        format.setParseIntegerOnly(true);
        return new NumberValidator(format);
    }

    /**
     * @return an instance using local percent format
     */
    public static NumberValidator getPercentInstance() {
        return new NumberValidator(NumberFormat.getPercentInstance());
    }

    /**
     * @return an instance using local number format
     */
    public static NumberValidator getNumberInstance() {
        return new NumberValidator(NumberFormat.getNumberInstance());
    }

    private final NumberFormat format;
    private Number minimum = null;
    private Number maximum = null;

    /**
     * Creates a new NumberValidator
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
     * @return the format of a valid Number
     */
    public NumberFormat getFormat() {
        return format;
    }

    /**
     * @return the maximum value for a valid Number
     */
    public Number getMaximum() {
        return maximum;
    }

    /**
     * @param maximum the maximum value for a valid Number
     */
    public void setMaximum(Number maximum) {
        this.maximum = maximum;
    }

    /**
     * @return the minimum value for a valid Number
     */
    public Number getMinimum() {
        return minimum;
    }

    /**
     * @param minimum the minimum value for a valid Number
     */
    public void setMinimum(Number minimum) {
        this.minimum = minimum;
    }
}
