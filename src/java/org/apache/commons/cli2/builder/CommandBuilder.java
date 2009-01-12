/*
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
package org.apache.commons.cli2.builder;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.cli2.Argument;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.option.Command;
import org.apache.commons.cli2.resource.ResourceConstants;
import org.apache.commons.cli2.resource.ResourceHelper;

/**
 * Builds Command instances
 */
public class CommandBuilder {
    /** the preferred name of the command */
    private String preferredName;

    /** the description of the command */
    private String description;

    /** the aliases of the command */
    private Set aliases;

    /** whether the command is required or not */
    private boolean required;

    /** the argument of the command */
    private Argument argument;

    /** the children of the command */
    private Group children;

    /** the id of the command */
    private int id;

    /**
     * Creates a new <code>CommandBuilder</code> instance.
     */
    public CommandBuilder() {
        reset();
    }

    /**
     * Creates a new <code>Command</code> instance using the properties of the
     * <code>CommandBuilder</code>.
     *
     * @return the new Command instance
     */
    public Command create() {
        // check we have a valid name
        if (preferredName == null) {
            throw new IllegalStateException(ResourceHelper.getResourceHelper().getMessage(ResourceConstants.OPTION_NO_NAME));
        }

        // build the command
        final Command option =
            new Command(preferredName, description, aliases, required, argument, children, id);

        // reset the builder
        reset();

        return option;
    }

    /**
     * Resets the CommandBuilder to the defaults for a new Command.
     *
     * This method is called automatically at the end of the
     * {@link #create() create} method.
     * @return this <code>CommandBuilder</code>
     */
    public CommandBuilder reset() {
        preferredName = null;
        description = null;
        aliases = new HashSet();
        required = false;
        argument = null;
        children = null;
        id = 0;

        return this;
    }

    /**
     * Specifies the name for the next <code>Command</code>
     * that is created.  The first name is used as the preferred
     * display name for the <code>Command</code> and then
     * later names are used as aliases.
     *
     * @param name the name for the next <code>Command</code>
     * that is created.
     * @return this <code>CommandBuilder</code>.
     */
    public CommandBuilder withName(final String name) {
        if (preferredName == null) {
            preferredName = name;
        } else {
            aliases.add(name);
        }

        return this;
    }

    /**
     * Specifies the description for the next <code>Command</code>
     * that is created.  This description is used to produce
     * help documentation for the <code>Command</code>.
     *
     * @param newDescription the description for the next
     * <code>Command</code> that is created.
     * @return this <code>CommandBuilder</code>.
     */
    public CommandBuilder withDescription(final String newDescription) {
        this.description = newDescription;

        return this;
    }

    /**
     * Specifies whether the next <code>Command</code> created is
     * required or not.
     * @param newRequired whether the next <code>Command</code> created is
     * required or not.
     * @return this <code>CommandBuilder</code>.
     */
    public CommandBuilder withRequired(final boolean newRequired) {
        this.required = newRequired;

        return this;
    }

    /**
     * Specifies the children for the next <code>Command</code>
     * that is created.
     *
     * @param newChildren the child options for the next <code>Command</code>
     * that is created.
     * @return this <code>CommandBuilder</code>.
     */
    public CommandBuilder withChildren(final Group newChildren) {
        this.children = newChildren;

        return this;
    }

    /**
     * Specifies the argument for the next <code>Command</code>
     * that is created.
     *
     * @param newArgument the argument for the next <code>Command</code>
     * that is created.
     * @return this <code>CommandBuilder</code>.
     */
    public CommandBuilder withArgument(final Argument newArgument) {
        this.argument = newArgument;

        return this;
    }

    /**
     * Specifies the id for the next <code>Command</code> that is created.
     *
     * @param newId the id for the next <code>Command</code> that is created.
     * @return this <code>CommandBuilder</code>.
     */
    public final CommandBuilder withId(final int newId) {
        this.id = newId;

        return this;
    }
}
