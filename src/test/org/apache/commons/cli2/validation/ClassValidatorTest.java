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
package org.apache.commons.cli2.validation;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.cli2.resource.ResourceHelper;

public class ClassValidatorTest extends TestCase {

    private final static ResourceHelper resources =
        ResourceHelper.getResourceHelper(ClassValidatorTest.class);

    private ClassValidator validator;

    protected void setUp() {
        validator = new ClassValidator();
    }

    public void testValidName() throws InvalidArgumentException {
        final Object[] array = new Object[] { "MyApp", "org.apache.ant.Main" };
        final List list = Arrays.asList(array);

        validator.validate(list);

        assertEquals("Name is incorrect", "MyApp", list.get(0));
        assertEquals("Name is incorrect", "org.apache.ant.Main", list.get(1));
    }

    public void testNameBadStart() {
        final String className = "1stClass";
        final Object[] array = new Object[] { className };
        final List list = Arrays.asList(array);

        try {
            validator.validate(list);
            fail("Class name cannot start with a number.");
        } catch (InvalidArgumentException ive) {
            assertEquals(
                resources.getMessage(
                    "ClassValidator.error.bad.classname",
                    className),
                ive.getMessage());
        }
    }

    public void testNameBadEnd() {
        final String className = "My.Class.";

        final Object[] array = new Object[] { className };
        final List list = Arrays.asList(array);

        try {
            validator.validate(list);
            fail("Trailing period not permitted.");
        } catch (InvalidArgumentException ive) {
            assertEquals(
                resources.getMessage(
                    "ClassValidator.error.bad.classname",
                    className),
                ive.getMessage());
        }
    }

    public void testNameBadMiddle() {
        final String className = "My..Class";

        final Object[] array = new Object[] { className };
        final List list = Arrays.asList(array);

        try {
            validator.validate(list);
            fail("Two consecutive periods is not permitted.");
        } catch (InvalidArgumentException ive) {
            assertEquals(
                resources.getMessage(
                    "ClassValidator.error.bad.classname",
                    className),
                ive.getMessage());
        }
    }

    public void testIllegalNameChar() {
        final String className = "My?Class";

        final Object[] array = new Object[] { className };
        final List list = Arrays.asList(array);

        try {
            validator.validate(list);
            fail("Illegal character not allowed in Class name.");
        } catch (InvalidArgumentException ive) {
            assertEquals(
                resources.getMessage(
                    "ClassValidator.error.bad.classname",
                    className),
                ive.getMessage());
        }
    }

    public void testLoadable() throws InvalidArgumentException {
        assertFalse("Validator is loadable", validator.isLoadable());
        validator.setLoadable(true);
        assertTrue("Validator is NOT loadable", validator.isLoadable());
        validator.setLoadable(false);
        assertFalse("Validator is loadable", validator.isLoadable());
    }

    public void testLoadValid() throws InvalidArgumentException {
        final Object[] array =
            new Object[] {
                "org.apache.commons.cli2.Option",
                "java.util.Vector" };
        final List list = Arrays.asList(array);

        validator.setLoadable(true);
        validator.validate(list);

        final Iterator i = list.iterator();
        assertEquals(
            "org.apache.commons.cli2.Option",
            ((Class) i.next()).getName());
        assertEquals("java.util.Vector", ((Class) i.next()).getName());
        assertFalse(i.hasNext());
    }

    public void testLoadInvalid() {
        final String className = "org.apache.commons.cli2.NonOption";

        final Object[] array = new Object[] { className, "java.util.Vectors" };
        final List list = Arrays.asList(array);

        validator.setLoadable(true);

        try {
            validator.validate(list);
            fail("Class Not Found");
        } catch (InvalidArgumentException ive) {
            assertEquals(
                resources.getMessage(
                    "ClassValidator.error.class.notfound",
                    className),
                ive.getMessage());
        }
    }

    public void testInstantiate() throws InvalidArgumentException {
        assertFalse("Validator creates instances", validator.isInstance());
        validator.setInstance(true);
        assertTrue(
            "Validator does NOT create instances",
            validator.isInstance());
        validator.setInstance(false);
        assertFalse("Validator creates instances", validator.isInstance());
    }

    public void testCreateClassInstance() throws InvalidArgumentException {
        final Object[] array = new Object[] { "java.util.Vector" };
        final List list = Arrays.asList(array);

        validator.setInstance(true);

        validator.validate(list);
        assertTrue(
            "Vector instance NOT found",
            list.get(0) instanceof java.util.Vector);
    }

    public void testCreateInterfaceInstance() throws InvalidArgumentException {
        final String className = "java.util.Map";
        final Object[] array = new Object[] { className };
        final List list = Arrays.asList(array);

        validator.setInstance(true);
        
        try {
            validator.validate(list);
            fail("It's not possible to create a '" + className + "'");
        }
        catch (final InvalidArgumentException ive) {
            assertEquals(
                    resources.getMessage(
                            "ClassValidator.error.class.create",
                            className),
                            ive.getMessage());
        }
    }

    public void testCreateProtectedInstance() throws InvalidArgumentException {
        final String className = "org.apache.commons.cli2.validation.protect.ProtectedClass";
        final Object[] array = new Object[] { className };
        final List list = Arrays.asList(array);

        validator.setInstance(true);
        
        try {
            validator.validate(list);
            fail("It's not possible to create a '" + className + "'");
        }
        catch (final InvalidArgumentException ive) {
            assertEquals(
                    resources.getMessage(
                            "ClassValidator.error.class.access",
                            className,
                            "Class org.apache.commons.cli2.validation.ClassValidator " +
                            "can not access a member of class " +
                            "org.apache.commons.cli2.validation.protect.ProtectedClass " +
                            "with modifiers \"protected\""),
                            ive.getMessage());
        }
    }
    
    public void testClassloader() throws InvalidArgumentException {
        assertEquals(
            "Wrong classloader found",
            validator.getClass().getClassLoader(),
            validator.getClassLoader());

        URLClassLoader classloader = new URLClassLoader(new URL[] {
        });
        validator.setClassLoader(classloader);

        assertEquals(
            "Wrong classloader found",
            classloader,
            validator.getClassLoader());
    }
}
