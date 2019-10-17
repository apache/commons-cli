/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.commons.cli.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The configuration parser takes an input stream and generates the
 * {@link GlobalConfiguration} and {@link OptionConfiguration} objects obtained
 * from the stream.
 *
 */
public class ConfigurationParser
{

    /**
     * All alphabetic characters regular expression.
     */
    static final String A_Z = "a-zA-Z";

    /**
     * Numbers regular expression.
     */
    static final String ALPHA_NUM = A_Z + "0-9";

    /**
     * Regular expression to match short options.
     */
    static final String SHORT_OPTION_FORMAT = "([" + ALPHA_NUM + "])";

    /**
     * Regular expression to match long options.
     */
    static final String LONG_OPTION_FORMAT = "([" + ALPHA_NUM + "\\-]+)";

    /**
     * Regular expression for lines matching the start of a
     * {@link OptionConfiguration} declaration (before the '=').
     */
    static final String OPTION_REGEX_PREFIX = "\\s*option\\.([" + ALPHA_NUM
            + "\\-_]*)\\.([" + A_Z + "\\-_]*)";

    /**
     * Regular expression for an entire line of an {@link OptionConfiguration}.
     */
    static final String OPTION_REGEX_BASIC_LINE = OPTION_REGEX_PREFIX
            + "=(.*)\\s*\\\\{0,1}";

    /**
     * Regular expression for an escaped line.
     */
    static final String OPTION_REGEX_ESCAPED_LINE = "\\s+(.*)\\s*\\\\{0,1}";

    /**
     * Defines the option value(s) for a given configuration - these will be the
     * long and/or short option for the configuration.
     */
    static final String OPTS = "opts";

    /**
     * Defines the constant string used to define a description for a
     * configuration.
     */
    static final String DESCRIPTION = "description";

    /**
     * Defines the constant string to define if a given configuration uses an
     * argument.
     */
    static final String HAS_ARG = "hasArg";

    /**
     * Defines the constant for the configuration argument name, if the
     * configuration has an argument; this is the name that will be suffixed
     * (for example) when a user invokes help in order to print out the
     * different options.
     */
    static final String ARG_NAME = "argName";

    /**
     * Defines the constant for providing a default value for a configuration.
     */
    static final String DEFAULT = "default";

    /**
     * Parse the input stream and create the option configuration.
     *
     * @param is non-{@code null} input stream to read.
     *
     * @param encoding non-{@code null} encoding for the input stream.
     *
     * @return the option configuration read from the input stream.
     *
     * @throws ConfigurationException if any of the configuration options are
     * not defined correctly, or there are no options defined.
     *
     * @throws IOException if the input stream could not be read.
     */
    public GlobalConfiguration parse(final InputStream is, final String encoding)
            throws ConfigurationException, IOException
    {
        final InputStreamReader isr = new InputStreamReader(is, encoding);
        final BufferedReader buf = new BufferedReader(isr);
        String line = "";
        StringBuilder builtLine = null;
        int currentLineNo = 0;
        boolean globalConfigParsed = false;
        final GlobalConfiguration globalConfig = new GlobalConfiguration();
        while ((line = buf.readLine()) != null)
        {
            currentLineNo++;
            if (line.trim().startsWith("#") || line.isEmpty())
            {
                continue;
            }
            boolean lineEscaped = false;
            if (builtLine != null && line.matches(OPTION_REGEX_ESCAPED_LINE))
            {
                // it's not null so we must be building up the data; the
                // previous line must have been an escaped line (as could this one)
                // first, remove leading spaces/tabs:
                final String stripLeading = line.replaceAll("^\\s+", "");
                int lastIndex = stripLeading.length();
                if (stripLeading.trim().endsWith("\\"))
                {
                    // it's another escaped line - keep building it up:
                    lineEscaped = true;
                    lastIndex = stripLeading.lastIndexOf('\\');
                }
                else
                {
                    // the line is complete based on previous escaped lines
                }
                builtLine.append(stripLeading.substring(0, lastIndex));
                if (lineEscaped)
                {
                    continue;
                }
            }
            else if (builtLine != null)
            {
                // doesn't match our regular expression
                throw new ConfigurationException(currentLineNo,
                        "Invalid escaped line: " + line);
            }
            if (builtLine == null)
            {
                // new line data to read
                builtLine = new StringBuilder();
                if (line.endsWith("\\"))
                {
                    // start of an escaped line, build it up from here:
                    builtLine.append(line.substring(0, line.lastIndexOf("\\")));
                    continue;
                }
                else
                {
                    // new line and it's complete and ready to go
                    builtLine.append(line);
                }
            }
            if (builtLine.toString().trim().matches(GlobalConfiguration.OPTION_REGEX))
            {
                // if this is /after/ any standard options, throw an exception
                // since all global options must come /before/ standard options
                if (globalConfigParsed)
                {
                    throw new ConfigurationException(currentLineNo, "Invalid global"
                            + " configuration definition; global configurations"
                            + " must come BEFORE standard \"option...\" definitions");
                }
                // it's a pattern of the form ABC_XYZ=[text], let the global
                // configuration deal with it:
                try
                {
                    globalConfig.updateGlobalConfiguration(builtLine.toString());
                    // global configuration updated, carry on
                    builtLine = null;
                }
                catch (ConfigurationException ex)
                {
                    throw new ConfigurationException(currentLineNo, ex.getMessage());
                }
            }
            else if (builtLine.toString().matches(OPTION_REGEX_BASIC_LINE))
            {
                // it's a standard option.[name].* definition;
                // there shall be no more global definitions after this:
                globalConfigParsed = true;
                final Pattern p = Pattern.compile(ConfigurationParser.OPTION_REGEX_BASIC_LINE);
                final Matcher m = p.matcher(builtLine);
                m.matches();
                // reset the line for next time, we're done
                final String name = m.group(1);
                final String subOption = m.group(2);
                final String value = m.group(3);
                updateCurrentOption(globalConfig, name, subOption,
                        value, currentLineNo);
                builtLine = null;
            }
            else
            {
                // doesn't match our regular expression
                throw new ConfigurationException(currentLineNo, "Invalid option"
                        + " definition: " + line);
            }
        }
        if (globalConfig.getOptionConfigurations().isEmpty())
        {
            // there were no options in the file
            throw new ConfigurationException("The configuration file contained no"
                    + " options to parse");
        }
        buf.close();
        isr.close();
        Map<String, OptionConfiguration> options = globalConfig.getOptionConfigurations();
        for (String name : options.keySet())
        {
            // any options that did not have their hasArg set will be null;
            // in which case set them to false
            OptionConfiguration optConfig = options.get(name);
            if (optConfig.hasArg() == null)
            {
                optConfig.setHasArg(false);
            }
        }
        return globalConfig;
    }

    /**
     * Given the specified name, update the current option detected in the
     * configuration file and determine if this option is a new configuration,
     * an existing configuration that is still being built, or the next
     * configuration.
     *
     * @param globalConfig non-{@code null} global configuration data.
     *
     * @param name non-{@code null} name of the option currently being examined.
     *
     * @param subOption non-{@code null} the sub-option being parsed, for
     * example 'description', 'hasArg'.
     *
     * @param value non-{@code null} the value of the option being examined.
     *
     * @param currentLineNo current line number of the input stream being
     * parsed.
     *
     * @throws ConfigurationException if there are any problems extracting the
     * data.
     */
    private void updateCurrentOption(
            final GlobalConfiguration globalConfig, final String name,
            final String subOption, final String value, final int currentLineNo)
            throws ConfigurationException
    {
        checkOptionNotRedefined(globalConfig.getOptionConfigurations(), name,
                currentLineNo);
        OptionConfiguration currentOption = globalConfig.getOptionConfigurations().get(name);
        if (currentOption == null)
        {
            // it's a new option configuration:
            currentOption = new OptionConfiguration();
            currentOption.setName(name);
            globalConfig.addOptionConfiguration(currentOption);
        }

        if (OPTS.equals(subOption))
        {
            OptionsTypeEnum optionsType = null;
            final String opt = value.trim();
            if (opt.contains("/"))
            {
                optionsType = OptionsTypeEnum.BOTH;
            }
            else if (opt.length() == 1)
            {
                optionsType = OptionsTypeEnum.SHORT;
            }
            else if (opt.length() > 1)
            {
                optionsType = OptionsTypeEnum.LONG;
            }
            else
            {
                String message = "Empty option value; must be a non-zero length"
                        + " string";
                // zero characters or null
                if (globalConfig.getOptionsType() != null)
                {
                    // already set, inform them of their decision
                    message += "; global configuration is defined as "
                            + globalConfig.getOptionsType().getType();
                }
                throw new ConfigurationException(currentLineNo, message);

            }
            if (currentOption.getShortOption() != null
                    || currentOption.getLongOption() != null)
            {
                throw new ConfigurationException(currentLineNo, OPTS
                        + " has already been defined for option " + name);
            }
            checkCurrentOption(globalConfig, optionsType, currentOption, opt,
                    currentLineNo);
        }
        else if (DESCRIPTION.equals(subOption))
        {
            if (currentOption.getDescription() != null)
            {
                throw new ConfigurationException(currentLineNo, DESCRIPTION
                        + " has already been defined for option " + name);
            }
            currentOption.setDescription(value);
        }
        else if (HAS_ARG.equals(subOption))
        {
            if (currentOption.hasArg() != null)
            {
                throw new ConfigurationException(currentLineNo, HAS_ARG
                        + " has already been defined for option " + name);
            }
            currentOption.setHasArg(Boolean.parseBoolean(value));
        }
        else if (ARG_NAME.equals(subOption))
        {
            if (currentOption.getArgName() != null)
            {
                throw new ConfigurationException(currentLineNo, ARG_NAME
                        + " has already been defined for option " + name);
            }
            currentOption.setArgName(value);
        }
        else if (DEFAULT.equals(subOption))
        {
            // TODO
        }
        else
        {
            throw new ConfigurationException(currentLineNo,
                    "Unknown configuration option: " + subOption);
        }
    }

    /**
     * Check that an option that has already been defined is not re-defined
     * after another option (or options) have been defined - options must be
     * declared together.
     *
     * @param options non-{@code null} configuration options.
     *
     * @param name non-{@code null} name of the option currently being examined.
     *
     * @param currentLineNo current line number of the input stream being
     * parsed.
     *
     * @throws ConfigurationException if the named option has already been
     * defined but the latest option is not the same.
     */
    private void checkOptionNotRedefined(
            final Map<String, OptionConfiguration> options, final String name, 
            final int currentLineNo)
            throws ConfigurationException
    {
        if (!options.isEmpty())
        {
            // check to ensure we've not defined an option that has already
            // been defined, and is defined after another option later on:
            int index = options.size();
            Object[] names = options.keySet().toArray();
            String lastName = names[index - 1].toString();
            if (!name.equals(lastName) && options.containsKey(name))
            {
                throw new ConfigurationException(currentLineNo,
                        "Bad configuration  ordering; options must be"
                        + " grouped together. Option '" + name + "' has"
                        + " been defined prior to the declaration of option"
                        + " '" + lastName + "'");
            }
        }
    }

    /**
     * Check that the option specified is of the correct type - short, long or
     * both - according to what has been defined in the global configuration (if
     * it has been defined), setting it if it hasn't and this is the first
     * option in the configuration and the option conforms to the expected type.
     *
     * @param globalConfig non-{@code null} global configuration.
     *
     * @param expectedType non-{@code null} expected type.
     *
     * @param currentOption non-{@code null} current option being parsed that
     * does not yet have the specified option set on it yet.
     *
     * @param option non-{@code null} option.
     *
     * @param currentLineNo current line number of where the data is encountered
     * in the input stream.
     *
     * @throws ConfigurationException if the specified option does not match the
     * global configuration's option type or the option formatting is incorrect
     * (such as specifying a short option when the option type is long option).
     */
    private void checkCurrentOption(final GlobalConfiguration globalConfig,
            final OptionsTypeEnum expectedType,
            final OptionConfiguration currentOption, final String option,
            final int currentLineNo)
            throws ConfigurationException
    {
        final OptionsTypeEnum globalOptType = globalConfig.getOptionsType();
        if (globalOptType != null && !(globalOptType.equals(expectedType)))
        {
            throw new ConfigurationException(currentLineNo,
                    "Configuration type specifies "
                    + GlobalConfiguration.OPTION_TYPE + " as "
                    + globalOptType.getType() + " but found "
                    + expectedType);
        }
        if (globalOptType == null)
        {
            globalConfig.setOptionsType(expectedType);
        }
        if (globalConfig.getOptionsType() == OptionsTypeEnum.BOTH)
        {
            final String forwardSlash = "/";
            final Pattern p = Pattern.compile(SHORT_OPTION_FORMAT
                    + forwardSlash + LONG_OPTION_FORMAT);
            final Matcher m = p.matcher(option);
            if (m.matches())
            {
                currentOption.setShortOption(m.group(1));
                currentOption.setLongOption(m.group(2));
            }
            else
            {
                throw new ConfigurationException(currentLineNo, "Invalid short and"
                        + " long option format; must be [character]/"
                        + " [text] but found " + option);
            }
        }
        else if (globalConfig.getOptionsType() == OptionsTypeEnum.SHORT)
        {
            final Pattern p = Pattern.compile(SHORT_OPTION_FORMAT);
            final Matcher m = p.matcher(option);
            if (m.matches())
            {
                currentOption.setShortOption(m.group(1));
            }
            else
            {
                throw new ConfigurationException(currentLineNo, "Expected single"
                        + " character for short option but found " + option);
            }
        }
        else
        {
            final Pattern p = Pattern.compile(LONG_OPTION_FORMAT);
            final Matcher m = p.matcher(option);
            if (m.matches())
            {
                currentOption.setLongOption(m.group(1));
            }
            else
            {
                throw new ConfigurationException(currentLineNo, "Expected text"
                        + " for long option but found " + option);
            }
        }
    }
}
