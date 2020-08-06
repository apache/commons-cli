/**
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

package org.apache.commons.cli;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * Default parser.
 *
 * @since 1.3
 */
public class DefaultParser implements CommandLineParser
{
    /** The command-line instance. */
    protected CommandLine cmd;

    /** The current options. */
    protected Options options;

    /**
     * Flag indicating how unrecognized tokens are handled. <code>true</code> to stop
     * the parsing and add the remaining tokens to the args list.
     * <code>false</code> to throw an exception.
     */
    protected boolean stopAtNonOption;

    /** The token currently processed. */
    protected String currentToken;

    /** The last option parsed. */
    protected Option currentOption;

    /** Flag indicating if tokens should no longer be analyzed and simply added as arguments of the command line. */
    protected boolean skipParsing;

    /** The required options and groups expected to be found when parsing the command line. */
    protected List expectedOpts;

    /** Flag indicating if partial matching of long options is supported. */
    private final  boolean allowPartialMatching;

    /**
     * Creates a new DefaultParser instance with partial matching enabled.
     *
     * By "partial matching" we mean that given the following code:
     * <pre>
     *     {@code
     *          final Options options = new Options();
     *      options.addOption(new Option("d", "debug", false, "Turn on debug."));
     *      options.addOption(new Option("e", "extract", false, "Turn on extract."));
     *      options.addOption(new Option("o", "option", true, "Turn on option with argument."));
     *      }
     * </pre>
     * with "partial matching" turned on, <code>-de</code> only matches the
     * <code>"debug"</code> option. However, with "partial matching" disabled,
     * <code>-de</code> would enable both <code>debug</code> as well as
     * <code>extract</code> options.
     */
    public DefaultParser() {
        this.allowPartialMatching = true;
    }

    /**
     * Create a new DefaultParser instance with the specified partial matching policy.
     *
     * By "partial matching" we mean that given the following code:
     * <pre>
     *     {@code
     *          final Options options = new Options();
     *      options.addOption(new Option("d", "debug", false, "Turn on debug."));
     *      options.addOption(new Option("e", "extract", false, "Turn on extract."));
     *      options.addOption(new Option("o", "option", true, "Turn on option with argument."));
     *      }
     * </pre>
     * with "partial matching" turned on, <code>-de</code> only matches the
     * <code>"debug"</code> option. However, with "partial matching" disabled,
     * <code>-de</code> would enable both <code>debug</code> as well as
     * <code>extract</code> options.
     *
     * @param allowPartialMatching if partial matching of long options shall be enabled
     */
    public DefaultParser(final boolean allowPartialMatching) {
        this.allowPartialMatching = allowPartialMatching;
    }

    @Override
    public CommandLine parse(final Options options, final String[] arguments) throws ParseException
    {
        return parse(options, arguments, null);
    }

    /**
     * Parse the arguments according to the specified options and properties.
     *
     * @param options    the specified Options
     * @param arguments  the command line arguments
     * @param properties command line option name-value pairs
     * @return the list of atomic option and value tokens
     *
     * @throws ParseException if there are any problems encountered
     * while parsing the command line tokens.
     */
    public CommandLine parse(final Options options, final String[] arguments, final Properties properties) throws ParseException
    {
        return parse(options, arguments, properties, false);
    }

    @Override
    public CommandLine parse(final Options options, final String[] arguments, final boolean stopAtNonOption) throws ParseException
    {
        return parse(options, arguments, null, stopAtNonOption);
    }

    /**
     * Parse the arguments according to the specified options and properties.
     *
     * @param options         the specified Options
     * @param arguments       the command line arguments
     * @param properties      command line option name-value pairs
     * @param stopAtNonOption if <code>true</code> an unrecognized argument stops
     *     the parsing and the remaining arguments are added to the
     *     {@link CommandLine}s args list. If <code>false</code> an unrecognized
     *     argument triggers a ParseException.
     *
     * @return the list of atomic option and value tokens
     * @throws ParseException if there are any problems encountered
     * while parsing the command line tokens.
     */
    public CommandLine parse(final Options options, final String[] arguments, final Properties properties, final boolean stopAtNonOption)
            throws ParseException
    {
        this.options = options;
        this.stopAtNonOption = stopAtNonOption;
        skipParsing = false;
        currentOption = null;
        expectedOpts = new ArrayList(options.getRequiredOptions());

        // clear the data from the groups
        for (final OptionGroup group : options.getOptionGroups())
        {
            group.setSelected(null);
        }

        cmd = new CommandLine();

        if (arguments != null)
        {
            for (final String argument : arguments)
            {
                handleToken(argument);
            }
        }

        // check the arguments of the last option
        checkRequiredArgs();

        // add the default options
        handleProperties(properties);

        checkRequiredOptions();

        return cmd;
    }

    /**
     * Sets the values of Options using the values in <code>properties</code>.
     *
     * @param properties The value properties to be processed.
     */
    private void handleProperties(final Properties properties) throws ParseException
    {
        if (properties == null)
        {
            return;
        }

        for (final Enumeration<?> e = properties.propertyNames(); e.hasMoreElements();)
        {
            final String option = e.nextElement().toString();

            final Option opt = options.getOption(option);
            if (opt == null)
            {
                throw new UnrecognizedOptionException("Default option wasn't defined", option);
            }

            // if the option is part of a group, check if another option of the group has been selected
            final OptionGroup group = options.getOptionGroup(opt);
            final boolean selected = group != null && group.getSelected() != null;

            if (!cmd.hasOption(option) && !selected)
            {
                // get the value from the properties
                final String value = properties.getProperty(option);

                if (opt.hasArg())
                {
                    if (opt.getValues() == null || opt.getValues().length == 0)
                    {
                        opt.addValueForProcessing(value);
                    }
                }
                else if (!("yes".equalsIgnoreCase(value)
                        || "true".equalsIgnoreCase(value)
                        || "1".equalsIgnoreCase(value)))
                {
                    // if the value is not yes, true or 1 then don't add the option to the CommandLine
                    continue;
                }

                handleOption(opt);
                currentOption = null;
            }
        }
    }

    /**
     * Throws a {@link MissingOptionException} if all of the required options
     * are not present.
     *
     * @throws MissingOptionException if any of the required Options
     * are not present.
     */
    protected void checkRequiredOptions() throws MissingOptionException
    {
        // if there are required options that have not been processed
        if (!expectedOpts.isEmpty())
        {
            throw new MissingOptionException(expectedOpts);
        }
    }

    /**
     * Throw a {@link MissingArgumentException} if the current option
     * didn't receive the number of arguments expected.
     */
    private void checkRequiredArgs() throws ParseException
    {
        if (currentOption != null && currentOption.requiresArg())
        {
            throw new MissingArgumentException(currentOption);
        }
    }

    /**
     * Handle any command line token.
     *
     * @param token the command line token to handle
     * @throws ParseException
     */
    private void handleToken(final String token) throws ParseException
    {
        currentToken = token;

        if (skipParsing)
        {
            cmd.addArg(token);
        }
        else if ("--".equals(token))
        {
            skipParsing = true;
        }
        else if (currentOption != null && currentOption.acceptsArg() && isArgument(token))
        {
            currentOption.addValueForProcessing(Util.stripLeadingAndTrailingQuotes(token));
        }
        else if (token.startsWith("--"))
        {
            handleLongOption(token);
        }
        else if (token.startsWith("-") && !"-".equals(token))
        {
            handleShortAndLongOption(token);
        }
        else
        {
            handleUnknownToken(token);
        }

        if (currentOption != null && !currentOption.acceptsArg())
        {
            currentOption = null;
        }
    }

    /**
     * Returns true is the token is a valid argument.
     *
     * @param token
     */
    private boolean isArgument(final String token)
    {
        return !isOption(token) || isNegativeNumber(token);
    }

    /**
     * Check if the token is a negative number.
     *
     * @param token
     */
    private boolean isNegativeNumber(final String token)
    {
        try
        {
            Double.parseDouble(token);
            return true;
        }
        catch (final NumberFormatException e)
        {
            return false;
        }
    }

    /**
     * Tells if the token looks like an option.
     *
     * @param token
     */
    private boolean isOption(final String token)
    {
        return isLongOption(token) || isShortOption(token);
    }

    /**
     * Tells if the token looks like a short option.
     *
     * @param token
     */
    private boolean isShortOption(final String token)
    {
        // short options (-S, -SV, -S=V, -SV1=V2, -S1S2)
        if (!token.startsWith("-") || token.length() == 1)
        {
            return false;
        }

        // remove leading "-" and "=value"
        final int pos = token.indexOf("=");
        final String optName = pos == -1 ? token.substring(1) : token.substring(1, pos);
        if (options.hasShortOption(optName))
        {
            return true;
        }
        // check for several concatenated short options
        return optName.length() > 0 && options.hasShortOption(String.valueOf(optName.charAt(0)));
    }

    /**
     * Tells if the token looks like a long option.
     *
     * @param token
     */
    private boolean isLongOption(final String token)
    {
        if (!token.startsWith("-") || token.length() == 1)
        {
            return false;
        }

        final int pos = token.indexOf("=");
        final String t = pos == -1 ? token : token.substring(0, pos);

        if (!getMatchingLongOptions(t).isEmpty())
        {
            // long or partial long options (--L, -L, --L=V, -L=V, --l, --l=V)
            return true;
        }
        else if (getLongPrefix(token) != null && !token.startsWith("--"))
        {
            // -LV
            return true;
        }

        return false;
    }

    /**
     * Handles an unknown token. If the token starts with a dash an
     * UnrecognizedOptionException is thrown. Otherwise the token is added
     * to the arguments of the command line. If the stopAtNonOption flag
     * is set, this stops the parsing and the remaining tokens are added
     * as-is in the arguments of the command line.
     *
     * @param token the command line token to handle
     */
    private void handleUnknownToken(final String token) throws ParseException
    {
        if (token.startsWith("-") && token.length() > 1 && !stopAtNonOption)
        {
            throw new UnrecognizedOptionException("Unrecognized option: " + token, token);
        }

        cmd.addArg(token);
        if (stopAtNonOption)
        {
            skipParsing = true;
        }
    }

    /**
     * Handles the following tokens:
     *
     * --L
     * --L=V
     * --L V
     * --l
     *
     * @param token the command line token to handle
     */
    private void handleLongOption(final String token) throws ParseException
    {
        if (token.indexOf('=') == -1)
        {
            handleLongOptionWithoutEqual(token);
        }
        else
        {
            handleLongOptionWithEqual(token);
        }
    }

    /**
     * Handles the following tokens:
     *
     * --L
     * -L
     * --l
     * -l
     *
     * @param token the command line token to handle
     */
    private void handleLongOptionWithoutEqual(final String token) throws ParseException
    {
        final List<String> matchingOpts = getMatchingLongOptions(token);
        if (matchingOpts.isEmpty())
        {
            handleUnknownToken(currentToken);
        }
        else if (matchingOpts.size() > 1 && !options.hasLongOption(token))
        {
            throw new AmbiguousOptionException(token, matchingOpts);
        }
        else
        {
            final String key = options.hasLongOption(token) ? token : matchingOpts.get(0);
            handleOption(options.getOption(key));
        }
    }

    /**
     * Handles the following tokens:
     *
     * --L=V
     * -L=V
     * --l=V
     * -l=V
     *
     * @param token the command line token to handle
     */
    private void handleLongOptionWithEqual(final String token) throws ParseException
    {
        final int pos = token.indexOf('=');

        final String value = token.substring(pos + 1);

        final String opt = token.substring(0, pos);

        final List<String> matchingOpts = getMatchingLongOptions(opt);
        if (matchingOpts.isEmpty())
        {
            handleUnknownToken(currentToken);
        }
        else if (matchingOpts.size() > 1 && !options.hasLongOption(opt))
        {
            throw new AmbiguousOptionException(opt, matchingOpts);
        }
        else
        {
            final String key = options.hasLongOption(opt) ? opt : matchingOpts.get(0);
            final Option option = options.getOption(key);

            if (option.acceptsArg())
            {
                handleOption(option);
                currentOption.addValueForProcessing(value);
                currentOption = null;
            }
            else
            {
                handleUnknownToken(currentToken);
            }
        }
    }

    /**
     * Handles the following tokens:
     *
     * -S
     * -SV
     * -S V
     * -S=V
     * -S1S2
     * -S1S2 V
     * -SV1=V2
     *
     * -L
     * -LV
     * -L V
     * -L=V
     * -l
     *
     * @param token the command line token to handle
     */
    private void handleShortAndLongOption(final String token) throws ParseException
    {
        final String t = Util.stripLeadingHyphens(token);

        final int pos = t.indexOf('=');

        if (t.length() == 1)
        {
            // -S
            if (options.hasShortOption(t))
            {
                handleOption(options.getOption(t));
            }
            else
            {
                handleUnknownToken(token);
            }
        }
        else if (pos == -1)
        {
            // no equal sign found (-xxx)
            if (options.hasShortOption(t))
            {
                handleOption(options.getOption(t));
            }
            else if (!getMatchingLongOptions(t).isEmpty())
            {
                // -L or -l
                handleLongOptionWithoutEqual(token);
            }
            else
            {
                // look for a long prefix (-Xmx512m)
                final String opt = getLongPrefix(t);

                if (opt != null && options.getOption(opt).acceptsArg())
                {
                    handleOption(options.getOption(opt));
                    currentOption.addValueForProcessing(t.substring(opt.length()));
                    currentOption = null;
                }
                else if (isJavaProperty(t))
                {
                    // -SV1 (-Dflag)
                    handleOption(options.getOption(t.substring(0, 1)));
                    currentOption.addValueForProcessing(t.substring(1));
                    currentOption = null;
                }
                else
                {
                    // -S1S2S3 or -S1S2V
                    handleConcatenatedOptions(token);
                }
            }
        }
        else
        {
            // equal sign found (-xxx=yyy)
            final String opt = t.substring(0, pos);
            final String value = t.substring(pos + 1);

            if (opt.length() == 1)
            {
                // -S=V
                final Option option = options.getOption(opt);
                if (option != null && option.acceptsArg())
                {
                    handleOption(option);
                    currentOption.addValueForProcessing(value);
                    currentOption = null;
                }
                else
                {
                    handleUnknownToken(token);
                }
            }
            else if (isJavaProperty(opt))
            {
                // -SV1=V2 (-Dkey=value)
                handleOption(options.getOption(opt.substring(0, 1)));
                currentOption.addValueForProcessing(opt.substring(1));
                currentOption.addValueForProcessing(value);
                currentOption = null;
            }
            else
            {
                // -L=V or -l=V
                handleLongOptionWithEqual(token);
            }
        }
    }

    /**
     * Search for a prefix that is the long name of an option (-Xmx512m)
     *
     * @param token
     */
    private String getLongPrefix(final String token)
    {
        final String t = Util.stripLeadingHyphens(token);

        int i;
        String opt = null;
        for (i = t.length() - 2; i > 1; i--)
        {
            final String prefix = t.substring(0, i);
            if (options.hasLongOption(prefix))
            {
                opt = prefix;
                break;
            }
        }

        return opt;
    }

    /**
     * Check if the specified token is a Java-like property (-Dkey=value).
     */
    private boolean isJavaProperty(final String token)
    {
        final String opt = token.substring(0, 1);
        final Option option = options.getOption(opt);

        return option != null && (option.getArgs() >= 2 || option.getArgs() == Option.UNLIMITED_VALUES);
    }

    private void handleOption(Option option) throws ParseException
    {
        // check the previous option before handling the next one
        checkRequiredArgs();

        option = (Option) option.clone();

        updateRequiredOptions(option);

        cmd.addOption(option);

        if (option.hasArg())
        {
            currentOption = option;
        }
        else
        {
            currentOption = null;
        }
    }

    /**
     * Removes the option or its group from the list of expected elements.
     *
     * @param option
     */
    private void updateRequiredOptions(final Option option) throws AlreadySelectedException
    {
        if (option.isRequired())
        {
            expectedOpts.remove(option.getKey());
        }

        // if the option is in an OptionGroup make that option the selected option of the group
        if (options.getOptionGroup(option) != null)
        {
            final OptionGroup group = options.getOptionGroup(option);

            if (group.isRequired())
            {
                expectedOpts.remove(group);
            }

            group.setSelected(option);
        }
    }

    /**
     * Returns a list of matching option strings for the given token, depending
     * on the selected partial matching policy.
     *
     * @param token the token (may contain leading dashes)
     * @return the list of matching option strings or an empty list if no matching option could be found
     */
    private List<String> getMatchingLongOptions(final String token)
    {
        if (allowPartialMatching)
        {
            return options.getMatchingOptions(token);
        }
        final List<String> matches = new ArrayList<>(1);
        if (options.hasLongOption(token))
        {
            final Option option = options.getOption(token);
            matches.add(option.getLongOpt());
        }

        return matches;
    }

    /**
     * Breaks <code>token</code> into its constituent parts
     * using the following algorithm.
     *
     * <ul>
     *  <li>ignore the first character ("<b>-</b>")</li>
     *  <li>for each remaining character check if an {@link Option}
     *  exists with that id.</li>
     *  <li>if an {@link Option} does exist then add that character
     *  prepended with "<b>-</b>" to the list of processed tokens.</li>
     *  <li>if the {@link Option} can have an argument value and there
     *  are remaining characters in the token then add the remaining
     *  characters as a token to the list of processed tokens.</li>
     *  <li>if an {@link Option} does <b>NOT</b> exist <b>AND</b>
     *  <code>stopAtNonOption</code> <b>IS</b> set then add the special token
     *  "<b>--</b>" followed by the remaining characters and also
     *  the remaining tokens directly to the processed tokens list.</li>
     *  <li>if an {@link Option} does <b>NOT</b> exist <b>AND</b>
     *  <code>stopAtNonOption</code> <b>IS NOT</b> set then add that
     *  character prepended with "<b>-</b>".</li>
     * </ul>
     *
     * @param token The current token to be <b>burst</b>
     * at the first non-Option encountered.
     * @throws ParseException if there are any problems encountered
     *                        while parsing the command line token.
     */
    protected void handleConcatenatedOptions(final String token) throws ParseException
    {
        for (int i = 1; i < token.length(); i++)
        {
            final String ch = String.valueOf(token.charAt(i));

            if (options.hasOption(ch))
            {
                handleOption(options.getOption(ch));

                if (currentOption != null && token.length() != i + 1)
                {
                    // add the trail as an argument of the option
                    currentOption.addValueForProcessing(token.substring(i + 1));
                    break;
                }
            }
            else
            {
                handleUnknownToken(stopAtNonOption && i > 1 ? token.substring(i) : token);
                break;
            }
        }
    }
}
