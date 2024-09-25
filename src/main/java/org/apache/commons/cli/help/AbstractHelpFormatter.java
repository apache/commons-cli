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

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.Util;

import java.io.IOException;
import java.util.function.Function;

import static java.lang.String.format;

public abstract class AbstractHelpFormatter {

    /** The string to display at the beginning of the usage statement */
    public static final String DEFAULT_SYNTAX_PREFIX = "usage: ";

    protected final Serializer serializer;
    protected final OptionFormatter.Builder optionFormatBuilder;
    protected String syntaxPrefix = DEFAULT_SYNTAX_PREFIX;
    protected Function<Iterable<Option>, TableDef> tableDefBuilder;

    protected AbstractHelpFormatter(Serializer serializer, OptionFormatter.Builder optionFormatBuilder,
                                    Function<Iterable<Option>, TableDef> defaultTableDefBuilder ) {
        this.serializer = serializer;
        this.optionFormatBuilder = optionFormatBuilder;
        this.tableDefBuilder = defaultTableDefBuilder;
    }

    /**
     * Sets the 'syntaxPrefix'.
     *
     * @param prefix the new value of 'syntaxPrefix'
     */
    public void setSyntaxPrefix(final String prefix) {
        this.syntaxPrefix = prefix;
    }

    public String getSyntaxPrefix() {
        return syntaxPrefix;
    }


    public Serializer getSerializer() {
        return serializer;
    }

    public OptionFormatter getOptionFormatter(Option option) {
        return optionFormatBuilder.build(option);
    }

    /**
     * Prints the help for {@code options} with the specified command line syntax.
     *
     * @param cmdLineSyntax the syntax for this application
     * @param header the banner to display at the beginning of the help
     * @param options the Options instance
     * @param footer the banner to display at the end of the help
     * @param autoUsage whether to print an automatically generated usage statement
     * @throws IllegalStateException if there is no room to print a line
     */
    public final void printHelp(final String cmdLineSyntax, final String header, final Options options,
                                final String footer, final boolean autoUsage) throws IOException {
        printHelp(cmdLineSyntax, header, options.getOptions(), footer, autoUsage);
    }
    /**
     * Prints the help for {@code options} with the specified command line syntax.
     *
     * @param cmdLineSyntax the syntax for this application
     * @param header the banner to display at the beginning of the help
     * @param options the Options instance
     * @param footer the banner to display at the end of the help
     * @param autoUsage whether to print an automatically generated usage statement
     * @throws IllegalStateException if there is no room to print a line
     */
    public final void printHelp(final String cmdLineSyntax, final String header, final Iterable<Option> options,
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
     * Prints the help for the specified Options to the specified writer, using the specified width, left padding and
     * description padding.
     */
    public void printOptions(Options options) throws IOException {
        printOptions(options.getOptions());
    }

    /**
     * Prints the help for the specified Options to the specified writer, using the specified width, left padding and
     * description padding.
     */
    public void printOptions(Iterable<Option> options) throws IOException {
        printOptions(tableDefBuilder.apply(options));
    }
    /**
     * Prints the help for the specified Options to the specified writer, using the specified width, left padding and
     * description padding.
     */
    public void printOptions(TableDef tableDef) throws IOException {
        serializer.writeTable(tableDef);
    }

    public String asArgName(String argName) {
        return optionFormatBuilder.asArgName(argName);
    }

}
