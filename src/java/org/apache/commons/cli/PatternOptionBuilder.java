/**
 * Copyright 1999-2001,2004 The Apache Software Foundation.
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
package org.apache.commons.cli;

/** 
 * <p>
 * Allows Options to be created from a single String.
 * The pattern contains various single character flags and via
 * an optional punctuation character, their expected type.
 * </p>
 *
 * <table border="1">
 * <tr><td>a</td><td>-a flag</td></tr>
 * <tr><td>b@</td><td>-b [classname]</td></tr>
 * <tr><td>c&gt;</td><td>-c [filename]</td></tr>
 * <tr><td>d+</td><td>-d [classname] (creates object via empty contructor)</td></tr>
 * <tr><td>e%</td><td>-e [number] (creates Number instance)</td></tr>
 * <tr><td>f/</td><td>-f [url]</td></tr>
 * </table>
 *
 * <p>
 * For example, the following allows command line flags of '-v -p string-value -f /dir/file'.
 * </p>
 * <code>Options options = PatternOptionBuilder.parsePattern("vp:f/");</code>
 *
 * <p>
 * TODO These need to break out to OptionType and also 
 * to be pluggable.
 * </p>
 *
 * @author Henri Yandell (bayard @ generationjava.com)
 * @version $Revision$
 */
public class PatternOptionBuilder {

    /** String class */
    public static final Class STRING_VALUE = java.lang.String.class;

    /** Object class */
    public static final Class OBJECT_VALUE = java.lang.Object.class;

    /** Number class */
    public static final Class NUMBER_VALUE = java.lang.Number.class;

    /** Date class */
    public static final Class DATE_VALUE = java.util.Date.class;

    /** Class class */
    public static final Class CLASS_VALUE = java.lang.Class.class;

    /// can we do this one?? 
    // is meant to check that the file exists, else it errors.
    // ie) it's for reading not writing.

    /** FileInputStream class */
    public static final Class EXISTING_FILE_VALUE = 
            java.io.FileInputStream.class;

    /** File class */
    public static final Class FILE_VALUE = java.io.File.class;

    /** File array class */
    public static final Class FILES_VALUE = java.io.File[].class;

    /** URL class */
    public static final Class URL_VALUE = java.net.URL.class;

    /**
     * <p>Retrieve the class that <code>ch</code> represents.</p>
     *
     * @param ch the specified character
     * @return The class that <code>ch</code> represents
     */
    public static Object getValueClass(char ch)
    {
        if (ch == '@')
        {
            return PatternOptionBuilder.OBJECT_VALUE;
        }
        else if (ch == ':')
        {
            return PatternOptionBuilder.STRING_VALUE;
        }
        else if (ch == '%')
        {
            return PatternOptionBuilder.NUMBER_VALUE;
        }
        else if (ch == '+')
        {
            return PatternOptionBuilder.CLASS_VALUE;
        }
        else if (ch == '#')
        {
            return PatternOptionBuilder.DATE_VALUE;
        }
        else if (ch == '<')
        {
            return PatternOptionBuilder.EXISTING_FILE_VALUE;
        }
        else if (ch == '>')
        {
            return PatternOptionBuilder.FILE_VALUE;
        }
        else if (ch == '*')
        {
            return PatternOptionBuilder.FILES_VALUE;
        }
        else if (ch == '/')
        {
            return PatternOptionBuilder.URL_VALUE;
        }

        return null;
    }

    /**
     * <p>Returns whether <code>ch</code> is a value code, i.e.
     * whether it represents a class in a pattern.</p>
     * 
     * @param ch the specified character
     * @return true if <code>ch</code> is a value code, otherwise false.
     */
    public static boolean isValueCode(char ch)
    {
        if ((ch != '@') && (ch != ':') && (ch != '%') && (ch != '+')
            && (ch != '#') && (ch != '<') && (ch != '>') && (ch != '*')
            && (ch != '/') && (ch != '!'))
        {
            return false;
        }

        return true;
    }

    /**
     * <p>Returns the {@link Options} instance represented by 
     * <code>pattern</code>.</p>
     *
     * @param pattern the pattern string
     * @return The {@link Options} instance
     */
    public static Options parsePattern(String pattern)
    {
        int sz = pattern.length();

        char opt = ' ';
        char ch = ' ';
        boolean required = false;
        Object type = null;

        Options options = new Options();

        for (int i = 0; i < sz; i++)
        {
            ch = pattern.charAt(i);

            // a value code comes after an option and specifies 
            // details about it
            if (!isValueCode(ch))
            {
                if (opt != ' ')
                {
                    OptionBuilder.hasArg(type != null);
                    OptionBuilder.isRequired(required);
                    OptionBuilder.withType(type);
                    
                    // we have a previous one to deal with
                    options.addOption(OptionBuilder.create(opt));
                    required = false;
                    type = null;
                    opt = ' ';
                }

                opt = ch;
            }
            else if (ch == '!')
            {
                required = true;
            }
            else
            {
                type = getValueClass(ch);
            }
        }

        if (opt != ' ')
        {
            OptionBuilder.hasArg(type != null);
            OptionBuilder.isRequired(required);
            OptionBuilder.withType(type);
            
            // we have a final one to deal with
            options.addOption(OptionBuilder.create(opt));
        }

        return options;
    }
}
