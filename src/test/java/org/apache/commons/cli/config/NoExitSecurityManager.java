/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.commons.cli.config;

import java.security.Permission;

/**
 * Adapted from
 * https://stackoverflow.com/questions/309396/java-how-to-test-methods-that-call-system-exit.
 *
 * One of the uses of the command line configuration is to auto-enable printing
 * of help options, with the user only having to specify their help option via
 * their arguments. Such a call invokes system exit with exit status 0; this
 * class, along with {@link ExitException}, are used to catch this in the tests.
 */
class NoExitSecurityManager extends SecurityManager
{

    /**
     * Allows anything.
     * 
     * @param perm not used.
     */
    @Override
    public void checkPermission(Permission perm)
    {
        // allow anything.
    }

    /**
     * Allows anything.
     * 
     * @param perm not used.
     * 
     * @param context not used.
     */
    @Override
    public void checkPermission(final Permission perm, final Object context)
    {
        // allow anything.
    }

    /**
     * Throws an {@link ExitException}.
     * 
     * @param status system exit status.
     */
    @Override
    public void checkExit(int status)
    {
        super.checkExit(status);
        throw new ExitException(status);
    }
}
