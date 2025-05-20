/*
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      https://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

/**
 * The help production system.
 * <p>
 * This package contains the classes to produce help output. In general,, there are 4 classes that may interest users.
 * </p>
 * <ul>
 * <li>{@link org.apache.commons.cli.help.HelpFormatter HelpFormatter} - The class used to produce the help output for most users.</li>
 * <li>{@link org.apache.commons.cli.help.HelpAppendable HelpAppendable} - Writes the output in a specific output format. For example
 * {@link org.apache.commons.cli.help.TextHelpAppendable TextHelpAppendable} for text, other classes for XHTML, Markdown, and so on.</li>
 * <li>{@link org.apache.commons.cli.help.OptionFormatter OptionFormatter} - Determines how to format the various data elements in an Option</li>
 * <li>{@link org.apache.commons.cli.help.TableDefinition TableDefinition} - Useful for developers who want to build custom option displays or use the help
 * system to produce additional information in the help system.</li>
 * </ul>
 *
 * @since 1.10.0
 */
package org.apache.commons.cli.help;
