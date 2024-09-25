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

import static java.lang.String.format;

/**
 * A class to write formatted text.
 */
public abstract class AbstractSerializer implements Serializer {
    protected final Appendable output;

    protected AbstractSerializer(final Appendable output) {
        this.output = output;
    }

    public void writeDirect(String text) throws IOException {
        output.append(text);
    }

    public void writeDirect(char ch) throws IOException {
        output.append(ch);
    }

    /**
     * Write a title.
     *
     * @param title  the title to write.
     * @throws IOException on error.
     */
    abstract public void writeTitle(final String title) throws IOException;

    /**
     * Write a paragraph.
     *
     * @param paragraph the paragraph to write.
     * @throws IOException on error.
     */
    abstract public void writePara(final String paragraph) throws IOException;

    /**
     * Write a header.
     *
     * @param level  the level of the header
     * @param text   the text for the header
     * @throws IOException on error.
     */
    abstract public void writeHeader(final int level, final String text) throws IOException;

    /**
     * Write a list .
     *
     * @param ordered {@code true} if the list should be ordered.
     * @param list   the list to write.
     * @throws IOException on error.
     */
    abstract public void writeList(boolean ordered, final Collection<String> list) throws IOException;

    /**
     * Write a table.
     *
     * @param table   the Table to write.  A collections of collections of Strings.
     * @throws IOException on error.
     */
    abstract public void writeTable(TableDef table) throws IOException;
}
