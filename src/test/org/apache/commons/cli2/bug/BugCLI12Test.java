/**
 * Copyright 2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.cli2.bug;

import junit.framework.TestCase;

import org.apache.commons.cli2.Argument;
import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.Parser;
import org.apache.commons.cli2.option.PropertyOption;

/**
 * http://issues.apache.org/jira/browse/CLI-12
 */
public class BugCLI12Test extends TestCase {

  public void testBug() {
    Argument arg = new ArgumentBuilder().withName("file").create();

    Option option = new PropertyOption();

    Group group = new GroupBuilder().withOption(option).withOption(arg).create();

    Parser p = new Parser();
    p.setGroup(group);

    CommandLine cl = p.parseAndHelp( new String[] { "-Dmyprop1=myval1", "-Dmyprop2=myval2", "myfile" } );
    if(cl == null) {
      assertTrue("Couldn't parse valid commandLine", false);
    }

    assertEquals( "myval1", cl.getProperty("myprop1"));
    assertEquals( "myval2", cl.getProperty("myprop2"));

    String extraArgs = (String) cl.getValue(arg);
    assertEquals( "myfile", extraArgs);
  }

}
