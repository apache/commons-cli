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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.option.GroupImpl;

/**
 * Builds Group instances
 */
public class GroupBuilder {

    private String name;
    private String description;
    private List options;
    private int minimum;
    private int maximum;

    /**
     * Creates a new GroupBuilder
     */
    public GroupBuilder() {
        reset();
    }

    /**
     * Creates a new Group instance
     * @return the new Group instance
     */
    public Group create() {
        final GroupImpl group =
            new GroupImpl(options, name, description, minimum, maximum);

        reset();

        return group;
    }

    /**
     * Resets the builder
     */
    public GroupBuilder reset() {
        name = null;
        description = null;
        options = new ArrayList();
        minimum = 0;
        maximum = Integer.MAX_VALUE;
        return this;
    }

    /**
     * Use this option description
     * @param newDescription the description to use
     * @return this builder
     */
    public GroupBuilder withDescription(final String newDescription) {
        this.description = newDescription;
        return this;
    }

    /**
     * Use this option name
     * @param newName the name to use
     * @return this builder
     */
    public GroupBuilder withName(final String newName) {
        this.name = newName;
        return this;
    }

    /**
     * A valid group requires at least this many options present
     * @param newMinimum the minimum Options required
     * @return this builder
     */
    public GroupBuilder withMinimum(final int newMinimum) {
        this.minimum = newMinimum;
        return this;
    }

    /**
     * A valid group requires at most this many options present
     * @param newMaximum the maximum Options allowed
     * @return this builder
     */
    public GroupBuilder withMaximum(final int newMaximum) {
        this.maximum = newMaximum;
        return this;
    }

    /**
     * Add this option to the group
     * @param option the Option to add
     * @return this builder
     */
    public GroupBuilder withOption(final Option option) {
        this.options.add(option);
        return this;
    }
}
