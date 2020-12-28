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
 * Class for test classes to utilise; they add this listener before they make
 * any calls to
 * {@link CommandLineConfig#process(java.io.InputStream, java.lang.String[])}
 * and interrogate this listener using {@link AbstractTestConfig#getOptions()}
 * after processing has occurred, to determine that the specified values were
 * registered during the parsing process.
 */
public class ConfigListener extends AbstractTestConfig
        implements OptionListener
{

    /**
     * Callers implementing this would normally set members or take actions when
     * receiving updates.
     *
     * @param option non-{@code null} command line option updated from the
     * result of the command line parsing process.
     * 
     * @param value value of the command line value, if the command line option
     * has an argument; {@code null} otherwise (and implies the command line
     * option does not have an argument).
     */
    @Override
    public void option(String option, Object value)
    {
        String optValue = null;
        if (value != null)
        {
            optValue = value.toString();
            /*
             e.g. 
             if ("host".equals(option)) {
             server.setHostname(optValue);
             }
             */
        }
        /*
         Else if value is false, implies option doesn't have an argument, e.g
        
         if ("fail".equals(option)) {
         server.setFail(true);
         }
         */
        addOption(option, optValue);
    }

}
