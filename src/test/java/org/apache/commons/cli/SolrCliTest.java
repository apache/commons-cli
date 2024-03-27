/*
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package org.apache.commons.cli;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Locale;

import org.junit.jupiter.api.Test;

/**
 * Test fixtures used in SOLR tests.
 */
class SolrCliTest {

    public static final String ZK_HOST = "localhost:9983";

    public static final String DEFAULT_CONFIG_SET = "_default";

    public static final Option OPTION_ZKHOST_DEPRECATED =
    // @formatter:off
        Option.builder("zkHost")
            .longOpt("zkHost")
            .deprecated(
                DeprecatedAttributes.builder()
                    .setForRemoval(true)
                    .setSince("9.6")
                    .setDescription("Use --zk-host instead")
                    .get())
            .argName("HOST")
            .hasArg()
            .required(false)
            .desc("Zookeeper connection string; unnecessary if ZK_HOST is defined in solr.in.sh; otherwise, defaults to "
                    + ZK_HOST
                    + '.')
            .build();
    // @formatter:on

    public static final Option OPTION_ZKHOST =
    // @formatter:off
        Option.builder("z")
            .longOpt("zk-host")
            .argName("HOST")
            .hasArg()
            .required(false)
            .desc("Zookeeper connection string; unnecessary if ZK_HOST is defined in solr.in.sh; otherwise, defaults to "
                    + ZK_HOST
                    + '.')
            .build();
    // @formatter:on

    public static final Option OPTION_SOLRURL_DEPRECATED =
    // @formatter:off
        Option.builder("solrUrl")
            .longOpt("solrUrl")
            .deprecated(
                DeprecatedAttributes.builder()
                    .setForRemoval(true)
                    .setSince("9.6")
                    .setDescription("Use --solr-url instead")
                    .get())
            .argName("HOST")
            .hasArg()
            .required(false)
            .desc("Base Solr URL, which can be used to determine the zk-host if that's not known; defaults to: "
                    + getDefaultSolrUrl()
                    + '.')
            .build();
    // @formatter:on

    public static final Option OPTION_SOLRURL =
    // @formatter:off
        Option.builder("url")
            .longOpt("solr-url")
            .argName("HOST")
            .hasArg()
            .required(false)
            .desc("Base Solr URL, which can be used to determine the zk-host if that's not known; defaults to: "
                    + getDefaultSolrUrl()
                    + '.')
            .build();
    // @formatter:on

    public static final Option OPTION_VERBOSE =
    // @formatter:off
        Option.builder("v")
            .longOpt("verbose")
            .argName("verbose")
            .required(false)
            .desc("Enable more verbose command output.")
            .build();
    // @formatter:on

    public static final Option OPTION_HELP =
    // @formatter:off
        Option.builder("h")
            .longOpt("help")
            .required(false)
            .desc("Print this message.")
            .build();
    // @formatter:on

    public static final Option OPTION_RECURSE =
    // @formatter:off
        Option.builder("r")
            .longOpt("recurse")
            .argName("recurse")
            .hasArg()
            .required(false)
            .desc("Recurse (true|false), default is false.")
            .build();
    // @formatter:on

    public static final Option OPTION_CREDENTIALS =
    // @formatter:off
        Option.builder("u")
            .longOpt("credentials")
            .argName("credentials")
            .hasArg()
            .required(false)
            .desc("Credentials in the format username:password. Example: --credentials solr:SolrRocks")
            .build();
    // @formatter:on

    public static String getDefaultSolrUrl() {
        final String scheme = "http";
        final String host = "localhost";
        final String port = "8983";
        return String.format(Locale.ROOT, "%s://%s:%s", scheme.toLowerCase(Locale.ROOT), host, port);
    }

    @Test
    public void testOptions() {
        // sanity checks
        assertNotNull(DEFAULT_CONFIG_SET);
        assertNotNull(OPTION_CREDENTIALS);
        assertNotNull(OPTION_HELP);
        assertNotNull(OPTION_RECURSE);
        assertNotNull(OPTION_SOLRURL);
        assertNotNull(OPTION_SOLRURL_DEPRECATED);
        assertNotNull(OPTION_VERBOSE);
        assertNotNull(OPTION_ZKHOST);
        assertNotNull(OPTION_ZKHOST_DEPRECATED);
        assertNotNull(ZK_HOST);
        assertNotNull(getDefaultSolrUrl());
    }
}
