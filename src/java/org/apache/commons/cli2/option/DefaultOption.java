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
package org.apache.commons.cli2.option;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.apache.commons.cli2.Argument;
import org.apache.commons.cli2.DisplaySetting;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.WriteableCommandLine;

/**
 * A Parent implementation representing normal options.
 */
public class DefaultOption extends ParentImpl {

    /**
     * The default token used to prefix a short option
     */
    public static final String DEFAULT_SHORT_PREFIX = "-";
    
    /**
     * The default token used to prefix a long option
     */
    public static final String DEFAULT_LONG_PREFIX = "--";
    
    /**
     * The default value for the burstEnabled constructor parameter
     */
    public static final boolean DEFAULT_BURST_ENABLED = true;

    private final String preferredName;
    private final Set aliases;
    private final Set burstAliases;
    private final Set triggers;
    private final Set prefixes;

    private final String shortPrefix;
    private final boolean burstEnabled;

    private final int burstLength;

    /**
     * Creates a new DefaultOption
     * 
     * @param shortPrefix the prefix used for short options
     * @param longPrefix the prefix used for long options
     * @param burstEnabled should option bursting be enabled
     * @param preferredName the preferred name for this Option
     * @param description a description of this Option
     * @param aliases the alternative names for this Option
     * @param burstAliases the aliases that can be burst
     * @param required whether the Option is strictly required
     * @param argument the Argument belonging to this Parent, or null
     * @param children the Group children belonging to this Parent, ot null
     * @param id the unique identifier for this Option
     */
    public DefaultOption(
        final String shortPrefix,
        final String longPrefix,
        final boolean burstEnabled,
        final String preferredName,
        final String description,
        final Set aliases,
        final Set burstAliases,
        final boolean required,
        final Argument argument,
        final Group children,
        final int id) {
        super(argument, children, description, id, required);

        this.shortPrefix = shortPrefix;
        this.burstEnabled = burstEnabled;

        this.burstLength = shortPrefix.length() + 1;

        this.preferredName = preferredName;
        if (aliases == null) {
            this.aliases = Collections.EMPTY_SET;
        }
        else {
            this.aliases = Collections.unmodifiableSet(new HashSet(aliases));
        }

        if (burstAliases == null) {
            this.burstAliases = Collections.EMPTY_SET;
        }
        else {
            this.burstAliases =
                Collections.unmodifiableSet(new HashSet(burstAliases));
        }

        final Set newTriggers = new HashSet();
        newTriggers.add(preferredName);
        newTriggers.addAll(this.aliases);
        newTriggers.addAll(this.burstAliases);
        this.triggers = Collections.unmodifiableSet(newTriggers);

        final Set newPrefixes = new HashSet(super.getPrefixes());
        newPrefixes.add(shortPrefix);
        newPrefixes.add(longPrefix);
        this.prefixes = Collections.unmodifiableSet(newPrefixes);
    }

    public boolean canProcess(final String argument) {
        return argument != null
            && (super.canProcess(argument)
                || (argument.length() >= burstLength
                    && burstAliases.contains(argument.substring(0, burstLength))));
    }

    public void processParent(
        WriteableCommandLine commandLine,
        ListIterator arguments)
        throws OptionException {

        final String argument = (String)arguments.next();

        if (triggers.contains(argument)) {
            commandLine.addOption(this);
            arguments.set(preferredName);
        }
        else if (burstEnabled && argument.length() >= burstLength) {
            final String burst = argument.substring(0, burstLength);

            if (burstAliases.contains(burst)) {
                commandLine.addOption(this);

                //HMM test bursting all vs bursting one by one.
                arguments.set(preferredName);

                if (getArgument() == null) {
                    arguments.add(
                        shortPrefix + argument.substring(burstLength));
                }
                else {
                    arguments.add(argument.substring(burstLength));
                }

                arguments.previous();
            }
            else {
                throw new OptionException(this, "cli.error.burst", argument);
            }
        }
        else {
            throw new OptionException(this, "cli.error.unexpected", argument);
        }
    }

    public Set getTriggers() {
        return triggers;
    }

    public Set getPrefixes() {
        return prefixes;
    }

    public void validate(WriteableCommandLine commandLine)
        throws OptionException {
        if (isRequired() && !commandLine.hasOption(this)) {
            throw new OptionException(this);
        }

        super.validate(commandLine);
    }

    public void appendUsage(
        final StringBuffer buffer,
        final Set helpSettings,
        final Comparator comp) {

        // do we display optionality
        final boolean optional =
            !isRequired() && helpSettings.contains(DisplaySetting.DISPLAY_OPTIONAL);
        final boolean displayAliases =
            helpSettings.contains(DisplaySetting.DISPLAY_ALIASES);

        if (optional) {
            buffer.append('[');
        }
        buffer.append(preferredName);

        if (displayAliases && !aliases.isEmpty()) {
            buffer.append(" (");

            final List list = new ArrayList(aliases);
            Collections.sort(list);

            for (final Iterator i = list.iterator(); i.hasNext();) {
                final String alias = (String)i.next();
                buffer.append(alias);
                if (i.hasNext()) {
                    buffer.append(',');
                }
            }
            buffer.append(')');
        }

        super.appendUsage(buffer, helpSettings, comp);

        if (optional) {
            buffer.append(']');
        }
    }

    public String getPreferredName() {
        return preferredName;
    }
}
