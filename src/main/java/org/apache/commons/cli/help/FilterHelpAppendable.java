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
package org.apache.commons.cli.help;

import java.io.IOException;

/**
 * An abstract implementation of {@link HelpAppendable} that writes output to an {@link Appendable} instance.
 *
 * @since 1.10.0
 */
public abstract class FilterHelpAppendable implements HelpAppendable {

    /**
     * The Appendable instance to write to.
     */
    protected final Appendable output;

    /**
     * Constructs an instance using the provided Appendable instance.
     *
     * @param output the Appendable instance to write to.
     */
    protected FilterHelpAppendable(final Appendable output) {
        this.output = output;
    }

    @Override
    public Appendable append(final char ch) throws IOException {
        output.append(ch);
        return this;
    }

    @Override
    public Appendable append(final CharSequence text) throws IOException {
        output.append(text);
        return this;
    }

    @Override
    public Appendable append(final CharSequence csq, final int start, final int end) throws IOException {
        output.append(csq, start, end);
        return this;
    }
}
