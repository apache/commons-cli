/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
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

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.util.HelpFormatter;

import junit.framework.TestCase;

/**
 * http://issues.apache.org/jira/browse/CLI-18
 */
public class BugCLI18Test extends TestCase {

  public BugCLI18Test() {
    super();
  }


  public void testBug() {
    Option a = new DefaultOptionBuilder().withLongName("aaa").withShortName("a").withDescription("aaaaaaa").create();
    Option b = new DefaultOptionBuilder().withLongName("bbb").withDescription("bbbbbbbb dksh fkshd fkhs dkfhsdk fhskd hksdks dhfowehfsdhfkjshf skfhkshf sf jkshfk sfh skfh skf f").create();
    Option c = new DefaultOptionBuilder().withLongName("ccc").withShortName("c").withDescription("ccccccc").create();

    Group g = new GroupBuilder().withOption(a).withOption(b).withOption(c).create();

    HelpFormatter formatter = new HelpFormatter();
    StringWriter out = new StringWriter();

    formatter.setPrintWriter(new PrintWriter(out));
    formatter.setHeader("dsfkfsh kdh hsd hsdh fkshdf ksdh fskdh fsdh fkshfk sfdkjhskjh fkjh fkjsh khsdkj hfskdhf skjdfh ksf khf s");
    formatter.setFooter("blort j jgj j jg jhghjghjgjhgjhg jgjhgj jhg jhg hjg jgjhghjg jhg hjg jhgjg jgjhghjg jg jgjhgjgjg jhg jhgjh" + '\r' + '\n' + "rarrr");
    formatter.setGroup(g);
    formatter.setShellCommand("foobar");

    formatter.print();

  }
}

