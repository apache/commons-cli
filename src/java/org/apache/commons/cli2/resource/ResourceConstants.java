/*
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
package org.apache.commons.cli2.resource;

public abstract class ResourceConstants {
    public static final String CLASSVALIDATOR_BAD_CLASSNAME = "ClassValidator.bad.classname";
    public static final String CLASSVALIDATOR_CLASS_NOTFOUND = "ClassValidator.class.notfound";
    public static final String CLASSVALIDATOR_CLASS_ACCESS = "ClassValidator.class.access";
    public static final String CLASSVALIDATOR_CLASS_CREATE = "ClassValidator.class.create";
    public static final String DATEVALIDATOR_DATE_OUTOFRANGE = "DateValidator.date.OutOfRange";
    public static final String URLVALIDATOR_MALFORMED_URL = "URLValidator.malformed.URL";
    public static final String NUMBERVALIDATOR_NUMBER_OUTOFRANGE =
        "NumberValidator.number.OutOfRange";
    public static final String ARGUMENT_UNEXPECTED_VALUE = "Argument.unexpected.value";
    public static final String ARGUMENT_MIN_EXCEEDS_MAX = "Argument.minimum.exceeds.maximum";
    public static final String ARGUMENT_TOO_FEW_DEFAULTS = "Argument.too.few.defaults";
    public static final String ARGUMENT_TOO_MANY_DEFAULTS = "Argument.too.many.defaults";
    public static final String ARGUMENT_MISSING_VALUES = "Argument.missing.values";
    public static final String ARGUMENT_TOO_MANY_VALUES = "Argument.too.many.values";
    public static final String OPTION_TRIGGER_NEEDS_PREFIX = "Option.trigger.needs.prefix";
    public static final String OPTION_MISSING_REQUIRED = "Option.missing.required";
    public static final String OPTION_NO_NAME = "Option.no.name";
    public static final String OPTION_ILLEGAL_LONG_PREFIX = "Option.illegal.long.prefix";
    public static final String OPTION_ILLEGAL_SHORT_PREFIX = "Option.illegal.short.prefix";
    public static final String UNEXPECTED_TOKEN = "Unexpected.token";
    public static final String MISSING_OPTION = "Missing.option";
    public static final String CANNOT_BURST = "Cannot.burst";
    public static final String COMMAND_PREFERRED_NAME_TOO_SHORT = "Command.preferredName.too.short";
    public static final String SWITCH_ILLEGAL_ENABLED_PREFIX = "Option.illegal.enabled.prefix";
    public static final String SWITCH_ILLEGAL_DISABLED_PREFIX = "Option.illegal.disabled.prefix";
    public static final String SWITCH_IDENTICAL_PREFIXES = "Option.identical.prefixes";
    public static final String SWITCH_ALREADY_SET = "Switch.already.set";
    public static final String SWITCH_NO_ENABLED_PREFIX = "Switch.no.enabledPrefix";
    public static final String SWITCH_NO_DISABLED_PREFIX = "Switch.no.disabledPrefix";
    public static final String SWITCH_ENABLED_STARTS_WITH_DISABLED =
        "Switch.enabled.startsWith.disabled";
    public static final String SWITCH_DISABLED_STARTWS_WITH_ENABLED =
        "Switch.disabled.startsWith.enabled";
    public static final String SWITCH_PREFERRED_NAME_TOO_SHORT = "Switch.preferredName.too.short";
    public static final String SOURCE_DEST_MUST_ENFORCE_VALUES = "SourceDest.must.enforce.values";
    public static final String HELPFORMATTER_GUTTER_TOO_LONG = "HelpFormatter.gutter.too.long";
    public static final String HELPFORMATTER_WIDTH_TOO_NARROW = "HelpFormatter.width.too.narrow";
    public static final String ENUM_ILLEGAL_VALUE = "Enum.illegal.value";
}
