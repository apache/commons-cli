/*
 * Copyright 2003-2005 The Apache Software Foundation
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

import java.util.List;
import java.util.ListIterator;

import org.apache.commons.cli2.resource.ResourceHelper;

/**
 * The <code>ClassValidator</code> validates the string argument
 * values are class names.
 *
 * The following example shows how to validate the 'logger'
 * argument value is a class name, that can be instantiated.
 *
 * <pre>
 * ...
 * ClassValidator validator = new ClassValidator();
 * validator.setInstance(true);
 *
 * ArgumentBuilder builder = new ArgumentBuilder();
 * Argument logger = 
 *     builder.withName("logger");
 *            .withValidator(validator);
 * </pre>
 * 
 * @author John Keyes
 */
public class ClassValidator implements Validator {

    /** i18n */
    private static final ResourceHelper resources =
        ResourceHelper.getResourceHelper(ClassValidator.class);

    /** whether the class argument is loadable */
    private boolean loadable;
    
    /** whether to create an instance of the class */
    private boolean instance;

    /** the classloader to load classes from */
    private ClassLoader loader;

    /**
     * Validate each argument value in the specified List against this instances
     * permitted attributes.
     * 
     * If a value is valid then it's <code>String</code> value in the list is
     * replaced with it's <code>Class</code> value or instance.
     * 
     * @see org.apache.commons.cli2.validation.Validator#validate(java.util.List)
     */
    public void validate(final List values) throws InvalidArgumentException {

        for (final ListIterator i = values.listIterator(); i.hasNext();) {
            final String name = (String)i.next();

            if (!isPotentialClassName(name)) {
                throw new InvalidArgumentException(
                    resources.getMessage(
                        "ClassValidator.error.bad.classname",
                        name));
            }

            if (loadable || instance) {
                final ClassLoader theLoader = getClassLoader();
                try {
                    final Class clazz = theLoader.loadClass(name);
                    if (instance) {
                        i.set(clazz.newInstance());
                    }
                    else {
                        i.set(clazz);
                    }
                }
                catch (final ClassNotFoundException exp) {
                    throw new InvalidArgumentException(
                        resources.getMessage(
                            "ClassValidator.error.class.notfound",
                            name));
                }
                catch (final IllegalAccessException exp) {
                    throw new InvalidArgumentException(
                        resources.getMessage(
                            "ClassValidator.error.class.access",
                            name,
                            exp.getMessage()));
                }
                catch (final InstantiationException exp) {
                    throw new InvalidArgumentException(
                        resources.getMessage(
                            "ClassValidator.error.class.create",
                            name));
                }
            }
        }
    }

    /**
     * Returns whether the argument value must represent a
     * class that is loadable.
     *
     * @return whether the argument value must represent a
     * class that is loadable.
     */
    public boolean isLoadable() {
        return loadable;
    }

    /**
     * Specifies whether the argument value must represent a
     * class that is loadable.
     *
     * @param loadable whether the argument value must 
     * represent a class that is loadable.
     */
    public void setLoadable(boolean loadable) {
        this.loadable = loadable;
    }

    /**
     * Returns the {@link ClassLoader} used to resolve and load
     * the classes specified by the argument values.
     *
     * @return the {@link ClassLoader} used to resolve and load
     * the classes specified by the argument values.
     */
    public ClassLoader getClassLoader() {
        if (loader == null) {
            loader = getClass().getClassLoader();
        }
        
        return loader;
    }

    /**
     * Specifies the {@link ClassLoader} used to resolve and load
     * the classes specified by the argument values.
     *
     * @param loader the {@link ClassLoader} used to resolve and load
     * the classes specified by the argument values.
     */
    public void setClassLoader(ClassLoader loader) {
        this.loader = loader;
    }

    /**
     * Returns whether the argument value must represent a
     * class that can be instantiated.
     *
     * @return whether the argument value must represent a
     * class that can be instantiated.
     */
    public boolean isInstance() {
        return instance;
    }

    /**
     * Specifies whether the argument value must represent a
     * class that can be instantiated.
     *
     * @param instance whether the argument value must 
     * represent a class that can be instantiated.
     */
    public void setInstance(boolean instance) {
        this.instance = instance;
    }

    /**
     * Returns whether the specified name is allowed as
     * a Java class name.
     */
    protected boolean isPotentialClassName(final String name) {
        final char[] chars = name.toCharArray();

        boolean expectingStart = true;

        for (int i = 0; i < chars.length; ++i) {
            final char c = chars[i];
            if (expectingStart) {
                if (!Character.isJavaIdentifierStart(c)) {
                    return false;
                }
                expectingStart = false;
            }
            else {
                if (c == '.') {
                    expectingStart = true;
                }
                else if (!Character.isJavaIdentifierPart(c)) {
                    return false;
                }
            }
        }
        return !expectingStart;
    }

}
