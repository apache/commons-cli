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

import java.util.function.Supplier;

import org.apache.commons.cli.Util;

/**
 * The definition for styling blocks of text.  Most common usage is to style columns in a table, but may also be used to
 * specify default stylings for a {@link Serializer}.
 */
public final class TextStyle {
    /** The unset value for maxWidth */
    public static final int UNSET_MAX_WIDTH = Integer.MAX_VALUE;
    /** The alignment possibilities */
    public enum Alignment {
        /** left justify the text */
        LEFT,
        /** center the text */
        CENTER,
        /** right justify the text */
        RIGHT };
    /** Types scaling */
    public enum Scaling {
        /** do not scale */
        FIXED,
        /** text may be scaled */
        VARIABLE };

    /** the alignment */
    private final Alignment alignment;
    /** the size of the left pad.  This is placed before each line of text */
    private final int leftPad;
    /** The size of the indent on the second and any subsequent lines of text */
    private final int indent;
    /** The scaling allowed for the block */
    private final Scaling scaling;
    /** The minimum size of the text  */
    private final int minWidth;
    /** The maximum size of the text */
    private final int maxWidth;

    /**
     * The builder for the TextStyle
     */
    public static final class Builder implements Supplier<TextStyle> {
        /** the alignment */
        private Alignment alignment;
        /** the left padding */
        private int leftPad;
        /** the subsequent line indentation */
        private int indent;
        /** the scaling */
        private Scaling scaling;
        /** the minimum width */
        private int minWidth;
        /** the maximum width */
        private int maxWidth;

        /**
         * Constructor with default values of:
         * <ul>
         *     <li>alignment = LEFT</li>
         *     <li>leftPad = 0</li>
         *     <li>scaling = VARIABLE</li>
         *     <li>minWidth = 0</li>
         *     <li>maxWidth = UNSET_MAX_WIDTH</li>
         * </ul>
         */
        public Builder() {
            alignment = Alignment.LEFT;
            leftPad = 0;
            indent = 0;
            scaling = Scaling.VARIABLE;
            minWidth = 0;
            maxWidth = UNSET_MAX_WIDTH;
        }

        /**
         * Create a builder from an existing TextStyle
         * @param style the TextStyle to set all values from.
         */
        public Builder(final TextStyle style) {
            this.alignment = style.alignment;
            this.leftPad = style.leftPad;
            this.indent = style.indent;
            this.scaling = style.scaling;
            this.minWidth = style.minWidth;
            this.maxWidth = style.maxWidth;
        }

        @Override
        public TextStyle get() {
            return new TextStyle(this);
        }

        /**
         * Sets the alignment.
         * @param alignment the desired alignment.
         * @return this
         */
        public Builder setAlignment(final Alignment alignment) {
            this.alignment = alignment;
            return this;
        }

        /**
         * Sets teh left padding.
         * @param leftPad the new left padding.
         * @return this
         */
        public Builder setLeftPad(final int leftPad) {
            this.leftPad = leftPad;
            return this;
        }

        /**
         * Gets the currently specified leftPad.
         * @return The currently specified leftPad.
         */
        public int getLeftPad() {
            return leftPad;
        }

        /**
         * Sets the indent value.
         * @param indent the new indent value.
         * @return this
         */
        public Builder setIndent(final int indent) {
            this.indent = indent;
            return this;
        }

        /**
         * Gets the currently specified indent value.
         * @return The currently specified indent value.
         */
        public int getIndent() {
            return indent;
        }

        /**
         * Sets the scaling value.
         * @param scaling the new scaling value.
         * @return this.
         */
        public Builder setScaling(final Scaling scaling) {
            this.scaling = scaling;
            return this;
        }

        /**
         * Gets the currently specified scaling value.
         * @return The currently specified scaling value.
         */
        public Scaling getScaling() {
            return scaling;
        }

        /**
         * Sets the currently specified minimum width.
         * @param minWidth The currently specified minimum width.
         * @return this
         */
        public Builder setMinWidth(final int minWidth) {
            this.minWidth = minWidth;
            return this;
        }

        /**
         * Gets the currently specified minimum width value.
         * @return The currently specified minimum width value.
         */
        public int getMinWidth() {
            return minWidth;
        }

        /**
         * Sets the currently specified minimum width.
         * @param maxWidth The currently specified maximum width.
         * @return this
         */
        public Builder setMaxWidth(final int maxWidth) {
            this.maxWidth = maxWidth;
            return this;
        }

        /**
         * Gets the currently specified maximum width value.
         * @return The currently specified maximum width value.
         */
        public int getMaxWidth() {
            return maxWidth;
        }
    }

    /**
     * The default style as generated by the default Builder.
     */
    public static final TextStyle DEFAULT = new Builder().get();

    /**
     * Constructor.
     * @param builder the builder to build the text style from.
     */
    private TextStyle(final Builder builder) {
        this.alignment = builder.alignment;
        this.leftPad = builder.leftPad;
        this.indent = builder.indent;
        this.scaling = builder.scaling;
        this.minWidth = builder.minWidth;
        this.maxWidth = builder.maxWidth;
    }

    /**
     * Gets the alignment.
     * @return the alignment.
     */
    public Alignment getAlignment() {
        return alignment;
    }

    /**
     * Gets the left padding.
     * @return the left padding.
     */
    public int getLeftPad() {
        return leftPad;
    }

    /**
     * Gets the indent value.
     * @return the indent value.
     */
    public int getIndent() {
        return indent;
    }

    /**
     * Gets the scaling value.
     * @return the scaling value.
     */
    public Scaling getScaling() {
        return scaling;
    }

    /**
     * gets the minimum width.
     * @return The minimum width.
     */
    public int getMinWidth() {
        return minWidth;
    }

    /**
     * gets the maximum width.
     * @return The maximum width.
     */
    public int getMaxWidth() {
        return maxWidth;
    }

    /**
     * Pads a string to the maximum width or optionally to the maximum width - indent.
     * <ul>
     *      <li>Returns the string unchanged if it is longer than the specified length.</li>
     *      <li>Will add the padding based on the alignment.</li>
     * </ul>
     * @param addIndent if {@code true} account for the indent when padding the string.
     * @param text the text to pad.
     * @return the padded string.
     */
    public String pad(final boolean addIndent, final String text) {
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

    /**
     * Left pad the string to maximum length.  Adds spaces to the string until it reaches maximum length.
     * If the string is already at or above maximum length returns it unchanged.
     * @param orig the string to pad
     * @return the string padded to maximum length.
     */
    public String lPad(final String orig) {
        int origLen = orig == null ? 0 : orig.length();
        if (origLen >= maxWidth) {
            return orig;
        }
        return Util.createPadding(maxWidth - origLen) + Util.defaultValue(orig, "");
    }

    /**
     * Right pad the string to maximum length.  Adds spaces to the string until it reaches maximum length.
     * If the string is already at or above maximum length returns it unchanged.
     * @param orig the string to pad
     * @return the string padded to maximum length.
     */
    public String rPad(final String orig) {
        int origLen = orig == null ? 0 : orig.length();
        if (origLen >= maxWidth) {
            return orig;
        }
        return Util.defaultValue(orig, "") + Util.createPadding(maxWidth - origLen);
    }
}
