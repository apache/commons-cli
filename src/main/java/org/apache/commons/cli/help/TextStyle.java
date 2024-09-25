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

import org.apache.commons.cli.Util;

import java.util.function.Supplier;

/**
 * The definition of a table header.  Specifies alignment, padding indent and the content of the header text.
 * This Definition is used to format the columns in the table.
 */
public final class TextStyle {
    public final static int UNSET = Integer.MAX_VALUE;
    public enum Alignment {LEFT, CENTER, RIGHT};
    public enum Scaling {FIXED, VARIABLE};
    final Alignment alignment;
    final int leftPad;
    final int indent;
    final Scaling scaling;
    final int minWidth;
    final int maxWidth;

    public static final class Builder implements Supplier<TextStyle> {
        private Alignment alignment;
        private int leftPad;
        private int indent;
        private Scaling scaling;
        private int minWidth;
        private int maxWidth;

        public Builder() {
            alignment = Alignment.LEFT;
            leftPad = 0;
            indent = 0;
            scaling = Scaling.VARIABLE;
            minWidth = 0;
            maxWidth = UNSET;
        }

        public Builder(TextStyle style) {
            this.alignment = style.alignment;
            this.leftPad = style.leftPad;
            this.indent = style.indent;
            this.scaling = style.scaling;
            this.minWidth = style.minWidth;
            this.maxWidth = style.maxWidth;
        }

        public TextStyle get() {
            return new TextStyle(this);
        }

        public Builder setAlignment(Alignment alignment) {
            this.alignment = alignment;
            return this;
        }

        public Builder setLeftPad(int leftPad) {
            this.leftPad = leftPad;
            return this;
        }

        public int getLeftPad() {
            return leftPad;
        }

        public Builder setIndent(int indent) {
            this.indent = indent;
            return this;
        }

        public int getIndent() {
            return indent;
        }

        public Builder setScaling(Scaling scaling) {
            this.scaling = scaling;
            return this;
        }

        public Scaling getScaling() {
            return scaling;
        }

        public Builder setMinWidth(int minWidth) {
            this.minWidth = minWidth;
            return this;
        }

        public int getMinWidth() {
            return minWidth;
        }

        public Builder setMaxWidth(int maxWidth) {
            this.maxWidth = maxWidth;
            return this;
        }

        public int getMaxWidth() {
            return maxWidth;
        }
    }

    public static TextStyle DEFAULT = new Builder().get();

    public TextStyle(Builder builder) {
        this.alignment = builder.alignment;
        this.leftPad = builder.leftPad;
        this.indent = builder.indent;
        this.scaling = builder.scaling;
        this.minWidth = builder.minWidth;
        this.maxWidth = builder.maxWidth;
    }

    public Alignment getAlignment() {
        return alignment;
    }

    public int getLeftPad() {
        return leftPad;
    }

    public int getIndent() {
        return indent;
    }

    public Scaling getScaling() {
        return scaling;
    }

    public int getMinWidth() {
        return minWidth;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public String pad(boolean addIndent, String text) {
        if (text.length() >= maxWidth) {
            return text;
        }
        String indentPad = addIndent ? Util.createPadding(indent) : "";
        int restLen = maxWidth - text.length();
        if (addIndent) {
            restLen -= indent;
        }
        if (restLen < 0) {
            restLen = maxWidth - text.length();
            indentPad = "";
        }
        String rest = Util.createPadding(restLen);
        StringBuilder sb = new StringBuilder();
        switch (alignment) {
            case LEFT:
                sb.append(indentPad).append(text).append(rest);
                break;
            case RIGHT:
                sb.append(indentPad).append(rest).append(text);
                break;
            case CENTER:
                int padLen = maxWidth - text.length();
                int left = padLen / 2;
                indentPad = Util.createPadding(left);
                rest = Util.createPadding(padLen - left);
                sb.append(indentPad).append(text).append(rest);
                break;
        }
        return sb.toString();
    }

    public String lPad(String orig) {
        int origLen = orig == null ? 0 : orig.length();
        if (origLen >= maxWidth) {
            return orig;
        }
        return Util.createPadding(maxWidth-origLen) + Util.defaultValue(orig, "");
    }

    public String rPad(String orig) {
        int origLen = orig == null ? 0 : orig.length();
        if (origLen >= maxWidth) {
            return orig;
        }
        return Util.defaultValue(orig, "") + Util.createPadding(maxWidth - origLen);
    }

}
