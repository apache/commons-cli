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

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A Validator for a list of known string values.
 * 
 * @author John Keyes
 */
public class EnumValidator implements Validator {

    /** List of permitted values */
    private Set validValues;

    /**
     * Creates a new StringValidator for the specified values.
     * 
     * @param values
     *            the list of permitted values
     */
    public EnumValidator(final Set values) {
        this.validValues = values;
    }

    /**
     * Validate the list of values against the list of permitted values.
     * 
     * @see org.apache.commons.cli2.validation.Validator#validate(java.util.List)
     */
    public void validate(final List values) throws InvalidArgumentException {
        for (final Iterator iter = values.iterator(); iter.hasNext();) {
            final String value = (String)iter.next();

            if (!this.validValues.contains(value)) {
                throw new InvalidArgumentException(
                    "'"
                        + value
                        + "' is not allowed.  Permitted values are:"
                        + getValuesAsString());
            }
        }
    }

    /**
     * Returns the permitted values in a String
     * 
     * @return String formatted list of values
     */
    private String getValuesAsString() {
        final StringBuffer buff = new StringBuffer();

        buff.append("[");

        for (final Iterator iter = this.validValues.iterator();
            iter.hasNext();
            ) {

            buff.append("'").append(iter.next()).append("'");

            if (iter.hasNext()) {
                buff.append(", ");
            }
        }

        buff.append("]");

        return buff.toString();
    }
}
