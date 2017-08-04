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
 * Portions Copyright (c) 1995  Colin Plumb.  All rights reserved.
 */
package org.firas.math

import org.firas.lang.Integer
import org.firas.lang.Math

/**
 * Immutable arbitrary-precision integers.  All operations behave as if
 * BigIntegers were represented in two's-complement notation (like Java's
 * primitive integer types).  BigInteger provides analogues to all of Java's
 * primitive integer operators, and all relevant methods from java.lang.Math.
 * Additionally, BigInteger provides operations for modular arithmetic, GCD
 * calculation, primality testing, prime generation, bit manipulation,
 * and a few other miscellaneous operations.
 *
 * <p>Semantics of arithmetic operations exactly mimic those of Java's integer
 * arithmetic operators, as defined in <i>The Java Language Specification</i>.
 * For example, division by zero throws an {@code ArithmeticException}, and
 * division of a negative by a positive yields a negative (or zero) remainder.
 * All of the details in the Spec concerning overflow are ignored, as
 * BigIntegers are made as large as necessary to accommodate the results of an
 * operation.
 *
 * <p>Semantics of shift operations extend those of Java's shift operators
 * to allow for negative shift distances.  A right-shift with a negative
 * shift distance results in a left shift, and vice-versa.  The unsigned
 * right shift operator ({@code >>>}) is omitted, as this operation makes
 * little sense in combination with the "infinite word size" abstraction
 * provided by this class.
 *
 * <p>Semantics of bitwise logical operations exactly mimic those of Java's
 * bitwise integer operators.  The binary operators ({@code and},
 * {@code or}, {@code xor}) implicitly perform sign extension on the shorter
 * of the two operands prior to performing the operation.
 *
 * <p>Comparison operations perform signed integer comparisons, analogous to
 * those performed by Java's relational and equality operators.
 *
 * <p>Modular arithmetic operations are provided to compute residues, perform
 * exponentiation, and compute multiplicative inverses.  These methods always
 * return a non-negative result, between {@code 0} and {@code (modulus - 1)},
 * inclusive.
 *
 * <p>Bit operations operate on a single bit of the two's-complement
 * representation of their operand.  If necessary, the operand is sign-
 * extended so that it contains the designated bit.  None of the single-bit
 * operations can produce a BigInteger with a different sign from the
 * BigInteger being operated on, as they affect only a single bit, and the
 * "infinite word size" abstraction provided by this class ensures that there
 * are infinitely many "virtual sign bits" preceding each BigInteger.
 *
 * <p>For the sake of brevity and clarity, pseudo-code is used throughout the
 * descriptions of BigInteger methods.  The pseudo-code expression
 * {@code (i + j)} is shorthand for "a BigInteger whose value is
 * that of the BigInteger {@code i} plus that of the BigInteger {@code j}."
 * The pseudo-code expression {@code (i == j)} is shorthand for
 * "{@code true} if and only if the BigInteger {@code i} represents the same
 * value as the BigInteger {@code j}."  Other pseudo-code expressions are
 * interpreted similarly.
 *
 * <p>All methods and constructors in this class throw
 * {@code NullPointerException} when passed
 * a null object reference for any input parameter.
 *
 * BigInteger must support values in the range
 * -2<sup>{@code Integer.MAX_VALUE}</sup> (exclusive) to
 * +2<sup>{@code Integer.MAX_VALUE}</sup> (exclusive)
 * and may support values outside of that range.
 *
 * The range of probable prime values is limited and may be less than
 * the full supported positive range of {@code BigInteger}.
 * The range must be at least 1 to 2<sup>500000000</sup>.
 *
 * @implNote
 * BigInteger constructors and operations throw {@code ArithmeticException} when
 * the result is out of the supported range of
 * -2<sup>{@code Integer.MAX_VALUE}</sup> (exclusive) to
 * +2<sup>{@code Integer.MAX_VALUE}</sup> (exclusive).
 *
 * @see     BigDecimal
 * @author  Josh Bloch
 * @author  Michael McCloskey
 * @author  Alan Eliasen
 * @author  Timothy Buktu
 */
class BigInteger private constructor(
        internal val signum: Int, internal var mag: IntArray): Number() {

    init {
        mag = stripLeadingZeroInts(mag)
    }

    companion object {
        /**
         * This mask is used to obtain the value of an int as if it were unsigned.
         */
        internal val LONG_MASK = 0xffffffffL

        /**
         * This constant limits {@code mag.length} of BigIntegers to the supported
         * range.
         */
        private val MAX_MAG_LENGTH = Int.MAX_VALUE / 4 + 1 // (1 << 26)

        /**
         * Bit lengths larger than this constant can cause overflow in searchLen
         * calculation and in BitSieve.singleSearch method.
         */
        private val PRIME_SEARCH_BIT_LENGTH_LIMIT = 500000000

        /**
         * The threshold value for using Karatsuba multiplication.  If the number
         * of ints in both mag arrays are greater than this number, then
         * Karatsuba multiplication will be used.   This value is found
         * experimentally to work well.
         */
        private val KARATSUBA_THRESHOLD = 80

        /**
         * The threshold value for using 3-way Toom-Cook multiplication.
         * If the number of ints in each mag array is greater than the
         * Karatsuba threshold, and the number of ints in at least one of
         * the mag arrays is greater than this threshold, then Toom-Cook
         * multiplication will be used.
         */
        private val TOOM_COOK_THRESHOLD = 240

        /**
         * The threshold value for using Karatsuba squaring.  If the number
         * of ints in the number are larger than this value,
         * Karatsuba squaring will be used.   This value is found
         * experimentally to work well.
         */
        private val KARATSUBA_SQUARE_THRESHOLD = 128

        /**
         * The threshold value for using Toom-Cook squaring.  If the number
         * of ints in the number are larger than this value,
         * Toom-Cook squaring will be used.   This value is found
         * experimentally to work well.
         */
        private val TOOM_COOK_SQUARE_THRESHOLD = 216

        /**
         * The threshold value for using Burnikel-Ziegler division.  If the number
         * of ints in the divisor are larger than this value, Burnikel-Ziegler
         * division may be used.  This value is found experimentally to work well.
         */
        internal val BURNIKEL_ZIEGLER_THRESHOLD = 80

        /**
         * The offset value for using Burnikel-Ziegler division.  If the number
         * of ints in the divisor exceeds the Burnikel-Ziegler threshold, and the
         * number of ints in the dividend is greater than the number of ints in the
         * divisor plus this value, Burnikel-Ziegler division will be used.  This
         * value is found experimentally to work well.
         */
        internal val BURNIKEL_ZIEGLER_OFFSET = 40

        /**
         * The threshold value for using Schoenhage recursive base conversion. If
         * the number of ints in the number are larger than this value,
         * the Schoenhage algorithm will be used.  In practice, it appears that the
         * Schoenhage routine is faster for any threshold down to 2, and is
         * relatively flat for thresholds between 2-25, so this choice may be
         * varied within this range for very small effect.
         */
        private val SCHOENHAGE_BASE_CONVERSION_THRESHOLD = 20

        /**
         * The threshold value for using squaring code to perform multiplication
         * of a {@code BigInteger} instance by itself.  If the number of ints in
         * the number are larger than this value, {@code multiply(this)} will
         * return {@code square()}.
         */
        private val MULTIPLY_SQUARE_THRESHOLD = 20

        /*
         * The following two arrays are used for fast String conversions.  Both
         * are indexed by radix.  The first is the number of digits of the given
         * radix that can fit in a Java long without "going negative", i.e., the
         * highest integer n such that radix**n < 2**63.  The second is the
         * "long radix" that tears each number into "long digits", each of which
         * consists of the number of digits in the corresponding element in
         * digitsPerLong (longRadix[i] = i**digitPerLong[i]).  Both arrays have
         * nonsense values in their 0 and 1 elements, as radixes 0 and 1 are not
         * used.
         */
        private var digitsPerLong = intArrayOf(0, 0,
                62, 39, 31, 27, 24, 22, 20, 19, 18, 18, 17, 17, 16, 16, 15, 15, 15,
                14, 14, 14, 14, 13, 13, 13, 13, 13, 13, 12, 12, 12, 12, 12, 12, 12, 12)

        /**
         * Returns a copy of the input array stripped of any leading zero bytes.
         */
        private fun stripLeadingZeroInts(v: IntArray): IntArray {
            val vlen = v.size
            var keep = 0
            while (keep < vlen && 0 == v[keep]) keep += 1
            return v.copyOfRange(vlen - keep, vlen)
        }

        /**
         * Takes an array a representing a negative 2's-complement number and
         * returns the minimal (no leading zero bytes) unsigned whose value is -a.
         */
        private fun makePositive(a: ByteArray): IntArray {
            val byteLength = a.size
            var keep = 0

            // Find first non-sign (0xff) byte of input
            while (keep < byteLength && a[keep].equals(-1))
                keep += 1

            /* Allocate output array.  If all non-sign bytes are 0x00, we must
             * allocate space for one extra output byte. */
            var k = keep
            while (k < byteLength && a[k].equals(0))
                k += 1

            val extraByte = if (k == byteLength) 1 else 0
            val intLength = ((byteLength - keep + extraByte) + 3) ushr 2
            val result = IntArray(intLength)

            /* Copy one's complement of input into output, leaving extra
             * byte (if it exists) == 0x00 */
            var b = byteLength - 1
            var i = intLength - 1
            while (i >= 0) {
                result[i] = a[b].toInt() and 0xff
                b -= 1
                val numBytesToTransfer = Math.min(3, b - keep + 1)
                var j = 8
                while (j <= 8 * numBytesToTransfer) {
                    result[i] = result[i] or ((a[b].toInt() and 0xff) shl j)
                    b -= 1
                    j += 8
                }

                // Mask indicates which bits must be complemented
                var mask = -1 ushr (8*(3-numBytesToTransfer))
                result[i] = result[i].inv() and mask
                i -= 1
            }

            // Add one to one's complement to generate two's complement
            i = result.size - 1
            while (i >= 0) {
                result[i] = ((result[i].toLong() and LONG_MASK) + 1).toInt()
                if (result[i] != 0) break
                i -= 1
            }

            return result
        }

        /**
         * Takes an array a representing a negative 2's-complement number and
         * returns the minimal (no leading zero ints) unsigned whose value is -a.
         */
        private fun makePositive(a: IntArray): IntArray {
            // Find first non-sign (0xffffffff) int of input
            var keep = 0
            while (keep < a.size && a[keep] == -1) keep += 1

            /* Allocate output array.  If all non-sign ints are 0x00, we must
             * allocate space for one extra output int. */
            var j = keep
            while (j < a.size && a[j] == 0) j += 1

            val extraInt = if (j == a.size) 1 else 0
            val result = IntArray(a.size - keep + extraInt)

            /* Copy one's complement of input into output, leaving extra
             * int (if it exists) == 0x00 */
            for (i in keep .. a.size - 1)
                result[i - keep + extraInt] = a[i].inv()

            // Add one to one's complement to generate two's complement
            var i = result.size - 1
            while (++result[i] == 0) i -= 1

            return result
        }

        /**
         * Calculate bitlength of contents of the first len elements an int array,
         * assuming there are no leading zero ints.
         */
        private fun bitLength(v: IntArray, len: Int): Int {
            if (len == 0)
                return 0
            return (len - 1 shl 5) + Integer.highestOneBit(v[0])
        }

        /**
         * Left shift int array a up to len by n bits. Returns the array that
         * results from the shift since space may have to be reallocated.
         */
        fun leftShift(a: IntArray, len: Int, n: Int): IntArray {
            val nInts = n.ushr(5)
            val nBits = n and 0x1F
            val bitsInHighWord = Integer.highestOneBit(a[0])

            // If shift can be done without recopy, do so
            if (n <= 32 - bitsInHighWord) {
                primitiveLeftShift(a, len, nBits)
                return a
            } else { // Array must be resized
                if (nBits <= 32 - bitsInHighWord) {
                    val result = IntArray(nInts + len)
                    for (i in 0 .. len) {
                        result[i] = a[i]
                    }
                    primitiveLeftShift(result, result.size, nBits)
                    return result
                } else {
                    val result = IntArray(nInts + len + 1)
                    for (i in 0 .. len) {
                        result[i] = a[i]
                    }
                    primitiveRightShift(result, result.size, 32 - nBits)
                    return result
                }
            }
        }

        // shifts a up to len right n bits assumes no leading zeros, 0<n<32
        fun primitiveRightShift(a: IntArray, len: Int, n: Int) {
            val n2 = 32 - n
            var i = len - 1
            var c = a[i]
            while (i > 0) {
                val b = c
                c = a[i - 1]
                a[i] = c shl n2 or b.ushr(n)
                i -= 1
            }
            a[0] = a[0] ushr n
        }

        // shifts a up to len left n bits assumes no leading zeros, 0<=n<32
        fun primitiveLeftShift(a: IntArray, len: Int, n: Int) {
            if (len == 0 || n == 0)
                return

            val n2 = 32 - n
            var i = 0
            var c = a[i]
            val m = i + len - 1
            while (i < m) {
                val b = c
                c = a[i + 1]
                a[i] = b shl n or c.ushr(n2)
                i += 1
            }
            a[len - 1] = a[len - 1] shl n
        }
    }

    override fun toByte(): Byte {
        return toInt().toByte()
    }

    override fun toChar(): Char {
        return toInt().toChar()
    }

    override fun toDouble(): Double {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun toFloat(): Float {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun toInt(): Int {
        return getInt(0)
    }

    override fun toLong(): Long {
        return (getInt(0).toLong() and LONG_MASK) +
                (getInt(1).toLong() and LONG_MASK).shl(Integer.SIZE)
    }

    override fun toShort(): Short {
        return toInt().toShort()
    }

    /**
     * Returns the number of bits in the minimal two's-complement
     * representation of this BigInteger, <i>excluding</i> a sign bit.
     * For positive BigIntegers, this is equivalent to the number of bits in
     * the ordinary binary representation.  (Computes
     * {@code (ceil(log2(this < 0 ? -this : this+1)))}.)
     *
     * @return number of bits in the minimal two's-complement
     *         representation of this BigInteger, <i>excluding</i> a sign bit.
     */
    fun bitLength(): Int {
        val m = mag
        val len = m.size
        if (len == 0) return 0
        // Calculate the bit length of the magnitude
        val magBitLength = ((len - 1) shl 5) + Integer.highestOneBit(mag[0])
        if (signum < 0) {
            // Check if magnitude is a power of two
            var pow2 = (Integer.bitCount(mag[0]) == 1)
            var i = 1
            while (i< len && pow2) {
                pow2 = (mag[i] == 0)
                i += 1
            }

            return if (pow2) magBitLength -1 else magBitLength
        }
        return magBitLength
    }

    /**
     * Returns the number of bits in the two's complement representation
     * of this BigInteger that differ from its sign bit.  This method is
     * useful when implementing bit-vector style sets atop BigIntegers.
     *
     * @return number of bits in the two's complement representation
     *         of this BigInteger that differ from its sign bit.
     */
    fun bitCount(): Int {
        var bc = mag.sumBy { Integer.bitCount(it) }
        // Count the bits in the magnitude
        if (signum < 0) { // Count the trailing zeros in the magnitude
            var magTrailingZeroCount = 0
            var j = mag.size - 1
            while (0 == mag[j]) {
                magTrailingZeroCount += 32
                j -= 0
            }
            magTrailingZeroCount += Integer.numberOfTrailingZeros(mag[j])
            bc += magTrailingZeroCount - 1
        }
        return bc
    }

    /**
     * Returns the length of the two's complement representation in ints,
     * including space for at least one sign bit.
     */
    private fun intLength(): Int {
        return (bitLength() ushr 5) + 1
    }

    /* Returns sign bit */
    private fun signBit(): Int {
        return if (signum < 0) 1 else 0
    }

    /* Returns an int of sign bits */
    private fun signInt(): Int {
        return if (signum < 0) -1 else 0;
    }

    /**
     * Returns the specified int of the little-endian two's complement
     * representation (int 0 is the least significant).  The int number can
     * be arbitrarily high (values are logically preceded by infinitely many
     * sign ints).
     */
    private fun getInt(n: Int): Int {
        if (n < 0) return 0
        if (n >= mag.size) return signInt()

        val magInt = mag[mag.size - n - 1]

        return if (signum >= 0) magInt else
                if (n <= firstNonzeroIntNum()) -magInt else magInt.inv()
    }

    /**
     * Returns the index of the int that contains the first nonzero int in the
     * little-endian binary representation of the magnitude (int 0 is the
     * least significant). If the magnitude is zero, return value is undefined.
     */
    private fun firstNonzeroIntNum(): Int {
        // Search for the first nonzero int
        val mlen = mag.size
        var i = mlen - 1
        while (i >= 0 && 0 == mag[i])
            i -= 1
        return mlen - i - 1
    }
}