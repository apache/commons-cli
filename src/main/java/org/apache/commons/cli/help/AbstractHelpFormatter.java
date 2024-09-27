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
package org.apache.commons.cli.help;

import static java.lang.String.format;

import java.io.IOException;
import java.util.function.Function;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Util;

/**
 * The class for help formatters provides the framework to link the {@link Serializer} with the {@link OptionFormatter}
 * and a default {@link TableDef} so to produce a standard format help page.
 */
public abstract class AbstractHelpFormatter {

    /** The string to display at the beginning of the usage statement */
    public static final String DEFAULT_SYNTAX_PREFIX = "usage: ";

    /**
     * The {@link Serializer} that produces the final output.
     */
    protected final Serializer serializer;
    /**
     * The OptionFormatter.Builder used to display options within the help page
     */
    protected final OptionFormatter.Builder optionFormatBuilder;
    /**
     * The phrase printed before the syntax line.
     */
    protected String syntaxPrefix = DEFAULT_SYNTAX_PREFIX;
    /**
     * A function to convert a collection of Options into a {@link TableDef} for display within the page
     */
    protected Function<Iterable<Option>, TableDef> tableDefBuilder;

    /**
     * Constructs the base formatter.
     * @param serializer the serializer to output with
     * @param optionFormatBuilder the builder of {@link OptionFormatter} to format options for display.
     * @param defaultTableDefBuilder A function to build a {@link TableDef} from a collection of {@link Option}s.
     */
    protected AbstractHelpFormatter(final Serializer serializer, final OptionFormatter.Builder optionFormatBuilder,
                                    final Function<Iterable<Option>, TableDef> defaultTableDefBuilder) {
        this.serializer = serializer;
        this.optionFormatBuilder = optionFormatBuilder;
        this.tableDefBuilder = defaultTableDefBuilder;
    }

    /**
     * Sets the syntax prefix.  This is the phrase that is printed before the syntax line.
     *
     * @param prefix the new value for the syntax prefix.
     */
    public final void setSyntaxPrefix(final String prefix) {
        this.syntaxPrefix = prefix;
    }

    /**
     * Gets the currently set syntax prefix.
     * @return The currently set syntax prefix.
     */
    public final String getSyntaxPrefix() {
        return syntaxPrefix;
    }

    /**
     * Gets the {@link Serializer} associated with this help formatter.
     * @return The {@link Serializer} associated with this help formatter.
     */
    public final Serializer getSerializer() {
        return serializer;
    }

    /**
     * Constructs an {@link OptionFormatter} for the specified {@link Option}.
     * @param option The Option to format.
     * @return an {@link OptionFormatter} for the specified {@link Option}.
     */
    public final OptionFormatter getOptionFormatter(final Option option) {
        return optionFormatBuilder.build(option);
    }

    /**
     * Prints the help for {@link Options} with the specified command line syntax.
     *
     * @param cmdLineSyntax the syntax for this application
     * @param header the banner to display at the beginning of the help
     * @param options the {@link Options} to print
     * @param footer the banner to display at the end of the help
     * @param autoUsage whether to print an automatically generated usage statement
     * @throws IOException If the output could not be written to the {@link Serializer}
     */
    public final void printHelp(final String cmdLineSyntax, final String header, final Options options,
                                final String footer, final boolean autoUsage) throws IOException {
        printHelp(cmdLineSyntax, header, options.getOptions(), footer, autoUsage);
    }

    /**
     * Prints the help for a collection of {@link Option}s with the specified command line syntax.
     *
     * @param cmdLineSyntax the syntax for this application
     * @param header the banner to display at the beginning of the help
     * @param options the collection of {@link Option} objects to print.
     * @param footer the banner to display at the end of the help
     * @param autoUsage whether to print an automatically generated usage statement
     * @throws IOException If the output could not be written to the {@link Serializer}
     */
    public void printHelp(final String cmdLineSyntax, final String header, final Iterable<Option> options,
                          final String footer, final boolean autoUsage) throws IOException {
        if (Util.isEmpty(cmdLineSyntax)) {
            throw new IllegalArgumentException("cmdLineSyntax not provided");
        }

        if (autoUsage) {
            serializer.writePara(format("%s %s %s", syntaxPrefix, cmdLineSyntax, OptionFormatter.asSyntaxOptions(optionFormatBuilder, options)));
        } else {
            serializer.writePara(format("%s %s", syntaxPrefix, cmdLineSyntax));
        }

        if (!Util.isEmpty(header)) {
            serializer.writePara(header);
        }

        serializer.writeTable(tableDefBuilder.apply(options));

        if (!Util.isEmpty(footer)) {
            serializer.writePara(footer);
        }
    }

    /**
     * Prints the option table for the specified {@link Options} to the {@link Serializer}.
     * @param options the Options to print in the table.
     * @throws IOException If the output could not be written to the {@link Serializer}
     */
    public final void printOptions(final Options options) throws IOException {
        printOptions(options.getOptions());
    }

    /**
     * Prints the option table for a collection of {@link Option} objects to the {@link Serializer}.
     * @param options the collection of Option objects to print in the table.
     * @throws IOException If the output could not be written to the {@link Serializer}
     */
    public final void printOptions(final Iterable<Option> options) throws IOException {
        printOptions(tableDefBuilder.apply(options));
    }
    /**
     * Prints a {@link TableDef} to the {@link Serializer}.
     * @param tableDef the {@link TableDef} to print.
     * @throws IOException If the output could not be written to the {@link Serializer}
     */
    public final void printOptions(final TableDef tableDef) throws IOException {
        serializer.writeTable(tableDef);
    }

    /**
     * Formats the {@code argName} as an argument a defined in the enclosed {@link OptionFormatter.Builder}
     * @param argName the string to format as an argument.
     * @return the {@code argName} formatted as an argument.
     */
    public final String asArgName(final String argName) {
        return optionFormatBuilder.asArgName(argName);
    }
}
