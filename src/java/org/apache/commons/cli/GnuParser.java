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

import java.util.ArrayList;

/**
 * The class GnuParser provides an implementation of the 
 * {@link Parser#flatten(Options,String[],boolean) flatten} method.
 *
 * @author John Keyes (john at integralsource.com)
 * @see Parser
 * @version $Revision$
 */
public class GnuParser extends Parser {

    /** holder for flattened tokens */
    private ArrayList tokens = new ArrayList();

    /**
     * <p>Resets the members to their original state i.e. remove
     * all of <code>tokens</code> entries.
     */
    private void init()
    {
        tokens.clear();
    }

    /**
     * <p>This flatten method does so using the following rules:
     * <ol>
     *  <li>If an {@link Option} exists for the first character of 
     *  the <code>arguments</code> entry <b>AND</b> an {@link Option} 
     *  does not exist for the whole <code>argument</code> then
     *  add the first character as an option to the processed tokens
     *  list e.g. "-D" and add the rest of the entry to the also.</li>
     *  <li>Otherwise just add the token to the processed tokens list.
     *  </li>
     * </ol>
     * </p>
     *
     * @param options The Options to parse the arguments by.
     * @param arguments The arguments that have to be flattened.
     * @param stopAtNonOption specifies whether to stop 
     * flattening when a non option has been encountered
     * @return a String array of the flattened arguments
     */
    protected String[] flatten(Options options, String[] arguments, 
                               boolean stopAtNonOption)
    {
        init();

        boolean eatTheRest = false;
        Option currentOption = null;

        for (int i = 0; i < arguments.length; i++)
        {
            if ("--".equals(arguments[i]))
            {
                eatTheRest = true;
                tokens.add("--");
            }
            else if ("-".equals(arguments[i]))
            {
                tokens.add("-");
            }
            else if (arguments[i].startsWith("-"))
            {
                Option option = options.getOption(arguments[i]);

                // this is not an Option
                if (option == null)
                {
                    // handle special properties Option
                    Option specialOption = 
                            options.getOption(arguments[i].substring(0, 2));

                    if (specialOption != null)
                    {
                        tokens.add(arguments[i].substring(0, 2));
                        tokens.add(arguments[i].substring(2));
                    }
                    else if (stopAtNonOption)
                    {
                        eatTheRest = true;
                        tokens.add(arguments[i]);
                    }
                    else
                    {
                        tokens.add(arguments[i]);
                    }
                }
                else
                {
                    currentOption = option;

                    // special option
                    Option specialOption = 
                            options.getOption(arguments[i].substring(0, 2));

                    if ((specialOption != null) && (option == null))
                    {
                        tokens.add(arguments[i].substring(0, 2));
                        tokens.add(arguments[i].substring(2));
                    }
                    else if ((currentOption != null) && currentOption.hasArg())
                    {
                        if (currentOption.hasArg())
                        {
                            tokens.add(arguments[i]);
                            currentOption = null;
                        }
                        else if (currentOption.hasArgs())
                        {
                            tokens.add(arguments[i]);
                        }
                        else if (stopAtNonOption)
                        {
                            eatTheRest = true;
                            tokens.add("--");
                            tokens.add(arguments[i]);
                        }
                        else
                        {
                            tokens.add(arguments[i]);
                        }
                    }
                    else if (currentOption != null)
                    {
                        tokens.add(arguments[i]);
                    }
                    else if (stopAtNonOption)
                    {
                        eatTheRest = true;
                        tokens.add("--");
                        tokens.add(arguments[i]);
                    }
                    else
                    {
                        tokens.add(arguments[i]);
                    }
                }
            }
            else
            {
                tokens.add(arguments[i]);
            }

            if (eatTheRest)
            {
                for (i++; i < arguments.length; i++)
                {
                    tokens.add(arguments[i]);
                }
            }
        }

        return (String[]) tokens.toArray(new String[] {  });
    }
}