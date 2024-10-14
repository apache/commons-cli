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

/**
 * The definition of a semantic scribe. The semantic scribe write string to output based on the semantic meaning of the type of string. e.g. a Paragraph versus
 * a Heading.
 * <p>
 * The representation of the semantics is dependant upon the format being output. For example the plain text output for a paragraph may print the text followed
 * by two line breaks, while an XHTML output would print the text surrounded by &lt;p&gt; and &lt;/p&gt;.
 * </p>
 *
 * @since 1.10.0
 */
public interface HelpWriter extends Appendable {

    /**
     * Appends a header.
     *
     * @param level the level of the header. This is equivalent to the "1", "2", or "3" in the HTML "h1", "h2", "h3" tags.
     * @param text  the text for the header
     * @throws IOException on write failure
     */
    void appendHeader(int level, CharSequence text) throws IOException;

    /**
     * Appends a list.
     *
     * @param ordered {@code true} if the list should be ordered.
     * @param list    the list to write.
     * @throws IOException on write failure
     */
    void appendList(boolean ordered, Collection<CharSequence> list) throws IOException;

    /**
     * Appends a paragraph.
     *
     * @param paragraph the paragraph to write.
     * @throws IOException on write failure
     */
    void appendParagraph(CharSequence paragraph) throws IOException;

    /**
     * Appends a table.
     *
     * @param table the table definition to write.
     * @throws IOException on write failure
     */
    void appendTable(TableDefinition table) throws IOException;

    /**
     * Appends a title.
     *
     * @param title the title to write.
     * @throws IOException on write failure
     */
    void appendTitle(CharSequence title) throws IOException;
}
