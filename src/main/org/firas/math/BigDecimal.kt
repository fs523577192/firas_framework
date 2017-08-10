/*
 * Copyright (c) 1996, 2013, Oracle and/or its affiliates. All rights reserved.
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
 * Portions Copyright IBM Corporation, 2001. All Rights Reserved.
 */
package org.firas.math

import org.firas.lang.ArithmeticException
import org.firas.lang.Integer
import org.firas.lang.Math


/**
 *  Immutable, arbitrary-precision signed decimal numbers.  A
 * {@code BigDecimal} consists of an arbitrary precision integer
 * <i>unscaled value</i> and a 32-bit integer <i>scale</i>.  If zero
 * or positive, the scale is the number of digits to the right of the
 * decimal point.  If negative, the unscaled value of the number is
 * multiplied by ten to the power of the negation of the scale.  The
 * value of the number represented by the {@code BigDecimal} is
 * therefore <tt>(unscaledValue &times; 10<sup>-scale</sup>)</tt>.
 *
 * <p>The {@code BigDecimal} class provides operations for
 * arithmetic, scale manipulation, rounding, comparison, hashing, and
 * format conversion.  The {@link #toString} method provides a
 * canonical representation of a {@code BigDecimal}.
 *
 * <p>The {@code BigDecimal} class gives its user complete control
 * over rounding behavior.  If no rounding mode is specified and the
 * exact result cannot be represented, an exception is thrown;
 * otherwise, calculations can be carried out to a chosen precision
 * and rounding mode by supplying an appropriate {@link MathContext}
 * object to the operation.  In either case, eight <em>rounding
 * modes</em> are provided for the control of rounding.  Using the
 * integer fields in this class (such as {@link #ROUND_HALF_UP}) to
 * represent rounding mode is largely obsolete; the enumeration values
 * of the {@code RoundingMode} {@code enum}, (such as {@link
 * RoundingMode#HALF_UP}) should be used instead.
 *
 * <p>When a {@code MathContext} object is supplied with a precision
 * setting of 0 (for example, {@link MathContext#UNLIMITED}),
 * arithmetic operations are exact, as are the arithmetic methods
 * which take no {@code MathContext} object.  (This is the only
 * behavior that was supported in releases prior to 5.)  As a
 * corollary of computing the exact result, the rounding mode setting
 * of a {@code MathContext} object with a precision setting of 0 is
 * not used and thus irrelevant.  In the case of divide, the exact
 * quotient could have an infinitely long decimal expansion; for
 * example, 1 divided by 3.  If the quotient has a nonterminating
 * decimal expansion and the operation is specified to return an exact
 * result, an {@code ArithmeticException} is thrown.  Otherwise, the
 * exact result of the division is returned, as done for other
 * operations.
 *
 * <p>When the precision setting is not 0, the rules of
 * {@code BigDecimal} arithmetic are broadly compatible with selected
 * modes of operation of the arithmetic defined in ANSI X3.274-1996
 * and ANSI X3.274-1996/AM 1-2000 (section 7.4).  Unlike those
 * standards, {@code BigDecimal} includes many rounding modes, which
 * were mandatory for division in {@code BigDecimal} releases prior
 * to 5.  Any conflicts between these ANSI standards and the
 * {@code BigDecimal} specification are resolved in favor of
 * {@code BigDecimal}.
 *
 * <p>Since the same numerical value can have different
 * representations (with different scales), the rules of arithmetic
 * and rounding must specify both the numerical result and the scale
 * used in the result's representation.
 *
 *
 * <p>In general the rounding modes and precision setting determine
 * how operations return results with a limited number of digits when
 * the exact result has more digits (perhaps infinitely many in the
 * case of division) than the number of digits returned.
 *
 * First, the
 * total number of digits to return is specified by the
 * {@code MathContext}'s {@code precision} setting; this determines
 * the result's <i>precision</i>.  The digit count starts from the
 * leftmost nonzero digit of the exact result.  The rounding mode
 * determines how any discarded trailing digits affect the returned
 * result.
 *
 * <p>For all arithmetic operators , the operation is carried out as
 * though an exact intermediate result were first calculated and then
 * rounded to the number of digits specified by the precision setting
 * (if necessary), using the selected rounding mode.  If the exact
 * result is not returned, some digit positions of the exact result
 * are discarded.  When rounding increases the magnitude of the
 * returned result, it is possible for a new digit position to be
 * created by a carry propagating to a leading {@literal "9"} digit.
 * For example, rounding the value 999.9 to three digits rounding up
 * would be numerically equal to one thousand, represented as
 * 100&times;10<sup>1</sup>.  In such cases, the new {@literal "1"} is
 * the leading digit position of the returned result.
 *
 * <p>Besides a logical exact result, each arithmetic operation has a
 * preferred scale for representing a result.  The preferred
 * scale for each operation is listed in the table below.
 *
 * <table border>
 * <caption><b>Preferred Scales for Results of Arithmetic Operations
 * </b></caption>
 * <tr><th>Operation</th><th>Preferred Scale of Result</th></tr>
 * <tr><td>Add</td><td>max(addend.scale(), augend.scale())</td>
 * <tr><td>Subtract</td><td>max(minuend.scale(), subtrahend.scale())</td>
 * <tr><td>Multiply</td><td>multiplier.scale() + multiplicand.scale()</td>
 * <tr><td>Divide</td><td>dividend.scale() - divisor.scale()</td>
 * </table>
 *
 * These scales are the ones used by the methods which return exact
 * arithmetic results; except that an exact divide may have to use a
 * larger scale since the exact result may have more digits.  For
 * example, {@code 1/32} is {@code 0.03125}.
 *
 * <p>Before rounding, the scale of the logical exact intermediate
 * result is the preferred scale for that operation.  If the exact
 * numerical result cannot be represented in {@code precision}
 * digits, rounding selects the set of digits to return and the scale
 * of the result is reduced from the scale of the intermediate result
 * to the least scale which can represent the {@code precision}
 * digits actually returned.  If the exact result can be represented
 * with at most {@code precision} digits, the representation
 * of the result with the scale closest to the preferred scale is
 * returned.  In particular, an exactly representable quotient may be
 * represented in fewer than {@code precision} digits by removing
 * trailing zeros and decreasing the scale.  For example, rounding to
 * three digits using the {@linkplain RoundingMode#FLOOR floor}
 * rounding mode, <br>
 *
 * {@code 19/100 = 0.19   // integer=19,  scale=2} <br>
 *
 * but<br>
 *
 * {@code 21/110 = 0.190  // integer=190, scale=3} <br>
 *
 * <p>Note that for add, subtract, and multiply, the reduction in
 * scale will equal the number of digit positions of the exact result
 * which are discarded. If the rounding causes a carry propagation to
 * create a new high-order digit position, an additional digit of the
 * result is discarded than when no new digit position is created.
 *
 * <p>Other methods may have slightly different rounding semantics.
 * For example, the result of the {@code pow} method using the
 * {@linkplain #pow(int, MathContext) specified algorithm} can
 * occasionally differ from the rounded mathematical result by more
 * than one unit in the last place, one <i>{@linkplain #ulp() ulp}</i>.
 *
 * <p>Two types of operations are provided for manipulating the scale
 * of a {@code BigDecimal}: scaling/rounding operations and decimal
 * point motion operations.  Scaling/rounding operations ({@link
 * #setScale setScale} and {@link #round round}) return a
 * {@code BigDecimal} whose value is approximately (or exactly) equal
 * to that of the operand, but whose scale or precision is the
 * specified value; that is, they increase or decrease the precision
 * of the stored number with minimal effect on its value.  Decimal
 * point motion operations ({@link #movePointLeft movePointLeft} and
 * {@link #movePointRight movePointRight}) return a
 * {@code BigDecimal} created from the operand by moving the decimal
 * point a specified distance in the specified direction.
 *
 * <p>For the sake of brevity and clarity, pseudo-code is used
 * throughout the descriptions of {@code BigDecimal} methods.  The
 * pseudo-code expression {@code (i + j)} is shorthand for "a
 * {@code BigDecimal} whose value is that of the {@code BigDecimal}
 * {@code i} added to that of the {@code BigDecimal}
 * {@code j}." The pseudo-code expression {@code (i == j)} is
 * shorthand for "{@code true} if and only if the
 * {@code BigDecimal} {@code i} represents the same value as the
 * {@code BigDecimal} {@code j}." Other pseudo-code expressions
 * are interpreted similarly.  Square brackets are used to represent
 * the particular {@code BigInteger} and scale pair defining a
 * {@code BigDecimal} value; for example [19, 2] is the
 * {@code BigDecimal} numerically equal to 0.19 having a scale of 2.
 *
 * <p>Note: care should be exercised if {@code BigDecimal} objects
 * are used as keys in a {@link java.util.SortedMap SortedMap} or
 * elements in a {@link java.util.SortedSet SortedSet} since
 * {@code BigDecimal}'s <i>natural ordering</i> is <i>inconsistent
 * with equals</i>.  See {@link Comparable}, {@link
 * java.util.SortedMap} or {@link java.util.SortedSet} for more
 * information.
 *
 * <p>All methods and constructors for this class throw
 * {@code NullPointerException} when passed a {@code null} object
 * reference for any input parameter.
 *
 * @see     BigInteger
 * @see     MathContext
 * @see     RoundingMode
 * @see     java.util.SortedMap
 * @see     java.util.SortedSet
 * @author  Josh Bloch
 * @author  Mike Cowlishaw
 * @author  Joseph D. Darcy
 * @author  Sergey V. Kuksenko
 */
class BigDecimal internal constructor(
        private val intVal: BigInteger?,
        private val intCompact: Long,
        val scale: Int,
        private var precision: Int): Number() {

    companion object {
        /**
         * Sentinel value for {@link #intCompact} indicating the
         * significand information is only available from {@code intVal}.
         */
        val INFLATED = Long.MIN_VALUE
        val INFLATED_BIGINT = BigInteger.valueOf(INFLATED)

        // All 18-digit base ten strings fit into a long; not all 19-digit
        // strings will
        val MAX_COMPACT_DIGITS = 18

        val HALF_LONG_MAX_VALUE = Long.MAX_VALUE / 2
        val HALF_LONG_MIN_VALUE = Long.MIN_VALUE / 2

        val zeroThroughTen = arrayOf(
                BigDecimal(BigInteger.ZERO, 0, 0, 1),
                BigDecimal(BigInteger.ONE, 1, 0, 1),
                BigDecimal(BigInteger.valueOf(2), 2, 0, 1),
                BigDecimal(BigInteger.valueOf(3), 3, 0, 1),
                BigDecimal(BigInteger.valueOf(4), 4, 0, 1),
                BigDecimal(BigInteger.valueOf(5), 5, 0, 1),
                BigDecimal(BigInteger.valueOf(6), 6, 0, 1),
                BigDecimal(BigInteger.valueOf(7), 7, 0, 1),
                BigDecimal(BigInteger.valueOf(8), 8, 0, 1),
                BigDecimal(BigInteger.valueOf(9), 9, 0, 1),
                BigDecimal(BigInteger.TEN, 10, 0, 2)
        )

        val ZERO_SCALED_BY = arrayOf(
                zeroThroughTen[0],
                BigDecimal(BigInteger.ZERO, 0, 1, 1),
                BigDecimal(BigInteger.ZERO, 0, 2, 1),
                BigDecimal(BigInteger.ZERO, 0, 3, 1),
                BigDecimal(BigInteger.ZERO, 0, 4, 1),
                BigDecimal(BigInteger.ZERO, 0, 5, 1),
                BigDecimal(BigInteger.ZERO, 0, 6, 1),
                BigDecimal(BigInteger.ZERO, 0, 7, 1),
                BigDecimal(BigInteger.ZERO, 0, 8, 1),
                BigDecimal(BigInteger.ZERO, 0, 9, 1),
                BigDecimal(BigInteger.ZERO, 0, 10, 1),
                BigDecimal(BigInteger.ZERO, 0, 11, 1),
                BigDecimal(BigInteger.ZERO, 0, 12, 1),
                BigDecimal(BigInteger.ZERO, 0, 13, 1),
                BigDecimal(BigInteger.ZERO, 0, 14, 1),
                BigDecimal(BigInteger.ZERO, 0, 15, 1)
        )

        /**
         * Translates a {@code long} value into a {@code BigDecimal}
         * with a scale of zero.  This {@literal "static factory method"}
         * is provided in preference to a ({@code long}) constructor
         * because it allows for reuse of frequently used
         * {@code BigDecimal} values.
         *
         * @param val value of the {@code BigDecimal}.
         * @return a {@code BigDecimal} whose value is {@code val}.
         */
        fun valueOf(v: Long): BigDecimal {
            if (INFLATED != v) return BigDecimal(null, v, 0, 0)
            return BigDecimal(INFLATED_BIGINT, v, 0, 0)
        }

        /**
         * Translates a {@code long} unscaled value and an
         * {@code int} scale into a {@code BigDecimal}.  This
         * {@literal "static factory method"} is provided in preference to
         * a ({@code long}, {@code int}) constructor because it
         * allows for reuse of frequently used {@code BigDecimal} values..
         *
         * @param unscaledVal unscaled value of the {@code BigDecimal}.
         * @param scale scale of the {@code BigDecimal}.
         * @return a {@code BigDecimal} whose value is
         *         <tt>(unscaledVal &times; 10<sup>-scale</sup>)</tt>.
         */
        fun valueOf(unscaledVal: Long, scale: Int): BigDecimal {
            if (0 == scale) return valueOf(unscaledVal)
            if (0L == unscaledVal) return zeroValueOf(scale)
            return BigDecimal(if (unscaledVal == INFLATED) INFLATED_BIGINT else null,
                    unscaledVal, scale, 0)
        }

        internal fun valueOf(unscaledVal: Long, scale: Int, precision: Int): BigDecimal {
            if (scale == 0 && unscaledVal >= 0 && unscaledVal < zeroThroughTen.size) {
                return zeroThroughTen[unscaledVal.toInt()]
            }
            if (0L == unscaledVal) {
                return zeroValueOf(scale)
            }
            return BigDecimal(if (unscaledVal == INFLATED) INFLATED_BIGINT else null,
                    unscaledVal, scale, precision)
        }

        internal fun valueOf(intVal: BigInteger, scale: Int, precision: Int): BigDecimal {
            val v = compactValFor(intVal)
            if (0L == v) return zeroValueOf(scale)
            if (scale == 0 && v >= 0 && v < zeroThroughTen.size) {
                return zeroThroughTen[v.toInt()]
            }
            return BigDecimal(intVal, v, scale, precision)
        }

        internal fun zeroValueOf(scale: Int): BigDecimal {
            if (scale >= 0 && scale < ZERO_SCALED_BY.size) return ZERO_SCALED_BY[scale]
            return BigDecimal(BigInteger.ZERO, 0, scale, 1)
        }


        /*
         * parse exponent
         */
        private fun parseExp(inArr: CharArray, offset: Int, len: Int): Long {
            var offset = offset
            var len = len
            var exp: Long = 0
            offset += 1
            var c = inArr[offset]
            len -= 1
            val negexp = c == '-'
            // optional sign
            if (negexp || c == '+') {
                offset += 1
                c = inArr[offset]
                len -= 1
            }
            if (len <= 0) throw NumberFormatException() // no exponent digits
            // skip leading zeros in the exponent
            while (len > 10 && c == '0') {
                offset += 1
                c = inArr[offset]
                len -= 1
            }
            if (len > 10) throw NumberFormatException() // too many nonzero exponent digits
            // c now holds first digit of exponent
            while (true) {
                if (!(c in '0'..'9')) throw NumberFormatException() // not a digit
                val v = c - '0'
                exp = exp * 10 + v
                if (len == 1) break // that was final character
                offset += 1
                c = inArr[offset]
                len -= 1
            }
            if (negexp) exp = -exp // apply sign
            return exp
        }

        /**
         * Returns the compact value for given {@code BigInteger}, or
         * INFLATED if too big. Relies on internal representation of
         * {@code BigInteger}.
         */
        private fun compactValFor(b: BigInteger): Long {
            val m = b.mag
            val len = m.size
            if (0 == len) return 0
            val d = m[0]
            if (len > 2 || (2 == len && d < 0)) return INFLATED
            val u = if (2 == len) m[1].toLong().and(BigInteger.LONG_MASK) + d.toLong().shl(Integer.SIZE)
                    else d.toLong().and(BigInteger.LONG_MASK)
            return if (b.signum < 0) -u else u
        }

        private fun longCompareMagnitude(x: Long, y: Long): Int {
            var x = if (x < 0) -x else x
            var y = if (y < 0) -y else y
            return if (x < y) -1 else if (x == y) 0 else 1
        }

        /* the same as checkScale where value!=0 */
        private fun checkScaleNonZero(v: Long): Int {
            val asInt = v.toInt()
            if (asInt.toLong() != v) {
                throw ArithmeticException(if (asInt > 0) "Underflow" else "Overflow")
            }
            return asInt
        }

        private fun checkScale(intCompact: Long, v: Long): Int {
            var asInt = v.toInt()
            if (asInt.toLong() != v) {
            asInt = if (v > Int.MAX_VALUE) Int.MAX_VALUE else Int.MIN_VALUE
            if (intCompact != 0L)
                throw ArithmeticException(if (asInt > 0) "Underflow" else "Overflow")
            }
            return asInt
        }

        private fun checkScale(intVal: BigInteger, v: Long): Int {
            var asInt = v.toInt()
            if (asInt.toLong() != v) {
                asInt = if (v > Int.MAX_VALUE) Int.MAX_VALUE else Int.MIN_VALUE
                if (intVal.signum != 0)
                    throw ArithmeticException(if (asInt > 0) "Underflow" else "Overflow")
            }
            return asInt
        }

        /**
         * Returns the length of the absolute value of a {@code long}, in decimal
         * digits.
         *
         * @param x the {@code long}
         * @return the length of the unscaled value, in deciaml digits.
         */
        internal fun longDigitLength(x: Long): Int {
            /*
             * As described in "Bit Twiddling Hacks" by Sean Anderson,
             * (http://graphics.stanford.edu/~seander/bithacks.html)
             * integer log 10 of x is within 1 of (1233/4096)* (1 +
             * integer log 2 of x). The fraction 1233/4096 approximates
             * log10(2). So we first do a version of log2 (a variant of
             * Long class with pre-checks and opposite directionality) and
             * then scale and check against powers table. This is a little
             * simpler in present context than the version in Hacker's
             * Delight sec 11-4. Adding one to bit length allows comparing
             * downward from the LONG_TEN_POWERS_TABLE that we need
             * anyway.
             */
            var xx = Math.abs(x)
            if (xx < 10) return 1
            val r = ((64 - Integer.numberOfLeadingZeros(x) + 1) * 1233).ushr(12)
            // if r >= length, must have max possible digits for long
            return if (r >= LONG_TEN_POWERS_TABLE.size || x < LONG_TEN_POWERS_TABLE[r]) r else r + 1
        }

        /**
         * Returns the length of the absolute value of a BigInteger, in
         * decimal digits.
         *
         * @param b the BigInteger
         * @return the length of the unscaled value, in decimal digits
         */
        private fun bigDigitLength(b: BigInteger): Int {
            /*
             * Same idea as the long version, but we need a better
             * approximation of log10(2). Using 646456993/2^31
             * is accurate up to max possible reported bitLength.
             */
            if (b.signum == 0) return 1
            val r = ((b.bitLength().toLong() + 1) * 646456993).ushr(31).toInt()
            return if (b.compareMagnitude(bigTenToThe(r)) < 0) r else r + 1
        }

        /**
         * Return 10 to the power n, as a {@code BigInteger}.
         *
         * @param  n the power of ten to be returned (>=0)
         * @return a {@code BigInteger} with the value (10<sup>n</sup>)
         */
        private fun bigTenToThe(n: Int): BigInteger {
            if (n < 0) return BigInteger.ZERO

            if (n < BIG_TEN_POWERS_TABLE_MAX) {
                return if (n < BIG_TEN_POWERS_TABLE.size) BIG_TEN_POWERS_TABLE[n]
                        else expandBigIntegerTenPowers(n)
            }

            return BigInteger.TEN.pow(n)
        }

        /**
         * Expand the BIG_TEN_POWERS_TABLE array to contain at least 10**n.
         *
         * @param n the power of ten to be returned (>=0)
         * @return a {@code BigDecimal} with the value (10<sup>n</sup>) and
         *         in the meantime, the BIG_TEN_POWERS_TABLE array gets
         *         expanded to the size greater than n.
         */
        private fun expandBigIntegerTenPowers(n: Int): BigInteger {
            var pows = BIG_TEN_POWERS_TABLE
            val curLen = BIG_TEN_POWERS_TABLE.size
            if (curLen <= n) {
                var newLen = curLen.shl(1)
                while (newLen <= n) newLen = newLen.shl(1)
                val temp = Array<BigInteger>(newLen, {BigInteger.ONE})
                for (i in 0 .. (pows.size - 1)) temp[i] = pows[i]
                for (i in pows.size .. (newLen - 1))
                    temp[i] = temp[i - 1].multiply(BigInteger.TEN)
                // Based on the following facts:
                // 1. pows is a private local varible;
                // 2. the following store is a volatile store.
                // the newly created array elements can be safely published.
                BIG_TEN_POWERS_TABLE = temp
            }
            return BIG_TEN_POWERS_TABLE[n]
        }

        private val LONG_TEN_POWERS_TABLE = longArrayOf(
                1, // 0 / 10^0
                10, // 1 / 10^1
                100, // 2 / 10^2
                1000, // 3 / 10^3
                10000, // 4 / 10^4
                100000, // 5 / 10^5
                1000000, // 6 / 10^6
                10000000, // 7 / 10^7
                100000000, // 8 / 10^8
                1000000000, // 9 / 10^9
                10000000000L, // 10 / 10^10
                100000000000L, // 11 / 10^11
                1000000000000L, // 12 / 10^12
                10000000000000L, // 13 / 10^13
                100000000000000L, // 14 / 10^14
                1000000000000000L, // 15 / 10^15
                10000000000000000L, // 16 / 10^16
                100000000000000000L, // 17 / 10^17
                1000000000000000000L   // 18 / 10^18
        )

        private var BIG_TEN_POWERS_TABLE: Array<BigInteger> = arrayOf(
                BigInteger.ONE,
                BigInteger.TEN,
                BigInteger.valueOf(100),
                BigInteger.valueOf(1000),
                BigInteger.valueOf(10000),
                BigInteger.valueOf(100000),
                BigInteger.valueOf(1000000),
                BigInteger.valueOf(10000000),
                BigInteger.valueOf(100000000),
                BigInteger.valueOf(1000000000),
                BigInteger.valueOf(10000000000L),
                BigInteger.valueOf(100000000000L),
                BigInteger.valueOf(1000000000000L),
                BigInteger.valueOf(10000000000000L),
                BigInteger.valueOf(100000000000000L),
                BigInteger.valueOf(1000000000000000L),
                BigInteger.valueOf(10000000000000000L),
                BigInteger.valueOf(100000000000000000L),
                BigInteger.valueOf(1000000000000000000L)
        )

        private val BIG_TEN_POWERS_TABLE_INIT_LEN = BIG_TEN_POWERS_TABLE.size
        private val BIG_TEN_POWERS_TABLE_MAX = BIG_TEN_POWERS_TABLE_INIT_LEN.shl(4)

        private val THRESHOLDS_TABLE = longArrayOf(
                Long.MAX_VALUE,                     // 0
                Long.MAX_VALUE/10L,                 // 1
                Long.MAX_VALUE/100L,                // 2
                Long.MAX_VALUE/1000L,               // 3
                Long.MAX_VALUE/10000L,              // 4
                Long.MAX_VALUE/100000L,             // 5
                Long.MAX_VALUE/1000000L,            // 6
                Long.MAX_VALUE/10000000L,           // 7
                Long.MAX_VALUE/100000000L,          // 8
                Long.MAX_VALUE/1000000000L,         // 9
                Long.MAX_VALUE/10000000000L,        // 10
                Long.MAX_VALUE/100000000000L,       // 11
                Long.MAX_VALUE/1000000000000L,      // 12
                Long.MAX_VALUE/10000000000000L,     // 13
                Long.MAX_VALUE/100000000000000L,    // 14
                Long.MAX_VALUE/1000000000000000L,   // 15
                Long.MAX_VALUE/10000000000000000L,  // 16
                Long.MAX_VALUE/100000000000000000L, // 17
                Long.MAX_VALUE/1000000000000000000L // 18
        )

        /**
         * Compute val * 10 ^ n; return this product if it is
         * representable as a long, INFLATED otherwise.
         */
        private fun longMultiplyPowerTen(v: Long, n: Int): Long {
            if (0L == v || n <= 0) return v
            if (n < LONG_TEN_POWERS_TABLE.size && n < THRESHOLDS_TABLE.size) {
                val tenPower = LONG_TEN_POWERS_TABLE[n]
                if (1L == v) return tenPower
                if (Math.abs(v) <= THRESHOLDS_TABLE[n]) return v * tenPower
            }
            return INFLATED
        }

        private fun add(xs: Long, ys: Long): Long {
            val sum = xs + ys
            // See "Hacker's Delight" section 2-12 for explanation of
            // the overflow test.
            if (sum.xor(xs).and(sum.xor(ys)) >= 0L) { // not overflowed
                return sum
            }
            return INFLATED
        }

        private fun add (xs: Long, ys: Long, scale: Int): BigDecimal {
            val sum = add(xs, ys)
            if (sum != INFLATED) return valueOf(sum, scale)
            return BigDecimal(BigInteger.valueOf(xs).add(ys), scale)
        }

        private fun add(xs: Long, scale1: Int, ys: Long, scale2: Int): BigDecimal {
            val sdiff = scale1.toLong() - scale2
            if (0L == sdiff)
                return add(xs, ys, scale1)
            if (sdiff < 0L) {
                val raise = checkScale(xs, -sdiff)
                val scaledX = longMultiplyPowerTen(xs, raise)
                if (scaledX != INFLATED) {
                    return add(scaledX, ys, scale2)
                }
                val bigsum = bigMultiplyPowerTen(xs, raise).add(ys)
                return if (xs.xor(ys) >= 0) // same sign test
                        BigDecimal(bigsum, INFLATED, scale2, 0)
                        else valueOf(bigsum, scale2, 0)
            }
            val raise = checkScale(ys,sdiff)
            val scaledY = longMultiplyPowerTen(ys, raise)
            if (scaledY != INFLATED) {
                return add(xs, scaledY, scale1)
            }
            val bigsum = bigMultiplyPowerTen(ys, raise).add(xs)
            return if (xs.xor(ys) >=0 )
                    BigDecimal(bigsum, INFLATED, scale1, 0)
                    else valueOf(bigsum, scale1, 0)
        }

        private fun add(xs: Long, scale1: Int, snd: BigInteger, scale2: Int): BigDecimal {
            var snd = snd
            var rscale = scale1
            val sdiff = rscale.toLong() - scale2
            val sameSigns = (Integer.signum(xs) == snd.signum)
            var sum: BigInteger
            if (sdiff < 0) {
                val raise = checkScale(xs, -sdiff)
                rscale = scale2
                val scaledX = longMultiplyPowerTen(xs, raise)
                if (scaledX == INFLATED) {
                    sum = snd.add(bigMultiplyPowerTen(xs, raise))
                } else {
                    sum = snd.add(scaledX)
                }
            } else { //if (sdiff > 0) {
                val raise = checkScale(snd,sdiff)
                snd = bigMultiplyPowerTen(snd, raise)
                sum = snd.add(xs)
            }
            return if (sameSigns) BigDecimal(sum, INFLATED, rscale, 0)
                    else valueOf(sum, rscale, 0)
        }

        private fun add(fst: BigInteger, scale1: Int, snd: BigInteger, scale2: Int): BigDecimal {
            var fst = fst
            var snd = snd
            var rscale = scale1
            val sdiff = rscale.toLong() - scale2
            if (sdiff != 0L) {
                if (sdiff < 0) {
                    val raise = checkScale(fst, -sdiff)
                    rscale = scale2
                    fst = bigMultiplyPowerTen(fst, raise)
                } else {
                    val raise = checkScale(snd, sdiff)
                    snd = bigMultiplyPowerTen(snd, raise)
                }
            }
            val sum = fst.add(snd)
            return if (fst.signum == snd.signum) BigDecimal(sum, INFLATED, rscale, 0)
                    else valueOf(sum, rscale, 0)
        }

        private fun bigMultiplyPowerTen(value: Long, n: Int): BigInteger {
            if (n <= 0) return BigInteger.valueOf(value)
            return bigTenToThe(n).multiply(value)
        }

        private fun bigMultiplyPowerTen(value: BigInteger, n: Int): BigInteger {
            if (n <= 0) return value
            if (n < LONG_TEN_POWERS_TABLE.size) return value.multiply(LONG_TEN_POWERS_TABLE[n])
            return value.multiply(bigTenToThe(n))
        }

        /*
         * performs divideAndRound for (dividend0*dividend1, divisor)
         * returns null if quotient can't fit into long value;
         */
        private fun multiplyDivideAndRound(dividend0: Long, dividend1: Long, divisor: Long, scale: Int,
                                           roundingMode: RoundingMode,
                                           preferredScale: Int): BigDecimal? {
            val qsign = Integer.signum(dividend0) * Integer.signum(dividend1) * Integer.signum(divisor)
            val dividend0 = Math.abs(dividend0)
            val dividend1 = Math.abs(dividend1)
            val divisor = Math.abs(divisor)
            // multiply dividend0 * dividend1
            val d0_hi = dividend0.ushr(32)
            val d0_lo = dividend0 and BigInteger.LONG_MASK
            val d1_hi = dividend1.ushr(32)
            val d1_lo = dividend1 and BigInteger.LONG_MASK
            var product = d0_lo * d1_lo
            val d0 = product and BigInteger.LONG_MASK
            var d1 = product.ushr(32)
            product = d0_hi * d1_lo + d1
            d1 = product and BigInteger.LONG_MASK
            var d2 = product.ushr(32)
            product = d0_lo * d1_hi + d1
            d1 = product and BigInteger.LONG_MASK
            d2 += product.ushr(32)
            var d3 = d2.ushr(32)
            d2 = d2 and BigInteger.LONG_MASK
            product = d0_hi * d1_hi + d2
            d2 = product and BigInteger.LONG_MASK
            d3 = product.ushr(32) + d3 and BigInteger.LONG_MASK
            val dividendHi = make64(d3, d2)
            val dividendLo = make64(d1, d0)
            // divide
            return divideAndRound128(dividendHi, dividendLo, divisor, qsign, scale, roundingMode, preferredScale)
        }

        /**
         * Returns a `BigDecimal` rounded according to the MathContext
         * settings;
         * If rounding is needed a new `BigDecimal` is created and returned.

         * @param v the value to be rounded
         * *
         * @param mc the context to use.
         * *
         * @return a `BigDecimal` rounded according to the MathContext
         * *         settings.  May return `value`, if no rounding needed.
         * *
         * @throws ArithmeticException if the rounding mode is
         * *         `RoundingMode.UNNECESSARY` and the
         * *         result is inexact.
         */
        private fun doRound(v: BigDecimal, mc: MathContext): BigDecimal {
            val mcp = mc.precision
            var wasDivided = false
            if (mcp > 0) {
                var intVal = v.intVal
                var compactVal = v.intCompact
                var scale = v.scale
                var prec = v.precision
                val mode = mc.roundingMode
                var drop: Int
                if (compactVal == INFLATED) {
                    drop = prec - mcp
                    while (drop > 0) {
                        scale = checkScaleNonZero(scale.toLong() - drop)
                        intVal = divideAndRoundByTenPow(intVal!!, drop, mode)
                        wasDivided = true
                        compactVal = compactValFor(intVal!!)
                        if (compactVal != INFLATED) {
                            prec = longDigitLength(compactVal)
                            break
                        }
                        prec = bigDigitLength(intVal)
                        drop = prec - mcp
                    }
                }
                if (compactVal != INFLATED) {
                    drop = prec - mcp  // drop can't be more than 18
                    while (drop > 0) {
                        scale = checkScaleNonZero(scale.toLong() - drop)
                        compactVal = divideAndRound(compactVal, LONG_TEN_POWERS_TABLE[drop], mc.roundingMode)
                        wasDivided = true
                        prec = longDigitLength(compactVal)
                        drop = prec - mcp
                        intVal = null
                    }
                }
                return if (wasDivided) BigDecimal(intVal, compactVal, scale, prec) else v
            }
            return v
        }

        /*
     * Returns a {@code BigDecimal} created from {@code long} value with
     * given scale rounded according to the MathContext settings
     */
        private fun doRound(compactVal: Long, scale: Int, mc: MathContext): BigDecimal {
            var compactVal = compactVal
            var scale = scale
            val mcp = mc.precision
            if (mcp in 1..18) {
                var prec = longDigitLength(compactVal)
                var drop = prec - mcp  // drop can't be more than 18
                while (drop > 0) {
                    scale = checkScaleNonZero(scale.toLong() - drop)
                    compactVal = divideAndRound(compactVal, LONG_TEN_POWERS_TABLE[drop], mc.roundingMode)
                    prec = longDigitLength(compactVal)
                    drop = prec - mcp
                }
                return valueOf(compactVal, scale, prec)
            }
            return valueOf(compactVal, scale)
        }

        /*
     * Returns a {@code BigDecimal} created from {@code BigInteger} value with
     * given scale rounded according to the MathContext settings
     */
        private fun doRound(intVal: BigInteger, scale: Int, mc: MathContext): BigDecimal {
            var intVal = intVal
            var scale = scale
            val mcp = mc.precision
            var prec = 0
            if (mcp > 0) {
                var compactVal = compactValFor(intVal)
                val mode = mc.roundingMode
                var drop: Int
                if (compactVal == INFLATED) {
                    prec = bigDigitLength(intVal)
                    drop = prec - mcp
                    while (drop > 0) {
                        scale = checkScaleNonZero(scale.toLong() - drop)
                        intVal = divideAndRoundByTenPow(intVal, drop, mode)
                        compactVal = compactValFor(intVal)
                        if (compactVal != INFLATED) {
                            break
                        }
                        prec = bigDigitLength(intVal)
                        drop = prec - mcp
                    }
                }
                if (compactVal != INFLATED) {
                    prec = longDigitLength(compactVal)
                    drop = prec - mcp     // drop can't be more than 18
                    while (drop > 0) {
                        scale = checkScaleNonZero(scale.toLong() - drop)
                        compactVal = divideAndRound(compactVal, LONG_TEN_POWERS_TABLE[drop], mc.roundingMode)
                        prec = longDigitLength(compactVal)
                        drop = prec - mcp
                    }
                    return valueOf(compactVal, scale, prec)
                }
            }
            return BigDecimal(intVal, INFLATED, scale, prec)
        }

        /*
         * calculate divideAndRound for ldividend*10^raise / divisor
         * when abs(dividend)==abs(divisor);
         */
        private fun roundedTenPower(qsign: Int, raise: Int, scale: Int, preferredScale: Int): BigDecimal {
            if (scale > preferredScale) {
                val diff = scale - preferredScale
                if (diff < raise) {
                    return scaledTenPow(raise - diff, qsign, preferredScale)
                }
                return valueOf(qsign.toLong(), scale - raise)
            }
            return scaledTenPow(raise, qsign, scale)
        }

        internal fun scaledTenPow(n: Int, sign: Int, scale: Int): BigDecimal {
            if (n < LONG_TEN_POWERS_TABLE.size)
                return valueOf(sign * LONG_TEN_POWERS_TABLE[n], scale)
            var unscaledVal = bigTenToThe(n)
            if (sign == -1) {
                unscaledVal = unscaledVal.negate()
            }
            return BigDecimal(unscaledVal, INFLATED, scale, n + 1)
        }

        private fun divide(dividend: Long, dividendScale: Int, divisor: Long, divisorScale: Int,
                           scale: Int, roundingMode: RoundingMode): BigDecimal {
            if (checkScale(dividend, scale.toLong() + divisorScale) > dividendScale) {
                val newScale = scale + divisorScale
                val raise = newScale - dividendScale
                if (raise < LONG_TEN_POWERS_TABLE.size) {
                    var xs = longMultiplyPowerTen(dividend, raise)
                    if (xs != INFLATED) {
                        return divideAndRound(xs, divisor, scale, roundingMode, scale)
                    }
                    val q = multiplyDivideAndRound(LONG_TEN_POWERS_TABLE[raise],
                            dividend, divisor, scale, roundingMode, scale)
                    if (q != null) {
                        return q
                    }
                }
                val scaledDividend = bigMultiplyPowerTen(dividend, raise)
                return divideAndRound(scaledDividend, divisor, scale, roundingMode, scale)
            }
            val newScale = checkScale(divisor, dividendScale.toLong() - scale)
            val raise = newScale - divisorScale
            if (raise < LONG_TEN_POWERS_TABLE.size) {
                var ys = longMultiplyPowerTen(divisor, raise)
                if (ys != INFLATED) {
                    return divideAndRound(dividend, ys, scale, roundingMode, scale)
                }
            }
            val scaledDivisor = bigMultiplyPowerTen(divisor, raise)
            return divideAndRound(BigInteger.valueOf(dividend), scaledDivisor, scale, roundingMode, scale)
        }

        private fun divide(dividend: BigInteger, dividendScale: Int, divisor: Long, divisorScale: Int,
                           scale: Int, roundingMode: RoundingMode): BigDecimal {
            if (checkScale(dividend, scale.toLong() + divisorScale) > dividendScale) {
                val newScale = scale + divisorScale
                val raise = newScale - dividendScale
                val scaledDividend = bigMultiplyPowerTen(dividend, raise)
                return divideAndRound(scaledDividend, divisor, scale, roundingMode, scale)
            }
            val newScale = checkScale(divisor, dividendScale.toLong() - scale)
            val raise = newScale - divisorScale
            if (raise < LONG_TEN_POWERS_TABLE.size) {
                var ys = longMultiplyPowerTen(divisor, raise)
                if (ys != INFLATED) {
                    return divideAndRound(dividend, ys, scale, roundingMode, scale)
                }
            }
            val scaledDivisor = bigMultiplyPowerTen(divisor, raise)
            return divideAndRound(dividend, scaledDivisor, scale, roundingMode, scale)
        }

        private fun divide(dividend: Long, dividendScale: Int, divisor: BigInteger, divisorScale: Int,
                           scale: Int, roundingMode: RoundingMode): BigDecimal {
            if (checkScale(dividend, scale.toLong() + divisorScale) > dividendScale) {
                val newScale = scale + divisorScale
                val raise = newScale - dividendScale
                val scaledDividend = bigMultiplyPowerTen(dividend, raise)
                return divideAndRound(scaledDividend, divisor, scale, roundingMode, scale)
            }
            val newScale = checkScale(divisor, dividendScale.toLong() - scale)
            val raise = newScale - divisorScale
            val scaledDivisor = bigMultiplyPowerTen(divisor, raise)
            return divideAndRound(BigInteger.valueOf(dividend), scaledDivisor, scale, roundingMode, scale)
        }

        private fun divide(dividend: BigInteger, dividendScale: Int, divisor: BigInteger, divisorScale: Int,
                           scale: Int, roundingMode: RoundingMode): BigDecimal {
            if (checkScale(dividend, scale.toLong() + divisorScale) > dividendScale) {
                val newScale = scale + divisorScale
                val raise = newScale - dividendScale
                val scaledDividend = bigMultiplyPowerTen(dividend, raise)
                return divideAndRound(scaledDividend, divisor, scale, roundingMode, scale)
            }
            val newScale = checkScale(divisor, dividendScale.toLong() - scale)
            val raise = newScale - divisorScale
            val scaledDivisor = bigMultiplyPowerTen(divisor, raise)
            return divideAndRound(dividend, scaledDivisor, scale, roundingMode, scale)
        }

        /**
         * Returns a `BigDecimal` whose value is `(xs /
         * ys)`, with rounding according to the context settings.

         * Fast path - used only when (xscale <= yscale && yscale < 18
         * && mc.presision<18) {
         */
        private fun divideSmallFastPath(xs: Long, xscale: Int,
                                        ys: Long, yscale: Int,
                                        preferredScale: Long, mc: MathContext): BigDecimal {
            var yscale = yscale
            val mcp = mc.precision
            val roundingMode = mc.roundingMode

            if (xscale > yscale || yscale >= 18 || mcp >= 18) throw IllegalArgumentException(null)
            val xraise = yscale - xscale // xraise >=0
            val scaledX = if (xraise == 0)
                xs
            else
                longMultiplyPowerTen(xs, xraise) // can't overflow here!
            var quotient: BigDecimal?

            val cmp = longCompareMagnitude(scaledX, ys)
            if (cmp > 0) { // satisfy constraint (b)
                yscale -= 1 // [that is, divisor *= 10]
                val scl = checkScaleNonZero(preferredScale + yscale - xscale + mcp)
                if (checkScaleNonZero(mcp.toLong() + yscale - xscale) > 0) {
                    // assert newScale >= xscale
                    val raise = checkScaleNonZero(mcp.toLong() + yscale - xscale)
                    val scaledXs = longMultiplyPowerTen(xs, raise)
                    if (scaledXs == INFLATED) {
                        quotient = null
                        if (mcp - 1 >= 0 && mcp - 1 < LONG_TEN_POWERS_TABLE.size) {
                            quotient = multiplyDivideAndRound(LONG_TEN_POWERS_TABLE[mcp - 1], scaledX,
                                    ys, scl, roundingMode, checkScaleNonZero(preferredScale))
                        }
                        if (quotient == null) {
                            val rb = bigMultiplyPowerTen(scaledX, mcp - 1)
                            quotient = divideAndRound(rb, ys,
                                    scl, roundingMode, checkScaleNonZero(preferredScale))
                        }
                    } else {
                        quotient = divideAndRound(scaledXs, ys, scl, roundingMode, checkScaleNonZero(preferredScale))
                    }
                } else {
                    val newScale = checkScaleNonZero(xscale.toLong() - mcp)
                    // assert newScale >= yscale
                    if (newScale == yscale) { // easy case
                        quotient = divideAndRound(xs, ys, scl, roundingMode, checkScaleNonZero(preferredScale))
                    } else {
                        val raise = checkScaleNonZero(newScale.toLong() - yscale)
                        val scaledYs = longMultiplyPowerTen(ys, raise)
                        if (scaledYs == INFLATED) {
                            val rb = bigMultiplyPowerTen(ys, raise)
                            quotient = divideAndRound(BigInteger.valueOf(xs),
                                    rb, scl, roundingMode, checkScaleNonZero(preferredScale))
                        } else {
                            quotient = divideAndRound(xs, scaledYs, scl, roundingMode, checkScaleNonZero(preferredScale))
                        }
                    }
                }
            } else {
                // abs(scaledX) <= abs(ys)
                // result is "scaledX * 10^msp / ys"
                val scl = checkScaleNonZero(preferredScale + yscale - xscale + mcp)
                if (cmp == 0) {
                    // abs(scaleX)== abs(ys) => result will be scaled 10^mcp + correct sign
                    quotient = roundedTenPower(if (scaledX < 0 == ys < 0) 1 else -1, mcp, scl, checkScaleNonZero(preferredScale))
                } else {
                    // abs(scaledX) < abs(ys)
                    val scaledXs = longMultiplyPowerTen(scaledX, mcp)
                    if (scaledXs == INFLATED) {
                        quotient = null
                        if (mcp < LONG_TEN_POWERS_TABLE.size) {
                            quotient = multiplyDivideAndRound(LONG_TEN_POWERS_TABLE[mcp], scaledX,
                                    ys, scl, roundingMode, checkScaleNonZero(preferredScale))
                        }
                        if (quotient == null) {
                            val rb = bigMultiplyPowerTen(scaledX, mcp)
                            quotient = divideAndRound(rb, ys,
                                    scl, roundingMode, checkScaleNonZero(preferredScale))
                        }
                    } else {
                        quotient = divideAndRound(scaledXs, ys, scl, roundingMode, checkScaleNonZero(preferredScale))
                    }
                }
            }
            // doRound, here, only affects 1000000000 case.
            return doRound(quotient, mc)
        }

        /**
         * Returns a `BigDecimal` whose value is `(xs /
         * ys)`, with rounding according to the context settings.
         */
        private fun divide(xs: Long, xscale: Int, ys: Long, yscale: Int, preferredScale: Long, mc: MathContext): BigDecimal {
            var yscale = yscale
            val mcp = mc.precision
            if (xscale <= yscale && yscale < 18 && mcp < 18) {
                return divideSmallFastPath(xs, xscale, ys, yscale, preferredScale, mc)
            }
            if (compareMagnitudeNormalized(xs, xscale, ys, yscale) > 0) {// satisfy constraint (b)
                yscale -= 1 // [that is, divisor *= 10]
            }
            val roundingMode = mc.roundingMode
            // In order to find out whether the divide generates the exact result,
            // we avoid calling the above divide method. 'quotient' holds the
            // return BigDecimal object whose scale will be set to 'scl'.
            val scl = checkScaleNonZero(preferredScale + yscale - xscale + mcp)
            val quotient: BigDecimal
            if (checkScaleNonZero(mcp.toLong() + yscale - xscale) > 0) {
                val raise = checkScaleNonZero(mcp.toLong() + yscale - xscale)
                val scaledXs = longMultiplyPowerTen(xs, raise)
                if (scaledXs == INFLATED) {
                    val rb = bigMultiplyPowerTen(xs, raise)
                    quotient = divideAndRound(rb, ys, scl, roundingMode, checkScaleNonZero(preferredScale))
                } else {
                    quotient = divideAndRound(scaledXs, ys, scl, roundingMode, checkScaleNonZero(preferredScale))
                }
            } else {
                val newScale = checkScaleNonZero(xscale.toLong() - mcp)
                // assert newScale >= yscale
                if (newScale == yscale) { // easy case
                    quotient = divideAndRound(xs, ys, scl, roundingMode, checkScaleNonZero(preferredScale))
                } else {
                    val raise = checkScaleNonZero(newScale.toLong() - yscale)
                    val scaledYs = longMultiplyPowerTen(ys, raise)
                    if (scaledYs == INFLATED) {
                        val rb = bigMultiplyPowerTen(ys, raise)
                        quotient = divideAndRound(BigInteger.valueOf(xs),
                                rb, scl, roundingMode, checkScaleNonZero(preferredScale))
                    } else {
                        quotient = divideAndRound(xs, scaledYs, scl, roundingMode, checkScaleNonZero(preferredScale))
                    }
                }
            }
            // doRound, here, only affects 1000000000 case.
            return doRound(quotient, mc)
        }

        /**
         * Returns a `BigDecimal` whose value is `(xs /
         * ys)`, with rounding according to the context settings.
         */
        private fun divide(xs: BigInteger, xscale: Int, ys: Long, yscale: Int, preferredScale: Long, mc: MathContext): BigDecimal {
            var yscale = yscale
            // Normalize dividend & divisor so that both fall into [0.1, 0.999...]
            if (-compareMagnitudeNormalized(ys, yscale, xs, xscale) > 0) {// satisfy constraint (b)
                yscale -= 1 // [that is, divisor *= 10]
            }
            val mcp = mc.precision
            val roundingMode = mc.roundingMode

            // In order to find out whether the divide generates the exact result,
            // we avoid calling the above divide method. 'quotient' holds the
            // return BigDecimal object whose scale will be set to 'scl'.
            val quotient: BigDecimal
            val scl = checkScaleNonZero(preferredScale + yscale - xscale + mcp)
            if (checkScaleNonZero(mcp.toLong() + yscale - xscale) > 0) {
                val raise = checkScaleNonZero(mcp.toLong() + yscale - xscale)
                val rb = bigMultiplyPowerTen(xs, raise)
                quotient = divideAndRound(rb, ys, scl, roundingMode, checkScaleNonZero(preferredScale))
            } else {
                val newScale = checkScaleNonZero(xscale.toLong() - mcp)
                // assert newScale >= yscale
                if (newScale == yscale) { // easy case
                    quotient = divideAndRound(xs, ys, scl, roundingMode, checkScaleNonZero(preferredScale))
                } else {
                    val raise = checkScaleNonZero(newScale.toLong() - yscale)
                    val scaledYs = longMultiplyPowerTen(ys, raise)
                    if (scaledYs == INFLATED) {
                        val rb = bigMultiplyPowerTen(ys, raise)
                        quotient = divideAndRound(xs, rb, scl, roundingMode, checkScaleNonZero(preferredScale))
                    } else {
                        quotient = divideAndRound(xs, scaledYs, scl, roundingMode, checkScaleNonZero(preferredScale))
                    }
                }
            }
            // doRound, here, only affects 1000000000 case.
            return doRound(quotient, mc)
        }

        /**
         * Returns a `BigDecimal` whose value is `(xs /
         * ys)`, with rounding according to the context settings.
         */
        private fun divide(xs: Long, xscale: Int, ys: BigInteger, yscale: Int, preferredScale: Long, mc: MathContext): BigDecimal {
            var yscale = yscale
            // Normalize dividend & divisor so that both fall into [0.1, 0.999...]
            if (compareMagnitudeNormalized(xs, xscale, ys, yscale) > 0) {// satisfy constraint (b)
                yscale -= 1 // [that is, divisor *= 10]
            }
            val mcp = mc.precision
            val roundingMode = mc.roundingMode

            // In order to find out whether the divide generates the exact result,
            // we avoid calling the above divide method. 'quotient' holds the
            // return BigDecimal object whose scale will be set to 'scl'.
            val quotient: BigDecimal
            val scl = checkScaleNonZero(preferredScale + yscale - xscale + mcp)
            if (checkScaleNonZero(mcp.toLong() + yscale - xscale) > 0) {
                val raise = checkScaleNonZero(mcp.toLong() + yscale - xscale)
                val rb = bigMultiplyPowerTen(xs, raise)
                quotient = divideAndRound(rb, ys, scl, roundingMode, checkScaleNonZero(preferredScale))
            } else {
                val newScale = checkScaleNonZero(xscale.toLong() - mcp)
                val raise = checkScaleNonZero(newScale.toLong() - yscale)
                val rb = bigMultiplyPowerTen(ys, raise)
                quotient = divideAndRound(BigInteger.valueOf(xs), rb, scl, roundingMode, checkScaleNonZero(preferredScale))
            }
            // doRound, here, only affects 1000000000 case.
            return doRound(quotient, mc)
        }

        /**
         * Returns a `BigDecimal` whose value is `(xs /
         * ys)`, with rounding according to the context settings.
         */
        private fun divide(xs: BigInteger, xscale: Int, ys: BigInteger, yscale: Int, preferredScale: Long, mc: MathContext): BigDecimal {
            var yscale = yscale
            // Normalize dividend & divisor so that both fall into [0.1, 0.999...]
            if (compareMagnitudeNormalized(xs, xscale, ys, yscale) > 0) {// satisfy constraint (b)
                yscale -= 1 // [that is, divisor *= 10]
            }
            val mcp = mc.precision
            val roundingMode = mc.roundingMode

            // In order to find out whether the divide generates the exact result,
            // we avoid calling the above divide method. 'quotient' holds the
            // return BigDecimal object whose scale will be set to 'scl'.
            val quotient: BigDecimal
            val scl = checkScaleNonZero(preferredScale + yscale - xscale + mcp)
            if (checkScaleNonZero(mcp.toLong() + yscale - xscale) > 0) {
                val raise = checkScaleNonZero(mcp.toLong() + yscale - xscale)
                val rb = bigMultiplyPowerTen(xs, raise)
                quotient = divideAndRound(rb, ys, scl, roundingMode, checkScaleNonZero(preferredScale))
            } else {
                val newScale = checkScaleNonZero(xscale.toLong() - mcp)
                val raise = checkScaleNonZero(newScale.toLong() - yscale)
                val rb = bigMultiplyPowerTen(ys, raise)
                quotient = divideAndRound(xs, rb, scl, roundingMode, checkScaleNonZero(preferredScale))
            }
            // doRound, here, only affects 1000000000 case.
            return doRound(quotient, mc)
        }

        /*
         * Divides {@code BigInteger} value by ten power.
         */
        private fun divideAndRoundByTenPow(intVal: BigInteger, tenPow: Int, roundingMode: RoundingMode): BigInteger {
            if (tenPow < LONG_TEN_POWERS_TABLE.size) {
                return divideAndRound(intVal, LONG_TEN_POWERS_TABLE[tenPow], roundingMode)
            }
            return divideAndRound(intVal, bigTenToThe(tenPow), roundingMode)
        }

        /**
         * Internally used for division operation for division {@code long} by
         * {@code long}.
         * The returned {@code BigDecimal} object is the quotient whose scale is set
         * to the passed in scale. If the remainder is not zero, it will be rounded
         * based on the passed in roundingMode. Also, if the remainder is zero and
         * the last parameter, i.e. preferredScale is NOT equal to scale, the
         * trailing zeros of the result is stripped to match the preferredScale.
         */
        private fun divideAndRound(ldividend: Long, ldivisor: Long, scale: Int,
                                   roundingMode: RoundingMode, preferredScale: Int): BigDecimal {
            val q = ldividend / ldivisor
            if (roundingMode == RoundingMode.DOWN && scale == preferredScale) return valueOf(q, scale)
            val r = ldividend % ldivisor
            val qsign = if ((ldividend < 0) == (ldivisor < 0)) 1 else -1
            if (r != 0L) {
                val increment = needIncrement(ldivisor, roundingMode, qsign, q, r)
                return valueOf(if (increment) q + qsign else q, scale)
            }
            if (preferredScale != scale) {
                return createAndStripZerosToMatchScale(q, scale, preferredScale.toLong())
            }
            return valueOf(q, scale)
        }

        /**
         * Divides {@code long} by {@code long} and do rounding based on the
         * passed in roundingMode.
         */
        private fun divideAndRound(ldividend: Long, ldivisor: Long, roundingMode: RoundingMode): Long {
            val q = ldividend / ldivisor // store quotient in long
            if (roundingMode == RoundingMode.DOWN) return q
            val r = ldividend % ldivisor // store remainder in long
            val qsign = if ((ldividend < 0) == (ldivisor < 0)) 1 else -1
            if (r != 0L) {
                val increment = needIncrement(ldivisor, roundingMode, qsign, q, r)
                return if (increment) q + qsign else q
            }
            return q
        }

        /**
         * Divides `BigInteger` value by `long` value and
         * do rounding based on the passed in roundingMode.
         */
        private fun divideAndRound(bdividend: BigInteger, ldivisor: Long, roundingMode: RoundingMode): BigInteger {
            // Descend into mutables for faster remainder checks
            val mdividend = MutableBigInteger(bdividend.mag)
            val mq = MutableBigInteger() // store quotient
            val r = mdividend.divide(ldivisor, mq)
            val isRemainderZero = (0L == r)
            val qsign = if (ldivisor < 0) -bdividend.signum else bdividend.signum
            if (!isRemainderZero) {
                if (needIncrement(ldivisor, roundingMode, qsign, mq, r)) {
                    mq.add(MutableBigInteger.ONE)
                }
            }
            return mq.toBigInteger(qsign)
        }

        /**
         * Internally used for division operation for division `BigInteger`
         * by `long`.
         * The returned `BigDecimal` object is the quotient whose scale is set
         * to the passed in scale. If the remainder is not zero, it will be rounded
         * based on the passed in roundingMode. Also, if the remainder is zero and
         * the last parameter, i.e. preferredScale is NOT equal to scale, the
         * trailing zeros of the result is stripped to match the preferredScale.
         */
        private fun divideAndRound(bdividend: BigInteger, ldivisor: Long, scale: Int,
                                   roundingMode: RoundingMode, preferredScale: Int): BigDecimal {
            // Descend into mutables for faster remainder checks
            val mdividend = MutableBigInteger(bdividend.mag)
            val mq = MutableBigInteger() // store quotient
            val r = mdividend.divide(ldivisor, mq)
            val isRemainderZero = (0L == r)
            val qsign = if (ldivisor < 0) -bdividend.signum else bdividend.signum
            if (!isRemainderZero) {
                if (needIncrement(ldivisor, roundingMode, qsign, mq, r)) {
                    mq.add(MutableBigInteger.ONE)
                }
                return mq.toBigDecimal(qsign, scale)
            }
            if (preferredScale != scale) {
                val compactVal = mq.toCompactValue(qsign)
                if (compactVal != INFLATED) {
                    return createAndStripZerosToMatchScale(compactVal, scale, preferredScale.toLong())
                }
                val intVal = mq.toBigInteger(qsign)
                return createAndStripZerosToMatchScale(intVal, scale, preferredScale.toLong())
            }
            return mq.toBigDecimal(qsign, scale)
        }

        /**
         * Divides `BigInteger` value by `BigInteger` value and
         * do rounding based on the passed in roundingMode.
         */
        private fun divideAndRound(bdividend: BigInteger, bdivisor: BigInteger, roundingMode: RoundingMode): BigInteger {
            // Descend into mutables for faster remainder checks
            val mdividend = MutableBigInteger(bdividend.mag)
            val mq = MutableBigInteger()
            val mdivisor = MutableBigInteger(bdivisor.mag)
            val mr = mdividend.divide(mdivisor, mq)
            val isRemainderZero = mr.isZero
            val qsign = if (bdividend.signum !== bdivisor.signum) -1 else 1
            if (!isRemainderZero) {
                if (needIncrement(mdivisor, roundingMode, qsign, mq, mr)) {
                    mq.add(MutableBigInteger.ONE)
                }
            }
            return mq.toBigInteger(qsign)
        }

        /**
         * Internally used for division operation for division `BigInteger`
         * by `BigInteger`.
         * The returned `BigDecimal` object is the quotient whose scale is set
         * to the passed in scale. If the remainder is not zero, it will be rounded
         * based on the passed in roundingMode. Also, if the remainder is zero and
         * the last parameter, i.e. preferredScale is NOT equal to scale, the
         * trailing zeros of the result is stripped to match the preferredScale.
         */
        private fun divideAndRound(bdividend: BigInteger, bdivisor: BigInteger, scale: Int,
                                   roundingMode: RoundingMode, preferredScale: Int): BigDecimal {
            // Descend into mutables for faster remainder checks
            val mdividend = MutableBigInteger(bdividend.mag)
            val mq = MutableBigInteger()
            val mdivisor = MutableBigInteger(bdivisor.mag)
            val mr = mdividend.divide(mdivisor, mq)
            val isRemainderZero = mr.isZero
            val qsign = if (bdividend.signum != bdivisor.signum) -1 else 1
            if (!isRemainderZero) {
                if (needIncrement(mdivisor, roundingMode, qsign, mq, mr)) {
                    mq.add(MutableBigInteger.ONE)
                }
                return mq.toBigDecimal(qsign, scale)
            } else {
                if (preferredScale != scale) {
                    val compactVal = mq.toCompactValue(qsign)
                    if (compactVal != INFLATED) {
                        return createAndStripZerosToMatchScale(compactVal, scale, preferredScale.toLong())
                    }
                    val intVal = mq.toBigInteger(qsign)
                    return createAndStripZerosToMatchScale(intVal, scale, preferredScale.toLong())
                } else {
                    return mq.toBigDecimal(qsign, scale)
                }
            }
        }

        /**
         * Shared logic of need increment computation.
         */
        private fun commonNeedIncrement(roundingMode: RoundingMode, qsign: Int,
                                cmpFracHalf: Int, oddQuot: Boolean): Boolean {
            when (roundingMode) {
                RoundingMode.UNNECESSARY -> throw ArithmeticException("Rounding necessary")

                RoundingMode.UP // Away from zero
                -> return true

                RoundingMode.DOWN // Towards zero
                -> return false

                RoundingMode.CEILING // Towards +infinity
                -> return qsign > 0

                RoundingMode.FLOOR // Towards -infinity
                -> return qsign < 0

                else // Some kind of half-way rounding
                -> {
                    if (cmpFracHalf < 0)
                        // We're closer to higher digit
                        return false
                    if (cmpFracHalf > 0)
                        // We're closer to lower digit
                        return true
                    // half-way
                    when (roundingMode) {
                        RoundingMode.HALF_DOWN -> return false

                        RoundingMode.HALF_UP -> return true

                        else -> return oddQuot // RoundingMode.HALF_EVEN
                    }
                } // else
            } // when
        } // commonNeedIncrement

        /**
         * Tests if quotient has to be incremented according the roundingMode
         */
        private fun needIncrement(ldivisor: Long, roundingMode: RoundingMode,
                                  qsign: Int, q: Long, r: Long): Boolean {
            if (r == 0L) throw IllegalArgumentException("r == 0")

            var cmpFracHalf: Int
            if (r <= HALF_LONG_MIN_VALUE || r > HALF_LONG_MAX_VALUE) {
                cmpFracHalf = 1 // 2 * r can't fit into long
            } else {
                cmpFracHalf = longCompareMagnitude(2 * r, ldivisor)
            }

            return commonNeedIncrement(roundingMode, qsign, cmpFracHalf, q.and(1L) != 0L)
        }

        private fun needIncrement(ldivisor: Long, roundingMode: RoundingMode,
                                  qsign: Int, mq: MutableBigInteger, r: Long): Boolean {
            if (r == 0L) throw IllegalArgumentException("r == 0")

            var cmpFracHalf: Int
            if (r <= HALF_LONG_MIN_VALUE || r > HALF_LONG_MAX_VALUE) {
                cmpFracHalf = 1 // 2 * r can't fit into long
            } else {
                cmpFracHalf = longCompareMagnitude(2 * r, ldivisor)
            }

            return commonNeedIncrement(roundingMode, qsign, cmpFracHalf, mq.isOdd)
        }

        /**
         * Tests if quotient has to be incremented according the roundingMode
         */
        private fun needIncrement(mdivisor: MutableBigInteger, roundingMode: RoundingMode,
                                  qsign: Int, mq: MutableBigInteger, mr: MutableBigInteger): Boolean {
            if (mr.isZero) throw ArithmeticException("Divide by 0")
            val cmpFracHalf = mr.compareHalf(mdivisor)
            return commonNeedIncrement(roundingMode, qsign, cmpFracHalf, mq.isOdd)
        }

        /**
         * Remove insignificant trailing zeros from this
         * {@code BigInteger} value until the preferred scale is reached or no
         * more zeros can be removed.  If the preferred scale is less than
         * Integer.MIN_VALUE, all the trailing zeros will be removed.
         *
         * @return new {@code BigDecimal} with a scale possibly reduced
         * to be closed to the preferred scale.
         */
        private fun createAndStripZerosToMatchScale(intVal: BigInteger, scale: Int, preferredScale: Long): BigDecimal {
            var qr: Array<BigInteger> // quotient-remainder pair
            var intVal = intVal
            var scale = scale
            while (intVal.compareMagnitude(BigInteger.TEN) >= 0
                   && scale > preferredScale) {
                if (intVal.testBit(0))
                    break // odd number cannot end in 0
                qr = intVal.divideAndRemainder(BigInteger.TEN)
                if (qr[1].signum != 0)
                    break // non-0 remainder
                intVal = qr[0]
                scale = checkScale(intVal, scale.toLong() - 1) // could Overflow
            }
            return valueOf(intVal, scale, 0)
        }

        /**
         * Remove insignificant trailing zeros from this
         * {@code long} value until the preferred scale is reached or no
         * more zeros can be removed.  If the preferred scale is less than
         * Integer.MIN_VALUE, all the trailing zeros will be removed.
         *
         * @return new {@code BigDecimal} with a scale possibly reduced
         * to be closed to the preferred scale.
         */
        private fun createAndStripZerosToMatchScale(compactVal: Long, scale: Int, preferredScale: Long): BigDecimal {
            var compactVal = compactVal
            var scale = scale
            while (Math.abs(compactVal) >= 10L && scale > preferredScale) {
                if (compactVal.and(1L) != 0L)
                    break // odd number cannot end in 0
                val r = compactVal % 10L
                if (r != 0L)
                    break // non-0 remainder
                compactVal /= 10
                scale = checkScale(compactVal, scale.toLong() - 1) // could Overflow
            }
            return valueOf(compactVal, scale)
        }

        private fun stripZerosToMatchScale(intVal: BigInteger?, intCompact: Long, scale: Int, preferredScale: Int): BigDecimal {
            if (intCompact != INFLATED) {
                return createAndStripZerosToMatchScale(intCompact, scale, preferredScale.toLong())
            }
            return createAndStripZerosToMatchScale(intVal?: INFLATED_BIGINT,
                                                   scale, preferredScale.toLong())
        }

        val DIV_NUM_BASE = 1L shl 32 // Number base (32 bits).

        private fun saturateLong(s: Long): Int {
            val i = s.toInt()
            return if (s == i.toLong()) i else if (s < 0) Int.MIN_VALUE else Int.MAX_VALUE
        }

        /*
         * divideAndRound 128-bit value by long divisor.
         * returns null if quotient can't fit into long value;
         * Specialized version of Knuth's division
         */
        private fun divideAndRound128(dividendHi: Long, dividendLo: Long,
                                      divisor: Long, sign: Int, scale: Int,
                                      roundingMode: RoundingMode, preferredScale: Int): BigDecimal? {
            if (dividendHi >= divisor) return null
            val shift = Integer.numberOfLeadingZeros(divisor)
            var divisor = divisor shl shift
            val v1 = divisor.ushr(32)
            val v0 = divisor.and(BigInteger.LONG_MASK)

            var tmp = dividendLo shl shift
            var u1 = tmp.ushr(32)
            val u0 = tmp.and(BigInteger.LONG_MASK)

            tmp = dividendHi.shl(shift).or(dividendLo.ushr(64 - shift))
            val u2 = tmp.and(BigInteger.LONG_MASK)
            tmp = divWord(tmp, v1)
            var q1 = tmp.and(BigInteger.LONG_MASK)
            var r_tmp = tmp.ushr(32)
            while(q1 >= DIV_NUM_BASE || unsignedLongCompare(q1 * v0, make64(r_tmp, u1))) {
                q1 -= 1
                r_tmp += v1
                if (r_tmp >= DIV_NUM_BASE) break
            }
            tmp = mulsub(u2, u1, v1, v0, q1)
            u1 = tmp.and(BigInteger.LONG_MASK)
            tmp = divWord(tmp, v1)
            var q0 = tmp.and(BigInteger.LONG_MASK)
            r_tmp = tmp.ushr(32)
            while (q0 >= DIV_NUM_BASE || unsignedLongCompare(q0 * v0, make64(r_tmp, u0))) {
                q0 -= 1
                r_tmp += v1
                if (r_tmp >= DIV_NUM_BASE) break
            }
            if (q1.toInt() < 0) {
                // result (which is positive and unsigned here)
                // can't fit into long due to sign bit is used for value
                val mq = MutableBigInteger(intArrayOf(q1.toInt(), q0.toInt()))
                if (roundingMode == RoundingMode.DOWN && scale == preferredScale) {
                    return mq.toBigDecimal(sign, scale)
                }
                val r = mulsub(u1, u0, v1, v0, q0).ushr(shift)
                if (r != 0L) {
                    if (needIncrement(divisor.ushr(shift), roundingMode, sign, mq, r)) {
                        mq.add(MutableBigInteger.ONE)
                    }
                    return mq.toBigDecimal(sign, scale)
                }
                if (preferredScale != scale) {
                    val intVal =  mq.toBigInteger(sign)
                    return createAndStripZerosToMatchScale(intVal, scale, preferredScale.toLong())
                }
                return mq.toBigDecimal(sign, scale)
            }
            val q = make64(q1, q0) * sign
            if (roundingMode == RoundingMode.DOWN && scale == preferredScale) {
                return valueOf(q, scale)
            }
            val r = mulsub(u1, u0, v1, v0, q0).ushr(shift)
            if (r != 0L) {
                val increment = needIncrement(divisor.ushr(shift), roundingMode, sign, q, r)
                return valueOf(if (increment) q + sign else q, scale)
            }
            if (preferredScale != scale) {
                return createAndStripZerosToMatchScale(q, scale, preferredScale.toLong())
            }
            return valueOf(q, scale)
        }

        private fun divWord(n: Long, dLong: Long): Long {
            if (1L == dLong) {
                return n.and(BigInteger.LONG_MASK)
            }
            // Approximate the quotient and remainder
            var q = n.ushr(1) / dLong.ushr(1)
            var r = n - q * dLong

            // Correct the approximation
            while (r < 0) {
                r += dLong
                q -= 1
            }
            while (r >= dLong) {
                r -= dLong
                q += 1
            }
            // n - q*dlong == r && 0 <= r <dLong, hence we're done.
            return r.shl(32).or(q.and(BigInteger.LONG_MASK))
        }

        private fun make64(hi: Long, lo: Long): Long {
            return hi.shl(32).or(lo)
        }

        private fun mulsub(u1: Long, u0: Long, v1: Long, v0: Long, q0: Long): Long {
            val tmp = u0 - q0 * v0
            return make64(u1 + tmp.ushr(32) - q0 * v1, tmp.and(BigInteger.LONG_MASK))
        }

        private fun unsignedLongCompare(one: Long, two: Long): Boolean {
            return (one + Long.MIN_VALUE) > (two + Long.MIN_VALUE)
        }

        private fun unsignedLongCompareEq(one: Long, two: Long): Boolean {
            return (one + Long.MIN_VALUE) >= (two + Long.MIN_VALUE)
        }

        // Compare Normalize dividend & divisor so that both fall into [0.1, 0.999...]
        private fun compareMagnitudeNormalized(xs: Long, xscale: Int, ys: Long, yscale: Int): Int {
            var xs = xs
            var ys = ys
            val sdiff = xscale - yscale
            if (sdiff != 0) {
                if (sdiff < 0) {
                    xs = longMultiplyPowerTen(xs, -sdiff);
                } else { // sdiff > 0
                    ys = longMultiplyPowerTen(ys, sdiff);
                }
            }
            if (xs != INFLATED)
                return if (ys != INFLATED) longCompareMagnitude(xs, ys) else -1
            return 1
        }

        private fun compareMagnitudeNormalized(xs: Long, xscale: Int, ys: BigInteger, yscale: Int): Int {
            // assert "ys can't be represented as long"
            if (0L == xs) return -1
            val sdiff = xscale - yscale
            if (sdiff < 0) {
                if (longMultiplyPowerTen(xs, -sdiff) == INFLATED ) {
                    return bigMultiplyPowerTen(xs, -sdiff).compareMagnitude(ys)
                }
            }
            return -1
        }

        private fun compareMagnitudeNormalized(xs: BigInteger, xscale: Int, ys: BigInteger, yscale: Int): Int {
            val sdiff = xscale - yscale
            if (sdiff < 0) {
                return bigMultiplyPowerTen(xs, -sdiff).compareMagnitude(ys)
            }
            // sdiff >= 0
            return xs.compareMagnitude(bigMultiplyPowerTen(ys, sdiff))
        }

        private fun multiply(x: Long, y: Long): Long {
            val product = x * y
            val ax = Math.abs(x)
            val ay = Math.abs(y)
            if (ax.or(ay).ushr(31) == 0L || 0L == y || product / y == x) {
                return product
            }
            return INFLATED
        }

        private fun multiply(x: Long, y: Long, scale: Int): BigDecimal {
            val product = multiply(x, y)
            if (product != INFLATED) return valueOf(product, scale)
            return BigDecimal(BigInteger.valueOf(x).multiply(y), INFLATED, scale, 0)
        }

        private fun multiply(x: Long, y: BigInteger, scale: Int): BigDecimal {
            if (0L == x) return zeroValueOf(scale)
            return BigDecimal(y.multiply(x), INFLATED, scale, 0)
        }

        private fun multiply(x: BigInteger, y: BigInteger, scale: Int): BigDecimal {
            return BigDecimal(x.multiply(y), INFLATED, scale, 0)
        }

        private inline fun shiftLeft16(n: Long): Long {
            return n.shl(16)
        }
        private var LONGLONG_TEN_POWERS_TABLE = arrayOf(
                longArrayOf(                    0L, shiftLeft16(0x8AC7_2304_89E8L)), //10^19
                longArrayOf(                  0x5L, shiftLeft16(0x6BC7_5E2D_6310L)), //10^20
                longArrayOf(                 0x36L, shiftLeft16(0x35C9_ADC5_DEA0L)), //10^21
                longArrayOf(                0x21EL, shiftLeft16(0x19E0_C9BA_B240L)), //10^22
                longArrayOf(               0x152DL, shiftLeft16(0x02C7_E14A_F680L)), //10^23
                longArrayOf(               0xD3C2L, shiftLeft16(0x1BCE_CCED_A100L)), //10^24
                longArrayOf(             0x8_4595L, shiftLeft16(0x1614_0148_4A00L)), //10^25
                longArrayOf(            0x52_B7D2L, shiftLeft16(0xDCC8_0CD2_E400L)), //10^26
                longArrayOf(           0x33B_2E3CL, shiftLeft16(0x9FD0_803C_E800L)), //10^27
                longArrayOf(          0x204F_CE5EL, shiftLeft16(0x3E25_0261_1000L)), //10^28
                longArrayOf(        0x1_431E_0FAEL, shiftLeft16(0x6D72_17CA_A000L)), //10^29
                longArrayOf(        0xC9_F2C9_CD0L, shiftLeft16(0x4674_EDEA_4000L)), //10^30
                longArrayOf(       0x7E_37BE_2022L, shiftLeft16(0xC091_4B26_8000L)), //10^31
                longArrayOf(      0x4EE_2D6D_415BL, shiftLeft16(0x85AC_EF81_0000L)), //10^32
                longArrayOf(     0x314D_C644_8D93L, shiftLeft16(0x38C1_5B0A_0000L)), //10^33
                longArrayOf(   0x1_ED09_BEAD_87C0L, shiftLeft16(0x378D_8E64_0000L)), //10^34
                longArrayOf(  0x13_4261_72C7_4D82L, shiftLeft16(0x2B87_8FE8_0000L)), //10^35
                longArrayOf(  0xC0_97CE_7BC9_0715L, shiftLeft16(0xB34B_9F10_0000L)), //10^36
                longArrayOf( 0x785_EE10_D5DA_46D9L, shiftLeft16(0x00F4_36A0_0000L)), //10^37
                longArrayOf(0x4B3B_4CA8_5A86_C47AL, shiftLeft16(0x098A_2240_0000L)))//10^38
        private fun precision(hi: Long, lo: Long): Int {
            if (0L == hi) {
                if (lo >= 0) return longDigitLength(lo)
                return if (unsignedLongCompareEq(lo, LONGLONG_TEN_POWERS_TABLE[0][1])) 20 else 19
                // 0x8AC7230489E80000L  = unsigned 2^19
            }
            val r = ((128 - Integer.numberOfLeadingZeros(hi) + 1) * 1233).ushr(12)
            val idx = r - 19
            return if (idx >= LONGLONG_TEN_POWERS_TABLE.size || longLongCompareMagnitude(hi, lo,
                    LONGLONG_TEN_POWERS_TABLE[idx][0], LONGLONG_TEN_POWERS_TABLE[idx][1]))
                        r
                    else
                        r + 1
        }

        /*
         * returns true if 128 bit number <hi0,lo0> is less then <hi1,lo1>
         * hi0 & hi1 should be non-negative
         */
        private fun longLongCompareMagnitude(hi0: Long, lo0: Long, hi1: Long, lo1: Long): Boolean {
            if (hi0 != hi1) {
                return hi0 < hi1
            }
            return lo0 + Long.MIN_VALUE < lo1 + Long.MIN_VALUE
        }

    }

    /**
     * Translates a {@code BigInteger} into a {@code BigDecimal}.
     * The scale of the {@code BigDecimal} is zero.
     *
     * @param v {@code BigInteger} value to be converted to
     *            {@code BigDecimal}.
     */
    constructor(v: BigInteger): this(v, compactValFor(v), 0, 0)

    /**
     * Translates a {@code BigInteger} unscaled value and an
     * {@code int} scale into a {@code BigDecimal}.  The value of
     * the {@code BigDecimal} is
     * <tt>(unscaledVal &times; 10<sup>-scale</sup>)</tt>.
     *
     * @param unscaledVal unscaled value of the {@code BigDecimal}.
     * @param scale scale of the {@code BigDecimal}.
     */
    constructor(unscaledVal: BigInteger, scale: Int):
            this(unscaledVal, compactValFor(unscaledVal), scale, 0)

    fun getPrecision(): Int {
        if (0 == precision) {
            val s = intCompact
            if (s != INFLATED) precision = longDigitLength(s)
            else precision = bigDigitLength(intVal!!)
        }
        return precision
    }

    /**
     * Returns the signum function of this {@code BigDecimal}.
     *
     * @return -1, 0, or 1 as the value of this {@code BigDecimal}
     *         is negative, zero, or positive.
     */
    fun signum(): Int {
        return if (intCompact != INFLATED) Integer.signum(intCompact)
                else intVal!!.signum
    }

    /**
     * Returns a {@code BigDecimal} whose value is {@code (this +
     * augend)}, and whose scale is {@code max(this.scale(),
     * augend.scale())}.
     *
     * @param  augend value to be added to this {@code BigDecimal}.
     * @return {@code this + augend}
     */
    fun add(augend: BigDecimal): BigDecimal {
        if (this.intCompact != INFLATED) {
            if (augend.intCompact != INFLATED)
                return add(this.intCompact, this.scale, augend.intCompact, augend.scale)
            return add(this.intCompact, this.scale, augend.intVal!!, augend.scale)
        }
        if (augend.intCompact != INFLATED)
            return add(augend.intCompact, augend.scale, this.intVal!!, this.scale)
        return add(this.intVal!!, this.scale, augend.intVal!!, augend.scale)
    }

    /**
     * Returns a {@code BigDecimal} whose value is {@code (this -
     * subtrahend)}, and whose scale is {@code max(this.scale(),
     * subtrahend.scale())}.
     *
     * @param  subtrahend value to be subtracted from this {@code BigDecimal}.
     * @return {@code this - subtrahend}
     */
    fun subtract(subtrahend: BigDecimal): BigDecimal {
        if (this.intCompact != INFLATED) {
            if ((subtrahend.intCompact != INFLATED)) {
                return add(this.intCompact, this.scale, -subtrahend.intCompact, subtrahend.scale)
            } else {
                return add(this.intCompact, this.scale, subtrahend.intVal!!.negate(), subtrahend.scale)
            }
        } else {
            if ((subtrahend.intCompact != INFLATED)) {
                // Pair of subtrahend values given before pair of
                // values from this BigDecimal to avoid need for
                // method overloading on the specialized add method
                return add(-subtrahend.intCompact, subtrahend.scale, this.intVal!!, this.scale)
            } else {
                return add(this.intVal!!, this.scale, subtrahend.intVal!!.negate(), subtrahend.scale)
            }
        }
    }

    /**
     * Returns a `BigDecimal` whose value is <tt>(this
     * multiplicand)</tt>, and whose scale is `(this.scale() +
     * multiplicand.scale())`.

     * @param  multiplicand value to be multiplied by this `BigDecimal`.
     * *
     * @return `this * multiplicand`
     */
    fun multiply(multiplicand: BigDecimal): BigDecimal {
        val productScale = checkScale(scale.toLong() + multiplicand.scale)
        if (this.intCompact != INFLATED) {
            if (multiplicand.intCompact != INFLATED) {
                return multiply(this.intCompact, multiplicand.intCompact, productScale)
            } else {
                return multiply(this.intCompact, multiplicand.intVal!!, productScale)
            }
        } else {
            if (multiplicand.intCompact != INFLATED) {
                return multiply(multiplicand.intCompact, this.intVal!!, productScale)
            } else {
                return multiply(this.intVal!!, multiplicand.intVal!!, productScale)
            }
        }
    }

    /**
     * Returns a {@code BigDecimal} whose value is
     * <tt>(this<sup>n</sup>)</tt>, The power is computed exactly, to
     * unlimited precision.
     *
     * <p>The parameter {@code n} must be in the range 0 through
     * 999999999, inclusive.  {@code ZERO.pow(0)} returns {@link
     * #ONE}.
     *
     * Note that future releases may expand the allowable exponent
     * range of this method.
     *
     * @param  n power to raise this {@code BigDecimal} to.
     * @return <tt>this<sup>n</sup></tt>
     * @throws ArithmeticException if {@code n} is out of range.
     */
    fun pow(n: Int): BigDecimal {
        if (n < 0 || n > 999999999) throw ArithmeticException("Invalid operation")
        // No need to calculate pow(n) if result will over/underflow.
        // Don't attempt to support "supernormal" numbers.
        val newScale = checkScale(scale.toLong() * n);
        return BigDecimal(this.inflated().pow(n), newScale)
    }

    /**
     * Returns a `BigDecimal` whose value is `(this /
     * divisor)`, and whose preferred scale is `(this.scale() -
     * divisor.scale())`; if the exact quotient cannot be
     * represented (because it has a non-terminating decimal
     * expansion) an `ArithmeticException` is thrown.

     * @param  divisor value by which this `BigDecimal` is to be divided.
     * @throws ArithmeticException if the exact quotient does not have a
     * *         terminating decimal expansion
     * @return `this / divisor`
     * @author Joseph D. Darcy
     */
    fun divide(divisor: BigDecimal): BigDecimal {
        /*
         * Handle zero cases first.
         */
        if (divisor.signum() == 0) {   // x/0
            if (this.signum() == 0)    // 0/0
                throw ArithmeticException("Division undefined")  // NaN
            throw ArithmeticException("Division by zero")
        }

        // Calculate preferred scale
        val preferredScale = saturateLong(this.scale.toLong() - divisor.scale)

        if (this.signum() == 0) // 0/y
            return zeroValueOf(preferredScale)
        /*
         * If the quotient this/divisor has a terminating decimal
         * expansion, the expansion can have no more than
         * (a.precision() + ceil(10*b.precision)/3) digits.
         * Therefore, create a MathContext object with this
         * precision and do a divide with the UNNECESSARY rounding
         * mode.
         */
        val mc = MathContext(Math.min(this.precision + Math.ceil(10.0 * divisor.precision / 3.0).toLong(),
                                    Int.MAX_VALUE.toLong()).toInt(),
                            RoundingMode.UNNECESSARY)
        val quotient: BigDecimal
        try {
            quotient = this.divide(divisor, mc)
        } catch (e: ArithmeticException) {
            throw ArithmeticException("Non-terminating decimal expansion; " + "no exact representable decimal result.")
        }

        val quotientScale = quotient.scale

        // divide(BigDecimal, mc) tries to adjust the quotient to
        // the desired one by removing trailing zeros; since the
        // exact divide method does not have an explicit digit
        // limit, we can add zeros too.
        if (preferredScale > quotientScale)
            return quotient.setScale(preferredScale, RoundingMode.UNNECESSARY)

        return quotient
    }

    /**
     * Returns a {@code BigDecimal} whose value is {@code (this /
     * divisor)}, and whose scale is as specified.  If rounding must
     * be performed to generate a result with the specified scale, the
     * specified rounding mode is applied.
     *
     * <p>The new {@link #divide(BigDecimal, int, RoundingMode)} method
     * should be used in preference to this legacy method.
     *
     * @param  divisor value by which this {@code BigDecimal} is to be divided.
     * @param  scale scale of the {@code BigDecimal} quotient to be returned.
     * @param  roundingMode rounding mode to apply.
     * @return {@code this / divisor}
     * @throws ArithmeticException if {@code divisor} is zero,
     *         {@code roundingMode==RoundMode.UNNECESSARY} and
     *         the specified scale is insufficient to represent the result
     *         of the division exactly.
     * @throws IllegalArgumentException if {@code roundingMode} does not
     *         represent a valid rounding mode.
     */
    fun divide(divisor: BigDecimal, scale: Int, roundingMode: RoundingMode): BigDecimal {
        if (this.intCompact != INFLATED) {
            if (divisor.intCompact != INFLATED) {
                return divide(this.intCompact, this.scale, divisor.intCompact, divisor.scale, scale, roundingMode)
            }
            return divide(this.intCompact, this.scale, divisor.intVal!!, divisor.scale, scale, roundingMode)
        }
        if (divisor.intCompact != INFLATED) {
            return divide(this.intVal!!, this.scale, divisor.intCompact, divisor.scale, scale, roundingMode)
        }
        return divide(this.intVal!!, this.scale, divisor.intVal!!, divisor.scale, scale, roundingMode)
    }

    /**
     * Returns a {@code BigDecimal} whose value is {@code (this /
     * divisor)}, and whose scale is {@code this.scale()}.  If
     * rounding must be performed to generate a result with the given
     * scale, the specified rounding mode is applied.
     *
     * @param  divisor value by which this {@code BigDecimal} is to be divided.
     * @param  roundingMode rounding mode to apply.
     * @return {@code this / divisor}
     * @throws ArithmeticException if {@code divisor==0}, or
     *         {@code roundingMode==RoundingMode.UNNECESSARY} and
     *         {@code this.scale()} is insufficient to represent the result
     *         of the division exactly.
     */
    fun divide(divisor: BigDecimal, roundingMode: RoundingMode): BigDecimal {
        return divide(divisor, this.scale, roundingMode)
    }

    /**
     * Returns a `BigDecimal` whose value is `(this /
     * divisor)`, with rounding according to the context settings.
     * @param  divisor value by which this `BigDecimal` is to be divided.
     * @param  mc the context to use.
     * @return `this / divisor`, rounded as necessary.
     * @throws ArithmeticException if the result is inexact but the
     * *         rounding mode is `UNNECESSARY` or
     * *         `mc.precision == 0` and the quotient has a
     * *         non-terminating decimal expansion.
     */
    fun divide(divisor: BigDecimal, mc: MathContext): BigDecimal {
        val mcp = mc.precision
        if (mcp == 0)
            return divide(divisor)

        val dividend = this
        val preferredScale = dividend.scale.toLong() - divisor.scale
        // Now calculate the answer.  We use the existing
        // divide-and-round method, but as this rounds to scale we have
        // to normalize the values here to achieve the desired result.
        // For x/y we first handle y=0 and x=0, and then normalize x and
        // y to give x' and y' with the following constraints:
        //   (a) 0.1 <= x' < 1
        //   (b)  x' <= y' < 10*x'
        // Dividing x'/y' with the required scale set to mc.precision then
        // will give a result in the range 0.1 to 1 rounded to exactly
        // the right number of digits (except in the case of a result of
        // 1.000... which can arise when x=y, or when rounding overflows
        // The 1.000... case will reduce properly to 1.
        if (divisor.signum() == 0) {      // x/0
            if (dividend.signum() == 0)   // 0/0
                throw ArithmeticException("Division undefined")  // NaN
            throw ArithmeticException("Division by zero")
        }
        if (dividend.signum() == 0)  // 0/y
            return zeroValueOf(saturateLong(preferredScale))
        val xscale = dividend.precision
        val yscale = divisor.precision
        if (dividend.intCompact != INFLATED) {
            if (divisor.intCompact != INFLATED) {
                return divide(dividend.intCompact, xscale, divisor.intCompact, yscale, preferredScale, mc)
            }
            return divide(dividend.intCompact, xscale, divisor.intVal!!, yscale, preferredScale, mc)
        }
        if (divisor.intCompact != INFLATED) {
            return divide(dividend.intVal!!, xscale, divisor.intCompact, yscale, preferredScale, mc)
        }
        return divide(dividend.intVal!!, xscale, divisor.intVal!!, yscale, preferredScale, mc)
    }

    /**
     * Returns a two-element `BigDecimal` array containing the
     * result of `divideToIntegralValue` followed by the result of
     * `remainder` on the two operands.
     *
     * Note that if both the integer quotient and remainder are
     * needed, this method is faster than using the
     * `divideToIntegralValue` and `remainder` methods
     * separately because the division need only be carried out once.
     *
     * @param  divisor value by which this `BigDecimal` is to be divided,
     * *         and the remainder computed.
     * @return a two element `BigDecimal` array: the quotient
     * *         (the result of `divideToIntegralValue`) is the initial element
     * *         and the remainder is the final element.
     * @throws ArithmeticException if `divisor==0`
     * @see .divideToIntegralValue
     * @see .remainder
     */
    fun divideAndRemainder(divisor: BigDecimal): Array<BigDecimal> {
        // we use the identity  x = i * y + r to determine r
        val q = this.divideToIntegralValue(divisor)
        return arrayOf(q, this.subtract(q.multiply(divisor)))
    }

    /**
     * Returns a `BigDecimal` whose value is the integer part
     * of the quotient `(this / divisor)` rounded down.  The
     * preferred scale of the result is `(this.scale() -
     * divisor.scale())`.
     * @param  divisor value by which this `BigDecimal` is to be divided.
     * @return The integer part of `this / divisor`.
     * @throws ArithmeticException if `divisor==0`
     */
    fun divideToIntegralValue(divisor: BigDecimal): BigDecimal {
        // Calculate preferred scale
        val preferredScale = saturateLong(this.scale.toLong() - divisor.scale)
        if (this.compareMagnitude(divisor) < 0) {
            // much faster when this << divisor
            return zeroValueOf(preferredScale)
        }

        if (this.signum() == 0 && divisor.signum() != 0)
            return this.setScale(preferredScale, RoundingMode.UNNECESSARY)

        // Perform a divide with enough digits to round to a correct
        // integer value; then remove any fractional digits

        val maxDigits = Math.min(this.precision +
                Math.ceil(10.0 * divisor.precision / 3.0).toLong() +
                Math.abs(this.scale.toLong() - divisor.scale) + 2,
                Int.MAX_VALUE.toLong()) as Int
        var quotient = this.divide(divisor, MathContext(maxDigits, RoundingMode.DOWN))
        if (quotient.scale > 0) {
            quotient = quotient.setScale(0, RoundingMode.DOWN)
            quotient = stripZerosToMatchScale(quotient.intVal, quotient.intCompact, quotient.scale, preferredScale)
        }

        if (quotient.scale < preferredScale) {
            // pad with zeros if necessary
            quotient = quotient.setScale(preferredScale, RoundingMode.UNNECESSARY)
        }

        return quotient
    }

    /**
     * Returns a `BigDecimal` which is equivalent to this one
     * with the decimal point moved `n` places to the left.  If
     * `n` is non-negative, the call merely adds `n` to
     * the scale.  If `n` is negative, the call is equivalent
     * to `movePointRight(-n)`.  The `BigDecimal`
     * returned by this call has value <tt>(this
     * 10<sup>-n</sup>)</tt> and scale `max(this.scale()+n,
     * 0)`.

     * @param  n number of places to move the decimal point to the left.
     * *
     * @return a `BigDecimal` which is equivalent to this one with the
     * *         decimal point moved `n` places to the left.
     * *
     * @throws ArithmeticException if scale overflows.
     */
    fun movePointLeft(n: Int): BigDecimal {
        // Cannot use movePointRight(-n) in case of n==Integer.MIN_VALUE
        val newScale = checkScale(scale.toLong() + n)
        val num = BigDecimal(intVal, intCompact, newScale, 0)
        return if (num.scale < 0) num.setScale(0, RoundingMode.UNNECESSARY) else num
    }

    /**
     * Returns a `BigDecimal` which is equivalent to this one
     * with the decimal point moved `n` places to the right.
     * If `n` is non-negative, the call merely subtracts
     * `n` from the scale.  If `n` is negative, the call
     * is equivalent to `movePointLeft(-n)`.  The
     * `BigDecimal` returned by this call has value <tt>(this
     *  10<sup>n</sup>)</tt> and scale `max(this.scale()-n,
     * 0)`.

     * @param  n number of places to move the decimal point to the right.
     * *
     * @return a `BigDecimal` which is equivalent to this one
     * *         with the decimal point moved `n` places to the right.
     * *
     * @throws ArithmeticException if scale overflows.
     */
    fun movePointRight(n: Int): BigDecimal {
        // Cannot use movePointLeft(-n) in case of n==Integer.MIN_VALUE
        val newScale = checkScale(scale.toLong() - n)
        val num = BigDecimal(intVal, intCompact, newScale, 0)
        return if (num.scale < 0) num.setScale(0, RoundingMode.UNNECESSARY) else num
    }

    override fun toByte(): Byte {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun toChar(): Char {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun toDouble(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun toFloat(): Float {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Converts this {@code BigDecimal} to an {@code int}.
     * This conversion is analogous to the
     * <i>narrowing primitive conversion</i> from {@code double} to
     * {@code short} as defined in section 5.1.3 of
     * <cite>The Java&trade; Language Specification</cite>:
     * any fractional part of this
     * {@code BigDecimal} will be discarded, and if the resulting
     * "{@code BigInteger}" is too big to fit in an
     * {@code int}, only the low-order 32 bits are returned.
     * Note that this conversion can lose information about the
     * overall magnitude and precision of this {@code BigDecimal}
     * value as well as return a result with the opposite sign.
     *
     * @return this {@code BigDecimal} converted to an {@code int}.
     */
    override fun toInt(): Int {
        return if (intCompact != INFLATED && 0 == scale) intCompact.toInt()
                else toBigInteger().toInt()
    }

    /**
     * Converts this {@code BigDecimal} to a {@code long}.
     * This conversion is analogous to the
     * <i>narrowing primitive conversion</i> from {@code double} to
     * {@code short} as defined in section 5.1.3 of
     * <cite>The Java&trade; Language Specification</cite>:
     * any fractional part of this
     * {@code BigDecimal} will be discarded, and if the resulting
     * "{@code BigInteger}" is too big to fit in a
     * {@code long}, only the low-order 64 bits are returned.
     * Note that this conversion can lose information about the
     * overall magnitude and precision of this {@code BigDecimal} value as well
     * as return a result with the opposite sign.
     *
     * @return this {@code BigDecimal} converted to a {@code long}.
     */
    override fun toLong(): Long {
        return if (intCompact != INFLATED && 0 == scale) intCompact
        else toBigInteger().toLong()
    }

    override fun toShort(): Short {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Converts this {@code BigDecimal} to a {@code BigInteger}.
     * This conversion is analogous to the
     * <i>narrowing primitive conversion</i> from {@code double} to
     * {@code long} as defined in section 5.1.3 of
     * <cite>The Java&trade; Language Specification</cite>:
     * any fractional part of this
     * {@code BigDecimal} will be discarded.  Note that this
     * conversion can lose information about the precision of the
     * {@code BigDecimal} value.
     * <p>
     * To have an exception thrown if the conversion is inexact (in
     * other words if a nonzero fractional part is discarded), use the
     * {@link #toBigIntegerExact()} method.
     *
     * @return this {@code BigDecimal} converted to a {@code BigInteger}.
     */
    fun toBigInteger(): BigInteger {
        return setScale(0, RoundingMode.DOWN).inflated()
    }

    /**
     * Converts this `BigDecimal` to a `BigInteger`,
     * checking for lost information.  An exception is thrown if this
     * `BigDecimal` has a nonzero fractional part.

     * @return this `BigDecimal` converted to a `BigInteger`.
     * *
     * @throws ArithmeticException if `this` has a nonzero
     * *         fractional part.
     * *
     * @since  1.5
     */
    fun toBigIntegerExact(): BigInteger {
        // round to an integer, with Exception if decimal part non-0
        return this.setScale(0, RoundingMode.UNNECESSARY).inflated()
    }

    /**
     * Returns a {@code BigDecimal} whose scale is the specified
     * value, and whose unscaled value is determined by multiplying or
     * dividing this {@code BigDecimal}'s unscaled value by the
     * appropriate power of ten to maintain its overall value.  If the
     * scale is reduced by the operation, the unscaled value must be
     * divided (rather than multiplied), and the value may be changed;
     * in this case, the specified rounding mode is applied to the
     * division.
     *
     * <p>Note that since BigDecimal objects are immutable, calls of
     * this method do <i>not</i> result in the original object being
     * modified, contrary to the usual convention of having methods
     * named <tt>set<i>X</i></tt> mutate field <i>{@code X}</i>.
     * Instead, {@code setScale} returns an object with the proper
     * scale; the returned object may or may not be newly allocated.
     *
     * @param  newScale scale of the {@code BigDecimal} value to be returned.
     * @param  roundingMode The rounding mode to apply.
     * @return a {@code BigDecimal} whose scale is the specified value,
     *         and whose unscaled value is determined by multiplying or
     *         dividing this {@code BigDecimal}'s unscaled value by the
     *         appropriate power of ten to maintain its overall value.
     * @throws ArithmeticException if {@code roundingMode==UNNECESSARY}
     *         and the specified scaling operation would require
     *         rounding.
     * @see    RoundingMode
     */
    fun setScale(newScale: Int, roundingMode: RoundingMode): BigDecimal {
        if (newScale == this.scale) return this
        if (this.signum() == 0) return zeroValueOf(newScale)
        if (this.intCompact != INFLATED) {
            var rs = this.intCompact
            if (newScale > this.scale) {
                val raise = checkScale(newScale.toLong() - this.scale)
                rs = longMultiplyPowerTen(rs, raise)
                if (rs != INFLATED) return valueOf(rs, newScale)
                val rb = bigMultiplyPowerTen(raise)
                return BigDecimal(rb, INFLATED, newScale,
                        if (precision > 0) precision + raise else 0)
            }
            // newScale < oldScale -- drop some digits
            // Can't predict the precision due to the effect of rounding.
            val drop = checkScale(this.scale.toLong() - newScale)
            if (drop < LONG_TEN_POWERS_TABLE.size) {
                return divideAndRound(rs, LONG_TEN_POWERS_TABLE[drop],
                        newScale, roundingMode, newScale)
            }
            return divideAndRound(this.inflated(), bigTenToThe(drop),
                    newScale, roundingMode, newScale)
        }
        if (newScale > this.scale) {
            val raise = checkScale(newScale.toLong() - this.scale)
            val rb = bigMultiplyPowerTen(this.intVal!!, raise)
            return BigDecimal(rb, INFLATED, newScale,
                    if (precision > 0) precision + raise else 0)
        }
        // newScale < oldScale -- drop some digits
        // Can't predict the precision due to the effect of rounding.
        val drop = checkScale(this.scale.toLong() - newScale)
        if (drop < LONG_TEN_POWERS_TABLE.size) {
            return divideAndRound(this.intVal!!,
                    LONG_TEN_POWERS_TABLE[drop],
                    newScale, roundingMode, newScale)
        }
        return divideAndRound(this.intVal!!, bigTenToThe(drop),
                newScale, roundingMode, newScale)
    }

    /**
     * Returns a {@code BigDecimal} whose scale is the specified
     * value, and whose value is numerically equal to this
     * {@code BigDecimal}'s.  Throws an {@code ArithmeticException}
     * if this is not possible.
     *
     * <p>This call is typically used to increase the scale, in which
     * case it is guaranteed that there exists a {@code BigDecimal}
     * of the specified scale and the correct value.  The call can
     * also be used to reduce the scale if the caller knows that the
     * {@code BigDecimal} has sufficiently many zeros at the end of
     * its fractional part (i.e., factors of ten in its integer value)
     * to allow for the rescaling without changing its value.
     *
     * <p>This method returns the same result as the two-argument
     * versions of {@code setScale}, but saves the caller the trouble
     * of specifying a rounding mode in cases where it is irrelevant.
     *
     * <p>Note that since {@code BigDecimal} objects are immutable,
     * calls of this method do <i>not</i> result in the original
     * object being modified, contrary to the usual convention of
     * having methods named <tt>set<i>X</i></tt> mutate field
     * <i>{@code X}</i>.  Instead, {@code setScale} returns an
     * object with the proper scale; the returned object may or may
     * not be newly allocated.
     *
     * @param  newScale scale of the {@code BigDecimal} value to be returned.
     * @return a {@code BigDecimal} whose scale is the specified value, and
     *         whose unscaled value is determined by multiplying or dividing
     *         this {@code BigDecimal}'s unscaled value by the appropriate
     *         power of ten to maintain its overall value.
     * @throws ArithmeticException if the specified scaling operation would
     *         require rounding.
     * @see    #setScale(int, RoundingMode)
     */
    fun setScale(newScale: Int): BigDecimal {
        return setScale(newScale, RoundingMode.UNNECESSARY)
    }

    fun adjustScale(scl: Int, exp: Long): Int {
        val adjustedScale = scl - exp
        if (adjustedScale > Int.MAX_VALUE || adjustedScale < Int.MIN_VALUE)
            throw NumberFormatException("Scale out of range.")
        return adjustedScale.toInt()
    }

    /**
     * Compute this * 10 ^ n.
     * Needed mainly to allow special casing to trap zero value
     */
    private fun bigMultiplyPowerTen(n: Int): BigInteger {
        if (n <= 0) return this.inflated()
        if (intCompact != INFLATED) return bigTenToThe(n).multiply(intCompact)
        return bigTenToThe(n).multiply(intVal!!)
    }

    /**
     * Check a scale for Underflow or Overflow.  If this BigDecimal is
     * nonzero, throw an exception if the scale is outof range. If this
     * is zero, saturate the scale to the extreme value of the right
     * sign if the scale is out of range.
     *
     * @param v The new scale.
     * @throws ArithmeticException (overflow or underflow) if the new
     *         scale is out of range.
     * @return validated scale as an int.
     */
    private fun checkScale(v: Long): Int {
        var asInt = v.toInt()
        if (asInt.toLong() != v) {
            asInt = if (v > Int.MAX_VALUE) Int.MAX_VALUE else Int.MIN_VALUE
            if (intCompact != 0L && (null == intVal || intVal.signum != 0))
                throw ArithmeticException(if (asInt > 0) "Underflow" else "Overflow")
        }
        return asInt
    }
    /**
     * Returns appropriate BigInteger from intVal field if intVal is
     * null, i.e. the compact representation is in use.
     */
    private fun inflated(): BigInteger {
        return intVal ?: BigInteger.valueOf(intCompact)
    }

    /**
     * Version of compareTo that ignores sign.
     */
    private fun compareMagnitude(v: BigDecimal): Int {
        // Match scales, avoid unnecessary inflation
        var ys = v.intCompact
        var xs = this.intCompact
        if (0L == xs)
            return if (0L == ys) 0 else -1
        if (0L == ys)
            return 1

        val sdiff = this.scale.toLong() - v.scale
        if (sdiff != 0L) {
            // Avoid matching scales if the (adjusted) exponents differ
            val xae = this.precision.toLong() - this.scale   // [-1]
            val yae = v.precision.toLong() - v.scale     // [-1]
            if (xae < yae)
                return -1
            if (xae > yae)
                return 1
            var rb: BigInteger? = null
            if (sdiff < 0) {
                // The cases sdiff <= Integer.MIN_VALUE intentionally fall through.
                if (sdiff > Int.MIN_VALUE && xs != INFLATED) {
                    xs = longMultiplyPowerTen(xs, (-sdiff).toInt())
                }
                if (sdiff > Int.MIN_VALUE && xs == INFLATED && ys == INFLATED) {
                    rb = bigMultiplyPowerTen((-sdiff).toInt())
                    return rb.compareMagnitude(v.intVal!!)
                }
            } else { // sdiff > 0
                // The cases sdiff > Integer.MAX_VALUE intentionally fall through.
                if (sdiff <= Int.MIN_VALUE && ys != INFLATED) {
                    ys = longMultiplyPowerTen(ys, sdiff.toInt())
                }
                if (sdiff <= Int.MAX_VALUE && ys == INFLATED && xs == INFLATED) {
                    rb = v.bigMultiplyPowerTen(sdiff.toInt())
                    return this.intVal!!.compareMagnitude(rb)
                }
            }
        }
        if (xs != INFLATED)
            return if (ys != INFLATED) longCompareMagnitude(xs, ys) else -1
        if (ys != INFLATED)
            return 1
        return this.intVal!!.compareMagnitude(v.intVal!!)
    }

}