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
 * Defines if the configuration takes a short, long or both options.
 */
public enum OptionsTypeEnum
{

    /**
     * Both options used - short and long.
     */
    BOTH("BOTH"),
    /**
     * Short options only.
     */
    SHORT("SHORT"),
    /**
     * Long options only.
     */
    LONG("LONG");

    /**
     * One of BOTH, SHORT or LONG.
     */
    private final String type;

    /**
     * Create a new options type.
     *
     * @param type the name of the options type.
     */
    private OptionsTypeEnum(final String type)
    {
        this.type = type;
    }

    /**
     * Get the type of the option.
     *
     * @return the option type.
     */
    public String getType()
    {
        return type;
    }
}
