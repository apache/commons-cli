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
 * <p>OptionBuilder allows the user to create Options using descriptive
 * methods.</p>
 * <p>Details on the Builder pattern can be found at 
 * <a href="http://c2.com/cgi-bin/wiki?BuilderPattern">
 * http://c2.com/cgi-bin/wiki?BuilderPattern</a>.</p>
 *
 * @author John Keyes (john at integralsource.com)
 * @since 1.0
 */
public class OptionBuilder {

    /** long option */
    private static String longopt;

    /** option description */
    private static String description;

    /** argument name */
    private static String argName;

    /** is required? */
    private static boolean required;

    /** the number of arguments */
    private static int numberOfArgs = Option.UNINITIALIZED;

    /** option type */
    private static Object type;

    /** option can have an optional argument value */
    private static boolean optionalArg;

    /** value separator for argument value */
    private static char valuesep;

    /** option builder instance */
    private static OptionBuilder instance = new OptionBuilder();

    /**
     * private constructor to prevent instances being created
     */
    private OptionBuilder()
    {
    }

    /**
     * Resets the member variables to their default values.
     */
    private static void reset()
    {
        description = null;
        argName = "arg";
        longopt = null;
        type = null;
        required = false;
        numberOfArgs = Option.UNINITIALIZED;


        // PMM 9/6/02 - these were missing
        optionalArg = false;
        valuesep = (char) 0;
    }

    /**
     * The next Option created will have the following long option value.
     *
     * @param longopt the long option value
     * @return the OptionBuilder instance
     */
    public static OptionBuilder withLongOpt(String longopt)
    {
        instance.longopt = longopt;

        return instance;
    }

    /**
     * The next Option created will require an argument value.
     *
     * @return the OptionBuilder instance
     */
    public static OptionBuilder hasArg()
    {
        instance.numberOfArgs = 1;

        return instance;
    }

    /**
     * The next Option created will require an argument value if
     * <code>hasArg</code> is true.
     *
     * @param hasArg if true then the Option has an argument value
     * @return the OptionBuilder instance
     */
    public static OptionBuilder hasArg(boolean hasArg)
    {
        instance.numberOfArgs = (hasArg == true) ? 1 : Option.UNINITIALIZED;

        return instance;
    }

    /**
     * The next Option created will have the specified argument value 
     * name.
     *
     * @param name the name for the argument value
     * @return the OptionBuilder instance
     */
    public static OptionBuilder withArgName(String name)
    {
        instance.argName = name;

        return instance;
    }

    /**
     * The next Option created will be required.
     *
     * @return the OptionBuilder instance
     */
    public static OptionBuilder isRequired()
    {
        instance.required = true;

        return instance;
    }

    /**
     * The next Option created uses <code>sep</code> as a means to
     * separate argument values.
     *
     * <b>Example:</b>
     * <pre>
     * Option opt = OptionBuilder.withValueSeparator(':')
     *                           .create('D');
     *
     * CommandLine line = parser.parse(args);
     * String propertyName = opt.getValue(0);
     * String propertyValue = opt.getValue(1);
     * </pre>
     *
     * @param sep The value separator to be used for the argument values.
     *
     * @return the OptionBuilder instance
     */
    public static OptionBuilder withValueSeparator(char sep)
    {
        instance.valuesep = sep;

        return instance;
    }

    /**
     * The next Option created uses '<code>=</code>' as a means to
     * separate argument values.
     *
     * <b>Example:</b>
     * <pre>
     * Option opt = OptionBuilder.withValueSeparator()
     *                           .create('D');
     *
     * CommandLine line = parser.parse(args);
     * String propertyName = opt.getValue(0);
     * String propertyValue = opt.getValue(1);
     * </pre>
     *
     * @return the OptionBuilder instance
     */
    public static OptionBuilder withValueSeparator()
    {
        instance.valuesep = '=';

        return instance;
    }

    /**
     * The next Option created will be required if <code>required</code>
     * is true.
     *
     * @param required if true then the Option is required
     * @return the OptionBuilder instance
     */
    public static OptionBuilder isRequired(boolean required)
    {
        instance.required = required;

        return instance;
    }

    /**
     * The next Option created can have unlimited argument values.
     *
     * @return the OptionBuilder instance
     */
    public static OptionBuilder hasArgs()
    {
        instance.numberOfArgs = Option.UNLIMITED_VALUES;

        return instance;
    }

    /**
     * The next Option created can have <code>num</code> 
     * argument values.
     *
     * @param num the number of args that the option can have
     * @return the OptionBuilder instance
     */
    public static OptionBuilder hasArgs(int num)
    {
        instance.numberOfArgs = num;

        return instance;
    }

    /**
     * The next Option can have an optional argument.
     *
     * @return the OptionBuilder instance
     */
    public static OptionBuilder hasOptionalArg()
    {
        instance.numberOfArgs = 1;
        instance.optionalArg = true;

        return instance;
    }

    /**
     * The next Option can have an unlimited number of
     * optional arguments.
     *
     * @return the OptionBuilder instance
     */
    public static OptionBuilder hasOptionalArgs()
    {
        instance.numberOfArgs = Option.UNLIMITED_VALUES;
        instance.optionalArg = true;

        return instance;
    }

    /**
     * The next Option can have the specified number of 
     * optional arguments.
     *
     * @param numArgs - the maximum number of optional arguments
     * the next Option created can have.
     * @return the OptionBuilder instance
     */
    public static OptionBuilder hasOptionalArgs(int numArgs)
    {
        instance.numberOfArgs = numArgs;
        instance.optionalArg = true;

        return instance;
    }

    /**
     * The next Option created will have a value that will be an instance 
     * of <code>type</code>.
     *
     * @param type the type of the Options argument value
     * @return the OptionBuilder instance
     */
    public static OptionBuilder withType(Object type)
    {
        instance.type = type;

        return instance;
    }

    /**
     * The next Option created will have the specified description
     *
     * @param description a description of the Option's purpose
     * @return the OptionBuilder instance
     */
    public static OptionBuilder withDescription(String description)
    {
        instance.description = description;

        return instance;
    }

    /**
     * Create an Option using the current settings and with 
     * the specified Option <code>char</code>.
     *
     * @param opt the character representation of the Option
     * @return the Option instance
     * @throws IllegalArgumentException if <code>opt</code> is not
     * a valid character.  See Option.
     */
    public static Option create(char opt)
                         throws IllegalArgumentException
    {
        return create(String.valueOf(opt));
    }

    /**
     * Create an Option using the current settings
     *
     * @return the Option instance
     * @throws IllegalArgumentException if <code>longOpt</code> has
     * not been set.  
     */
    public static Option create()
                         throws IllegalArgumentException
    {
        if (longopt == null)
        {
            throw new IllegalArgumentException("must specify longopt");
        }

        return create(null);
    }

    /**
     * Create an Option using the current settings and with 
     * the specified Option <code>char</code>.
     *
     * @param opt the <code>java.lang.String</code> representation 
     * of the Option
     * @return the Option instance
     * @throws IllegalArgumentException if <code>opt</code> is not
     * a valid character.  See Option.
     */
    public static Option create(String opt)
                         throws IllegalArgumentException
    {
        // create the option
        Option option = new Option(opt, description);


        // set the option properties
        option.setLongOpt(longopt);
        option.setRequired(required);
        option.setOptionalArg(optionalArg);
        option.setArgs(numberOfArgs);
        option.setType(type);
        option.setValueSeparator(valuesep);
        option.setArgName(argName);


        // reset the OptionBuilder properties
        instance.reset();

        // return the Option instance
        return option;
    }
}