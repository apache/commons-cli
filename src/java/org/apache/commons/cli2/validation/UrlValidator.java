/**
 * Copyright 2003-2004 The Apache Software Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.cli2.validation;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.ListIterator;

/**
 * A Validator instance that accepts urls
 */
public class UrlValidator implements Validator {

    private String protocol = null;

    public void validate(final List values) throws InvalidArgumentException {
        for (final ListIterator i = values.listIterator(); i.hasNext();) {
            final String name = (String)i.next();
            try {
                final URL url = new URL(name);

                if (protocol != null && !protocol.equals(url.getProtocol())) {
                    throw new InvalidArgumentException(name);
                }

                i.set(url);
            }
            catch (final MalformedURLException mue) {
                throw new InvalidArgumentException(
                    "Cannot understand url: " + name);
            }
        }
    }

    /**
     * @return the protocol that must be used by a valid url
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * @param protocol the protocol that a valid url must use
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
