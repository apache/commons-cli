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

import org.apache.commons.cli2.Argument;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.builder.ArgumentBuilder;
import org.apache.commons.cli2.builder.CommandBuilder;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.Parser;
import junit.framework.TestCase;

public class Bug28005Test extends TestCase {
    public void testInfiniteLoop() {
        final DefaultOptionBuilder optionBuilder = new DefaultOptionBuilder();
        final ArgumentBuilder argumentBuilder = new ArgumentBuilder();
        final GroupBuilder groupBuilder = new GroupBuilder();
        final CommandBuilder commandBuilder = new CommandBuilder();

        final Option inputFormatOption =
            optionBuilder
                .withLongName("input-format")
                //.withArgument(argumentBuilder.create())
                .create();

        final Argument argument =
            argumentBuilder
                .withName("file")
                .create();

        final Group children =
            groupBuilder
                .withName("options")
                .withOption(inputFormatOption)
                .create();

        final Option command =
            commandBuilder
                .withName("convert")
                .withChildren(children)
                .withArgument(argument)
                .create();

        final Group root =
            groupBuilder
                .withName("commands")
                .withOption(command)
                .create();

        final Parser parser = new Parser();
        parser.setGroup(root);
        final String[] args = new String[]{"convert", "test.txt",
                "--input-format", "a"};

        try {
            parser.parse(args);
            fail("a isn't valid!!");
        } catch (OptionException e) {
            assertEquals("Unexpected a while processing commands",e.getMessage());
        }
    }
}