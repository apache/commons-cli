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

package org.apache.commons.cli.bug;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Test for CLI-260.
 * <p>
 * The issue is that if there is an = in the argument with no space after the option
 * the DefaultParser sends the string as a token and breaks the option at the = and is then
 * not recognized (example: -k'foo=value' option=-k'foo  arg=value instead of option=-k arg=foo=value).
 */
public class BugCLI260Test {

  private DefaultParser parser;
  private Options options;

  @Before
  public void setUp()
  {
    parser = new DefaultParser();
    options = new Options()
      .addOption("a", "enable-a", false, "turn [a] on or off")
      .addOption("b", "bfile", true, "set the value of [b]")
      .addOption("c", "copt", false, "turn [c] on or off");
  }

  @Test
  public void testEqualInArgShort() throws Exception
  {
    String[] args = new String[] { "-b", "foo=bar"};

    CommandLine cl = parser.parse(options, args);

    assertTrue("Confirm -b is set", cl.hasOption("b"));
    assertTrue("Confirm arg of -b", cl.getOptionValue("b").equals("foo=bar"));
  }

  @Test
  public void testEqualinArgLong() throws Exception
  {
    String[] args = new String[] {"--bfile", "toast=ie"};

    CommandLine cl = parser.parse(options, args);

    assertTrue( "Confirm -b is set", cl.hasOption("b") );
    assertTrue( "Confirm arg of -b", cl.getOptionValue("b").equals("toast=ie") );
    assertTrue( "Confirm arg of --bfile", cl.getOptionValue( "bfile" ).equals( "toast=ie" ) );
  }

}
