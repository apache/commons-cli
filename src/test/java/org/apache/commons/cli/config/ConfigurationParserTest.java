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

import java.io.InputStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.apache.commons.cli.config.GlobalConfiguration.OPTION_TYPE;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 */
public class ConfigurationParserTest
{

    public ConfigurationParserTest()
    {
    }

    @BeforeClass
    public static void setUpClass()
    {
    }

    @AfterClass
    public static void tearDownClass()
    {
    }

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }

    /**
     * Test of parse method, of class ConfigurationParser.
     */
    @Test
    public void testParseOpts() throws Exception
    {
        String text = "option.fail.opts=F/fail";
        Pattern p = Pattern.compile(ConfigurationParser.OPTION_REGEX_BASIC_LINE);
        Matcher m = p.matcher(text);
        assertTrue(m.matches());
        assertEquals("fail", m.group(1));
        assertEquals("opts", m.group(2));
        assertEquals("F/fail", m.group(3));
    }

    /**
     * Test of parse method, of class ConfigurationParser.
     */
    @Test
    public void testParseHasArg() throws Exception
    {
        String text = "option.fail.hasArg=true";
        Pattern p = Pattern.compile(ConfigurationParser.OPTION_REGEX_BASIC_LINE);
        Matcher m = p.matcher(text);
        assertTrue(m.matches());
        assertEquals("fail", m.group(1));
        assertEquals("hasArg", m.group(2));
        assertEquals("true", m.group(3));
    }

    /**
     * Test of parse method, of class ConfigurationParser.
     */
    @Test
    public void testParseDescription() throws Exception
    {
        String text = "option.fail.description=fail gracefully.";
        Pattern p = Pattern.compile(ConfigurationParser.OPTION_REGEX_BASIC_LINE);
        Matcher m = p.matcher(text);
        assertTrue(m.matches());
        assertEquals("fail", m.group(1));
        assertEquals("description", m.group(2));
        assertEquals("fail gracefully.", m.group(3));
    }

    /**
     * Test of parse method, of class ConfigurationParser.
     */
    @Test
    public void testParseDescriptionEscaped() throws Exception
    {
        String text = "option.fail.description=fail gracefully, \\";
        Pattern p = Pattern.compile(ConfigurationParser.OPTION_REGEX_BASIC_LINE);
        Matcher m = p.matcher(text);
        assertTrue(m.matches());
        assertEquals("fail", m.group(1));
        assertEquals("description", m.group(2));
        assertEquals("fail gracefully, \\", m.group(3));
    }

    /**
     * Test of parse method, of class ConfigurationParser.
     */
    @Test
    public void testParseInputStream() throws Exception
    {
        ConfigurationParser configParser = new ConfigurationParser();
        InputStream is = ConfigurationParserTest.class.getResourceAsStream(
                "/config/config_001_short_and_long_options.conf");
        GlobalConfiguration globalConfig = configParser.parse(is, "UTF-8");
        Map<String, OptionConfiguration> optionConfig = globalConfig.getOptionConfigurations();
        is.close();
        OptionConfiguration optConfig = optionConfig.get("fail");
        checkOptionConfiguration(optConfig, "fail", "F", "fail",
                "Fail if no connection made, rather than retrying.", false);
        optConfig = optionConfig.get("host");
        checkOptionConfiguration(optConfig, "host", "h", "host",
                "Specify the host; optional. Use localhost if not set."
                + " Protocol is optional, assumes HTTP.", true);
        optConfig = optionConfig.get("port");
        checkOptionConfiguration(optConfig, "port", "p", "port",
                "Port number to use. Required.", true);
        optConfig = optionConfig.get("path");
        checkOptionConfiguration(optConfig, "path", "P", "path",
                "Comma separated list of paths to test on the server."
                + " If spaces are contained within the arguments, surround with"
                + " double quotes. Have as many lines as required, so long as"
                + " they are escaped.", true);
    }

    /**
     * Test of parse method, of class ConfigurationParser.
     */
    @Test
    public void testParseInputStreamShortOption() throws Exception
    {
        ConfigurationParser configParser = new ConfigurationParser();
        InputStream is = ConfigurationParserTest.class.getResourceAsStream(
                "/config/config_002_short_options.conf");
        GlobalConfiguration globalConfig = configParser.parse(is, "UTF-8");
        Map<String, OptionConfiguration> optionConfig = globalConfig.getOptionConfigurations();
        is.close();
        OptionConfiguration optConfig = optionConfig.get("fail");
        checkOptionConfiguration(optConfig, "fail", "F", null,
                "Fail if no connection made, rather than retrying.", false);
        optConfig = optionConfig.get("host");
        checkOptionConfiguration(optConfig, "host", "h", null,
                "Specify the host; optional. Use localhost if not set."
                + " Protocol is optional, assumes HTTP.", true);
        optConfig = optionConfig.get("port");
        checkOptionConfiguration(optConfig, "port", "p", null,
                "Port number to use. Required.", true);
        optConfig = optionConfig.get("path");
        checkOptionConfiguration(optConfig, "path", "P", null,
                "Comma separated list of paths to test on the server."
                + " If spaces are contained within the arguments, surround with"
                + " double quotes. Have as many lines as required, so long as"
                + " they are escaped.", true);
    }

    /**
     * Test of parse method, of class ConfigurationParser.
     */
    @Test
    public void testParseInputStreamLongOption() throws Exception
    {
        ConfigurationParser configParser = new ConfigurationParser();
        InputStream is = ConfigurationParserTest.class.getResourceAsStream(
                "/config/config_003_long_options.conf");
        GlobalConfiguration globalConfig = configParser.parse(is, "UTF-8");
        Map<String, OptionConfiguration> optionConfig = globalConfig.getOptionConfigurations();
        is.close();
        OptionConfiguration optConfig = optionConfig.get("fail");
        checkOptionConfiguration(optConfig, "fail", null, "fail",
                "Fail if no connection made, rather than retrying.", false);
        optConfig = optionConfig.get("host");
        checkOptionConfiguration(optConfig, "host", null, "host",
                "Specify the host; optional. Use localhost if not set."
                + " Protocol is optional, assumes HTTP.", true);
        optConfig = optionConfig.get("port");
        checkOptionConfiguration(optConfig, "port", null, "port",
                "Port number to use. Required.", true);
        optConfig = optionConfig.get("path");
        checkOptionConfiguration(optConfig, "path", null, "path",
                "Comma separated list of paths to test on the server."
                + " If spaces are contained within the arguments, surround with"
                + " double quotes. Have as many lines as required, so long as"
                + " they are escaped.", true);
    }

    /**
     * Test that an invalid mix of characters when not using option type BOTH
     * (so having a separate long option and short option) throws an exception.
     */
    @Test
    public void testParseInputStreamBadOptionMix() throws Exception
    {
        ConfigurationParser configParser = new ConfigurationParser();
        InputStream is = ConfigurationParserTest.class.getResourceAsStream(
                "/config/config_004_bad_short_long_mix.conf");
        try
        {
            configParser.parse(is, "UTF-8");
            fail("Expected exception.");
        }
        catch (ConfigurationException ex)
        {
            assertTrue(ex.getMessage().contains("Configuration type specifies"
                    + " OPTION_TYPE as LONG but found SHORT"));
        }
    }

    /**
     * Test that empty opts value throws an exception.
     */
    @Test
    public void testParseInputStreamZeroLengthOption() throws Exception
    {
        ConfigurationParser configParser = new ConfigurationParser();
        InputStream is = ConfigurationParserTest.class.getResourceAsStream(
                "/config/config_005_empty_opts_value.conf");
        try
        {
            configParser.parse(is, "UTF-8");
            fail("Expected exception.");
        }
        catch (ConfigurationException ex)
        {
            assertTrue(ex.getMessage().contains("Empty option value; must be a"
                    + " non-zero length string"));
        }
    }

    /**
     * Test that having no options at all throws an exception.
     */
    @Test
    public void testParseInputStreamNoOptionsAtAll() throws Exception
    {
        ConfigurationParser configParser = new ConfigurationParser();
        InputStream is = ConfigurationParserTest.class.getResourceAsStream(
                "/config/config_006_no_options.conf");
        try
        {
            configParser.parse(is, "UTF-8");
            fail("Expected exception.");
        }
        catch (ConfigurationException ex)
        {
            assertTrue(ex.getMessage().contains("The configuration file"
                    + " contained no options to parse"));
        }
    }

    /**
     * Test that an unknown sub-option throws an exception.
     */
    @Test
    public void testParseInputStreamBadSubOptionName() throws Exception
    {
        ConfigurationParser configParser = new ConfigurationParser();
        InputStream is = ConfigurationParserTest.class.getResourceAsStream(
                "/config/config_007_unknown_config_option.conf");
        try
        {
            configParser.parse(is, "UTF-8");
            fail("Expected exception.");
        }
        catch (ConfigurationException ex)
        {
            assertTrue(ex.getMessage().contains(
                    "Unknown configuration option: "));
        }
    }

    /**
     * Test that having no white space at the start of a succeeding line
     * following in from an escaped line throws an exception.
     */
    @Test
    public void testParseInputStreamBadEscapedLine() throws Exception
    {
        ConfigurationParser configParser = new ConfigurationParser();
        InputStream is = ConfigurationParserTest.class.getResourceAsStream(
                "/config/config_008_invalid_escaped_line.conf");
        try
        {
            configParser.parse(is, "UTF-8");
            fail("Expected exception.");
        }
        catch (ConfigurationException ex)
        {
            assertTrue(ex.getMessage().contains("Invalid escaped line: "));
        }
    }

    /**
     * Test that using type BOTH when not formatted correctly throws an
     * exception.
     */
    @Test
    public void testParseInputStreamBadLongShortOptionFormat() throws Exception
    {
        ConfigurationParser configParser = new ConfigurationParser();
        InputStream is = ConfigurationParserTest.class.getResourceAsStream(
                "/config/config_009_invalid_short_long_format.conf");
        try
        {
            configParser.parse(is, "UTF-8");
            fail("Expected exception.");
        }
        catch (ConfigurationException ex)
        {
            assertTrue(ex.getMessage().contains("Invalid short and"
                    + " long option format; must be [character]/"
                    + " [text] but found "));
        }
    }

    /**
     * Test that an invalid character for opts throws an exception for SHORT
     * option.
     */
    @Test
    public void testParseInputStreamBadShortOptionFormat() throws Exception
    {
        ConfigurationParser configParser = new ConfigurationParser();
        InputStream is = ConfigurationParserTest.class.getResourceAsStream(
                "/config/config_010_invalid_short_format.conf");
        try
        {
            configParser.parse(is, "UTF-8");
            fail("Expected exception.");
        }
        catch (ConfigurationException ex)
        {
            assertTrue(ex.getMessage().contains("Expected single"
                    + " character for short option but found "));
        }
    }

    /**
     * Test that invalid characters for LONG option fails.
     */
    @Test
    public void testParseInputStreamBadLongOptionFormat() throws Exception
    {
        ConfigurationParser configParser = new ConfigurationParser();
        InputStream is = ConfigurationParserTest.class.getResourceAsStream(
                "/config/config_011_invalid_long_option_format.conf");
        try
        {
            configParser.parse(is, "UTF-8");
            fail("Expected exception.");
        }
        catch (ConfigurationException ex)
        {
            assertTrue(ex.getMessage().contains("Expected text"
                    + " for long option but found "));
        }
    }

    /**
     * Test that completely invalid option throws an exception.
     */
    @Test
    public void testParseInputStreamBadLineOptionFormat() throws Exception
    {
        ConfigurationParser configParser = new ConfigurationParser();
        InputStream is = ConfigurationParserTest.class.getResourceAsStream(
                "/config/config_012_invalid_option_definition.conf");
        try
        {
            configParser.parse(is, "UTF-8");
            fail("Expected exception.");
        }
        catch (ConfigurationException ex)
        {
            assertTrue(ex.getMessage().contains("Invalid option definition: "));
        }
    }

    /**
     * Test that when the user defines a correct option, if a succeeding option
     * contains an empty name, the error message will also inform them of what
     * option type they're using (short, long, both).
     */
    @Test
    public void testParseInputStreamZeroLength2ndOption() throws Exception
    {
        ConfigurationParser configParser = new ConfigurationParser();
        InputStream is = ConfigurationParserTest.class.getResourceAsStream(
                "/config/config_013_empty_option_value.conf");
        try
        {
            configParser.parse(is, "UTF-8");
            fail("Expected exception.");
        }
        catch (ConfigurationException ex)
        {
            assertTrue(ex.getMessage().contains("Empty option value; must be a"
                    + " non-zero length string; global configuration is defined as "));
        }
    }

    /**
     * Test that a repeated global option declaration throws the appropriate
     * exception.
     */
    @Test
    public void testParseInputStreamRepeatedGlobalConfig() throws Exception
    {
        ConfigurationParser configParser = new ConfigurationParser();
        InputStream is = ConfigurationParserTest.class.getResourceAsStream(
                "/config/config_014_option_type_defined_twice.conf");
        try
        {
            configParser.parse(is, "UTF-8");
            fail("Expected exception.");
        }
        catch (ConfigurationException ex)
        {
            assertTrue(ex.getMessage().contains(OPTION_TYPE
                    + " has already been defined as "
                    + OptionsTypeEnum.BOTH.getType()
                    + " but found second definition: "
                    + OptionsTypeEnum.BOTH.getType()));
        }
    }

    /**
     * Test that a global option defined after common "option." options throws
     * an exception.
     */
    @Test
    public void testParseInputStreamBadGlobalConfiguration() throws Exception
    {
        ConfigurationParser configParser = new ConfigurationParser();
        InputStream is = ConfigurationParserTest.class.getResourceAsStream(
                "/config/config_020_bad_global_config_order.csv");
        try
        {
            configParser.parse(is, "UTF-8");
            fail("Expected exception.");
        }
        catch (ConfigurationException ex)
        {
            assertTrue(ex.getMessage().contains("Invalid global"
                    + " configuration definition; global configurations"
                    + " must come BEFORE standard \"option...\" definitions"));
        }
    }

    /**
     * Test of addOptionListener method, of class ConfigurationParser.
     */
    @Test
    public void testAddOptionListener()
    {

    }

    /**
     * Test of removeOptionListener method, of class ConfigurationParser.
     */
    @Test
    public void testRemoveOptionListener()
    {

    }

    /**
     * Check that the specified option configuration values match the other
     * arguments passed in.
     *
     * @param optConfig non-{@code null} option configuration.
     *
     * @param optionName non-{@code null} name of the option to match.
     *
     * @param shortOption short option name to match; if {@code null}, implies
     * using long options.
     *
     * @param longOption long option name to match; if {@code null}, implies
     * using short options.
     *
     * @param descrption non-{@code null} description to match.
     *
     * @param hasArg match if the option has an argument or not.
     */
    private void checkOptionConfiguration(OptionConfiguration optConfig,
            String optionName, String shortOption, String longOption,
            String descrption, boolean hasArg)
    {
        assertNotNull(optConfig);
        assertEquals(shortOption, optConfig.getShortOption());
        assertEquals(longOption, optConfig.getLongOption());
        assertEquals(optionName, optConfig.getName());
        assertEquals(descrption, optConfig.getDescription());
        assertEquals(hasArg, optConfig.hasArg());
    }

}
