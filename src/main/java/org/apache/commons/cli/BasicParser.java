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

package org.apache.commons.cli;

/**
 * The class BasicParser provides a very simple implementation of the {@link Parser#flatten(Options,String[],boolean)
 * flatten} method.
 *
 * @deprecated since 1.3, use the {@link DefaultParser} instead
 */
@Deprecated
public class BasicParser extends Parser {
    /**
     * <p>
     * A simple implementation of {@link Parser}'s abstract {@link Parser#flatten(Options, String[], boolean) flatten}
     * method.
     * </p>
     *
     * <p>
     * <b>Note:</b> {@code options} and {@code stopAtNonOption} are not used in this {@code flatten} method.
     * </p>
     *
     * @param options The command line {@link Options}
     * @param arguments The command line arguments to be parsed
     * @param stopAtNonOption Specifies whether to stop flattening when an non option is found.
     * @return The {@code arguments} String array.
     */
    @Override
    protected String[] flatten(@SuppressWarnings("unused") final Options options, final String[] arguments,
        @SuppressWarnings("unused") final boolean stopAtNonOption) {
        // just echo the arguments
        return arguments;
    }
}
