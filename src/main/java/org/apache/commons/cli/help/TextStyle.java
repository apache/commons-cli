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
 * The definition for styling recommendations blocks of text.  Most common usage is to style columns in a table, but may also be used to
 * specify default styling for a {@link HelpWriter}.  HelpWriters are free to ignore the TextStyle recommendations
 * particularly where they are not supported or contradict common usage.
 * @since 1.10.0
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
        RIGHT }

    /** the alignment */
    private final Alignment alignment;
    /** the size of the left pad.  This is placed before each line of text */
    private final int leftPad;
    /** The size of the indent on the second and any subsequent lines of text */
    private final int indent;
    /** The scaling allowed for the block */
    private final boolean scalable;
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
        /** the scalable flag. Identifies text blocks that can be made narrower or wider as needed by the HelpWriter. */
        private boolean scalable;
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
            scalable = true;
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
            this.scalable = style.scalable;
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
         * Specifies if the column can be made wider or to narrower width to fit constraints of the HelpWriter and
         * formatting.
         * @param scalable if {@code true} the text width can be adjusted.
         * @return this.
         */
        public Builder setScalable(final boolean scalable) {
            this.scalable = scalable;
            return this;
        }

        /**
         * Specifies if the column can be made wider or to narrower width to fit constraints of the HelpWriter and
         * formatting.
         * @return The currently specified scaling value.
         */
        public boolean isScalable() {
            return scalable;
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
        this.scalable = builder.scalable;
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
     * Specifies if the column can be made wider or to narrower width to fit constraints of the HelpWriter and
     * formatting.
     * @return the scaling value.
     */
    public boolean isScalable() {
        return scalable;
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

    @Override
    public String toString() {
        return String.format("TextStyle{%s, l:%s, i:%s, %s, min:%s, max:%s}",
                alignment, leftPad, indent, scalable, minWidth, maxWidth == UNSET_MAX_WIDTH ? "unset" : maxWidth);
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
    public CharSequence pad(final boolean addIndent, final CharSequence text) {
        if (text.length() >= maxWidth) {
            return text;
        }
        String indentPad;
        String rest;
        StringBuilder sb = new StringBuilder();
        switch (alignment) {
            case CENTER:
                int padLen;
                if (maxWidth == UNSET_MAX_WIDTH) {
                    padLen = addIndent ? indent : 0;
                } else {
                    padLen = maxWidth - text.length();
                }
                int left = padLen / 2;
                indentPad = Util.createPadding(left);
                rest = Util.createPadding(padLen - left);
                sb.append(indentPad).append(text).append(rest);
                break;
            case LEFT:
            case RIGHT:
            default: // default should never happen.  It is here to keep code coverage happy.
                if (maxWidth == UNSET_MAX_WIDTH) {
                    indentPad = addIndent ? Util.createPadding(indent) : "";
                    rest = "";
                } else {
                    int restLen = maxWidth - text.length();
                    if (addIndent && restLen > indent) {
                        indentPad = Util.createPadding(indent);
                        restLen -= indent;
                    } else {
                        indentPad = "";
                    }
                    rest = Util.createPadding(restLen);
                }

                if  (alignment == Alignment.LEFT) {
                    sb.append(indentPad).append(text).append(rest);
                } else {
                    sb.append(indentPad).append(rest).append(text);
                }
                break;
        }
        return sb.toString();
    }
}
