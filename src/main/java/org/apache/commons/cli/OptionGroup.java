/*
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      https://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package org.apache.commons.cli;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A group of mutually exclusive options.
 */
public class OptionGroup implements Serializable {

    /** The serial version UID. */
    private static final long serialVersionUID = 1L;

    /** Hold the options */
    private final Map<String, Option> optionMap = new LinkedHashMap<>();

    /** The name of the selected option */
    private String selected;

    /** Specified whether this group is required */
    private boolean required;

    /**
     * Constructs a new instance.
     */
    public OptionGroup() {
        // empty
    }

    /**
     * Adds the given {@code Option} to this group.
     *
     * @param option the option to add to this group
     * @return this option group with the option added
     */
    public OptionGroup addOption(final Option option) {
        // key - option name
        // value - the option
        optionMap.put(option.getKey(), option);
        return this;
    }

    /**
     * Gets the names of the options in this group as a {@code Collection}.
     *
     * @return the names of the options in this group as a {@code Collection}.
     */
    public Collection<String> getNames() {
        // the key set is the collection of names
        return optionMap.keySet();
    }

    /**
     * Gets the options in this group as a {@code Collection}.
     *
     * @return the options in this group as a {@code Collection}.
     */
    public Collection<Option> getOptions() {
        // the values are the collection of options
        return optionMap.values();
    }

    /**
     * Gets the selected option name.
     *
     * If the selected option is deprecated <em>no warning is logged</em>.
     * @return the selected option name.
     */
    public String getSelected() {
        return selected;
    }

    /**
     * Tests whether this option group is required.
     *
     * @return whether this option group is required
     */
    public boolean isRequired() {
        return required;
    }

    /**
     * Tests whether an option is selected.
     *
     *  If an option is selected and is deprecated <em>no warning is logged</em>.
     * @return whether whether an option is selected.
     * @since 1.9.0
     */
    public boolean isSelected() {
        return selected != null;
    }

    /**
     * Sets whether this group is required.
     *
     * @param required whether this group is required.
     */
    public void setRequired(final boolean required) {
        this.required = required;
    }

    /**
     * Sets the selected option of this group to {@code name}.
     *
     * If the selected option is deprecated <em>no warning is logged</em>.
     * @param option the option that is selected
     * @throws AlreadySelectedException if an option from this group has already been selected.
     */
    public void setSelected(final Option option) throws AlreadySelectedException {
        if (option == null) {
            // reset the option previously selected
            selected = null;
            return;
        }
        // if no option has already been selected or the
        // same option is being reselected then set the
        // selected member variable
        if (selected != null && !selected.equals(option.getKey())) {
            throw new AlreadySelectedException(this, option);
        }
        selected = option.getKey();
    }

    /**
     * Returns the stringified version of this OptionGroup.
     *
     * @return the stringified representation of this group
     */
    @Override
    public String toString() {
        final StringBuilder buff = new StringBuilder();
        final Iterator<Option> iter = getOptions().iterator();
        buff.append("[");
        while (iter.hasNext()) {
            final Option option = iter.next();
            if (option.getOpt() != null) {
                buff.append("-");
                buff.append(option.getOpt());
            } else {
                buff.append("--");
                buff.append(option.getLongOpt());
            }

            if (option.getDescription() != null) {
                buff.append(Char.SP);
                buff.append(option.getDescription());
            }

            if (iter.hasNext()) {
                buff.append(", ");
            }
        }
        buff.append("]");
        return buff.toString();
    }
}
