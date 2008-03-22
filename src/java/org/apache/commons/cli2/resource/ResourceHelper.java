/*
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
package org.apache.commons.cli2.resource;

import java.text.MessageFormat;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * A utility class used to provide internationalisation support.
 *
 * @author John Keyes
 */
public class ResourceHelper {
    /** system property */
    private static final String PROP_LOCALE = "org.apache.commons.cli2.resource.bundle";

    /** default package name */
    private static final String DEFAULT_BUNDLE =
        "org.apache.commons.cli2.resource.CLIMessageBundle_en_US";
    private static ResourceHelper helper;

    /** resource bundle */
    private ResourceBundle bundle;

    private String prop;

    /**
     * Create a new ResourceHelper for the current locale.
     */
    private ResourceHelper() {
        String bundleName = System.getProperty(PROP_LOCALE);

        if (bundleName == null) {
            bundleName = DEFAULT_BUNDLE;
        }

        this.prop = bundleName;

        int firstUnderscore = bundleName.indexOf('_');
        int secondUnderscore = bundleName.indexOf('_', firstUnderscore + 1);

        Locale locale;
        if (firstUnderscore != -1) {
        String language = bundleName.substring(firstUnderscore + 1, secondUnderscore);
        String country = bundleName.substring(secondUnderscore + 1);
        	locale = new Locale(language, country);
        }
        else {
        	locale = Locale.getDefault();
        }
        // initialize the bundle
        try {
            bundle = ResourceBundle.getBundle(bundleName, locale);
        } catch (MissingResourceException exp) {
            bundle = ResourceBundle.getBundle(DEFAULT_BUNDLE, locale);
        }
    }

    public String getBundleName() {
    	return this.prop;
    }

    /**
     * Gets the ResourceHelper appropriate to the current locale.
     * @return a ResourceHelper
     */
    public static ResourceHelper getResourceHelper() {
        String bundleName = System.getProperty(PROP_LOCALE);
        if (helper == null || !helper.getBundleName().equals(bundleName)) {
            helper = new ResourceHelper();
        }

        return helper;
    }

    /**
     * Returns the message for the specified key.
     *
     * @param key the unique identifier of the message
     * @return String the formatted String
     */
    public String getMessage(final String key) {
        return getMessage(key, new Object[] {  });
    }

    /**
     * Returns the message for the specified key and argument.
     *
     * @param key the unique identifier of the message
     * @param value the argument value
     * @return String the formatted String
     */
    public String getMessage(final String key,
                             final Object value) {
        return getMessage(key, new Object[] { value });
    }

    /**
     * Returns the message for the specified key and arguments.
     *
     * @param key the unique identifier of the message
     * @param value1 an argument value
     * @param value2 an argument value
     * @return String the formatted String
     */
    public String getMessage(final String key,
                             final Object value1,
                             final Object value2) {
        return getMessage(key, new Object[] { value1, value2 });
    }

    /**
     * Returns the message for the specified key and arguments.
     *
     * @param key the unique identifier of the message
     * @param value1 an argument value
     * @param value2 an argument value
     * @param value3 an argument value
     *
     * @return String the formatted String
     */
    public String getMessage(final String key,
                             final Object value1,
                             final Object value2,
                             final Object value3) {
        return getMessage(key, new Object[] { value1, value2, value3 });
    }

    /**
     * Returns the message for the specified key and arguments.
     *
     * @param key the unique identifier of the message
     * @param values argument values
     * @return String the formatted String
     */
    public String getMessage(final String key,
                             final Object[] values) {
        final String msgFormatStr = bundle.getString(key);
        final MessageFormat msgFormat = new MessageFormat(msgFormatStr);

        return msgFormat.format(values);
    }
}
