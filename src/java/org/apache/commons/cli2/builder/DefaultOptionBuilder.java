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
package org.apache.commons.cli2.builder;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli2.Argument;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.option.DefaultOption;

/**
 * Builds DefaultOption instances.
 */
public class DefaultOptionBuilder {

    private final String shortPrefix;
    private final String longPrefix;
    private final boolean burstEnabled;

    private String preferredName;
    private Set aliases;
    private Set burstAliases;
    private boolean required;

    private String description;
    private Argument argument;
    private Group children;
    private int id;

    /**
     * Creates a new DefaultOptionBuilder using defaults
     * @see DefaultOption#DEFAULT_SHORT_PREFIX
     * @see DefaultOption#DEFAULT_LONG_PREFIX
     * @see DefaultOption#DEFAULT_BURST_ENABLED
     */
    public DefaultOptionBuilder() {
        this(
            DefaultOption.DEFAULT_SHORT_PREFIX,
            DefaultOption.DEFAULT_LONG_PREFIX,
            DefaultOption.DEFAULT_BURST_ENABLED);
    }

    /**
     * Creates a new DefaultOptionBuilder
     * @param shortPrefix the prefix to use for short options
     * @param longPrefix the prefix to use for long options
     * @param burstEnabled whether to allow gnu style bursting
     * @throws IllegalArgumentException if either prefix is less than on 
     *                                  character long
     */
    public DefaultOptionBuilder(
        final String shortPrefix,
        final String longPrefix,
        final boolean burstEnabled) throws IllegalArgumentException{
        if (shortPrefix == null || shortPrefix.length() == 0) {
            throw new IllegalArgumentException("shortPrefix should be at least 1 character long");
        }
        else {
            this.shortPrefix = shortPrefix;
        }

        if (longPrefix == null || longPrefix.length() == 0) {
            throw new IllegalArgumentException("longPrefix should be at least 1 character long");
        }
        else {
            this.longPrefix = longPrefix;
        }

        this.burstEnabled = burstEnabled;
        reset();
    }

    /**
     * Creates a DefaultOption instance
     * @return the new instance
     * @throws IllegalStateException if no names have been supplied
     */
    public DefaultOption create() throws IllegalStateException {
        if (preferredName == null) {
            throw new IllegalStateException("Options must have at least one name");
        }

        final DefaultOption option =
            new DefaultOption(
                shortPrefix,
                longPrefix,
                burstEnabled,
                preferredName,
                description,
                aliases,
                burstAliases,
                required,
                argument,
                children,
                id);

        reset();

        return option;
    }
    
    /**
     * Resets the builder
     */
    public DefaultOptionBuilder reset() {
        preferredName = null;
        description = null;
        aliases = new HashSet();
        burstAliases = new HashSet();
        required = false;
        argument = null;
        children = null;
        id = 0;
        return this;
    }

    /**
     * Use this short option name. The first name is used as the preferred
     * display name for the Command and then later names are used as aliases.
     * 
     * @param shortName the name to use
     * @return this builder
     */
    public DefaultOptionBuilder withShortName(final String shortName) {
        final String name = shortPrefix + shortName;

        if (preferredName == null) {
            preferredName = name;
        }
        else {
            aliases.add(name);
        }

        if (burstEnabled && name.length() == shortPrefix.length() + 1) {
            burstAliases.add(name);
        }

        return this;
    }

    /**
     * Use this long option name.  The first name is used as the preferred
     * display name for the Command and then later names are used as aliases.
     * 
     * @param longName the name to use
     * @return this builder
     */
    public DefaultOptionBuilder withLongName(final String longName) {
        final String name = longPrefix + longName;
        if (preferredName == null) {
            preferredName = name;
        }
        else {
            aliases.add(name);
        }
        return this;
    }

    /**
     * Use this option description
     * @param newDescription the description to use
     * @return this builder
     */
    public DefaultOptionBuilder withDescription(final String newDescription) {
        this.description = newDescription;
        return this;
    }

    /**
     * Use this optionality
     * @param newRequired true iff the Option is required
     * @return this builder
     */
    public DefaultOptionBuilder withRequired(final boolean newRequired) {
        this.required = newRequired;
        return this;
    }

    /**
     * Use this child Group
     * @param newChildren the child Group to use
     * @return this builder
     */
    public DefaultOptionBuilder withChildren(final Group newChildren) {
        this.children = newChildren;
        return this;
    }

    /**
     * Use this Argument
     * @param newArgument the argument to use
     * @return this builder
     */
    public DefaultOptionBuilder withArgument(final Argument newArgument) {
        this.argument = newArgument;
        return this;
    }

    /**
     * Sets the id
     * 
     * @param newId
     *            the id of the DefaultOption
     * @return this DefaultOptionBuilder
     */
    public final DefaultOptionBuilder withId(final int newId) {
        this.id = newId;
        return this;
    }
}
