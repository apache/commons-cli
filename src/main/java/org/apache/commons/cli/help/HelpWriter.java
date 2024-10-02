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
 * The definition of a semantic scribe.  The semantic scribe write string to output based on the semantic meaning
 * of the type of string.  e.g. a Paragraph -vs- a Heading.
 * <p>
 *     the representation of the semantics is dependant upon the format being output.  For example the plain text
 *     output for a paragraph may print the text followed by two line breaks, while an XHTML output would print the
 *     text surrounded by &lt;p&gt; and &lt;/p&gt;
 * </p>
 */
public interface HelpWriter {

    /**
     * A method that writes a string directly to the output without formatting for any semantic meaning.
     * @param text the string to write.
     * @throws IOException on write failure
     */
    void writeDirect(String text) throws IOException;

    /**
     * A method that writes a char directly to the output without formatting for any semantic meaning.
     * @param ch the char to write.
     * @throws IOException on write failure.
     */
    void writeDirect(char ch) throws IOException;

    /**
     * Write a title.
     *
     * @param title the title to write.
     * @throws IOException on write failure
     */
    void writeTitle(String title) throws IOException;

    /**
     * Write a paragraph.
     *
     * @param paragraph the paragraph to write.
     * @throws IOException on write failure
     */
    void writePara(String paragraph) throws IOException;

    /**
     * Write a header.
     *
     * @param level  the level of the header
     * @param text   the text for the header
     * @throws IOException on write failure
     */
    void writeHeader(int level, String text) throws IOException;

    /**
     * Write a list.
     *
     * @param ordered {@code true} if the list should be ordered.
     * @param list   the list to write.
     * @throws IOException on write failure
     */
    void writeList(boolean ordered, Collection<String> list) throws IOException;

    /**
     * Write a table.
     *
     * @param table   the table definition to write.
     * @throws IOException on write failure
     */
    void writeTable(TableDef table) throws IOException;
}
