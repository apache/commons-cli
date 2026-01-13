/*
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      https://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package org.apache.commons.cli;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.help.OptionFormatter;

/**
 * The class GnuParser provides an implementation of the {@link Parser#flatten(Options, String[], boolean) flatten}
 * method.
 *
 * @deprecated Since 1.3, use the {@link DefaultParser} instead.
 */
@Deprecated
@SuppressWarnings("java:S1133") // Deprecated class retained for backward compatibility
public class GnuParser extends Parser {

    /**
     * Constructs a new instance.
     */
    public GnuParser() {
        // empty
    }

    /**
     * This flatten method does so using the following rules:
     * <ol>
     * <li>If an {@link Option} exists for the first character of the {@code arguments} entry <strong>AND</strong> an
     * {@link Option} does not exist for the whole {@code argument} then add the first character as an option to the
     * processed tokens list for example "-D" and add the rest of the entry to the also.</li>
     * <li>Otherwise just add the token to the processed tokens list.</li>
     * </ol>
     *
     * @param options The Options to parse the arguments by.
     * @param arguments The arguments that have to be flattened.
     * @param stopAtNonOption specifies whether to stop flattening when a non option has been encountered.
     * @return a String array of the flattened arguments.
     */
    @Override
    protected String[] flatten(final Options options, final String[] arguments, final boolean stopAtNonOption) {
        final List<String> tokens = new ArrayList<>();
        boolean eatTheRest = false;
        for (int i = 0; i < arguments.length; i++) {
            final String arg = arguments[i];
            if (arg != null) {
                eatTheRest = processArgument(options, tokens, arg, eatTheRest, stopAtNonOption);
                if (eatTheRest) {
                    for (int j = i + 1; j < arguments.length; j++) {
                        tokens.add(arguments[j]);
                    }
                    break;
                }
            }
        }
        return tokens.toArray(Util.EMPTY_STRING_ARRAY);
    }

    private boolean processArgument(final Options options, final List<String> tokens, final String arg,
            final boolean eatTheRest, final boolean stopAtNonOption) {
        if (OptionFormatter.DEFAULT_LONG_OPT_PREFIX.equals(arg)) {
            tokens.add(OptionFormatter.DEFAULT_LONG_OPT_PREFIX);
            return true;
        }
        if (OptionFormatter.DEFAULT_OPT_PREFIX.equals(arg)) {
            tokens.add(OptionFormatter.DEFAULT_OPT_PREFIX);
            return eatTheRest;
        }
        if (arg.startsWith(OptionFormatter.DEFAULT_OPT_PREFIX)) {
            return processOptionArgument(options, tokens, arg, stopAtNonOption);
        }
        tokens.add(arg);
        return eatTheRest;
    }

    private boolean processOptionArgument(final Options options, final List<String> tokens, final String arg,
            final boolean stopAtNonOption) {
        final String opt = Util.stripLeadingHyphens(arg);
        if (options.hasOption(opt)) {
            tokens.add(arg);
            return false;
        }
        final int equalPos = DefaultParser.indexOfEqual(opt);
        if (equalPos != -1 && options.hasOption(opt.substring(0, equalPos))) {
            // the format is --foo=value or -foo=value
            tokens.add(arg.substring(0, arg.indexOf(Char.EQUAL))); // --foo
            tokens.add(arg.substring(arg.indexOf(Char.EQUAL) + 1)); // value
            return false;
        }
        if (options.hasOption(arg.substring(0, 2))) {
            // the format is a special properties option (-Dproperty=value)
            tokens.add(arg.substring(0, 2)); // -D
            tokens.add(arg.substring(2)); // property=value
            return false;
        }
        tokens.add(arg);
        return stopAtNonOption;
    }
}
