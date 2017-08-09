/*
 * Copyright (c) 2003, 2007, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * Portions Copyright IBM Corporation, 1997, 2001. All Rights Reserved.
 */
package org.firas.math

/**
 * Immutable objects which encapsulate the context settings which
 * describe certain rules for numerical operators, such as those
 * implemented by the {@link BigDecimal} class.
 *
 * <p>The base-independent settings are:
 * <ol>
 * <li>{@code precision}:
 * the number of digits to be used for an operation; results are
 * rounded to this precision
 *
 * <li>{@code roundingMode}:
 * a {@link RoundingMode} object which specifies the algorithm to be
 * used for rounding.
 * </ol>
 *
 * @see     BigDecimal
 * @see     RoundingMode
 * @author  Mike Cowlishaw
 * @author  Joseph D. Darcy
 */
class MathContext(val precision: Int, val roundingMode: RoundingMode) {

    companion object {

        /**
         * A `MathContext` object whose settings have the values
         * required for unlimited precision arithmetic.
         * The values of the settings are:
         * `
         * precision=0 roundingMode=HALF_UP
        ` *
         */
        val UNLIMITED = MathContext(0, RoundingMode.HALF_UP)

        /**
         * A `MathContext` object with a precision setting
         * matching the IEEE 754R Decimal32 format, 7 digits, and a
         * rounding mode of [HALF_EVEN][RoundingMode.HALF_EVEN], the
         * IEEE 754R default.
         */
        val DECIMAL32 = MathContext(7, RoundingMode.HALF_EVEN)

        /**
         * A `MathContext` object with a precision setting
         * matching the IEEE 754R Decimal64 format, 16 digits, and a
         * rounding mode of [HALF_EVEN][RoundingMode.HALF_EVEN], the
         * IEEE 754R default.
         */
        val DECIMAL64 = MathContext(16, RoundingMode.HALF_EVEN)

        /**
         * A `MathContext` object with a precision setting
         * matching the IEEE 754R Decimal128 format, 34 digits, and a
         * rounding mode of [HALF_EVEN][RoundingMode.HALF_EVEN], the
         * IEEE 754R default.
         */
        val DECIMAL128 = MathContext(34, RoundingMode.HALF_EVEN)

        // defaults for constructors
        private val DEFAULT_DIGITS = 9
        private val DEFAULT_ROUNDINGMODE = RoundingMode.HALF_UP
        // Smallest values for digits (Maximum is Integer.MAX_VALUE)
        private val MIN_DIGITS = 0
    }

    init {
        if (precision < MIN_DIGITS) throw IllegalArgumentException("Digits < 0")
    }

    constructor(precision: Int): this(precision, DEFAULT_ROUNDINGMODE)

    /**
     * Compares this {@code MathContext} with the specified
     * {@code Object} for equality.
     *
     * @param  x {@code Object} to which this {@code MathContext} is to
     *         be compared.
     * @return {@code true} if and only if the specified {@code Object} is
     *         a {@code MathContext} object which has exactly the same
     *         settings as this object
     */
    override fun equals(other: Any?): Boolean {
        if (other is MathContext) {
            return precision == other.precision && roundingMode == other.roundingMode
        }
        return false
    }

    /**
     * Returns the hash code for this {@code MathContext}.
     *
     * @return hash code for this {@code MathContext}
     */
    override fun hashCode(): Int {
        return precision + roundingMode.hashCode() * 59
    }

    /**
     * Returns the string representation of this {@code MathContext}.
     * The {@code String} returned represents the settings of the
     * {@code MathContext} object as two space-delimited words
     * (separated by a single space character, <tt>'&#92;u0020'</tt>,
     * and with no leading or trailing white space), as follows:
     * <ol>
     * <li>
     * The string {@code "precision="}, immediately followed
     * by the value of the precision setting as a numeric string as if
     * generated by the {@link Integer#toString(int) Integer.toString}
     * method.
     *
     * <li>
     * The string {@code "roundingMode="}, immediately
     * followed by the value of the {@code roundingMode} setting as a
     * word.  This word will be the same as the name of the
     * corresponding public constant in the {@link RoundingMode}
     * enum.
     * </ol>
     * <p>
     * For example:
     * <pre>
     * precision=9 roundingMode=HALF_UP
     * </pre>
     *
     * Additional words may be appended to the result of
     * {@code toString} in the future if more properties are added to
     * this class.
     *
     * @return a {@code String} representing the context settings
     */
    override fun toString(): String {
        return "precision=$precision roundingMode=$roundingMode"
    }
}