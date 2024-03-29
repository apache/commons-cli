/*
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package org.apache.commons.cli;

import java.util.function.Supplier;

/**
 * Deprecated attributes.
 * <p>
 * Note: This class isn't called "Deprecated" to avoid clashing with "java.lang.Deprecated".
 * </p>
 * <p>
 * If you want to serialize this class, use a serialization proxy.
 * </p>
 *
 * @since 1.7.0
 * @see Deprecated
 */
public final class DeprecatedAttributes {

    /**
     * Builds {@link DeprecatedAttributes}.
     */
    public static class Builder implements Supplier<DeprecatedAttributes> {

        /** The description. */
        private String description;

        /**
         * Whether this option is subject to removal in a future version.
         *
         * @see <a href="https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Deprecated.html#forRemoval()">Deprecated.forRemoval</a>
         */
        private boolean forRemoval;

        /**
         * The version in which the option became deprecated.
         *
         * @see <a href="https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Deprecated.html#forRemoval()">Deprecated.since</a>
         */
        private String since;

        @Override
        public DeprecatedAttributes get() {
            return new DeprecatedAttributes(description, since, forRemoval);
        }

        /**
         * Sets the description.
         *
         * @param description the description.
         * @return this.
         */
        public Builder setDescription(final String description) {
            this.description = description;
            return this;
        }

        /**
         * Whether this option is subject to removal in a future version.
         *
         * @param forRemoval whether this is subject to removal in a future version.
         * @return this.
         * @see <a href="https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/lang/Deprecated.html#forRemoval()">Deprecated.forRemoval</a>
         */
        public Builder setForRemoval(final boolean forRemoval) {
            this.forRemoval = forRemoval;
            return this;
        }

        /**
         * Sets the version in which the option became deprecated.
         *
         * @param since the version in which the option became deprecated.
         * @return this.
         */
        public Builder setSince(final String since) {
            this.since = since;
            return this;
        }
    }

    /**
     * The default value for a DeprecatedAttributes.
     */
    static final DeprecatedAttributes DEFAULT = new DeprecatedAttributes("", "", false);

    /**
     * The empty string.
     */
    private static final String EMPTY_STRING = "";

    /**
     * Creates a new builder.
     *
     * @return a new builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    /** The description. */
    private final String description;

    /** Whether this option will be removed. */
    private final boolean forRemoval;

    /** The version label for removal. */
    private final String since;

    /**
     * Constructs a new instance.
     *
     * @param description The description.
     * @param since       The version label for removal.
     * @param forRemoval  Whether this option will be removed.
     */
    private DeprecatedAttributes(final String description, final String since, final boolean forRemoval) {
        this.description = toEmpty(description);
        this.since = toEmpty(since);
        this.forRemoval = forRemoval;
    }

    /**
     * Gets the descriptions.
     *
     * @return the descriptions.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets version in which the option became deprecated.
     *
     * @return the version in which the option became deprecated.
     */
    public String getSince() {
        return since;
    }

    /**
     * Tests whether this option is subject to removal in a future version.
     *
     * @return whether this option is subject to removal in a future version.
     */
    public boolean isForRemoval() {
        return forRemoval;
    }

    private String toEmpty(final String since) {
        return since != null ? since : EMPTY_STRING;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("Deprecated");
        if (forRemoval) {
            builder.append(" for removal");
        }
        if (!since.isEmpty()) {
            builder.append(" since ");
            builder.append(since);
        }
        if (!description.isEmpty()) {
            builder.append(": ");
            builder.append(description);
        }
        return builder.toString();
    }
}
