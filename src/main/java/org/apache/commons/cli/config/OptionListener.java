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
 * Listener for receiving updates from the process of parsing a option
 * configuration.
 *
 * <p>
 * An example option configuration is defined in {@link OptionConfiguration} and
 * how this is processed is explained in {@link CommandLineConfiguration}. Once
 * the call to
 * {@link CommandLineConfiguration#process(java.io.InputStream, java.lang.String[])}
 * has been invoked, all registered listeners will have received all necessary
 * updates from the command line. All that is left to do now is do something
 * useful with the supplied arguments.
 *
 * <p>
 * In the following example, updating of the options and then doing something
 * with them is all dealt with in the {@link OptionListener}; however in
 * principle this can easily be split into different classes such that the
 * {@link OptionListener} contains the values updated via the command line which
 * a separate class then utilises to do something useful with.
 *
 * <p>
 * Continuing from the configuration defined in {@link OptionConfiguration} and
 * the call to process the file defined in {@link CommandLineConfiguration},
 * let's take a look at an example implementation of {@code MyAppListener}
 * within the {@code option(String, Object)} method - members are {@code public}
 * so that once processing of arguments is complete, external classes can obtain
 * the values read from the command line:
 * <pre>
 * public File outFile;
 *
 * public String text;
 *
 * public boolean overwrite;
 *
 * &#064;Override
 * public void option(final String option, final Object value)
 * {
 *     if ("file".equals(option))
 *     {
 *         outFile = new File(value.toString());
 *     }
 *     else if ("text".equals(option))
 *     {
 *         text = value.toString();
 *     }
 *     else if ("overwrite".equals(option))
 *     {
 *         overwrite = true;
 *     }
 *  }
 * </pre>
 *
 * <p>
 * Note that since the example option configuration defines both short and long
 * options, the listener will still receive an update in the above code for the
 * {@code "file".equals(option)} test even if the user specified {@code -f} via
 * the command line. Therefore it is up to listeners of configurations of both
 * short and long options to decide which to cater for; in general long options
 * aid readability in source files compared to single characters.
 */
public interface OptionListener
{

    /**
     * Update with the specified option and it's value (if it has one). Note
     * that the option will be a short option if only short options are defined,
     * a long option if only long options are defined, or both if both short and
     * long options are defined. In the latter case listeners will receive two
     * updates and can decide which form to cater for; in general long options
     * aid readability in source files compared to single characters.
     *
     * @param option non-{@code null} option, either in short form or long form.
     *
     * @param value the value of the argument; for options that do not have an
     * argument, the value will be {@code null}.
     */
    void option(final String option, final Object value);
}
