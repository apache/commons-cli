/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.cli;

import java.util.Collection;
import java.util.Iterator;

/**
 * Exception thrown when an option can't be identified from a partial name.
 * 
 * @author Emmanuel Bourg
 * @version $Revision$, $Date$
 * @since 1.3
 */
public class AmbiguousOptionException extends UnrecognizedOptionException
{
    /** The list of options matching the partial name specified */
    private Collection matchingOptions;

    /**
     * Constructs a new AmbiguousOptionException.
     *
     * @param option          the partial option name
     * @param matchingOptions the options matching the name
     */
    public AmbiguousOptionException(String option, Collection matchingOptions)
    {
        super(createMessage(option, matchingOptions), option);
        this.matchingOptions = matchingOptions;
    }

    /**
     * Returns the options matching the partial name.
     */
    public Collection getMatchingOptions()
    {
        return matchingOptions;
    }

    /**
     * Build the exception message from the specified list of options.
     * 
     * @param option
     * @param matchingOptions
     * @return
     */
    private static String createMessage(String option, Collection matchingOptions)
    {
        StringBuffer buff = new StringBuffer("Ambiguous option: '");
        buff.append(option);
        buff.append("'  (could be: ");

        Iterator it = matchingOptions.iterator();
        while (it.hasNext())
        {
            buff.append("'");
            buff.append(it.next());
            buff.append("'");
            if (it.hasNext())
            {
                buff.append(", ");
            }
        }
        buff.append(")");

        return buff.toString();
    }
}
