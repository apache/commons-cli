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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 *
 */
public class MainTester implements OptionListener
{

    private String md5;
    private String file;

    public static void main(String[] args) throws Exception
    {
        new MainTester(args);
    }

    public MainTester(final String[] args) throws Exception
    {
        InputStream is = MainTester.class.getResourceAsStream(
                "/config/config_real.conf");
//                "/config/config_real.csv");
        CommandLineConfiguration cliConfig = new CommandLineConfiguration();
        cliConfig.addOptionListener(this);
        cliConfig.process(is, "UTF-8", args);
        if (md5 != null && file != null)
        {
            checkMd5();
        }
        else
        {
            System.err.println("Invalid arguments; try -h/--help");
        }
    }

    @Override
    public void option(String option, Object value)
    {
        if ("md5".equals(option))
        {
            md5 = value.toString();
        }
        if ("file".equals(option))
        {
            file = value.toString();
        }
    }

    private void checkMd5() throws Exception
    {
        FileInputStream is = new FileInputStream(new File(file));
        
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[0xFFFF];
        for (int len = is.read(buffer); len != -1; len = is.read(buffer))
        {
            os.write(buffer, 0, len);
        }
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.reset();
        messageDigest.update(os.toString("UTF-8").getBytes());
        byte[] digestData = messageDigest.digest();

        BigInteger bigInt = new BigInteger(1, digestData);
        String hashtext = bigInt.toString(16);
        System.out.println("Digest of file: " + hashtext);
        System.out.println("MATCHES: " + md5.equalsIgnoreCase(hashtext));
    }
}
