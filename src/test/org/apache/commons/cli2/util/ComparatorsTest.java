/**
 * Copyright 2003-2004 The Apache Software Foundation
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
package org.apache.commons.cli2.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.builder.SwitchBuilder;
import org.apache.commons.cli2.option.CommandTest;
import org.apache.commons.cli2.option.DefaultOptionTest;
import org.apache.commons.cli2.option.GroupTest;
import org.apache.commons.cli2.option.OptionTestCase;
import org.apache.commons.cli2.option.ParentTest;
import org.apache.commons.cli2.option.SwitchTest;

/**
 * @author Rob Oxspring
 */
public class ComparatorsTest extends TestCase {
    public void testGroupFirst() {
        final Option o1 = GroupTest.buildAntGroup();
        final Option o2 = ParentTest.buildLibParent();
        final List list = OptionTestCase.list(o1, o2);

        Collections.sort(list, Comparators.groupFirst());

        OptionTestCase.assertListContentsEqual(
            OptionTestCase.list(o1, o2),
            list);
    }

    public void testGroupLast() {
        final Option o1 = GroupTest.buildAntGroup();
        final Option o2 = ParentTest.buildLibParent();
        final List list = OptionTestCase.list(o1, o2);

        Collections.sort(list, Comparators.groupLast());

        OptionTestCase.assertListContentsEqual(
            OptionTestCase.list(o2, o1),
            list);
    }

    public void testSwitchFirst() {
        final Option o1 = SwitchTest.buildDisplaySwitch();
        final Option o2 = ParentTest.buildLibParent();
        final List list = OptionTestCase.list(o1, o2);

        Collections.sort(list, Comparators.switchFirst());

        OptionTestCase.assertListContentsEqual(
            OptionTestCase.list(o1, o2),
            list);
    }

    public void testSwitchLast() {
        final Option o1 = SwitchTest.buildDisplaySwitch();
        final Option o2 = ParentTest.buildLibParent();
        final Option o3 = new SwitchBuilder().withName("hidden").create();
        final List list = OptionTestCase.list(o1, o2);

        Collections.sort(list, Comparators.switchLast());

        OptionTestCase.assertListContentsEqual(
            OptionTestCase.list(o2, o1),
            list);
    }

    public void testCommandFirst() {
        final Option o1 = CommandTest.buildCommitCommand();
        final Option o2 = ParentTest.buildLibParent();
        final List list = OptionTestCase.list(o1, o2);

        Collections.sort(list, Comparators.commandFirst());

        OptionTestCase.assertListContentsEqual(
            OptionTestCase.list(o1, o2),
            list);
    }

    public void testCommandLast() {
        final Option o1 = CommandTest.buildCommitCommand();
        final Option o2 = ParentTest.buildLibParent();
        final List list = OptionTestCase.list(o1, o2);

        Collections.sort(list, Comparators.commandLast());

        OptionTestCase.assertListContentsEqual(
            OptionTestCase.list(o2, o1),
            list);
    }

    public void testDefaultOptionFirst() {
        final Option o1 = DefaultOptionTest.buildHelpOption();
        final Option o2 = CommandTest.buildCommitCommand();
        final List list = OptionTestCase.list(o1, o2);

        Collections.sort(list, Comparators.defaultOptionFirst());

        OptionTestCase.assertListContentsEqual(
            OptionTestCase.list(o1, o2),
            list);
    }

    public void testDefaultOptionLast() {
        final Option o1 = DefaultOptionTest.buildHelpOption();
        final Option o2 = CommandTest.buildCommitCommand();
        final List list = OptionTestCase.list(o1, o2);

        Collections.sort(list, Comparators.defaultOptionLast());

        OptionTestCase.assertListContentsEqual(
            OptionTestCase.list(o2, o1),
            list);
    }

    public void testNamedFirst() {
        final Option o1 = DefaultOptionTest.buildHelpOption();
        final Option o2 = ParentTest.buildLibParent();
        final List list = OptionTestCase.list(o1, o2);

        Collections.sort(list, Comparators.namedFirst("--help"));

        OptionTestCase.assertListContentsEqual(
            OptionTestCase.list(o1, o2),
            list);
    }

    public void testNamedLast() {
        final Option o1 = DefaultOptionTest.buildHelpOption();
        final Option o2 = ParentTest.buildLibParent();
        final List list = OptionTestCase.list(o1, o2);

        Collections.sort(list, Comparators.namedLast("--help"));

        OptionTestCase.assertListContentsEqual(
            OptionTestCase.list(o2, o1),
            list);
    }

    public void testPreferredNameFirst() {
        final Option o1 = DefaultOptionTest.buildHelpOption();
        final Option o2 = ParentTest.buildLibParent();
        final List list = OptionTestCase.list(o1, o2);

        Collections.sort(list, Comparators.preferredNameFirst());

        OptionTestCase.assertListContentsEqual(
            OptionTestCase.list(o1, o2),
            list);
    }

    public void testPreferredNameLast() {
        final Option o1 = DefaultOptionTest.buildHelpOption();
        final Option o2 = ParentTest.buildLibParent();
        final List list = OptionTestCase.list(o1, o2);

        Collections.sort(list, Comparators.preferredNameLast());

        OptionTestCase.assertListContentsEqual(
            OptionTestCase.list(o2, o1),
            list);
    }

    public void testRequiredFirst() {
        final Option o1 = DefaultOptionTest.buildHelpOption();
        final Option o2 = DefaultOptionTest.buildXOption();
        final List list = OptionTestCase.list(o1, o2);

        Collections.sort(list, Comparators.requiredFirst());

        OptionTestCase.assertListContentsEqual(
            OptionTestCase.list(o2, o1),
            list);
    }

    public void testRequiredLast() {
        final Option o1 = DefaultOptionTest.buildHelpOption();
        final Option o2 = DefaultOptionTest.buildXOption();
        final List list = OptionTestCase.list(o1, o2);

        Collections.sort(list, Comparators.requiredLast());

        OptionTestCase.assertListContentsEqual(
            OptionTestCase.list(o1, o2),
            list);
    }
    
    public void testChained() {
        final Option o1 = CommandTest.buildCommitCommand();
        final Option o2 = SwitchTest.buildDisplaySwitch();
        final Option o3 = DefaultOptionTest.buildHelpOption();
        final List list = OptionTestCase.list(o1, o2, o3);
        
        Collections.sort(
            list, 
            Comparators.chain(
                Comparators.namedFirst("--help"),
                Comparators.commandFirst()));

        OptionTestCase.assertListContentsEqual(
            OptionTestCase.list(o3, o1, o2),
            list);
    }
}
