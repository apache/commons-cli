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

import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.builder.DefaultOptionBuilder;
import org.apache.commons.cli2.builder.GroupBuilder;
import org.apache.commons.cli2.commandline.Parser;

import junit.framework.TestCase;

/**
 * @author roxspring
 */
public class Bug32533Test extends TestCase {

    public void testBlah() throws OptionException {

        Option a1 = new DefaultOptionBuilder().withLongName("a1").create();
        Option b1 = new DefaultOptionBuilder().withLongName("b1").create();
        Option c1 = new DefaultOptionBuilder().withLongName("c1").create();

        Group b = new GroupBuilder().withOption(b1).create();
        Group c = new GroupBuilder().withOption(c1).create();
        Group a = new GroupBuilder().withOption(a1).withOption(b).withOption(c).create();

        Parser parser = new Parser();
        parser.setGroup(a);
        parser.parse(new String[]{"--a1","--b1"});
    }

}
