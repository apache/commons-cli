/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//cli/src/java/org/apache/commons/cli/OptionValidator.java,v 1.3 2003/10/09 20:57:01 rdonkin Exp $
 * $Revision: 1.3 $
 * $Date: 2003/10/09 20:57:01 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package org.apache.commons.cli;

/**
 * Validates an Option string.
 *
 * @author John Keyes ( john at integralsource.com )
 */
public class OptionValidator {

    /**
     * <p>Validates whether <code>opt</code> is a permissable Option
     * shortOpt.  The rules that specify if the <code>opt</code>
     * is valid are:</p>
     * <ul>
     *  <li><code>opt</code> is not NULL</li>
     *  <li>a single character <code>opt</code> that is either
     *  ' '(special case), '?', '@' or a letter</li>
     *  <li>a multi character <code>opt</code> that only contains
     *  letters.</li>
     * </ul>
     *
     * @param opt The option string to validate
     * @throws IllegalArgumentException if the Option is not valid.
     */
    static void validateOption(String opt)
                        throws IllegalArgumentException
    {
        // check that opt is not NULL
        if (opt == null)
        {
            return;
        }

        // handle the single character opt
        else if (opt.length() == 1)
        {
            char ch = opt.charAt(0);

            if (!isValidOpt(ch))
            {
                throw new IllegalArgumentException("illegal option value '" + ch
                                                   + "'");
            }
        }

        // handle the multi character opt
        else
        {
            char[] chars = opt.toCharArray();

            for (int i = 0; i < chars.length; i++)
            {
                if (!isValidChar(chars[i]))
                {
                    throw new IllegalArgumentException(
                            "opt contains illegal character value '" + chars[i]
                            + "'");
                }
            }
        }
    }

    /**
     * <p>Returns whether the specified character is a valid Option.</p>
     *
     * @param c the option to validate
     * @return true if <code>c</code> is a letter, ' ', '?' or '@', 
     * otherwise false.
     */
    private static boolean isValidOpt(char c)
    {
        return (isValidChar(c) || (c == ' ') || (c == '?') || c == '@');
    }

    /**
     * <p>Returns whether the specified character is a valid character.</p>
     *
     * @param c the character to validate
     * @return true if <code>c</code> is a letter.
     */
    private static boolean isValidChar(char c)
    {
        return Character.isJavaIdentifierPart(c);
    }
}