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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class DeprecatedAttributesTest {

    @Test
    public void testBuilderNonDefaults() {
        // @formatter:off
        final DeprecatedAttributes value = DeprecatedAttributes.builder()
                .setDescription("Use Bar instead!")
                .setForRemoval(true)
                .setSince("2.0")
                .get();
        // @formatter:on
        assertEquals("Use Bar instead!", value.getDescription());
        assertEquals("2.0", value.getSince());
        assertEquals(true, value.isForRemoval());
    }

    @Test
    public void testBuilderNonDefaultsToString() {
        // @formatter:off
        assertEquals("Deprecated for removal since 2.0: Use Bar instead!", DeprecatedAttributes.builder()
                .setDescription("Use Bar instead!")
                .setForRemoval(true)
                .setSince("2.0")
                .get().toString());
        assertEquals("Deprecated for removal: Use Bar instead!", DeprecatedAttributes.builder()
                .setDescription("Use Bar instead!")
                .setForRemoval(true)
                .get().toString());
        assertEquals("Deprecated since 2.0: Use Bar instead!",
                DeprecatedAttributes.builder()
                .setDescription("Use Bar instead!")
                .setSince("2.0")
                .get().toString());
        assertEquals("Deprecated: Use Bar instead!", DeprecatedAttributes.builder()
                .setDescription("Use Bar instead!")
                .get().toString());
        // @formatter:on
    }

    @Test
    public void testDefaultBuilder() {
        final DeprecatedAttributes defaultValue = DeprecatedAttributes.builder().get();
        assertEquals(DeprecatedAttributes.DEFAULT.getDescription(), defaultValue.getDescription());
        assertEquals(DeprecatedAttributes.DEFAULT.getSince(), defaultValue.getSince());
        assertEquals(DeprecatedAttributes.DEFAULT.isForRemoval(), defaultValue.isForRemoval());
    }

    @Test
    public void testDefaultToString() {
        assertEquals("Deprecated", DeprecatedAttributes.DEFAULT.toString());
    }
}
