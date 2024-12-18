/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.cli.processor;


import org.apache.commons.cli.Option;

public class ArgumentProcessor {

    /**
     * Processes the value. If this Option has a value separator the value will have to be parsed into individual tokens.
     * When n-1 tokens have been processed and there are more value separators in the value, parsing is ceased and the remaining characters are added as a single token.
     *
     * @param value The String to be processed.
     * @param option The Option object (to access separators).
     */
    public static void processValue(Option option, String value) {
        if (option.getArgs() == Option.UNINITIALIZED) {
            throw new IllegalArgumentException("NO_ARGS_ALLOWED");
        }
        String add = value;
        if (option.hasValueSeparator()) {
            final char sep = option.getValueSeparator();
            int index = add.indexOf(sep);
            while (index != -1) {
                if (option.getValuesList().size() == option.getArgs() - 1) {
                    break;
                }
                option.add(add.substring(0, index));
                add = add.substring(index + 1);
                index = add.indexOf(sep);
            }
        }
        option.add(add);
    }
}

