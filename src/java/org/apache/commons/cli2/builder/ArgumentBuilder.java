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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli2.Argument;
import org.apache.commons.cli2.option.ArgumentImpl;
import org.apache.commons.cli2.resource.ResourceConstants;
import org.apache.commons.cli2.resource.ResourceHelper;
import org.apache.commons.cli2.validation.Validator;

/**
 * Builds Argument instances.
 */
public class ArgumentBuilder {

    /** i18n */
    private final static ResourceHelper resources = ResourceHelper.getResourceHelper();

    /** name of the argument. Used for display and lookups in CommandLine */
    private String name;

    /** description of the argument. Used in the automated online help */
    private String description;

    /** minimum number of values required */
    private int minimum;

    /** maximum number of values permitted */
    private int maximum;

    /** character used to separate the values from the option */
    private char initialSeparator;

    /** character used to separate the values from each other */
    private char subsequentSeparator;

    /** object that should be used to ensure the values are valid */
    private Validator validator;

    /** used to identify the consume remaining option, typically "--" */
    private String consumeRemaining;

    /** default values for argument */
    private List defaultValues;

    /** id of the argument */
    private int id;

    /**
     * Creates a new ArgumentBuilder instance
     */
    public ArgumentBuilder() {
        reset();
    }

    /**
     * Creates a new Argument instance using the options specified in this
     * ArgumentBuilder.
     *
     * @return A new Argument instance using the options specified in this
     * ArgumentBuilder.
     */
    public final Argument create() {
        final Argument argument =
            new ArgumentImpl(
                name,
                description,
                minimum,
                maximum,
                initialSeparator,
                subsequentSeparator,
                validator,
                consumeRemaining,
                defaultValues,
                id);

        reset();

        return argument;
    }

    /**
     * Resets the ArgumentBuilder to the defaults for a new Argument. The
     * method is called automatically at the end of a create() call.
     * @return this ArgumentBuilder
     */
    public final ArgumentBuilder reset() {
        name = "arg";
        description = null;
        minimum = 0;
        maximum = Integer.MAX_VALUE;
        initialSeparator = ArgumentImpl.DEFAULT_INITIAL_SEPARATOR;
        subsequentSeparator = ArgumentImpl.DEFAULT_SUBSEQUENT_SEPARATOR;
        validator = null;
        consumeRemaining = "--";
        defaultValues = null;
        id = 0;
        return this;
    }

    /**
     * Sets the name of the argument. The name is used when displaying usage
     * information and to allow lookups in the CommandLine object.
     *
     * @see org.apache.commons.cli2.CommandLine#getValue(String)
     *
     * @param newName the name of the argument
     * @return this ArgumentBuilder
     */
    public final ArgumentBuilder withName(final String newName) {
        if (newName == null) {
            throw new IllegalArgumentException(resources.getMessage(ResourceConstants.ARGUMENT_BUILDER_NULL_NAME));
        }
        if ("".equals(newName)) {
            throw new IllegalArgumentException(resources.getMessage(ResourceConstants.ARGUMENT_BUILDER_EMPTY_NAME));
        }
        this.name = newName;
        return this;
    }

    /**
     * Sets the description of the argument.
     *
     * The description is used when displaying online help.
     *
     * @param newDescription a description of the argument
     * @return this ArgumentBuilder
     */
    public final ArgumentBuilder withDescription(final String newDescription) {
        this.description = newDescription;
        return this;
    }

    /**
     * Sets the minimum number of values needed for the argument to be valid.
     *
     * @param newMinimum the number of values needed
     * @return this ArgumentBuilder
     */
    public final ArgumentBuilder withMinimum(final int newMinimum) {
        if (newMinimum < 0) {
            throw new IllegalArgumentException(resources.getMessage(ResourceConstants.ARGUMENT_BUILDER_NEGATIVE_MINIMUM));
        }
        this.minimum = newMinimum;
        return this;
    }

    /**
     * Sets the maximum number of values allowed for the argument to be valid.
     *
     * @param newMaximum the number of values allowed
     * @return this ArgumentBuilder
     */
    public final ArgumentBuilder withMaximum(final int newMaximum) {
        if (newMaximum < 0) {
            throw new IllegalArgumentException(resources.getMessage(ResourceConstants.ARGUMENT_BUILDER_NEGATIVE_MAXIMUM));
        }
        this.maximum = newMaximum;
        return this;
    }

    /**
     * Sets the character used to separate the values from the option. When an
     * argument is of the form -libs:dir1,dir2,dir3 the initialSeparator would
     * be ':'.
     *
     * @param newInitialSeparator the character used to separate the values
     * from the option
     * @return this ArgumentBuilder
     */
    public final ArgumentBuilder withInitialSeparator(
        final char newInitialSeparator) {

        this.initialSeparator = newInitialSeparator;
        return this;
    }

    /**
     * Sets the character used to separate the values from each other. When an
     * argument is of the form -libs:dir1,dir2,dir3 the subsequentSeparator
     * would be ','.
     *
     * @param newSubsequentSeparator the character used to separate the values
     * from each other
     * @return this ArgumentBuilder
     */
    public final ArgumentBuilder withSubsequentSeparator(
        final char newSubsequentSeparator) {

        this.subsequentSeparator = newSubsequentSeparator;
        return this;
    }

    /**
     * Sets the validator instance used to perform validation on the Argument
     * values.
     *
     * @param newValidator a Validator instance
     * @return this ArgumentBuilder
     */
    public final ArgumentBuilder withValidator(final Validator newValidator) {
        if (newValidator == null) {
            throw new IllegalArgumentException(resources.getMessage(ResourceConstants.ARGUMENT_BUILDER_NULL_VALIDATOR));
        }
        this.validator = newValidator;
        return this;
    }

    /**
     * Sets the "consume remaining" option, defaults to "--". Use this if you
     * want to allow values that might be confused with option strings.
     *
     * @param newConsumeRemaining the string to use for the consume
     * remaining option
     * @return this ArgumentBuilder
     */
    public final ArgumentBuilder withConsumeRemaining(final String newConsumeRemaining) {
        if (newConsumeRemaining == null) {
            throw new IllegalArgumentException(resources.getMessage(ResourceConstants.ARGUMENT_BUILDER_NULL_CONSUME_REMAINING));
        }
        if ( "".equals(newConsumeRemaining)) {
            throw new IllegalArgumentException(resources.getMessage(ResourceConstants.ARGUMENT_BUILDER_EMPTY_CONSUME_REMAINING));
        }
        this.consumeRemaining = newConsumeRemaining;
        return this;
    }

    /**
     * Sets the default value.
     *
     * @param defaultValue the default value for the Argument
     * @return this ArgumentBuilder
     */
    public final ArgumentBuilder withDefault(final Object defaultValue) {
        if (defaultValue == null) {
            throw new IllegalArgumentException(resources.getMessage(ResourceConstants.ARGUMENT_BUILDER_NULL_DEFAULT));
        }

        if (this.defaultValues == null) {
            this.defaultValues = new ArrayList(1);
        }
        this.defaultValues.add(defaultValue);
        return this;
    }

    /**
     * Sets the default values.
     *
     * @param newDefaultValues the default values for the Argument
     * @return this ArgumentBuilder
     */
    public final ArgumentBuilder withDefaults(final List newDefaultValues) {
        if (newDefaultValues == null) {
            throw new IllegalArgumentException(resources.getMessage(ResourceConstants.ARGUMENT_BUILDER_NULL_DEFAULTS));
        }
        this.defaultValues = newDefaultValues;
        return this;
    }

    /**
     * Sets the id
     *
     * @param newId the id of the Argument
     * @return this ArgumentBuilder
     */
    public final ArgumentBuilder withId(final int newId) {
        this.id = newId;
        return this;
    }
}
