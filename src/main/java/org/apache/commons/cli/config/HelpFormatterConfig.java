package org.apache.commons.cli.config;

import org.apache.commons.cli.Option;

import java.io.PrintWriter;
import java.util.Objects;
import java.util.function.Function;

public class HelpFormatterConfig {
    private static final Function<Option, String> DEFAULT_DEPRECATED_FORMAT = o -> "[Deprecated] " + getDescription(o);
    private Function<Option, String> deprecatedFormatFunction = DEFAULT_DEPRECATED_FORMAT;
    private PrintWriter printStream = createDefaultPrintWriter();
    private boolean showSince;

    public HelpFormatterConfig setPrintWriter(PrintWriter printWriter) {
        this.printStream = Objects.requireNonNull(printWriter, "printWriter");
        return this;
    }

    public HelpFormatterConfig setShowDeprecated(boolean useDefaultFormat) {
        return setShowDeprecated(useDefaultFormat ? DEFAULT_DEPRECATED_FORMAT : null);
    }

    public HelpFormatterConfig setShowDeprecated(Function<Option, String> deprecatedFormatFunction) {
        this.deprecatedFormatFunction = deprecatedFormatFunction;
        return this;
    }

    public HelpFormatterConfig setShowSince(boolean showSince) {
        this.showSince = showSince;
        return this;
    }

    // Getters for the fields to be used by Builder
    public Function<Option, String> getDeprecatedFormatFunction() {
        return deprecatedFormatFunction;
    }

    public PrintWriter getPrintStream() {
        return printStream;
    }

    public boolean isShowSince() {
        return showSince;
    }

    private static PrintWriter createDefaultPrintWriter() {
        return new PrintWriter(System.out);
    }

    public static String getDescription(final Option option) {
        final String desc = option.getDescription();
        return desc == null ? "" : desc;
    }
}

