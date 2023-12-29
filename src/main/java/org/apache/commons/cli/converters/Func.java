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
package org.apache.commons.cli.converters;

/**
 * The definition of the functional interface to call when doing a conversion.
 * Like {@code Function<String,T>} but can throw an Exception.
 *
 * @param <T> The return type for the function.
 */
@FunctionalInterface
public interface Func<T> {
    /**
     * Applies the conversion function to the String argument.
     * @param str the String to convert
     * @return the Object from the conversion.
     * @throws Exception on error.
     */
    T apply(String str) throws Exception;
}
