/**
 * Copyright 1999-2001,2004 The Apache Software Foundation.
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
package org.apache.commons.cli;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * A group of mutually exclusive options.
 * @author John Keyes ( john at integralsource.com )
 * @version $Revision$
 */
public class OptionGroup {

    /** hold the options */
    private HashMap optionMap = new HashMap();

    /** the name of the selected option */
    private String selected;

    /** specified whether this group is required */
    private boolean required;

    /**
     * add <code>opt</code> to this group
     *
     * @param opt the option to add to this group
     * @return this option group with opt added
     */
    public OptionGroup addOption(Option opt)
    {
        // key   - option name
        // value - the option
        optionMap.put(opt.getKey(), opt);

        return this;
    }

    /**
     * @return the names of the options in this group as a 
     * <code>Collection</code>
     */
    public Collection getNames()
    {
        // the key set is the collection of names
        return optionMap.keySet();
    }

    /**
     * @return the options in this group as a <code>Collection</code>
     */
    public Collection getOptions()
    {
        // the values are the collection of options
        return optionMap.values();
    }

    /**
     * set the selected option of this group to <code>name</code>.
     * @param opt the option that is selected
     * @throws AlreadySelectedException if an option from this group has 
     * already been selected.
     */
    public void setSelected(Option opt)
                     throws AlreadySelectedException
    {
        // if no option has already been selected or the 
        // same option is being reselected then set the
        // selected member variable
        if ((this.selected == null) || this.selected.equals(opt.getOpt()))
        {
            this.selected = opt.getOpt();
        }
        else
        {
            throw new AlreadySelectedException("an option from this group has "
                                               + "already been selected: '"
                                               + selected + "'");
        }
    }

    /**
     * @return the selected option name
     */
    public String getSelected()
    {
        return selected;
    }

    /**
     * @param required specifies if this group is required
     */
    public void setRequired(boolean required)
    {
        this.required = required;
    }

    /**
     * Returns whether this option group is required.
     *
     * @return whether this option group is required
     */
    public boolean isRequired()
    {
        return this.required;
    }

    /**
     * <p>Returns the stringified version of this OptionGroup.</p>
     * @return the stringified representation of this group
     */
    public String toString()
    {
        StringBuffer buff = new StringBuffer();

        Iterator iter = getOptions().iterator();

        buff.append("[");

        while (iter.hasNext())
        {
            Option option = (Option) iter.next();

            if (option.getOpt() != null)
            {
                buff.append("-");
                buff.append(option.getOpt());
            }
            else
            {
                buff.append("--");
                buff.append(option.getLongOpt());
            }

            buff.append(" ");
            buff.append(option.getDescription());

            if (iter.hasNext())
            {
                buff.append(", ");
            }
        }

        buff.append("]");

        return buff.toString();
    }
}