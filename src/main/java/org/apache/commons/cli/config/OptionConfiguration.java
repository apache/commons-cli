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

/**
 * Contains information from a set of properties of the form
 * {@code option.[name].*}. The following option values are supported:
 *
 * <ul>
 * <li>{@code option.[name].opts=[options]}: specify short and long options, a
 * short option or a long option. For the first form, a single character is
 * specified for the short option followed by a forward slash, followed by a
 * text string of at least two characters for the long option. For the second
 * form, a single character is specified for the short option. For the final
 * form, text of at least two characters is specified for the long option. This
 * is the only required configuration option.
 *
 * <p>
 * If the {@link GlobalConfiguration} {@code OPTION_TYPE} is not defined, all
 * defined options must take the same form, otherwise an exception will the
 * thrown. if the global configuration is defined then all options must conform
 * to that type;
 * </li>
 * <li>{@code option.[name].hasArg=[true|false]}: if {@code true} then the
 * argument will be supplied with a value via the command line; otherwise the
 * option will not require a value. By default, this is {@code false} so is not
 * required for options that do not require arguments although can be supplied
 * for clarity;</li>
 * <li>{@code option.[name].argName=[argName]}: used when displaying help. By
 * default, this configuration is not required;</li>
 * <li>{@code option.[name].description=[description]}: used when displaying
 * help. By default this configuration is not required.</li>
 * </ul>
 *
 * <p>
 * The {@code [name]} section of the option is the name that
 * {@link OptionListener}s will receive an update for via the {@code option}
 * parameter via
 * {@link OptionListener#option(java.lang.String, java.lang.Object)}; if the the
 * argument's {@code hasArg} is {@code true}, then the {@code value} will be the
 * value supplied via the command line; for arguments that have {@code hasArg}
 * as {@code false}, {@code value} will be {@code null}.
 *
 * <p>
 * Lines beginning with &#35; are ignored.
 *
 * <p>
 * For example, a configuration file named {@code opt.config} could be created
 * with the following option configuration (note that typically, creators of
 * configurations will likely have the {@code name} and the long option text
 * using the same text, although here they are different to add clarity to how
 * the {@link OptionListener} is updated):
 *
 * <pre>
 * option.outfile.opts=f/file
 * option.outfile.hasArg=true
 * option.outfile.argName=file
 * option.outfile.description=File to write to.
 *
 * option.textToWrite.opts=t/text
 * option.textToWrite.hasArg=true
 * option.textToWrite.argName=text
 * option.textToWrite.description=Text to write to the file.
 *
 * option.writeover.opts=o/overwrite
 * # We do not need to specify this since all options are false for hasArg if not specified
 * # option.writeover.hasArg=false
 * option.writeover.description=If set, write over the existing file; \
 *      otherwise, append to the file.
 *
 * option.showHelp.opts=h/help
 * option.showHelp.hasArg=false
 * option.showHelp.description=Print this help then quit.
 * </pre>
 *
 * <p>
 * ... When parsed by the {@link CommandLineConfiguration} with the value
 * {@code --file datafile.txt} would update all registered
 * {@link OptionListener}s via
 * {@link OptionListener#option(java.lang.String, java.lang.Object)} and would
 * receive an update with the option {@code file} given a value of
 * {@code datafile.txt}.
 *
 * <p>
 * In all cases, lines may be escaped; escaped lines must end in a trailing
 * backslash character; lines to be appended must be indented using white space
 * (space character and/or tab characters). This is especially useful when
 * defining help descriptions. For example:
 *
 * <pre>
 * option.file.description=Supply the output file to write results to. If the \
 *      file doesn't exist, it is created. If the file does exist, it is
 *      appended to.
 * </pre>
 *
 * Note in the above example the spaces before the backslashes - this is so
 * sentences are not 'glued' together and provide spacing that is easy on the
 * eye to readers of the output.
 */
public class OptionConfiguration
{

    /**
     * The name of the property; this is the name as it appears in the
     * configuration after the {@code option.} declaration.
     */
    private String name;

    /**
     * The description for the option that will be displayed when help is
     * displayed.
     */
    private String description;

    /**
     * The short-form option of the CLI declaration.
     */
    private String shortOption;

    /**
     * The long-form option of the CLI declaration.
     */
    private String longOption;

    /**
     * The argument name (if required).
     */
    private String argName;

    /**
     * Determines if this option configuration has an argument. The motivation
     * for using {@code java.lang.Boolean} is that we need to determine if a
     * configuration has been defined where the user defines hasArg twice for
     * the same option. Only be having the option of having the argument as 
     * {@code null} can we check for this.
     */
    private Boolean hasArg;

    /**
     * Get the name.
     *
     * @return the name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Set the name.
     *
     * @param name the name.
     */
    public void setName(final String name)
    {
        this.name = name;
    }

    /**
     * Get the description.
     *
     * @return the description.
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Set the description.
     *
     * @param description the description.
     */
    public void setDescription(final String description)
    {
        this.description = description;
    }

    /**
     * Get the short option.
     *
     * @return the short option.
     */
    public String getShortOption()
    {
        return shortOption;
    }

    /**
     * Set the short option.
     *
     * @param shortOption the short option.
     */
    public void setShortOption(final String shortOption)
    {
        this.shortOption = shortOption;
    }

    /**
     * Get the long option.
     *
     * @return the long option.
     */
    public String getLongOption()
    {
        return longOption;
    }

    /**
     * Set the long option.
     *
     * @param longOption the long option.
     */
    public void setLongOption(String longOption)
    {
        this.longOption = longOption;
    }

    /**
     * Get the argument name.
     *
     * @return the argument name.
     */
    public String getArgName()
    {
        return argName;
    }

    /**
     * Set the argument name.
     *
     * @param argName the argument name.
     */
    public void setArgName(final String argName)
    {
        this.argName = argName;
    }

    /**
     * Determine if this option has an argument.
     *
     * @return {@code true} if the option takes an argument, {@code false}
     * if it doesn't; {@code null} if the argument has never been set.
     */
    public Boolean hasArg()
    {
        return hasArg;
    }

    /**
     * Set that the option has an argument.
     *
     * @param hasArg {@code true} if the option takes an argument; {@code false}
     * otherwise.
     */
    public void setHasArg(final boolean hasArg)
    {
        this.hasArg = hasArg;
    }
}
