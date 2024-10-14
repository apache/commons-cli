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

import java.util.List;

/**
 * The definition of a table to display.
 * <p>
 * Aa table definition contains a caption and data that describes each column. Every column in the table may have
 * </p>
 * <ul>
 * <li>A caption.</li>
 * <li>A {@link TextStyle} that describes the width of the entry, its offset from the previous column (leftPad) and how much each line after the first should be
 * indented (indent).</li>
 * <li>A heading (String) is placed at the top of the column</li>
 * <li>A collection of rows</li>
 * </ul>
 *
 * @since 1.10.0
 */
public interface TableDefinition {

    /**
     * A helper function to create a table instance from the various components.
     *
     * @param caption     The caption, May be {@code null}
     * @param columnStyle a list of TextStyle elements defining the columns.
     * @param headers     the list of column headers.
     * @param rows        a collection of Rows.
     * @return A TableDefinition returning the parameters as appropriate.
     */
    static TableDefinition from(final String caption, final List<TextStyle> columnStyle, final List<String> headers, final Iterable<List<String>> rows) {
        return new TableDefinition() {

            @Override
            public String caption() {
                return caption;
            }

            @Override
            public List<TextStyle> columnStyle() {
                return columnStyle;
            }

            @Override
            public List<String> headers() {
                return headers;
            }

            @Override
            public Iterable<List<String>> rows() {
                return rows;
            }
        };
    }

    /**
     * Gets the caption for the table. May be @{code null}.
     *
     * @return The caption for the table. May be @{code null}.
     */
    String caption();

    /**
     * Gets the list TextStyles. One for each column in order.
     *
     * @return the list of TextStyles.
     */
    List<TextStyle> columnStyle();

    /**
     * Gets the list of header strings. One for each column in order.
     *
     * @return The list of header strings.
     */
    List<String> headers();

    /**
     * Gets the collection of rows.
     * <p>
     * Each row is a list of Strings, one for each column in the table.
     * </p>
     *
     * @return The collection of rows.
     */
    Iterable<List<String>> rows();
}
