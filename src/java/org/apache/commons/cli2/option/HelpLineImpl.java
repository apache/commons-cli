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
package org.apache.commons.cli2.option;

import java.util.Comparator;
import java.util.Set;

import org.apache.commons.cli2.HelpLine;
import org.apache.commons.cli2.Option;

/**
 * Represents a line in the help screen.
 */
public class HelpLineImpl implements HelpLine {

    /** The option that this HelpLineImpl describes */
    private final Option option;

    /** The level of indenting for this item */
    private final int indent;

    /** The help settings used to obtain the previous usage */
    private transient Set cachedHelpSettings;
    
    /** The comparator used to obtain the previous usage */
    private transient Comparator cachedComparator;
    
    /** The previously obtained usage */
    private transient String cachedUsage;
    
    /**
     * Creates a new HelpLineImpl to represent a particular Option in the online
     * help.
     * 
     * @param option
     *            Option that the HelpLineImpl describes
     * @param indent
     *            Level of indentation for this line
     */
    public HelpLineImpl(final Option option, final int indent) {
        this.option = option;
        this.indent = indent;
    }

    /**
     * @return The description of the option
     */
    public String getDescription() {
        return option.getDescription();
    }

    /**
     * @return The level of indentation for this line
     */
    public int getIndent() {
        return indent;
    }

    /**
     * @return The Option that the help line relates to
     */
    public Option getOption() {
        return option;
    }
    
    /**
     * Builds a usage string for the option using the specified settings and 
     * comparator.
     * 
     *  
     * @param helpSettings the settings to apply
     * @param comparator a comparator to sort options when applicable
     * @return the usage string
     */
    public String usage(final Set helpSettings, final Comparator comparator) {
        if (cachedUsage == null
            || cachedHelpSettings != helpSettings
            || cachedComparator != comparator) {
            
            // cache the arguments to avoid redoing work
            cachedHelpSettings = helpSettings;
            cachedComparator = comparator;
            
            // build the new buffer
            final StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < indent; ++i) {
                buffer.append("  ");
            }
            option.appendUsage(buffer, helpSettings, comparator);
            
            // cache the usage string
            cachedUsage = buffer.toString();
        }
        return cachedUsage;
    }
}
