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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * Main entry point to the configuration library.
 *
 * <p>
 * Combines processing a command line configuration file and passes the options
 * to the command line parser. Listeners should register themselves with this
 * instance in order to be notified of options as they are encountered.
 *
 * <p>
 * Configuration comes in two forms, each in the same file - <i>global</i> or
 * <i>option</i> configurations. {@link GlobalConfiguration} enables the
 * definition of command line help properties (command name, header, footer
 * etc.) as well as what combination of options to use - short, long or both.
 * {@link OptionConfiguration}s enable users to define options that are
 * programmatically built into {@link Options} and dealt with under the hood.
 * All that is required is that an {@link OptionListener} is added as a listener
 * and contains the code to execute when different options are passed in.
 *
 * <p>
 * By default, no global options are required and only one option configuration
 * is required minimum to get going. Regardless, all global options must be
 * defined before any standard options, otherwise an error will be thrown. Each
 * option configuration takes the form of {@code option.[optionName].[property]}
 * where each {@code property} is a pre-defined configuration proeprty defined
 * in {@link OptionConfiguration}.
 *
 * <p>
 * An example of using the command line configuration - see
 * {@link OptionListener} for how a listener would deal with the updates from
 * the processing of command line arguments, and {@link OptionConfiguration} for
 * an example configuration that matches this example:
 *
 * <pre>
 * {@code
 * MyAppListener listener = new MyAppListener();
 InputStream is = ConfigParserTest.class.getResourceAsStream("opt.config");
 CommandLineConfiguration cliConfig = new CommandLineConfiguration();
 cliConfig.addOptionListener(listener);
 // args[] from the public static void main(String[] args) call:
 cliConfig.process(is, args);
 Application application = new Application();
 if (listener.file != null && listener.text != null)
 {
      application.write(listener.file, listener.text, listener.overwrite);
 }
 else
 {
      System.err.println("File and text must be supplied.");
      System.exit(1);
 }
 }
 * </pre>
 */
public class CommandLineConfiguration
{

    /**
     * Listeners to be notified of updates.
     */
    private final List<OptionListener> listeners = new ArrayList<>();

    /**
     * Global configuration parsed by this CLI configuration.
     */
    private GlobalConfiguration globalConfig;

    /**
     * Options parsed from the command line parser.
     */
    private List<Option> parsedOptions;

    /**
     * Options built up from converting {@link OptionConfiguration}s.
     */
    private Options options;

    /**
     * Take the given input stream and arguments and process them, informing all
     * registered listeners of any options that are parsed along with their
     * values. Note that if both short and long options are used, listeners will
     * be notified twice for each option; it is up to listeners to cater for
     * this to their needs.
     *
     * @param is non-{@code null} readable input stream of a command line
     * configuration to parse.
     *
     * @param args non-{@code null} arguments, typically supplied via a call
     * from a command line process.
     *
     * @throws ConfigurationException if the configuration file contains any invalid
     * definitions.
     *
     * @throws IOException if there is a problem reading the input stream.
     */
    public void process(final InputStream is, final String[] args)
            throws ConfigurationException, IOException
    {
        final ConfigurationParser configParser = new ConfigurationParser();
        globalConfig = configParser.parse(is);
        final Map<String, OptionConfiguration> optionConfig = globalConfig.getOptionConfigurations();
        options = buildOptions(optionConfig);
        final CommandLineParser parser = new DefaultParser();
        final CommandLine cli;
        try
        {
            cli = parser.parse(options, args);
        }
        catch (ParseException ex)
        {
            throw new ConfigurationException(ex.getMessage(), ex);
        }

        // if help has been defined, this is done for the user
        if (globalConfig.getHelpOptionName() != null)
        {
            OptionConfiguration optionHelp = optionConfig.get(
                    globalConfig.getHelpOptionName());
            if (optionHelp.hasArg())
            {
                throw new ConfigurationException("Error: Option "
                        + optionHelp.getName() + " cannot have an argument"
                        + " associated with it.");
            }
            // see if the caller has asked for help
            String optionHelpValue = optionHelp.getShortOption();
            if (optionHelpValue == null)
            {
                // they specified long options only so short option will be null
                optionHelpValue = optionHelp.getLongOption();
            }
            if (cli.hasOption(optionHelpValue))
            {
                printHelp();
                System.exit(0);
            }
        }

        final Option[] cliOptions = cli.getOptions();
        parsedOptions = Arrays.asList(cliOptions);
        for (final Option option : cliOptions)
        {
            for (int i = 0; i < listeners.size(); i++)
            {
                if (option.getOpt() != null)
                {
                    listeners.get(i).option(option.getOpt(), option.getValue());
                }
                if (option.getLongOpt() != null)
                {
                    listeners.get(i).option(option.getLongOpt(),
                            option.getValue());
                }
            }
        }
    }

    /**
     * Used if the caller wants to define their own help configuration or have
     * access to the underlying options.
     *
     * @return the non-{@code null} options.
     */
    public List<Option> getOptions()
    {
        return parsedOptions;
    }

    /**
     * Use the {@link HelpFormatter} to print help - for the help to contain
     * informative information, see {@link GlobalConfiguration} on how global
     * options can be set to inform the user of values to define in order to
     * create a valid help listing.
     */
    public void printHelp()
    {
        // print help, then quit
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(globalConfig.getHelpCommandName(),
                globalConfig.getHelpCommandHeader(), options,
                globalConfig.getHelpCommandFooter());
    }

    /**
     * Add the specified listener.
     *
     * @param listener non-{@code null} listener to add.
     *
     * @return {@code true} if the listener was added, {@code false} otherwise.
     */
    public boolean addOptionListener(final OptionListener listener)
    {
        return listeners.add(listener);
    }

    /**
     * Remove the specified listener.
     *
     * @param listener non-{@code null} listener to remove.
     *
     * @return {@code true} if the listener was removed, {@code false}
     * otherwise.
     */
    public boolean removeOptionListener(final OptionListener listener)
    {
        return listeners.remove(listener);
    }

    /**
     * Convert the specified map of configurations to valid command line
     * options.
     *
     * @param map non-{@code null} map of configuration options.
     *
     * @return Options based on the converted map.
     */
    private Options buildOptions(final Map<String, OptionConfiguration> map)
    {
        final Options options = new Options();
        for (OptionConfiguration optConfig : map.values())
        {
            final String shortOption = optConfig.getShortOption();
            final String longOption = optConfig.getLongOption();
            final String description = optConfig.getDescription();
            final String argName = optConfig.getArgName();
            final boolean hasArg = optConfig.hasArg();
            final Option option = new Option(shortOption, longOption, hasArg,
                    description);
            if (argName != null)
            {
                option.setArgName(argName);
            }
            options.addOption(option);
        }
        return options;
    }

}
