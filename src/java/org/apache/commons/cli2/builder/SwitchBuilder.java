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
package org.apache.commons.cli2.builder;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli2.Argument;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.option.Switch;
import org.apache.commons.cli2.resource.ResourceConstants;
import org.apache.commons.cli2.resource.ResourceHelper;

/**
 * Builds Switch instance.
 */
public class SwitchBuilder {
    private final String enabledPrefix;
    private final String disabledPrefix;
    private String description;
    private String preferredName;
    private Set aliases;
    private boolean required;
    private Argument argument;
    private Group children;
    private int id;
    private Boolean switchDefault;

    /**
     * Creates a new SwitchBuilder using defaults.
     * @see Switch#DEFAULT_ENABLED_PREFIX
     * @see Switch#DEFAULT_DISABLED_PREFIX
     */
    public SwitchBuilder() {
        this(Switch.DEFAULT_ENABLED_PREFIX, Switch.DEFAULT_DISABLED_PREFIX);
    }

    /**
     * Creates a new SwitchBuilder
     * @param enabledPrefix the prefix to use for enabling the option
     * @param disabledPrefix the prefix to use for disabling the option
     * @throws IllegalArgumentException if either prefix is less than 1
     *                                  character long or the prefixes match
     */
    public SwitchBuilder(final String enabledPrefix,
                         final String disabledPrefix)
        throws IllegalArgumentException {
        if ((enabledPrefix == null) || (enabledPrefix.length() < 1)) {
            throw new IllegalArgumentException(ResourceHelper.getResourceHelper().getMessage(ResourceConstants.SWITCH_ILLEGAL_ENABLED_PREFIX));
        }

        if ((disabledPrefix == null) || (disabledPrefix.length() < 1)) {
            throw new IllegalArgumentException(ResourceHelper.getResourceHelper().getMessage(ResourceConstants.SWITCH_ILLEGAL_DISABLED_PREFIX));
        }

        if (enabledPrefix.equals(disabledPrefix)) {
            throw new IllegalArgumentException(ResourceHelper.getResourceHelper().getMessage(ResourceConstants.SWITCH_IDENTICAL_PREFIXES));
        }

        this.enabledPrefix = enabledPrefix;
        this.disabledPrefix = disabledPrefix;
        reset();
    }

    /**
     * Creates a new Switch instance
     * @return a new Switch instance
     */
    public Switch create() {
        final Switch option =
            new Switch(enabledPrefix, disabledPrefix, preferredName, aliases, description,
                       required, argument, children, id, switchDefault);

        reset();

        return option;
    }

    /**
     * Resets the builder
     */
    public SwitchBuilder reset() {
        description = null;
        preferredName = null;
        required = false;
        aliases = new HashSet();
        argument = null;
        children = null;
        id = 0;
        switchDefault = null;

        return this;
    }

    /**
     * Use this option description
     * @param newDescription the description to use
     * @return this builder
     */
    public SwitchBuilder withDescription(final String newDescription) {
        this.description = newDescription;

        return this;
    }

    /**
     * Use this option name. The first name is used as the preferred
     * display name for the Command and then later names are used as aliases.
     *
     * @param name the name to use
     * @return this builder
     */
    public SwitchBuilder withName(final String name) {
        if (preferredName == null) {
            preferredName = name;
        } else {
            aliases.add(name);
        }

        return this;
    }

    /**
     * Use this optionality
     * @param newRequired true iff the Option is required
     * @return this builder
     */
    public SwitchBuilder withRequired(final boolean newRequired) {
        this.required = newRequired;

        return this;
    }

    /**
     * Use this Argument
     * @param newArgument the argument to use
     * @return this builder
     */
    public SwitchBuilder withArgument(final Argument newArgument) {
        this.argument = newArgument;

        return this;
    }

    /**
     * Use this child Group
     * @param newChildren the child Group to use
     * @return this builder
     */
    public SwitchBuilder withChildren(final Group newChildren) {
        this.children = newChildren;

        return this;
    }

    /**
     * Sets the id
     *
     * @param newId
     *            the id of the Switch
     * @return this SwitchBuilder
     */
    public final SwitchBuilder withId(final int newId) {
        this.id = newId;

        return this;
    }

    /**
     * Sets the default state for this switch
     *
     * @param newSwitchDefault the default state
     * @return this SwitchBuilder
     */
    public final SwitchBuilder withSwitchDefault(final Boolean newSwitchDefault) {
        this.switchDefault = newSwitchDefault;

        return this;
    }
}
