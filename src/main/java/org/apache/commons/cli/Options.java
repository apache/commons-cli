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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Main entry-point into the library.
 * <p>
 * Options represents a collection of {@link Option} objects, which describe the possible options for a command-line.
 * </p>
 * <p>
 * It may flexibly parse long and short options, with or without values. Additionally, it may parse only a portion of a
 * commandline, allowing for flexible multi-stage parsing.
 * </p>
 *
 * @see org.apache.commons.cli.CommandLine
 */
public class Options implements Serializable {
    /** The serial version UID. */
    private static final long serialVersionUID = 1L;

    /** A map of the options with the character key */
    private final Map<String, Option> shortOpts = new LinkedHashMap<>();

    /** A map of the options with the long key */
    private final Map<String, Option> longOpts = new LinkedHashMap<>();

    /** A map of the required options */
    // N.B. This can contain either a String (addOption) or an OptionGroup (addOptionGroup)
    // TODO this seems wrong
    private final List<Object> requiredOpts = new ArrayList<>();

    /** A map of the option groups */
    private final Map<String, OptionGroup> optionGroups = new LinkedHashMap<>();

    /**
     * Adds an option instance
     *
     * @param opt the option that is to be added
     * @return the resulting Options instance
     */
    public Options addOption(final Option opt) {
        final String key = opt.getKey();
        // add it to the long option list
        if (opt.hasLongOpt()) {
            longOpts.put(opt.getLongOpt(), opt);
        }
        // if the option is required add it to the required list
        if (opt.isRequired()) {
            if (requiredOpts.contains(key)) {
                requiredOpts.remove(requiredOpts.indexOf(key));
            }
            requiredOpts.add(key);
        }
        shortOpts.put(key, opt);
        return this;
    }

    /**
     * Adds an option that only contains a short-name.
     * <p>
     * It may be specified as requiring an argument.
     * </p>
     *
     * @param opt Short single-character name of the option.
     * @param hasArg flag signalling if an argument is required after this option
     * @param description Self-documenting description
     * @return the resulting Options instance
     */
    public Options addOption(final String opt, final boolean hasArg, final String description) {
        addOption(opt, null, hasArg, description);
        return this;
    }

    /**
     * Adds an option that only contains a short name.
     * <p>
     * The option does not take an argument.
     * </p>
     *
     * @param opt Short single-character name of the option.
     * @param description Self-documenting description
     * @return the resulting Options instance
     * @since 1.3
     */
    public Options addOption(final String opt, final String description) {
        addOption(opt, null, false, description);
        return this;
    }

    /**
     * Adds an option that contains a short-name and a long-name.
     * <p>
     * It may be specified as requiring an argument.
     * </p>
     *
     * @param opt Short single-character name of the option.
     * @param longOpt Long multi-character name of the option.
     * @param hasArg flag signalling if an argument is required after this option
     * @param description Self-documenting description
     * @return the resulting Options instance
     */
    public Options addOption(final String opt, final String longOpt, final boolean hasArg, final String description) {
        addOption(new Option(opt, longOpt, hasArg, description));
        return this;
    }

    /**
     * Adds the specified option group.
     *
     * @param group the OptionGroup that is to be added
     * @return the resulting Options instance
     */
    public Options addOptionGroup(final OptionGroup group) {
        if (group.isRequired()) {
            requiredOpts.add(group);
        }
        for (final Option option : group.getOptions()) {
            // an Option cannot be required if it is in an
            // OptionGroup, either the group is required or
            // nothing is required
            option.setRequired(false);
            addOption(option);
            optionGroups.put(option.getKey(), group);
        }
        return this;
    }

    /**
     * Adds options to this option.  If any Option in {@code options} already exists
     * in this Options an IllegalArgumentException is thrown
     *
     * @param options the options to add.
     * @return The resulting Options instance.
     * @since 1.7.0
     */
    public Options addOptions(final Options options) {
        for (final Option opt : options.getOptions()) {
            if (hasOption(opt.getKey())) {
                throw new IllegalArgumentException("Duplicate key: " + opt.getKey());
            }
            addOption(opt);
        }
        options.getOptionGroups().forEach(this::addOptionGroup);
        return this;
    }

    /**
     * Adds an option that contains a short-name and a long-name.
     * <p>
     * The added option is set as required. It may be specified as requiring an argument. This method is a shortcut for:
     * </p>
     * <pre>
     * <code>
     * Options option = new Option(opt, longOpt, hasArg, description);
     * option.setRequired(true);
     * options.add(option);
     * </code>
     * </pre>
     *
     * @param opt Short single-character name of the option.
     * @param longOpt Long multi-character name of the option.
     * @param hasArg flag signalling if an argument is required after this option
     * @param description Self-documenting description
     * @return the resulting Options instance
     * @since 1.4
     */
    public Options addRequiredOption(final String opt, final String longOpt, final boolean hasArg, final String description) {
        final Option option = new Option(opt, longOpt, hasArg, description);
        option.setRequired(true);
        addOption(option);
        return this;
    }

    /**
     * Gets the options with a long name starting with the name specified.
     *
     * @param opt the partial name of the option
     * @return the options matching the partial name specified, or an empty list if none matches
     * @since 1.3
     */
    public List<String> getMatchingOptions(final String opt) {
        final String clean = Util.stripLeadingHyphens(opt);
        final List<String> matchingOpts = new ArrayList<>();
        // for a perfect match return the single option only
        if (longOpts.containsKey(clean)) {
            return Collections.singletonList(clean);
        }
        for (final String longOpt : longOpts.keySet()) {
            if (longOpt.startsWith(clean)) {
                matchingOpts.add(longOpt);
            }
        }
        return matchingOpts;
    }

    /**
     * Gets the {@link Option} matching the long or short name specified.
     * <p>
     * The leading hyphens in the name are ignored (up to 2).
     * </p>
     *
     * @param opt short or long name of the {@link Option}
     * @return the option represented by opt
     */
    public Option getOption(final String opt) {
        final String clean = Util.stripLeadingHyphens(opt);
        final Option option = shortOpts.get(clean);
        return option != null ? option : longOpts.get(clean);
    }

    /**
     * Gets the OptionGroup the {@code opt} belongs to.
     *
     * @param opt the option whose OptionGroup is being queried.
     * @return the OptionGroup if {@code opt} is part of an OptionGroup, otherwise return null
     */
    public OptionGroup getOptionGroup(final Option opt) {
        return optionGroups.get(opt.getKey());
    }

    /**
     * Gets the OptionGroups that are members of this Options instance.
     *
     * @return a Collection of OptionGroup instances.
     */
    Collection<OptionGroup> getOptionGroups() {
        /* The optionGroups map will have duplicates in the values() results.  We
         * use the HashSet to filter out duplicates and return a collection of
         * OpitonGroup.  The decision to return a Collection rather than a set
         * was probably to keep symmetry with the getOptions() method.
         */
        return new HashSet<>(optionGroups.values());
    }

    /**
     * Gets a read-only list of options in this set
     *
     * @return read-only Collection of {@link Option} objects in this descriptor
     */
    public Collection<Option> getOptions() {
        return Collections.unmodifiableCollection(helpOptions());
    }

    /**
     * Gets the required options.
     *
     * @return read-only List of required options
     */
    public List<?> getRequiredOptions() {
        return Collections.unmodifiableList(requiredOpts);
    }

    /**
     * Tests whether the named {@link Option} is a member of this {@link Options}.
     *
     * @param opt long name of the {@link Option}
     * @return true if the named {@link Option} is a member of this {@link Options}
     * @since 1.3
     */
    public boolean hasLongOption(final String opt) {
        return longOpts.containsKey(Util.stripLeadingHyphens(opt));
    }

    /**
     * Tests whether the named {@link Option} is a member of this {@link Options}.
     *
     * @param opt short or long name of the {@link Option}
     * @return true if the named {@link Option} is a member of this {@link Options}
     */
    public boolean hasOption(final String opt) {
        final String clean = Util.stripLeadingHyphens(opt);
        return shortOpts.containsKey(clean) || longOpts.containsKey(clean);
    }

    /**
     * Tests whether the named {@link Option} is a member of this {@link Options}.
     *
     * @param opt short name of the {@link Option}
     * @return true if the named {@link Option} is a member of this {@link Options}
     * @since 1.3
     */
    public boolean hasShortOption(final String opt) {
        final String clean = Util.stripLeadingHyphens(opt);
        return shortOpts.containsKey(clean);
    }

    /**
     * Returns the Options for use by the HelpFormatter.
     *
     * @return the List of Options
     */
    List<Option> helpOptions() {
        return new ArrayList<>(shortOpts.values());
    }

    /**
     * Dump state, suitable for debugging.
     *
     * @return Stringified form of this object
     */
    @Override
    public String toString() {
        final StringBuilder buf = new StringBuilder();
        buf.append("[ Options: [ short ");
        buf.append(shortOpts.toString());
        buf.append(" ] [ long ");
        buf.append(longOpts);
        buf.append(" ]");
        return buf.toString();
    }
}
