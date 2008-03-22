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
package org.apache.commons.cli2.application;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.Parser;
import org.apache.commons.cli2.option.PropertyOption;

//TODO Build up AntTest like CpTest
public class AntTest extends TestCase {
    public void testAnt() throws OptionException {
        final DefaultOptionBuilder obuilder = new DefaultOptionBuilder();
        final ArgumentBuilder abuilder = new ArgumentBuilder();
        final GroupBuilder gbuilder = new GroupBuilder();

        final Group options =
            gbuilder
                .withName("ant")
                .withOption(
                    obuilder
                        .withShortName("help")
                        .withDescription("print this message")
                        .create())
                .withOption(
                    obuilder
                        .withShortName("projecthelp")
                        .withDescription("print project help information")
                        .create())
                .withOption(
                    obuilder
                        .withShortName("version")
                        .withDescription("print the version information and exit")
                        .create())
                .withOption(
                    obuilder
                        .withShortName("diagnostics")
                        .withDescription("print information that might be helpful to diagnose or report problems.")
                        .create())
                .withOption(
                    obuilder
                        .withShortName("quiet")
                        .withShortName("q")
                        .withDescription("be extra quiet")
                        .create())
                .withOption(
                    obuilder
                        .withShortName("verbose")
                        .withShortName("v")
                        .withDescription("be extra verbose")
                        .create())
                .withOption(
                    obuilder
                        .withShortName("debug")
                        .withDescription("print debugging information")
                        .create())
                .withOption(
                    obuilder
                        .withShortName("emacs")
                        .withDescription("produce logging information without adornments")
                        .create())
                .withOption(
                    obuilder
                        .withShortName("logfile")
                        .withShortName("l")
                        .withDescription("use given file for log")
                        .withArgument(
                            abuilder
                                .withName("file")
                                .withMinimum(1)
                                .withMaximum(1)
                                .create())
                        .create())
                .withOption(
                    obuilder
                        .withShortName("logger")
                        .withDescription("the class which is to perform logging")
                        .withArgument(
                            abuilder
                                .withName("classname")
                                .withMinimum(1)
                                .withMaximum(1)
                                .create())
                        .create())
                .withOption(
                    obuilder
                        .withShortName("listener")
                        .withDescription("add an instance of class as a project listener")
                        .withArgument(
                            abuilder
                                .withName("classname")
                                .withMinimum(1)
                                .withMaximum(1)
                                .create())
                        .create())
                .withOption(
                    obuilder
                        .withShortName("buildfile")
                        .withShortName("file")
                        .withShortName("f")
                        .withDescription("use given buildfile")
                        .withArgument(
                            abuilder
                                .withName("file")
                                .withMinimum(1)
                                .withMaximum(1)
                                .create())
                        .create())
                .withOption(PropertyOption.INSTANCE)
                .withOption(
                    obuilder
                        .withShortName("propertyfile")
                        .withDescription("load all properties from file with -D properties taking precedence")
                        .withArgument(
                            abuilder
                                .withName("name")
                                .withMinimum(1)
                                .withMaximum(1)
                                .create())
                        .create())
                .withOption(
                    obuilder
                        .withShortName("inputhandler")
                        .withDescription("the class which will handle input requests")
                        .withArgument(
                            abuilder
                                .withName("class")
                                .withMinimum(1)
                                .withMaximum(1)
                                .create())
                        .create())
                .withOption(
                    obuilder
                        .withShortName("find")
                        .withDescription("search for buildfile towards the root of the filesystem and use it")
                        .withArgument(
                            abuilder
                                .withName("file")
                                .withMinimum(1)
                                .withMaximum(1)
                                .create())
                        .create())
                .withOption(abuilder.withName("target").create())
                .create();

        Parser parser = new Parser();
        parser.setGroup(options);
        CommandLine line =
            parser.parse(
                new String[] {
                    "-buildfile",
                    "mybuild.xml",
                    "-Dproperty=value",
                    "-Dproperty1=value1",
                    "-projecthelp",
                    "compile",
                    "docs" });

        // check properties
        assertEquals(2, line.getProperties().size());
        assertEquals("value", line.getProperty("property"));
        assertEquals("value1", line.getProperty("property1"));

        // check single values
        assertEquals("mybuild.xml", line.getValue("-buildfile"));
        assertTrue(line.hasOption("-projecthelp"));
        assertFalse(line.hasOption("-help"));

        assertTrue(line.hasOption("target"));
        final List targets = new ArrayList();
        targets.add("compile");
        targets.add("docs");
        assertEquals(targets, line.getValues("target"));
    }
}
