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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * The class PosixParser provides an implementation of the {@link Parser#flatten(Options,String[],boolean) flatten}
 * method.
 *
 * @deprecated since 1.3, use the {@link DefaultParser} instead
 */
@Deprecated
public class PosixParser extends Parser {

    /** Holder for flattened tokens */
    private final List<String> tokens = new ArrayList<>();

    /** Specifies if bursting should continue */
    private boolean eatTheRest;

    /** Holder for the current option */
    private Option currentOption;

    /** The command line Options */
    private Options options;

    /**
     * Constructs a new instance.
     */
    public PosixParser() {
        // empty
    }

    /**
     * Breaks {@code token} into its constituent parts using the following algorithm.
     *
     * <ul>
     * <li>ignore the first character ("<strong>-</strong>")</li>
     * <li>for each remaining character check if an {@link Option} exists with that id.</li>
     * <li>if an {@link Option} does exist then add that character prepended with "<strong>-</strong>" to the list of processed
     * tokens.</li>
     * <li>if the {@link Option} can have an argument value and there are remaining characters in the token then add the
     * remaining characters as a token to the list of processed tokens.</li>
     * <li>if an {@link Option} does <strong>NOT</strong> exist <strong>AND</strong> {@code stopAtNonOption} <strong>IS</strong> set then add the
     * special token "<strong>--</strong>" followed by the remaining characters and also the remaining tokens directly to the
     * processed tokens list.</li>
     * <li>if an {@link Option} does <strong>NOT</strong> exist <strong>AND</strong> {@code stopAtNonOption} <strong>IS NOT</strong> set then add
     * that character prepended with "<strong>-</strong>".</li>
     * </ul>
     *
     * @param token The current token to be <strong>burst</strong>
     * @param stopAtNonOption Specifies whether to stop processing at the first non-Option encountered.
     */
    protected void burstToken(final String token, final boolean stopAtNonOption) {
        for (int i = 1; i < token.length(); i++) {
            final String ch = String.valueOf(token.charAt(i));

            if (!options.hasOption(ch)) {
                if (stopAtNonOption) {
                    processNonOptionToken(token.substring(i), true);
                } else {
                    tokens.add(token);
                }
                break;
            }
            tokens.add("-" + ch);
            currentOption = options.getOption(ch);

            if (currentOption.hasArg() && token.length() != i + 1) {
                tokens.add(token.substring(i + 1));

                break;
            }
        }
    }

    /**
     * <p>
     * An implementation of {@link Parser}'s abstract {@link Parser#flatten(Options,String[],boolean) flatten} method.
     * </p>
     *
     * <p>
     * The following are the rules used by this flatten method.
     * </p>
     * <ol>
     * <li>if {@code stopAtNonOption} is <strong>true</strong> then do not burst anymore of {@code arguments} entries, just
     * add each successive entry without further processing. Otherwise, ignore {@code stopAtNonOption}.</li>
     * <li>if the current {@code arguments} entry is "<strong>--</strong>" just add the entry to the list of processed
     * tokens</li>
     * <li>if the current {@code arguments} entry is "<strong>-</strong>" just add the entry to the list of processed tokens</li>
     * <li>if the current {@code arguments} entry is two characters in length and the first character is "<strong>-</strong>"
     * then check if this is a valid {@link Option} id. If it is a valid id, then add the entry to the list of processed
     * tokens and set the current {@link Option} member. If it is not a valid id and {@code stopAtNonOption} is true,
     * then the remaining entries are copied to the list of processed tokens. Otherwise, the current entry is ignored.</li>
     * <li>if the current {@code arguments} entry is more than two characters in length and the first character is
     * "<strong>-</strong>" then we need to burst the entry to determine its constituents. For more information on the bursting
     * algorithm see {@link PosixParser#burstToken(String, boolean) burstToken}.</li>
     * <li>if the current {@code arguments} entry is not handled by any of the previous rules, then the entry is added
     * to the list of processed tokens.</li>
     * </ol>
     *
     * @param options The command line {@link Options}
     * @param arguments The command line arguments to be parsed
     * @param stopAtNonOption Specifies whether to stop flattening when an non option is found.
     * @return The flattened {@code arguments} String array.
     */
    @Override
    protected String[] flatten(final Options options, final String[] arguments, final boolean stopAtNonOption) throws ParseException {
        init();
        this.options = options;
        // an iterator for the command line tokens
        final Iterator<String> iter = Arrays.asList(arguments).iterator();
        // process each command line token
        while (iter.hasNext()) {
            // get the next command line token
            final String token = iter.next();
            if (token != null) {
                // single or double hyphen
                if ("-".equals(token) || "--".equals(token)) {
                    tokens.add(token);
                } else if (token.startsWith("--")) {
                    // handle long option --foo or --foo=bar
                    final int pos = DefaultParser.indexOfEqual(token);
                    final String opt = pos == -1 ? token : token.substring(0, pos); // --foo

                    final List<String> matchingOpts = options.getMatchingOptions(opt);

                    if (matchingOpts.isEmpty()) {
                        processNonOptionToken(token, stopAtNonOption);
                    } else if (matchingOpts.size() > 1) {
                        throw new AmbiguousOptionException(opt, matchingOpts);
                    } else {
                        currentOption = options.getOption(matchingOpts.get(0));

                        tokens.add("--" + currentOption.getLongOpt());
                        if (pos != -1) {
                            tokens.add(token.substring(pos + 1));
                        }
                    }
                } else if (token.startsWith("-")) {
                    if (token.length() == 2 || options.hasOption(token)) {
                        processOptionToken(token, stopAtNonOption);
                    } else if (!options.getMatchingOptions(token).isEmpty()) {
                        final List<String> matchingOpts = options.getMatchingOptions(token);
                        if (matchingOpts.size() > 1) {
                            throw new AmbiguousOptionException(token, matchingOpts);
                        }
                        final Option opt = options.getOption(matchingOpts.get(0));
                        processOptionToken("-" + opt.getLongOpt(), stopAtNonOption);
                    }
                    // requires bursting
                    else {
                        burstToken(token, stopAtNonOption);
                    }
                } else {
                    processNonOptionToken(token, stopAtNonOption);
                }
            }
            gobble(iter);
        }
        return tokens.toArray(Util.EMPTY_STRING_ARRAY);
    }

    /**
     * Adds the remaining tokens to the processed tokens list.
     *
     * @param iter An iterator over the remaining tokens
     */
    private void gobble(final Iterator<String> iter) {
        if (eatTheRest) {
            while (iter.hasNext()) {
                tokens.add(iter.next());
            }
        }
    }

    /**
     * Resets the members to their original state i.e. remove all of {@code tokens} entries and set
     * {@code eatTheRest} to false.
     */
    private void init() {
        eatTheRest = false;
        tokens.clear();
    }

    /**
     * Add the special token "<strong>--</strong>" and the current {@code value} to the processed tokens list. Then add all the
     * remaining {@code argument} values to the processed tokens list.
     *
     * @param value The current token
     */
    private void processNonOptionToken(final String value, final boolean stopAtNonOption) {
        if (stopAtNonOption && (currentOption == null || !currentOption.hasArg())) {
            eatTheRest = true;
            tokens.add("--");
        }

        tokens.add(value);
    }

    /**
     * <p>
     * If an {@link Option} exists for {@code token} then add the token to the processed list.
     * </p>
     *
     * <p>
     * If an {@link Option} does not exist and {@code stopAtNonOption} is set then add the remaining tokens to the
     * processed tokens list directly.
     * </p>
     *
     * @param token The current option token
     * @param stopAtNonOption Specifies whether flattening should halt at the first non option.
     */
    private void processOptionToken(final String token, final boolean stopAtNonOption) {
        if (stopAtNonOption && !options.hasOption(token)) {
            eatTheRest = true;
        }

        if (options.hasOption(token)) {
            currentOption = options.getOption(token);
        }

        tokens.add(token);
    }
}
