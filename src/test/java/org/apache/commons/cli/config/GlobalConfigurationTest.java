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

import static org.apache.commons.cli.config.GlobalConfiguration.OPTION_TYPE;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 */
public class GlobalConfigurationTest
{

    /**
     * Test of updateGlobalConfiguration method, of class GlobalConfiguration,
     * for option type {@link GlobalConfiguration#GLOBAL_OPTION_TYPE_BOTH}.
     */
    @Test
    public void testUpdateGlobalConfigurationOptionTypeBoth() throws Exception
    {
        final String data = GlobalConfiguration.OPTION_TYPE + "="
                + OptionsTypeEnum.BOTH.getType();
        GlobalConfiguration globalConfig = new GlobalConfiguration();
        globalConfig.updateGlobalConfiguration(data);
        assertEquals(OptionsTypeEnum.BOTH, globalConfig.getOptionsType());
    }

    /**
     * Test of updateGlobalConfiguration method, of class GlobalConfiguration,
     * for option type {@link GlobalConfiguration#GLOBAL_OPTION_TYPE_SHORT}.
     */
    @Test
    public void testUpdateGlobalConfigurationOptionTypeShort() throws Exception
    {
        final String data = GlobalConfiguration.OPTION_TYPE + "="
                + OptionsTypeEnum.SHORT.getType();
        GlobalConfiguration globalConfig = new GlobalConfiguration();
        globalConfig.updateGlobalConfiguration(data);
        assertEquals(OptionsTypeEnum.SHORT, globalConfig.getOptionsType());
    }

    /**
     * Test of updateGlobalConfiguration method, of class GlobalConfiguration,
     * for option type {@link GlobalConfiguration#GLOBAL_OPTION_TYPE_LONG}.
     */
    @Test
    public void testUpdateGlobalConfigurationOptionTypeLong() throws Exception
    {
        final String data = GlobalConfiguration.OPTION_TYPE + "="
                + OptionsTypeEnum.LONG.getType();
        GlobalConfiguration globalConfig = new GlobalConfiguration();
        globalConfig.updateGlobalConfiguration(data);
        assertEquals(OptionsTypeEnum.LONG, globalConfig.getOptionsType());
    }

    /**
     * Test of updateGlobalConfiguration method, of class GlobalConfiguration,
     * for option type {@link GlobalConfiguration#GLOBAL_OPTION_TYPE_LONG}.
     */
    @Test
    public void testUpdateGlobalConfigurationCommandFooter() throws Exception
    {
        final String data = GlobalConfiguration.HELP_COMMAND_FOOTER + "="
                + "Some useful footer information";
        GlobalConfiguration globalConfig = new GlobalConfiguration();
        globalConfig.updateGlobalConfiguration(data);
        assertEquals("Some useful footer information", 
                globalConfig.getHelpCommandFooter());
    }

    /**
     * Test of updateGlobalConfiguration method, of class GlobalConfiguration,
     * for option type {@link GlobalConfiguration#GLOBAL_OPTION_TYPE_LONG}.
     */
    @Test
    public void testUpdateGlobalConfigurationCommandHeader() throws Exception
    {
        final String data = GlobalConfiguration.HELP_COMMAND_HEADER + "="
                + "Some useful header information";
        GlobalConfiguration globalConfig = new GlobalConfiguration();
        globalConfig.updateGlobalConfiguration(data);
        assertEquals("Some useful header information", 
                globalConfig.getHelpCommandHeader());
    }

    /**
     * Test of updateGlobalConfiguration method, of class GlobalConfiguration,
     * for option type {@link GlobalConfiguration#GLOBAL_OPTION_TYPE_LONG}.
     */
    @Test
    public void testUpdateGlobalConfigurationOptionTypeUnknown() throws Exception
    {
        // The Good, the Bad and the Ugly: Unknown grave, Bill Carson:
        final String data = GlobalConfiguration.OPTION_TYPE
                + "=Bill Carson";
        GlobalConfiguration globalConfig = new GlobalConfiguration();
        try
        {
            globalConfig.updateGlobalConfiguration(data);
            fail("Expected an exception");
        }
        catch(ConfigurationException ex)
        {
            assertEquals(ex.getMessage(), "Unknown options type: Bill Carson");
        }
    }

    /**
     * Test of updateGlobalConfiguration method, of class GlobalConfiguration,
     * for option type {@link GlobalConfiguration#GLOBAL_OPTION_TYPE_LONG}.
     */
    @Test
    public void testUpdateGlobalConfigurationUnknownOptionType() throws Exception
    {
        // The Good, the Bad and the Ugly: Unknown grave, Bill Carson:
        final String data = "BILL_CARSON=Bill Carson";
        GlobalConfiguration globalConfig = new GlobalConfiguration();
        try
        {
            globalConfig.updateGlobalConfiguration(data);
            fail("Expected an exception");
        }
        catch(ConfigurationException ex)
        {
            assertEquals(ex.getMessage(), 
                    "Unknown global configuration declaration: BILL_CARSON");
        }
    }

    /**
     * Test of updateGlobalConfiguration method, testing that when a global
     * option type is specified more than once, an error occurs.
     */
    @Test
    public void testUpdateGlobalConfigurationReDefinedOptionType() throws Exception
    {
        // The Good, the Bad and the Ugly: Unknown grave, Bill Carson:
        final String data = GlobalConfiguration.OPTION_TYPE + "="
                + OptionsTypeEnum.BOTH.getType();
        GlobalConfiguration globalConfig = new GlobalConfiguration();
        globalConfig.setOptionsType(OptionsTypeEnum.BOTH);
        try
        {
            globalConfig.updateGlobalConfiguration(data);
            fail("Expected an exception");
        }
        catch(ConfigurationException ex)
        {
            assertEquals(ex.getMessage(), OPTION_TYPE
                    + " has already been defined as "
                    + OptionsTypeEnum.BOTH.getType()
                    + " but found second definition: "
                    + OptionsTypeEnum.BOTH.getType());
        }
    }

    /**
     * Test of updateGlobalConfiguration method, testing that a badly named
     * help option throws an exception
     */
    @Test
    public void testUpdateGlobalConfigurationBadHelpOption() throws Exception
    {
        // The Good, the Bad and the Ugly: Unknown grave, Bill Carson:
        final String data = "HELP_FOO=exception!";
        GlobalConfiguration globalConfig = new GlobalConfiguration();
        try
        {
            globalConfig.updateGlobalConfiguration(data);
            fail("Expected an exception");
        }
        catch(ConfigurationException ex)
        {
            assertEquals(ex.getMessage(),
                    "Unknown help configuration: HELP_FOO=exception!");
        }
    }

    /**
     * Test of addOptionConfiguration method, of class GlobalConfiguration.
     */
    @Test
    public void testAddOptionConfiguration()
    {
    }

    /**
     * Test of getOptionConfigurations method, of class GlobalConfiguration.
     */
    @Test
    public void testGetOptionMap()
    {
    }

    /**
     * Test of getOptionsType method, of class GlobalConfiguration.
     */
    @Test
    public void testGetOptionsType()
    {
    }

    /**
     * Test of setOptionsType method, of class GlobalConfiguration.
     */
    @Test
    public void testSetOptionsType()
    {
    }
    
}
