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
import org.apache.commons.cli2.option.Command;

/**
 * Builds Command instances
 */
public class CommandBuilder {

    private String preferredName;
    private String description;
    private Set aliases;
    private boolean required;
    private Argument argument;
    private Group children;
    private int id;

    /**
     * Creates a new CommandBuilder instance
     */
    public CommandBuilder() {
        reset();
    }

    /**
     * Creates a new Command instance using the properties of the
     * CommandBuilder.
     * 
     * @return the new Command instance
     */
    public Command create() {

        // check we have a valid name
        if (preferredName == null) {
            throw new IllegalStateException("Options must have at least one name");
        }

        // build the command
        final Command option =
            new Command(
                preferredName,
                description,
                aliases,
                required,
                argument,
                children,
                id);

        // reset the builder
        reset();

        return option;
    }

    /**
     * Resets the CommandBuilder to the defaults for a new Command. The method
     * should be called automatically at the end of a create() call.
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
     * Sets the name of the command. The first name is used as the preferred
     * display name for the Command and then later names are used as aliases.
     * 
     * @param name
     *            a name for the Command
     * @return this CommandBuilder
     */
    public CommandBuilder withName(final String name) {
        if (preferredName == null) {
            preferredName = name;
        }
        else {
            aliases.add(name);
        }

        return this;
    }

    /**
     * Sets the description of the command. The description is used to produce
     * online help for the command.
     * 
     * @param newDescription
     *            The description of the command
     * @return this CommandBuilder
     */
    public CommandBuilder withDescription(final String newDescription) {
        this.description = newDescription;
        return this;
    }

    /**
     * Use this optionality
     * @param newRequired true iff the Option is required
     * @return this builder
     */
    public CommandBuilder withRequired(final boolean newRequired) {
        this.required = newRequired;
        return this;
    }

    /**
     * Sets the children of the Command.
     * 
     * @param newChildren
     *            the child options for the Command
     * @return this CommandBuilder
     */
    public CommandBuilder withChildren(final Group newChildren) {
        this.children = newChildren;
        return this;
    }

    /**
     * Sets the argument of the Command.
     * 
     * @param newArgument
     *            the argument for the Command
     * @return this CommandBuilder
     */
    public CommandBuilder withArgument(final Argument newArgument) {
        this.argument = newArgument;
        return this;
    }

    /**
     * Sets the id
     * 
     * @param newId
     *            the id of the Command
     * @return this CommandBuilder
     */
    public final CommandBuilder withId(final int newId) {
        this.id = newId;
        return this;
    }
}
