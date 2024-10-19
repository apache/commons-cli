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

import java.io.IOException;
import java.util.Collection;
import java.util.Formatter;
import java.util.IllegalFormatException;

/**
 * Defines a semantic scribe. The semantic scribe appends text to an output based on the semantic meaning of the type of string. For example, a Paragraph versus
 * a Heading.
 * <p>
 * The representation of the semantics is dependent upon the format being output. For example, the plain text output for a paragraph may print the text followed
 * by two line breaks, while an XHTML output would print the text surrounded by &lt;p&gt; and &lt;/p&gt;.
 * </p>
 * <p>
 * Note the {@link Appendable} documentation on the topics of Unicode and threading, these comments also apply here.
 * </p>
 *
 * @since 1.10.0
 */
public interface HelpAppendable extends Appendable {

    /**
     * Appends a formatted string using the specified format string and arguments.
     * <p>
     * Short-hand for calling:
     * </p>
     *
     * <pre>
     * helpAppendable.{@link #append(CharSequence) append.}({@link String#format(String, Object...) String.format}(format, args));
     * </pre>
     *
     * @param format The format string for {@link String#format(String, Object...)}.
     * @param args   Arguments to {@link String#format(String, Object...)}.
     * @throws IllegalFormatException See {@link String#format(String, Object...)}.
     * @throws IOException            If an output error occurs.
     * @see String#format(String, Object...)
     * @see Formatter
     * @see #append(CharSequence)
     */
    default void appendFormat(final String format, final Object... args) throws IOException {
        append(String.format(format, args));
    }

    /**
     * Appends a header.
     *
     * @param level the level of the header. This is equivalent to the "1", "2", or "3" in the HTML "h1", "h2", "h3" tags.
     * @param text  the text for the header, null is a noop.
     * @throws IOException If an output error occurs.
     */
    void appendHeader(int level, CharSequence text) throws IOException;

    /**
     * Appends a list.
     *
     * @param ordered {@code true} if the list should be ordered.
     * @param list    the list to write, null is a noop.
     * @throws IOException If an output error occurs.
     */
    void appendList(boolean ordered, Collection<CharSequence> list) throws IOException;

    /**
     * Appends a paragraph.
     *
     * @param paragraph the paragraph to write, null is a noop.
     * @throws IOException If an output error occurs.
     */
    void appendParagraph(CharSequence paragraph) throws IOException;

    /**
     * Appends a formatted string as a paragraph.
     *
     * <pre>
     * helpAppendable.{@link #appendParagraph(CharSequence) appendParagraph.}({@link String#format(String, Object...) String.format}(format, args));
     * </pre>
     *
     * @param format The format string for {@link String#format(String, Object...)}.
     * @param args   Arguments to {@link String#format(String, Object...)}.
     * @throws IllegalFormatException See {@link String#format(String, Object...)}.
     * @throws IOException            If an output error occurs.
     * @see String#format(String, Object...)
     * @see Formatter
     * @see #append(CharSequence)
     */
    default void appendParagraphFormat(final String format, final Object... args) throws IOException {
        appendParagraph(String.format(format, args));
    }

    /**
     * Appends a table.
     *
     * @param table the table definition to write, null is a noop.
     * @throws IOException If an output error occurs.
     */
    void appendTable(TableDefinition table) throws IOException;

    /**
     * Appends a title.
     *
     * @param title the title to write, null is a noop.
     * @throws IOException If an output error occurs.
     */
    void appendTitle(CharSequence title) throws IOException;

}
