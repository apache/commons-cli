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

import org.apache.commons.beanutils.converters.AbstractConverter;

/**
 * A simple converter that takes a Func and the datatype to create a Converter
 * for the BeanUtils.
 *
 * @param <T> The type of object the converter will return.
 */
public class SimpleConverter<T> extends AbstractConverter {

    private final Func<? extends T> func;
    private final Class<T> type;

    public SimpleConverter(Func<? extends T> func, Class<T> type) {
        this.func = func;
        this.type = type;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected <T2> T2 convertToType(Class<T2> type, Object value) throws Throwable {
        if (this.type.equals(type)) {
            return (T2) func.apply(value.toString());
        }

        throw conversionException(type, value);
    }

    @Override
    protected Class<?> getDefaultType() {
        return type;
    }
}
