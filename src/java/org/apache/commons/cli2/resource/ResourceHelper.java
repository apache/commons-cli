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
package org.apache.commons.cli2.resource;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * A utility class used to provide internationalisation support.
 * @author John Keyes
 */
public class ResourceHelper {

    /** resource bundle */
    private ResourceBundle bundle;

    /** default bundle name */
    private static final String DEFAULT_BUNDLE = "messages";

    /** cache */
    private static Map cache = new HashMap(13);
    
    /**
     * Gets the ResourceHelper appropriate to the specified class.
     * @param clazz the class to get resources for
     * @return a ResourceHelper
     */
    public static ResourceHelper getResourceHelper(final Class clazz) {

        if (cache.containsKey(clazz)) {
            return (ResourceHelper)cache.get(clazz);
        }

        return new ResourceHelper(clazz);
    }

    /**
     * Create a new ResourceHelper for the specified class.
     * 
     * @param clazz
     *            the Class that requires some resources
     */
    private ResourceHelper(final Class clazz) {

        // get the name of the class
        final String className = clazz.getName();

        // discover the package name
        final String packageName =
            className.substring(0, className.lastIndexOf(".") + 1);

        final String bundleName = packageName + DEFAULT_BUNDLE;

        // initialize the bundle
        try {
            bundle = ResourceBundle.getBundle(bundleName);
        }
        catch (MissingResourceException e) {
            //TODO Handle missing resources nicely
            bundle = null;
        }

        // cache bundle
        cache.put(bundleName, bundle);
    }

    /**
     * Returns the message for the specified key.
     * 
     * @param key
     *            the unique identifier of the message
     * 
     * @return String the formatted String
     */
    public String getMessage(final String key) {
        return getMessage(key, new Object[] {
        });
    }

    /**
     * Returns the message for the specified key and argument.
     * 
     * @param key
     *            the unique identifier of the message
     * 
     * @param value
     *            the argument value
     * 
     * @return String the formatted String
     */
    public String getMessage(final String key, final Object value) {

        return getMessage(key, new Object[] { value });
    }

    /**
     * Returns the message for the specified key and arguments.
     * 
     * @param key
     *            the unique identifier of the message
     * 
     * @param value1
     *            an argument value
     * 
     * @param value2
     *            an argument value
     * 
     * @return String the formatted String
     */
    public String getMessage(
        final String key,
        final Object value1,
        final Object value2) {

        return getMessage(key, new Object[] { value1, value2 });
    }

    /**
     * Returns the message for the specified key and arguments.
     * 
     * @param key
     *            the unique identifier of the message
     * 
     * @param value1
     *            an argument value
     * 
     * @param value2
     *            an argument value
     * 
     * @param value3
     *            an argument value
     * 
     * @return String the formatted String
     */
    public String getMessage(
        final String key,
        final Object value1,
        final Object value2,
        final Object value3) {

        return getMessage(key, new Object[] { value1, value2, value3 });
    }

    /**
     * Returns the message for the specified key and arguments.
     * 
     * @param key
     *            the unique identifier of the message
     * 
     * @param values
     *            argument values
     * 
     * @return String the formatted String
     */
    public String getMessage(final String key, final Object[] values) {

        final String msgFormatStr = bundle.getString(key);
        final MessageFormat msgFormat = new MessageFormat(msgFormatStr);

        return msgFormat.format(values);
    }
}
