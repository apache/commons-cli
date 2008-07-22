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
package org.apache.commons.cli2.commandline;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.resource.ResourceConstants;
import org.apache.commons.cli2.resource.ResourceHelper;

/**
 * Instances of CommandLine represent a command line that has been processed
 * according to the definition supplied to the parser.
 */
public abstract class CommandLineImpl implements CommandLine {
    public final boolean hasOption(final String trigger) {
        return hasOption(getOption(trigger));
    }

    public final List getValues(final String trigger) {
        return getValues(getOption(trigger), Collections.EMPTY_LIST);
    }

    public final List getValues(final String trigger,
                                final List defaultValues) {
        return getValues(getOption(trigger), defaultValues);
    }

    public final List getValues(final Option option) {
        return getValues(option, Collections.EMPTY_LIST);
    }

    public final Object getValue(final String trigger) {
        return getValue(getOption(trigger), null);
    }

    public final Object getValue(final String trigger,
                                 final Object defaultValue) {
        return getValue(getOption(trigger), defaultValue);
    }

    public final Object getValue(final Option option) {
        return getValue(option, null);
    }

    public final Object getValue(final Option option,
                                 final Object defaultValue) {
        final List values;

        if (defaultValue == null) {
            values = getValues(option);
        } else {
            values = getValues(option, Collections.singletonList(defaultValue));
        }

        if (values.size() > 1) {
            throw new IllegalStateException(ResourceHelper.getResourceHelper().getMessage(ResourceConstants.ARGUMENT_TOO_MANY_VALUES));
        }

        if (values.isEmpty()) {
            return defaultValue;
        }

        return values.get(0);
    }

    public final Boolean getSwitch(final String trigger) {
        return getSwitch(getOption(trigger), null);
    }

    public final Boolean getSwitch(final String trigger,
                                   final Boolean defaultValue) {
        return getSwitch(getOption(trigger), defaultValue);
    }

    public final Boolean getSwitch(final Option option) {
        return getSwitch(option, null);
    }

    public final String getProperty(final Option option, final String property) {
        return getProperty(option, property, null);
    }

    public final int getOptionCount(final String trigger) {
        return getOptionCount(getOption(trigger));
    }

    public final int getOptionCount(final Option option) {
        if (option == null) {
            return 0;
        }

        int count = 0;

        for (Iterator i = getOptions().iterator(); i.hasNext();) {
            if (option.equals(i.next())) {
                ++count;
            }
        }

        return count;
    }
}
