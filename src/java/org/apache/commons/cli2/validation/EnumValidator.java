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

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli2.resource.ResourceConstants;
import org.apache.commons.cli2.resource.ResourceHelper;

/**
 * The <code>EnumValidator</code> validates the string argument
 * values are valid.
 *
 * The following example shows how to limit the valid values
 * for the color argument to 'red', 'green', or 'blue'.
 *
 * <pre>
 * Set values = new HashSet();
 * values.add("red");
 * values.add("green");
 * values.add("blue");
 * ...
 * ArgumentBuilder builder = new ArgumentBuilder();
 * Argument color =
 *     builder.withName("color");
 *            .withValidator(new EnumValidator(values));
 * </pre>
 *
 * @author John Keyes
 */
public class EnumValidator implements Validator {
    /** List of permitted values */
    private Set validValues;

    /**
     * Creates a new EnumValidator for the specified values.
     *
     * @param values The list of permitted values
     */
    public EnumValidator(final Set values) {
        setValidValues(values);
    }

    /**
     * Validate the list of values against the list of permitted values.
     *
     * @see org.apache.commons.cli2.validation.Validator#validate(java.util.List)
     */
    public void validate(final List values)
        throws InvalidArgumentException {
        for (final Iterator iter = values.iterator(); iter.hasNext();) {
            final String value = (String) iter.next();

            if (!this.validValues.contains(value)) {
                throw new InvalidArgumentException(ResourceHelper.getResourceHelper().getMessage(ResourceConstants.ENUM_ILLEGAL_VALUE,
                                                                                                 new Object[] {
                                                                                                     value,
                                                                                                     getValuesAsString()
                                                                                                 }));
            }
        }
    }

    /**
     * Returns the permitted values in a comma separated String
     *
     * @return String formatted list of values
     */
    String getValuesAsString() {
        final StringBuffer buff = new StringBuffer();

        buff.append("[");

        for (final Iterator iter = this.validValues.iterator(); iter.hasNext();) {
            buff.append("'").append(iter.next()).append("'");

            if (iter.hasNext()) {
                buff.append(", ");
            }
        }

        buff.append("]");

        return buff.toString();
    }

    /**
     * Returns the Set of valid argument values.
     *
     * @return Returns the Set of valid argument values.
     */
    public Set getValidValues() {
        return validValues;
    }

    /**
     * Specifies the Set of valid argument values.
     *
     * @param validValues The Set of valid argument values.
     */
    protected void setValidValues(Set validValues) {
        this.validValues = validValues;
    }
}
