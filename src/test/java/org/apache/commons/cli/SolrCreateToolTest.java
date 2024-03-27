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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

public class SolrCreateToolTest {

    public List<Option> getOptions() {
        // @formatter:off
        return Arrays.asList(
            SolrCliTest.OPTION_ZKHOST,
            SolrCliTest.OPTION_SOLRURL,
            SolrCliTest.OPTION_ZKHOST_DEPRECATED,
            SolrCliTest.OPTION_SOLRURL,
            Option.builder("c")
                .longOpt("name")
                .argName("NAME")
                .hasArg()
                .required(true)
                .desc("Name of collection or core to create.")
                .build(),
            Option.builder("s")
                .longOpt("shards")
                .argName("#")
                .hasArg()
                .required(false)
                .desc("Number of shards; default is 1.")
                .build(),
            Option.builder("rf")
                .longOpt("replication-factor")
                .argName("#")
                .hasArg()
                .required(false)
                .desc("Number of copies of each document across the collection (replicas per shard); default is 1.")
                .build(),
            Option.builder("d")
                .longOpt("confdir")
                .argName("NAME")
                .hasArg()
                .required(false)
                .desc("Configuration directory to copy when creating the new collection; default is "
                        + SolrCliTest.DEFAULT_CONFIG_SET
                        + '.')
                .build(),
            Option.builder("n")
                .longOpt("confname")
                .argName("NAME")
                .hasArg()
                .required(false)
                .desc("Configuration name; default is the collection name.")
                .build(),
            SolrCliTest.OPTION_CREDENTIALS);
      // @formatter:on
    }

    private String printHelp(final HelpFormatter formatter) {
        final Options options = new Options();
        getOptions().forEach(options::addOption);
        final String cmdLineSyntax = getClass().getName();
        final StringWriter out = new StringWriter();
        final PrintWriter pw = new PrintWriter(out);
        formatter.printHelp(pw, formatter.getWidth(), cmdLineSyntax, null, options, formatter.getLeftPadding(), formatter.getDescPadding(), null, false);
        pw.flush();
        final String actual = out.toString();
        assertTrue(actual.contains("-z,--zk-host <HOST>              Zookeeper connection string; unnecessary"));
        return actual;
    }

    @Test
    public void testHelpFormatter() {
        final HelpFormatter formatter = new HelpFormatter();
        final String actual = printHelp(formatter);
        assertFalse(actual.contains("Deprecated"));
    }

    @Test
    public void testHelpFormatterDeprecated() {
        final HelpFormatter formatter = HelpFormatter.builder().setShowDeprecated(true).get();
        final String actual = printHelp(formatter);
        assertTrue(actual.contains("-zkHost,--zkHost <HOST>          [Deprecated] Zookeeper connection"));
    }
}
