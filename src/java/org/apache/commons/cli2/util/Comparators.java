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
package org.apache.commons.cli2.util;

import java.util.Comparator;
import java.util.List;

import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.option.Command;
import org.apache.commons.cli2.option.DefaultOption;
import org.apache.commons.cli2.option.Switch;

/**
 * A collection of Comparators suitable for use with Option instances.
 */
public class Comparators {

    private Comparators(){
        // constructor hiden from potential users
    }


    /**
     * Chains comparators together.
     *
     * @see #chain(Comparator[])
     * @param c0
     *            a comparator
     * @param c1
     *            a comparator
     * @return a chained comparator
     */
    public static Comparator chain(final Comparator c0, final Comparator c1) {
        return chain(new Comparator[] { c0, c1 });
    }

    /**
     * Chains comparators together.
     *
     * @see #chain(Comparator[])
     * @param c0
     *            a comparator
     * @param c1
     *            a comparator
     * @param c2
     *            a comparator
     * @return a chained comparator
     */
    public static Comparator chain(
        final Comparator c0,
        final Comparator c1,
        final Comparator c2) {
        return chain(new Comparator[] { c0, c1, c2 });
    }

    /**
     * Chains comparators together.
     *
     * @see #chain(Comparator[])
     * @param c0
     *            a comparator
     * @param c1
     *            a comparator
     * @param c2
     *            a comparator
     * @param c3
     *            a comparator
     * @return a chained comparator
     */
    public static Comparator chain(
        final Comparator c0,
        final Comparator c1,
        final Comparator c2,
        final Comparator c3) {
        return chain(new Comparator[] { c0, c1, c2, c3 });
    }

    /**
     * Chains comparators together.
     *
     * @see #chain(Comparator[])
     * @param c0
     *            a comparator
     * @param c1
     *            a comparator
     * @param c2
     *            a comparator
     * @param c3
     *            a comparator
     * @param c4
     *            a comparator
     * @return a chained comparator
     */
    public static Comparator chain(
        final Comparator c0,
        final Comparator c1,
        final Comparator c2,
        final Comparator c3,
        final Comparator c4) {
        return chain(new Comparator[] { c0, c1, c2, c3, c4 });
    }

    /**
     * Chains comparators together.
     *
     * @see #chain(Comparator[])
     * @param comparators
     *            a List of comparators to chain together
     * @return a chained comparator
     */
    public static Comparator chain(final List comparators) {
        return new Chain(
            (Comparator[])comparators.toArray(
                new Comparator[comparators.size()]));
    }

    /**
     * Chains an array of comparators together. Each Comparator will be called
     * in turn until one of them return a non-zero value, this value will be
     * returned.
     *
     * @param comparators
     *            the array of comparators
     * @return a chained comparator
     */
    public static Comparator chain(final Comparator[] comparators) {
        return new Chain(comparators);
    }

    /**
     * Chains a series of Comparators together.
     */
    private static class Chain implements Comparator {

        final Comparator[] chain;

        /**
         * Creates a Comparator chain using the specified array of Comparators
         * @param chain the Comparators in the chain
         */
        public Chain(final Comparator[] chain) {
            this.chain = new Comparator[chain.length];
            System.arraycopy(chain, 0, this.chain, 0, chain.length);
        }

        public int compare(final Object left, final Object right) {
            int result = 0;
            for (int i = 0; result == 0 && i < chain.length; ++i) {
                result = chain[i].compare(left, right);
            }
            return result;
        }
    }

    /**
     * Reverses a comparator's logic.
     *
     * @param wrapped
     *            the Comparator to reverse the logic of
     * @return a comparator with reverse logic
     */
    private static Comparator reverse(final Comparator wrapped) {
        return new Reverse(wrapped);
    }

    private static class Reverse implements Comparator {
        private final Comparator wrapped;

        /**
         * Creates a Comparator with reverse logic
         * @param wrapped the original logic
         */
        public Reverse(final Comparator wrapped) {
            this.wrapped = wrapped;
        }

        public int compare(final Object left, final Object right) {
            return -wrapped.compare(left, right);
        }
    }

    /**
     * Forces Group instances to appear at the beginning of lists
     *
     * @see Group
     * @return a new comparator
     */
    public static Comparator groupFirst() {
        return new GroupFirst();
    }

    /**
     * Forces Group instances to appear at the end of lists
     *
     * @see Group
     * @return a new comparator
     */
    public static Comparator groupLast() {
        return reverse(groupFirst());
    }

    private static class GroupFirst implements Comparator {
        public int compare(final Object left, final Object right) {
            final boolean l = left instanceof Group;
            final boolean r = right instanceof Group;

            if (l ^ r) {
                if (l) {
                    return -1;
                }
                return 1;
            }
            return 0;
        }
    }

    /**
     * Forces Switch instances to appear at the beginning of lists
     *
     * @see Switch
     * @return a new comparator
     */
    public static Comparator switchFirst() {
        return new SwitchFirst();
    }

    /**
     * Forces Switch instances to appear at the end of lists
     *
     * @see Switch
     * @return a new comparator
     */
    public static Comparator switchLast() {
        return reverse(switchFirst());
    }

    private static class SwitchFirst implements Comparator {
        public int compare(final Object left, final Object right) {
            final boolean l = left instanceof Switch;
            final boolean r = right instanceof Switch;

            if (l ^ r) {
                if (l) {
                    return -1;
                }
                return 1;
            }
            return 0;
        }
    }

    /**
     * Forces Command instances to appear at the beginning of lists
     *
     * @see Command
     * @return a new comparator
     */
    public static Comparator commandFirst() {
        return new CommandFirst();
    }

    /**
     * Forces Command instances to appear at the end of lists
     *
     * @see Command
     * @return a new comparator
     */
    public static Comparator commandLast() {
        return reverse(commandFirst());
    }

    private static class CommandFirst implements Comparator {
        public int compare(final Object left, final Object right) {
            final boolean l = left instanceof Command;
            final boolean r = right instanceof Command;

            if (l ^ r) {
                if (l) {
                    return -1;
                }
                return 1;
            }
            return 0;
        }
    }

    /**
     * Forces DefaultOption instances to appear at the beginning of lists
     *
     * @see DefaultOption
     * @return a new comparator
     */
    public static Comparator defaultOptionFirst() {
        return new DefaultOptionFirst();
    }

    /**
     * Forces DefaultOption instances to appear at the end of lists
     *
     * @see DefaultOption
     * @return a new comparator
     */
    public static Comparator defaultOptionLast() {
        return reverse(defaultOptionFirst());
    }

    private static class DefaultOptionFirst implements Comparator {
        public int compare(final Object left, final Object right) {
            final boolean l = left instanceof DefaultOption;
            final boolean r = right instanceof DefaultOption;

            if (l ^ r) {
                if (l) {
                    return -1;
                }
                return 1;
            }
            return 0;
        }
    }

    /**
     * Forces Comparators with a particular trigger to appear at the beginning
     * of lists
     *
     * @param name
     *            the trigger name to select
     * @see Option#getTriggers()
     * @return a new comparator
     */
    public static Comparator namedFirst(final String name) {
        return new Named(name);
    }

    /**
     * Forces Comparators with a particular trigger to appear at the end of
     * lists
     *
     * @param name
     *            the trigger name to select
     * @see Option#getTriggers()
     * @return a new comparator
     */
    public static Comparator namedLast(final String name) {
        return reverse(new Named(name));
    }

    private static class Named implements Comparator {
        private final String name;

        /**
         * Creates a Comparator that sorts a particular name high in order
         * @param name the trigger name to select
         */
        public Named(final String name) {
            this.name = name;
        }
        public int compare(final Object oleft, final Object oright) {
            final Option left = (Option)oleft;
            final Option right = (Option)oright;

            final boolean l = left.getTriggers().contains(name);
            final boolean r = right.getTriggers().contains(name);

            if (l ^ r) {
                if (l) {
                    return -1;
                }
                return 1;
            }
            return 0;
        }
    }

    /**
     * Orders Options by preferredName
     *
     * @see Option#getPreferredName()
     * @return a new comparator
     */
    public static Comparator preferredNameFirst() {
        return new PreferredName();
    }

    /**
     * Orders Options by preferredName, reversed
     *
     * @see Option#getPreferredName()
     * @return a new comparator
     */
    public static Comparator preferredNameLast() {
        return reverse(preferredNameFirst());
    }

    private static class PreferredName implements Comparator {
        public int compare(final Object oleft, final Object oright) {
            final Option left = (Option)oleft;
            final Option right = (Option)oright;

            return left.getPreferredName().compareTo(right.getPreferredName());
        }
    }

    /**
     * Orders Options grouping required Options first
     *
     * @see Option#isRequired()
     * @return a new comparator
     */
    public static Comparator requiredFirst() {
        return new Required();
    }

    /**
     * Orders Options grouping required Options last
     *
     * @see Option#isRequired()
     * @return a new comparator
     */
    public static Comparator requiredLast() {
        return reverse(requiredFirst());
    }

    private static class Required implements Comparator {
        public int compare(final Object oleft, final Object oright) {
            final Option left = (Option)oleft;
            final Option right = (Option)oright;

            final boolean l = left.isRequired();
            final boolean r = right.isRequired();

            if (l ^ r) {
                if (l) {
                    return -1;
                }
                return 1;
            }
            return 0;
        }
    }
}
