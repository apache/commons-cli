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
package org.apache.commons.cli2.jdepend;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;
import junit.framework.TestCase;

/**
 * @author Rob Oxspring
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class JDependTest extends TestCase {

    private JDepend dependancies = null;

    public void setUp() throws IOException {
        dependancies = new JDepend();
        dependancies.addDirectory("target/classes");
        dependancies.analyze();
    }

    public void testJUnitNotPresent() {
        // if junit dependancy is found then jdepend has been poluted
        // with test classes and all tests are meaningless
        assertNull(
            "JUnit dependancy found",
            dependancies.getPackage("junit.framework"));

        // the same applies to jdepend
        assertNull(
            "JDepend dependancy found",
            dependancies.getPackage("jdepend.framework"));
    }

    public void testAcceptableDistance() {
        Collection packages = dependancies.getPackages();
        // only interested in cli2
        packages = cli2Packages(packages);
        // resources is well off the line
        packages =
            namedPackages(packages, "org.apache.commons.cli2.resource", false);

        for (final Iterator i = packages.iterator(); i.hasNext();) {
            final JavaPackage pkg = (JavaPackage)i.next();
            final float distance = pkg.distance();
            final String message = pkg.getName() + " too far from line: " + distance;
            assertTrue(
                message,
                distance < 0.21d);
        }
    }

    public void testNoCyclesPresent() {
        assertEquals("Cycles exist", false, dependancies.containsCycles());
    }

    public void testApiIndependance() {
        dependancies.analyze();

        final JavaPackage apiPackage =
            dependancies.getPackage("org.apache.commons.cli2");
        final Collection dependsUpon = cli2Packages(apiPackage.getEfferents());

        assertEquals("Api should depend on one package", 1, dependsUpon.size());
        
        JavaPackage pkg = (JavaPackage) dependsUpon.iterator().next();
        assertEquals(
                "Wrong package name", 
                "org.apache.commons.cli2.resource",
                pkg.getName());
    }

    private Collection cli2Packages(final Collection incoming) {
        return namedPackages(incoming, "org.apache.commons.cli2", true);
    }

    private Collection namedPackages(
        final Collection incoming,
        final String name,
        final boolean include) {
        final Collection outgoing = new ArrayList();
        for (final Iterator i = incoming.iterator(); i.hasNext();) {
            final JavaPackage pkg = (JavaPackage)i.next();
            if (include ^ !pkg.getName().startsWith(name)) {
                outgoing.add(pkg);
            }
        }
        return outgoing;
    }
}
