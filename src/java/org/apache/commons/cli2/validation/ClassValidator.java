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

import java.util.List;
import java.util.ListIterator;

import org.apache.commons.cli2.resource.ResourceHelper;

/**
 * A validator checking for classnames
 */
public class ClassValidator implements Validator {

    /** i18n */
    private static final ResourceHelper resources =
        ResourceHelper.getResourceHelper(ClassValidator.class);

    private boolean loadable;
    private boolean instance;

    private ClassLoader loader;

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

    protected boolean isPotentialClassName(final String name) {
        final char[] chars = name.toCharArray();

        boolean expectingStart = true;

        for (int i = 0; i < chars.length; ++i) {
            final char c = chars[i];
            if (expectingStart) {
                if (!Character.isJavaIdentifierStart(c)) {
                    return false;
                }
                else {
                    expectingStart = false;
                }
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

    /**
     * @return true iff class must be loadable to be valid
     */
    public boolean isLoadable() {
        return loadable;
    }

    /**
     * true iff class must be loadable to be valid
     * @param loadable new loadable value
     */
    public void setLoadable(boolean loadable) {
        this.loadable = loadable;
    }

    /**
     * @return the classloader to resolve classes in
     */
    public ClassLoader getClassLoader() {
        if (loader == null) {
            loader = getClass().getClassLoader();
        }
        
        return loader;
    }

    /**
     * @param loader the classloader to resolve classes in
     */
    public void setClassLoader(ClassLoader loader) {
        this.loader = loader;
    }

    /**
     * @return true iff class instance is needed to be valid 
     */
    public boolean isInstance() {
        return instance;
    }

    /**
     * @param instance true iff class instance is needed to be valid
     */
    public void setInstance(boolean instance) {
        this.instance = instance;
    }
}
