/*
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      https://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package org.apache.commons.cli;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * Tests {@link ParseException}.
 */
class ParseExceptionTest {

    @Test
    void testConstructor() {
        assertEquals("a", new ParseException("a").getMessage());
        final Throwable t = new IOException();
        assertEquals(t, new ParseException(t).getCause());
        assertEquals(t, ParseException.wrap(t).getCause());
        final ParseException pe = new ParseException("A");
        assertEquals(pe, ParseException.wrap(pe));
    }
}
