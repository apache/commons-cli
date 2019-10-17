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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.cli.HelpFormatter;

/**
 * The global configuration is the configuration for an entire file with at
 * least one {@link OptionConfiguration}.
 *
 * <p>
 * Lines beginning with &#35; are ignored.
 *
 * <p>
 * Global configuration items are as follows - the global options beginning with
 * {@code HELP_} enable callers to print help linked to an option without any
 * code whatsoever (more on this below):
 *
 * <ul>
 *
 * <li>{@code OPTION_TYPE=[OPTION_TYPE]}: where {@code [OPTION_TYPE]} is one of
 * {@code BOTH} where both short and long options are used, specified as
 * {@code [char]/[text]}, {@code SHORT} where only short options are specified
 * as a single character, and {@code LONG} where only long options are
 * specified. However, {@code OPTION_TYPE} is not strictly required so long as
 * the options specified are all consistent (although it aids readability for
 * others maintaining the file to implicitly define {@code OPTION_TYPE});</li>
 *
 * <li>{@code HELP_OPTION_NAME=[optionName]}: where {@code [optionName]} is the
 * name of an option defined in the options configuration further down in the
 * options configuration (see {@link OptionConfiguration});</li>
 *
 * <li>{@code HELP_COMMAND_NAME=[commandName]}: The command name is the name of
 * the command that will be printed when the "Usage: commandName..." is printed
 * from a call to invoking the help option;</li>
 *
 * <li>{@code HELP_COMMAND_HEADER=[headerText]}: where {@code [headerText]} is
 * the text that will be displayed as the header text from an invocation to call
 * for help to be printed. The command header is optional; and</li>
 *
 * <li>{@code HELP_COMMAND_FOOTER=[footerText]}: where {@code [footerText]} is
 * the text that will be displayed as the footer text from an invocation to call
 * for help to be printed. Like the header, the command footer is optional.
 * </li>
 * </ul>
 *
 * In all cases, lines may be escaped; escaped lines must end in a trailing
 * backslash character; lines to be appended must be indented using white space
 * (space character and/or tab characters). For example:
 *
 * <pre>
 * HELP_COMMAND_HEADER=Show some useful information, \
 *     with some extra escaped lines. \
 *     Also, there's some extra information here about the command.
 * </pre>
 *
 * Note in the above example the spaces before the backslashes - this is so
 * sentences are not 'glued' together and provide spacing that is easy on the
 * eye to readers of the output.
 *
 * <p>
 * Regardless of how the lines are escaped with regard to the number of
 * characters per line, this will not affect the CLI help output since the CLI
 * {@link HelpFormatter} will format this according to the API rules for line
 * sizes.
 *
 * <p>
 * For example, by adding the following global options to the start of the
 * example file defined in {@link OptionConfiguration}:
 *
 * <pre>
 * # The following definition implies there must be an option.showHelp option
 * # defined in the options following these global definitions:
 * HELP_OPTION_NAME=showHelp
 * HELP_COMMAND_NAME=writeData
 * HELP_COMMAND_HEADER=Write the specified text to the specified file. If the \
 *      options contain spaces or special characters, supply the arguments in \
 *      double quotes.
 * HELP_COMMAND_FOOTER=Copyleft Foo, Bar &amp; Baz International.
 * </pre>
 *
 * <p>
 * ... When invoking {@code --help} or {@code -h} via the
 * {@link CommandLineConfiguration}:
 *
 * <pre>
 * InputStream is = ConfigurationParserTest.class.getResourceAsStream("opt.config");
 * CommandLineConfiguration cliConfig = new CommandLineConfiguration();
 * cliConfig.addOptionListener(listener);
 * // args[] from the public static void main(String[] args) call:
 * cliConfig.process(is, args);
 * </pre>
 *
 * <p>
 * ... Would produce the following output:
 *
 * <pre>
 * usage: writeData
 * Write the specified text to the specified file. If the options contain
 * spaces or special characters, supply the arguments in double quotes.
 *  -f,--file &lt;file&gt;   File to write to.
 *  -h,--help          Print this help then quit.
 *  -o,--overwrite     If set, write over the existing file; otherwise,
 *                     append to the file.
 *  -t,--text &lt;text&gt;   Text to write to the file.
 * Copyleft Foo, Bar &amp; Baz International.
 * </pre>
 *
 * <p>
 * Callers are required to decide what to do when help is invoked in this
 * manner; typically, in the {@link OptionListener}, callers will check for the
 * call to help and then exit gracefully.
 *
 * <p>
 * The {@code HELP_} global configuration options are not mandatory and are
 * there for convenience; however, configuration creators can omit these if they
 * wish to define their own help (in which case, the {@link OptionListener} must
 * cater for the call to help).
 *
 */
public class GlobalConfiguration
{

    /**
     * Regular expression to match global option configurations. The general
     * form of a global configuration is upper case characters using underscores
     * (if necessary) with the value separated by an equals symbol, with the
     * value being any number of characters (with a minimum of one).
     */
    public static final String OPTION_REGEX = "([A-Z_]+)=(.+)";

    /**
     * Declaration for global option type (short, long, both).
     */
    public static final String OPTION_TYPE = "OPTION_TYPE";

    /**
     * Declaration for the name of the command that will be invoked to show help
     * options.
     */
    public static final String HELP_COMMAND_NAME = "HELP_COMMAND_NAME";

    /**
     * Declaration for the header of the command that will be shown when
     * invoking help.
     */
    public static final String HELP_COMMAND_HEADER = "HELP_COMMAND_HEADER";

    /**
     * Declaration for the footer of the command that will be shown when
     * invoking help.
     */
    public static final String HELP_COMMAND_FOOTER = "HELP_COMMAND_FOOTER";

    /**
     * Declaration for the option name of that is defined in the
     * {@link OptionConfiguration} such that when that CLI option is invoked,
     * help will be printed.
     */
    public static final String HELP_OPTION_NAME = "HELP_OPTION_NAME";

    /**
     * The key is the actual name part of the {@code option.[name].*}
     * declaration, in other words the option name.
     */
    private final Map<String, OptionConfiguration> optionMap = new LinkedHashMap<>();

    /**
     * The option type of the configuration.
     */
    private OptionsTypeEnum optionsType;

    /**
     * If the global configuration has help defined, this is the name of the
     * command that will be printed with the help text.
     */
    private String helpCommandName;

    /**
     * If the global configuration has help defined, this is the header text of
     * the command that will be printed with the help text.
     */
    private String helpCommandHeader;

    /**
     * If the global configuration has help defined, this is the footer text of
     * the command that will be printed with the help text.
     */
    private String helpCommandFooter;

    /**
     * The option configuration name {@code option.[name]}, for example,
     * {@code option.help}.
     */
    private String helpOptionName;

    /**
     * Update the given global configuration with the specified line data read.
     *
     * @param line non-{@code null} line data to parse that matches
     * {@link #OPTION_REGEX}.
     *
     * @throws ConfigurationException if the configuration is defined
     * incorrectly.
     */
    public void updateGlobalConfiguration(String line) throws ConfigurationException
    {
        String[] data = line.split("=");
        if (data[0].trim().matches(GlobalConfiguration.OPTION_TYPE))
        {
            parseOptionType(data[1].trim());
        }
        else if (data[0].trim().startsWith("HELP"))
        {
            parseHelp(line);
        }
        else
        {
            throw new ConfigurationException(
                    "Unknown global configuration declaration: " + data[0].trim());
        }
    }

    /**
     * Get the name of the command that will be printed when (if) the user has
     * defined help.
     *
     * @return the name of the command that the help displays information for.
     */
    public String getHelpCommandName()
    {
        return helpCommandName;
    }

    /**
     * Get the help command header.
     *
     * @return the command header, if it has been set; {@code null} otherwise.
     */
    public String getHelpCommandHeader()
    {
        return helpCommandHeader;
    }

    /**
     * Get the help command footer.
     *
     * @return the command footer, if it has been set; {@code null} otherwise.
     */
    public String getHelpCommandFooter()
    {
        return helpCommandFooter;
    }

    /**
     * Get the option name specified by the global configuration; the name is
     * the name of the {@link OptionConfiguration} that must exist in the option
     * configurations.
     *
     * @return the help option name if it is set; {@code null} otherwise.
     */
    public String getHelpOptionName()
    {
        return helpOptionName;
    }

    /**
     * Add the specified option configuration.
     *
     * @param optionConfig non-{@code null} option configuration to add.
     */
    public void addOptionConfiguration(OptionConfiguration optionConfig)
    {
        optionMap.put(optionConfig.getName(), optionConfig);
    }

    /**
     * Get the option map for this configuration; the key to the map will be the
     * option configuration names defined by the {@code option.[name]}
     * declarations.
     *
     * @return the non-{@code null}, non-empty option map (note that if no
     * option configurations are defined when parsing an exception will be
     * thrown.
     */
    public Map<String, OptionConfiguration> getOptionConfigurations()
    {
        return Collections.unmodifiableMap(optionMap);
    }

    /**
     * Get the options type for this global configuration.
     *
     * @return the options type; may be {@code null}.
     */
    public OptionsTypeEnum getOptionsType()
    {
        return optionsType;
    }

    /**
     * Set the options type for this global configuration.
     *
     * @param optionsType the options type.
     */
    public void setOptionsType(OptionsTypeEnum optionsType)
    {
        this.optionsType = optionsType;
    }

    /**
     * Parse the option type.
     *
     * @param data data containing the option type - one of
     * {@link #GLOBAL_OPTION_TYPE_SHORT}, {@link #GLOBAL_OPTION_TYPE_LONG}, or
     * {@link #GLOBAL_OPTION_TYPE_BOTH}.
     *
     * @throws ConfigurationException if the global options type has already
     * been set, or if the options type did not match a known type.
     */
    private void parseOptionType(String data) throws ConfigurationException
    {
        if (getOptionsType() != null)
        {
            throw new ConfigurationException(OPTION_TYPE
                    + " has already been defined as "
                    + getOptionsType().getType()
                    + " but found second definition: " + data);
        }
        if (OptionsTypeEnum.BOTH.getType().equals(data))
        {
            setOptionsType(OptionsTypeEnum.BOTH);
        }
        else if (OptionsTypeEnum.SHORT.getType().equals(data))
        {
            setOptionsType(OptionsTypeEnum.SHORT);
        }
        else if (OptionsTypeEnum.LONG.getType().equals(data))
        {
            setOptionsType(OptionsTypeEnum.LONG);
        }
        else
        {
            throw new ConfigurationException("Unknown options type: " + data);
        }
    }

    /**
     * Parses global configurations that begin with {@code HELP_}.
     *
     * @param line non-{@code null} line to parse.
     */
    private void parseHelp(final String line) throws ConfigurationException
    {
        String[] data = line.split("=");
        if (GlobalConfiguration.HELP_COMMAND_NAME.equals(data[0].trim()))
        {
            helpCommandName = data[1].trim();
        }
        else if (GlobalConfiguration.HELP_COMMAND_HEADER.equals(data[0].trim()))
        {
            helpCommandHeader = data[1].trim();
        }
        else if (GlobalConfiguration.HELP_COMMAND_FOOTER.equals(data[0].trim()))
        {
            helpCommandFooter = data[1].trim();
        }
        else if (GlobalConfiguration.HELP_OPTION_NAME.equals(data[0].trim()))
        {
            helpOptionName = data[1].trim();
        }
        else
        {
            throw new ConfigurationException("Unknown help configuration: " + line);
        }
    }
}
