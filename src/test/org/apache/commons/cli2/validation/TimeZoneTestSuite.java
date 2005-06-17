/**
 * Copyright 2005 The Apache Software Foundation
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

import java.util.TimeZone;

import junit.extensions.TestDecorator;

import junit.framework.Test;
import junit.framework.TestResult;

public class TimeZoneTestSuite
    extends TestDecorator {
    private final TimeZone timeZone;
    private final TimeZone originalTimeZone;

    public TimeZoneTestSuite(String timeZone,
                             Test test) {
        super(test);
        this.timeZone = TimeZone.getTimeZone(timeZone);
        this.originalTimeZone = TimeZone.getDefault();
    }

    public void run(TestResult testResult) {
        try {
            TimeZone.setDefault(timeZone);
            super.run(testResult);
        } finally {
            TimeZone.setDefault(originalTimeZone); // cleanup
        }
    }
}
