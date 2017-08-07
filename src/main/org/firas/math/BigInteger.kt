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

import org.firas.lang.ArithmeticException
import org.firas.lang.Integer
import org.firas.lang.Math
import org.firas.lang.UIntComparator

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
         * The BigInteger constant zero.
         */
        val ZERO = BigInteger(0, intArrayOf())

        val ONE = BigInteger(1, intArrayOf(1))
        val TEN = BigInteger(1, intArrayOf(10))

        internal val NEGATIVE_ONE = BigInteger(-1, intArrayOf(1))

        /**
         * This mask is used to obtain the value of an int as if it were unsigned.
         */
        internal val LONG_MASK = 0xffffffffL

        fun valueOf(v: Long): BigInteger {
            if (0L == v) return ZERO
            if (1L == v) return ONE
            if (10L == v) return TEN
            var signum: Int
            var temp: Long
            if (v < 0) {
                signum = -1
                temp = -v
            } else {
                signum = 1
                temp = v
            }
            val hi = temp.ushr(32).toInt()
            var mag: IntArray
            if (0 == hi) {
                mag = intArrayOf(temp.toInt())
            } else {
                mag = intArrayOf(hi, temp.toInt())
            }
            return BigInteger(signum, mag)
        }

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

        private val comparator = UIntComparator()

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
         * Returns the input array stripped of any leading zero bytes.
         * Since the source is trusted the copying may be skipped.
         */
        private fun trustedStripLeadingZeroInts(v: IntArray): IntArray {
            val vlen = v.size
            var keep = 0
            while (keep < vlen && 0 == v[keep]) keep += 1
            return if (0 == keep) v else v.copyOfRange(vlen - keep, vlen)
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
                    val result = a.copyOf(nInts + len)
                    primitiveLeftShift(result, result.size, nBits)
                    return result
                } else {
                    val result = a.copyOf(nInts + len + 1)
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

        /**
         * Multiply an array by one word k and add to result, return the carry
         */
        internal fun mulAdd(o: IntArray, i: IntArray, offset: Int, len: Int, k: Int): Int {
            val kLong = k.toLong() and LONG_MASK
            var carry = 0
            var offset = o.size - offset - 1
            var j = len - 1
            while (j >= 0) {
                val product = i[j].toLong().and(LONG_MASK) * kLong +
                        o[offset].toLong().and(LONG_MASK) + carry
                o[offset] = product.toInt()
                offset -= 1
                carry = product.ushr(32).toInt()
            }
            return carry
        }

        /**
         * Add one word to the number a mlen words into a. Return the resulting
         * carry.
         */
        internal fun addOne(a: IntArray, offset: Int, mlen: Int, carry: Int): Int {
            var offset = a.size - mlen - offset - 1
            val t = a[offset].toLong().and(LONG_MASK) + carry.toLong().and(LONG_MASK)
            a[offset] = t.toInt()
            if (t.ushr(32) == 0L) return 0

            var mlen = mlen - 1
            while (mlen >= 0) {
                offset -= 1
                if (offset < 0) return 1 // Carry out of number
                a[offset] += 1
                if (a[offset] != 0) return 0
                mlen -= 1
            }
            return 1
        }

        /**
         * Returns a magnitude array whose value is {@code (mag << n)}.
         * The shift distance, {@code n}, is considered unnsigned.
         * (Computes <tt>this * 2<sup>n</sup></tt>.)
         *
         * @param mag magnitude, the most-significant int ({@code mag[0]}) must be non-zero.
         * @param  n unsigned shift distance, in bits.
         * @return {@code mag << n}
         */
        private fun shiftLeft(mag: IntArray, n: Int): IntArray {
            val nInts = n ushr 5
            val nBits = n and 0x1F
            var newMag: IntArray
            if (0 == nBits) {
                newMag = mag.copyOf(mag.size + nInts)
            } else {
                var i = 0
                val nBits2 = Integer.SIZE - nBits
                val highBits = mag[0] ushr nBits2
                if (highBits != 0) {
                    newMag = IntArray(mag.size + nInts + 1)
                    newMag[i] = highBits
                    i += 1
                } else {
                    newMag = IntArray(mag.size + nInts)
                }
                var j = 0
                while (j < mag.size - 1) {
                    newMag[i] = mag[j] shl nBits
                    j += 1
                    newMag[i] = newMag[i] or mag[j].ushr(nBits2)
                    i += 1
                }
                newMag[i] = mag[j] shl nBits
            }
            return newMag
        }

        private fun reportOverflow() {
            throw ArithmeticException("BigInteger would overflow supported range")
        }

        private fun valueOf(v: IntArray): BigInteger {
            if (v.isEmpty()) throw NumberFormatException("Zero length BigInteger")
            if (v[0] > 0) return BigInteger(1, v)
            return BigInteger(-1, makePositive(v))
        }

        private fun multiplyByInt(x: IntArray, y: Int, sign: Int): BigInteger {
            if (Integer.bitCount(y) == 1) {
                return BigInteger(sign, shiftLeft(x, Integer.numberOfTrailingZeros(y)))
            }
            var rstart = x.size
            val rmag = IntArray(rstart + 1)
            var carry = 0
            val y1 = y.toLong() and LONG_MASK
            var i = rstart - 1
            while (i >= 0) {
                val product = x[i].toLong().and(LONG_MASK) * y1 + carry
                rmag[rstart] = product.toInt()
                rstart -= 1
                carry = product.ushr(Integer.SIZE).toInt()
            }
            if (0 == carry) return BigInteger(sign, rmag.copyOfRange(1, rmag.size))
            rmag[rstart] = carry
            return BigInteger(sign, rmag)
        }

        /**
         * Squares the contents of the int array x. The result is placed into the
         * int array z.  The contents of x are not changed.
         */
        private fun squareToLen(x: IntArray, len: Int, z: IntArray?): IntArray {
            /*
             * The algorithm used here is adapted from Colin Plumb's C library.
             * Technique: Consider the partial products in the multiplication
             * of "abcde" by itself:
             *
             *               a  b  c  d  e
             *            *  a  b  c  d  e
             *          ==================
             *              ae be ce de ee
             *           ad bd cd dd de
             *        ac bc cc cd ce
             *     ab bb bc bd be
             *  aa ab ac ad ae
             *
             * Note that everything above the main diagonal:
             *              ae be ce de = (abcd) * e
             *           ad bd cd       = (abc) * d
             *        ac bc             = (ab) * c
             *     ab                   = (a) * b
             *
             * is a copy of everything below the main diagonal:
             *                       de
             *                 cd ce
             *           bc bd be
             *     ab ac ad ae
             *
             * Thus, the sum is 2 * (off the diagonal) + diagonal.
             *
             * This is accumulated beginning with the diagonal (which
             * consist of the squares of the digits of the input), which is then
             * divided by two, the off-diagonal added, and multiplied by two
             * again.  The low bit is simply a copy of the low bit of the
             * input, so it doesn't need special care.
             */
            val zlen = len shl 1
            val z1: IntArray = if (null == z || z.size < zlen) IntArray(zlen) else z

            // Store the squares, right shifted one bit (i.e., divided by 2)
            var lastProductLowWord = 0
            var j = 0
            var i = 0
            while (j < len) {
                val piece = x[j].toLong() and LONG_MASK
                val product = piece * piece
                z1[i] = lastProductLowWord.shl(31) or product.ushr(33).toInt()
                i += 1
                z1[i] = product.ushr(1).toInt()
                i += 1
                lastProductLowWord = product.toInt()
                j += 1
            }

            // Add in off-diagonal sums
            i = len
            j = 1
            while (i > 0) {
                var t = x[i-1]
                t = mulAdd(z1, x, j, i-1, t)
                addOne(z1, j - 1, i, t)
                i -= 1
                j += 2
            }

            // Shift back up and set low bit
            primitiveLeftShift(z1, zlen, 1)
            z1[zlen-1] = z1[zlen-1].or(x[len-1] and 1)

            return z1
        }
    }

    /**
     * Returns a BigInteger whose value is {@code (-this)}.
     *
     * @return {@code -this}
     */
    fun negate(): BigInteger {
        return BigInteger(-signum, mag)
    }

    /**
     * Returns a BigInteger whose value is the absolute value of this
     * BigInteger.
     *
     * @return {@code abs(this)}
     */
    fun abs(): BigInteger {
        return if (signum >= 0) this else negate()
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
     * Returns a BigInteger whose value is {@code (this & val)}.  (This
     * method returns a negative BigInteger if and only if this and val are
     * both negative.)
     *
     * @param val value to be AND'ed with this BigInteger.
     * @return {@code this & val}
     */
    fun and(v: BigInteger): BigInteger {
        val result = IntArray(Math.max(intLength(), v.intLength()))
        for (i in 0 .. (result.size - 1)) {
            val j = result.size - i - 1
            result[i] = getInt(j) and v.getInt(j)
        }
        return valueOf(result)
    }

    /**
     * Returns a BigInteger whose value is {@code (this | val)}.  (This method
     * returns a negative BigInteger if and only if either this or val is
     * negative.)
     *
     * @param val value to be OR'ed with this BigInteger.
     * @return {@code this | val}
     */
    fun or(v: BigInteger): BigInteger {
        val result = IntArray(Math.max(intLength(), v.intLength()))
        for (i in 0 .. (result.size - 1)) {
            val j = result.size - i - 1
            result[i] = getInt(j) or v.getInt(j)
        }
        return valueOf(result)
    }

    /**
     * Returns a BigInteger whose value is {@code (this ^ val)}.  (This method
     * returns a negative BigInteger if and only if exactly one of this and
     * val are negative.)
     *
     * @param val value to be XOR'ed with this BigInteger.
     * @return {@code this ^ val}
     */
    fun xor(v: BigInteger): BigInteger {
        val result = IntArray(Math.max(intLength(), v.intLength()))
        for (i in 0 .. (result.size - 1)) {
            val j = result.size - i - 1
            result[i] = getInt(j) xor v.getInt(j)
        }
        return valueOf(result)
    }

    /**
     * Returns a BigInteger whose value is {@code (~this)}.  (This method
     * returns a negative value if and only if this BigInteger is
     * non-negative.)
     *
     * @return {@code ~this}
     */
    fun not(): BigInteger {
        val result = IntArray(intLength())
        for (i in 0 .. (result.size - 1)) {
            result[i] = getInt(result.size - i - 1).inv()
        }
        return valueOf(result)
    }

    /**
     * Returns a BigInteger whose value is {@code (this & ~val)}.  This
     * method, which is equivalent to {@code and(val.not())}, is provided as
     * a convenience for masking operations.  (This method returns a negative
     * BigInteger if and only if {@code this} is negative and {@code val} is
     * positive.)
     *
     * @param val value to be complemented and AND'ed with this BigInteger.
     * @return {@code this & ~val}
     */
    fun andNot(v: BigInteger): BigInteger {
        val result = IntArray(Math.max(intLength(), v.intLength()))
        for (i in 0 .. (result.size - 1)) {
            val j = result.size - i - 1
            result[i] = getInt(j) and v.getInt(j).inv()
        }
        return valueOf(result)
    }


    /**
     * Returns {@code true} if and only if the designated bit is set.
     * (Computes {@code ((this & (1<<n)) != 0)}.)
     *
     * @param  n index of bit to test.
     * @return {@code true} if and only if the designated bit is set.
     * @throws ArithmeticException {@code n} is negative.
     */
    fun testBit(n: Int): Boolean {
        if (n < 0) throw ArithmeticException("Negative bit address")
        return (getInt(n ushr 5) and 1.shr(n and 31)) != 0
    }

    /**
     * Returns a BigInteger whose value is equivalent to this BigInteger
     * with the designated bit set.  (Computes {@code (this | (1<<n))}.)
     *
     * @param  n index of bit to set.
     * @return {@code this | (1<<n)}
     * @throws ArithmeticException {@code n} is negative.
     */
    fun setBit(n: Int): BigInteger {
        if (n < 0) throw ArithmeticException("Negative bit address")
        val intNum = n ushr 5
        val result = IntArray(Math.max(intLength(), intNum + 2))
        for (i in 0 .. (result.size - 1)) {
            result[result.size - i - 1] = getInt(i)
        }
        result[result.size - intNum - 1] = result[result.size - intNum - 1] or
                1.shl(n and 0x1F)
        return valueOf(result)
    }

    /**
     * Returns a BigInteger whose value is equivalent to this BigInteger
     * with the designated bit cleared.
     * (Computes {@code (this & ~(1<<n))}.)
     *
     * @param  n index of bit to clear.
     * @return {@code this & ~(1<<n)}
     * @throws ArithmeticException {@code n} is negative.
     */
    fun clearBit(n: Int): BigInteger {
        if (n < 0) throw ArithmeticException("Negative bit address")
        val intNum = n ushr 5
        val result = IntArray(Math.max(intLength(), intNum + 2))
        for (i in 0 .. (result.size - 1)) {
            result[result.size - i - 1] = getInt(i)
        }
        result[result.size - intNum - 1] = result[result.size - intNum - 1] and
                1.shl(n and 0x1F).inv()
        return valueOf(result)
    }

    /**
     * Returns a BigInteger whose value is equivalent to this BigInteger
     * with the designated bit flipped.
     * (Computes {@code (this ^ (1<<n))}.)
     *
     * @param  n index of bit to flip.
     * @return {@code this ^ (1<<n)}
     * @throws ArithmeticException {@code n} is negative.
     */
    fun flipBit(n: Int): BigInteger {
        if (n < 0) throw ArithmeticException("Negative bit address")
        val intNum = n ushr 5
        val result = IntArray(Math.max(intLength(), intNum + 2))
        for (i in 0 .. (result.size - 1)) {
            result[result.size - i - 1] = getInt(i)
        }
        result[result.size - intNum - 1] = result[result.size - intNum - 1] xor
                1.shl(n and 0x1F)
        return valueOf(result)
    }

    /**
     * Returns the index of the rightmost (lowest-order) one bit in this
     * BigInteger (the number of zero bits to the right of the rightmost
     * one bit).  Returns -1 if this BigInteger contains no one bits.
     * (Computes {@code (this == 0? -1 : log2(this & -this))}.)
     *
     * @return index of the rightmost one bit in this BigInteger.
     */
    fun getLowestSetBit(): Int {
        var lsb = 0
        if (0 == signum) lsb -= 1
        else {
            // Search for lowest order nonzero int
            var i = 0
            var b: Int
            while (true) {
                b = getInt(i)
                if (b != 0) break
                i += 1
            }
            lsb += i.shl(5) + Integer.numberOfTrailingZeros(b)
        }
        return lsb
    }


    /**
     * Throws an {@code ArithmeticException} if the {@code BigInteger} would be
     * out of the supported range.
     *
     * @throws ArithmeticException if {@code this} exceeds the supported range.
     */
    fun shiftLeft(n: Int): BigInteger {
        if (0 == signum) return ZERO
        if (0 == n) return this
        if (n > 0) return BigInteger(signum, shiftLeft(mag, n))
        // Possible int overflow in (-n) is not a trouble,
        // because shiftRightImpl considers its argument unsigned
        return shiftRightImpl(-n)
    }

    /**
     * Returns a BigInteger whose value is {@code (this >> n)}.  Sign
     * extension is performed.  The shift distance, {@code n}, may be
     * negative, in which case this method performs a left shift.
     * (Computes <tt>floor(this / 2<sup>n</sup>)</tt>.)
     *
     * @param  n shift distance, in bits.
     * @return {@code this >> n}
     * @see #shiftLeft
     */
    fun shiftRight(n: Int): BigInteger {
        if (0 == signum) return ZERO
        if (n > 0) return shiftRightImpl(n)
        if (0 == n) return this
        // Possible int overflow in {@code -n} is not a trouble,
        // because shiftLeft considers its argument unsigned
        return BigInteger(signum, shiftLeft(mag, -n))
    }


    /**
     * Returns a BigInteger whose value is {@code (this<sup>2</sup>)}.
     *
     * @return {@code this<sup>2</sup>}
     */
    fun square(): BigInteger {
        if (0 == signum) return ZERO
        if (mag.size < KARATSUBA_SQUARE_THRESHOLD) {
            val z = squareToLen(mag, mag.size, null)
            return BigInteger(1, trustedStripLeadingZeroInts(z))
        }
        if (mag.size < TOOM_COOK_SQUARE_THRESHOLD) {
            return squareKaratsuba()
        }
        return squareToomCook3()
    }

    /**
     * Returns a BigInteger whose value is {@code (this * val)}.
     *
     * @implNote An implementation may offer better algorithmic
     * performance when {@code val == this}.
     *
     * @param  val value to be multiplied by this BigInteger.
     * @return {@code this * val}
     */
    fun multiply(v: BigInteger): BigInteger {
        if (0 == signum || 0 == v.signum) return ZERO
        if (this == v && mag.size > MULTIPLY_SQUARE_THRESHOLD) return square()
        if (mag.size < KARATSUBA_THRESHOLD || v.mag.size < KARATSUBA_THRESHOLD) {
            val resultSign = if (signum == v.signum) 1 else -1
            if (1 == v.mag.size) return multiplyByInt(mag, v.mag[0], resultSign)
            if (1 == mag.size) return multiplyByInt(v.mag, mag[0], resultSign)
        }
    }


    /**
     * Returns a BigInteger whose value is <tt>(this<sup>exponent</sup>)</tt>.
     * Note that {@code exponent} is an integer rather than a BigInteger.
     *
     * @param  exponent exponent to which this BigInteger is to be raised.
     * @return <tt>this<sup>exponent</sup></tt>
     * @throws ArithmeticException {@code exponent} is negative.  (This would
     *         cause the operation to yield a non-integer value.)
     */
    fun pow(exponent: Int): BigInteger {
        if (exponent < 0) throw ArithmeticException("Negative exponent")
        if (0 == exponent) return ONE
        if (0 == signum) return this
        var partToSquare = abs()

        // Factor out powers of two from the base, as the exponentiation of
        // these can be done by left shifts only.
        // The remaining part can then be exponentiated faster.  The
        // powers of two will be multiplied back at the end.
        val powersOfTwo = partToSquare.getLowestSetBit()
        val bitsToShift = powersOfTwo.toLong() * exponent
        if (bitsToShift > Int.MAX_VALUE) reportOverflow()

        var remainingBits = partToSquare.bitLength()
        if (powersOfTwo > 0) {
            partToSquare = partToSquare.shiftRight(powersOfTwo)
            if (1 == remainingBits) {
                if (signum < 0 && exponent.and(1) == 1) {
                    return NEGATIVE_ONE.shiftLeft(powersOfTwo * exponent)
                }
                return ONE.shiftLeft(powersOfTwo * exponent)
            }
        } else {
            if (1 == remainingBits) {
                if (signum < 0 && exponent.and(1) == 1) {
                    return NEGATIVE_ONE
                }
                return ONE
            }
        }

        // This is a quick way to approximate the size of the result,
        // similar to doing log2[n] * exponent.  This will give an upper bound
        // of how big the result can be, and which algorithm to use.
        val scaleFactor = remainingBits.toLong() * exponent

        var workingExponent = exponent

        // Use slightly different algorithms for small and large operands.
        // See if the result will safely fit into a long. (Largest 2^63-1)
        if (partToSquare.mag.size == 1 && scaleFactor <= 62) {
            // Small number algorithm.  Everything fits into a long.
            val newSign = if (signum <0  && exponent.and(1) == 1) -1 else 1
            var result = 1L
            var baseToPow2 = partToSquare.mag[0].toLong() and LONG_MASK

            // Perform exponentiation using repeated squaring trick
            while (workingExponent != 0) {
                if (workingExponent.and(1) == 1) {
                    result = result * baseToPow2
                }

                workingExponent = workingExponent ushr 1
                if (workingExponent != 0) {
                    baseToPow2 = baseToPow2 * baseToPow2
                }
            }

            // Multiply back the powers of two (quickly, by shifting left)
            if (powersOfTwo > 0) {
                if (bitsToShift + scaleFactor <= 62) { // Fits in long?
                    return valueOf(result.shl(bitsToShift.toInt()) * newSign)
                }
                return valueOf(result * newSign).shiftLeft(bitsToShift.toInt())
            }
            return valueOf(result * newSign)
        }
        // Large number algorithm.  This is basically identical to
        // the algorithm above, but calls multiply() and square()
        // which may use more efficient algorithms for large numbers.
        var answer = ONE

        // Perform exponentiation using repeated squaring trick
        while (workingExponent != 0) {
            if (workingExponent.and(1) == 1) {
                answer = answer.multiply(partToSquare);
            }

            workingExponent = workingExponent.ushr(1)
            if (workingExponent != 0) {
                partToSquare = partToSquare.square()
            }
        }
        // Multiply back the (exponentiated) powers of two (quickly,
        // by shifting left)
        if (powersOfTwo > 0) {
            answer = answer.shiftLeft(powersOfTwo * exponent)
        }

        if (signum < 0 && exponent.and(1) == 1) {
            return answer.negate()
        }
        return answer
    }

    /**
     * Compares the magnitude array of this BigInteger with the specified
     * BigInteger's. This is the version of compareTo ignoring sign.
     *
     * @param val BigInteger whose magnitude array to be compared.
     * @return -1, 0 or 1 as this magnitude array is less than, equal to or
     *         greater than the magnitude aray for the specified BigInteger's.
     */
    internal fun compareMagnitude(v: BigInteger): Int {
        if (mag.size > v.mag.size) return 1
        if (mag.size < v.mag.size) return -1
        for (i in 0 .. (mag.size - 1)) {
            val temp = comparator.compare(mag[i], v.mag[i])
            if (0 != temp) return temp
        }
        return 0
    }

    internal fun javaIncrement(v: IntArray): IntArray {
        var lastSum = 0
        var i = v.size - 1
        while (i >= 0 && 0 == lastSum) {
            v[i] += 1
            lastSum = v[i]
            i -= 1
        }
        if (0 == lastSum) {
            val result = IntArray(v.size + 1)
            result[0] = 1
            return result
        }
        return v
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

    /**
     * Returns a magnitude array whose value is {@code (mag << n)}.
     * The shift distance, {@code n}, is considered unnsigned.
     * (Computes <tt>this * 2<sup>n</sup></tt>.)
     *
     * @param mag magnitude, the most-significant int ({@code mag[0]}) must be non-zero.
     * @param  n unsigned shift distance, in bits.
     * @return {@code mag << n}
     */
    private fun shiftLeft(mag: IntArray, n: Int): IntArray {
        val nInts = n.ushr(5)
        val nBits = n.and(0x1F)
        var newMag: IntArray

        if (0 == nBits) {
            newMag = mag.copyOf(mag.size + nInts)
        } else {
            var i = 0
            val nBits2 = Integer.SIZE - nBits
            val highBits = mag[0].ushr(nBits2)
            if (highBits != 0) {
                newMag = IntArray(mag.size + nInts + 1)
                newMag[i] = highBits
                i += 1
            } else {
                newMag = IntArray(mag.size + nInts)
            }
            var j = 0
            while (j < mag.size - 1) {
                newMag[i] = mag[j] shl nBits
                j += 1
                newMag[i] = newMag[i] or mag[j].ushr(nBits2)
                i += 1
            }
            newMag[i] = mag[j] shl nBits
        }
        return newMag
    }

    /**
     * Returns a BigInteger whose value is {@code (this >> n)}. The shift
     * distance, {@code n}, is considered unsigned.
     * (Computes <tt>floor(this * 2<sup>-n</sup>)</tt>.)
     *
     * @param  n unsigned shift distance, in bits.
     * @return {@code this >> n}
     */
    private fun shiftRightImpl(n: Int): BigInteger {
        val nInts = n ushr 5
        val nBits = n and 0x1F
        var newMag: IntArray

        // Special case: entire contents shifted off the end
        if (nInts >= mag.size) {
            return if (signum >= 0) ZERO else NEGATIVE_ONE
        }
        if (0 == nBits) {
            newMag = mag.copyOfRange(0, mag.size - nInts)
        } else {
            var i = 0
            val highBits = mag[0] ushr nBits
            if (highBits != 0) {
                newMag = IntArray(mag.size - nInts)
                newMag[i] = highBits
                i += 1
            } else {
                newMag = IntArray(mag.size - nInts - 1)
            }
            val nBits2 = Integer.SIZE - nBits
            var j = 0
            while (j < mag.size - nInts - 1) {
                newMag[i] = mag[j] shl nBits2
                j += 1
                newMag[i] = newMag[i] or mag[j].ushr(nBits)
                i += 1
            }
        }

        if (signum < 0) {
            // Find out whether any one-bits were shifted off the end.
            var onesLost = false
            var i = mag.size - 1
            var j = mag.size - nInts
            while (i >= j && !onesLost) {
                onesLost = (mag[i] != 0)
                i -= 1
            }
            if (!onesLost && nBits != 0)
                onesLost = (mag[mag.size - nInts - 1] shl (32 - nBits) != 0)

            if (onesLost)
                newMag = javaIncrement(newMag)
        }
        return BigInteger(signum, newMag)
    }

    /**
     * Squares a BigInteger using the Karatsuba squaring algorithm.  It should
     * be used when both numbers are larger than a certain threshold (found
     * experimentally).  It is a recursive divide-and-conquer algorithm that
     * has better asymptotic performance than the algorithm used in
     * squareToLen.
     */
    private fun squareKaratsuba(): BigInteger {
        val half = (mag.size + 1) shr 1
        val xl = getLower(half)
        val xh = getUpper(half)
        val xls = xl.square()
        val xhs = xh.square()
        // xh^2 << 64  +  (((xl+xh)^2 - (xh^2 + xl^2)) << 32) + xl^2
        return xhs.shiftLeft(half * 32).add(xl.add(xh).square()
                .subtract(xhs.add(xls))).shiftLeft(half * 32).add(xls)
    }

    /**
     * Squares a BigInteger using the 3-way Toom-Cook squaring algorithm.  It
     * should be used when both numbers are larger than a certain threshold
     * (found experimentally).  It is a recursive divide-and-conquer algorithm
     * that has better asymptotic performance than the algorithm used in
     * squareToLen or squareKaratsuba.
     */
    private fun squareToomCook3(): BigInteger {
        // k is the size (in ints) of the lower-order slices.
        val k = (mag.size + 2) / 3   // Equal to ceil(largest/3)

        // r is the size (in ints) of the highest-order slice.
        val r = mag.size - 2 * k

        // Obtain slices of the numbers. a2 is the most significant
        // bits of the number, and a0 the least significant.
        val a2 = getToomSlice(k, r, 0, mag.size)
        val a1 = getToomSlice(k, r, 1, mag.size)
        val a0 = getToomSlice(k, r, 2, mag.size)

        val v0 = a0.square()
        var da1 = a2.add(a0)
        val vm1 = da1.subtract(a1).square()
        da1 = da1.add(a1)
        val v1 = da1.square()
        val vinf = a2.square()
        val v2 = da1.add(a2).shiftLeft(1).subtract(a0).square()

        // The algorithm requires two divisions by 2 and one by 3.
        // All divisions are known to be exact, that is, they do not produce
        // remainders, and all results are positive.  The divisions by 2 are
        // implemented as right shifts which are relatively efficient, leaving
        // only a division by 3.
        // The division by 3 is done by an optimized algorithm for this case.
        var t2 = v2.subtract(vm1).exactDivideBy3()
        var tm1 = v1.subtract(vm1).shiftRight(1)
        var t1 = v1.subtract(v0)
        t2 = t2.subtract(t1).shiftRight(1)
        t1 = t1.subtract(tm1).subtract(vinf)
        t2 = t2.subtract(vinf.shiftLeft(1))
        tm1 = tm1.subtract(t2)

        // Number of bits to shift left.
        val ss = k * 32

        return vinf.shiftLeft(ss).add(t2).shiftLeft(ss)
                .add(t1).shiftLeft(ss).add(tm1).shiftLeft(ss).add(v0)
    }

    /**
     * Returns a slice of a BigInteger for use in Toom-Cook multiplication.
     *
     * @param lowerSize The size of the lower-order bit slices.
     * @param upperSize The size of the higher-order bit slices.
     * @param slice The index of which slice is requested, which must be a
     * number from 0 to size-1. Slice 0 is the highest-order bits, and slice
     * size-1 are the lowest-order bits. Slice 0 may be of different size than
     * the other slices.
     * @param fullsize The size of the larger integer array, used to align
     * slices to the appropriate position when multiplying different-sized
     * numbers.
     */
    private fun getToomSlice(lowerSize: Int, upperSize: Int,
                             slice: Int, fullSize: Int): BigInteger {
        val offset = fullSize - mag.size
        var start: Int
        var end: Int
        if (0 == slice) {
            start = -offset
            end = upperSize - 1 - offset
        } else {
            start = upperSize + (slice - 1) * lowerSize - offset
            end = start + lowerSize - 1
        }

        if (end < 0) return ZERO
        if (start < 0) start = 0
        val sliceSize = end - start + 1
        if (sliceSize <= 0) return ZERO

        // While performing Toom-Cook, all slices are positive and
        // the sign is adjusted when the final number is composed.
        if (start == 0 && sliceSize >= mag.size) {
            return this.abs()
        }

        val intSlice = mag.copyOfRange(start, start + sliceSize)
        return BigInteger(1, trustedStripLeadingZeroInts(intSlice))
    }

    /**
     * Returns a new BigInteger representing n lower ints of the number.
     * This is used by Karatsuba multiplication and Karatsuba squaring.
     */
    private fun getLower(n: Int): BigInteger {
        if (mag.size <= n) return abs()
        val lowerInts = mag.copyOfRange(mag.size - n, mag.size)
        return BigInteger(1, trustedStripLeadingZeroInts(lowerInts))
    }

    /**
     * Returns a new BigInteger representing mag.length-n upper
     * ints of the number.  This is used by Karatsuba multiplication and
     * Karatsuba squaring.
     */
    private fun getUpper(n: Int): BigInteger {
        if (mag.size <= n) return ZERO
        val upperInts = mag.copyOfRange(0, mag.size - n)
        return BigInteger(1, trustedStripLeadingZeroInts(upperInts))
    }


    /**
     * Throws an {@code ArithmeticException} if the {@code BigInteger} would be
     * out of the supported range.
     *
     * @throws ArithmeticException if {@code this} exceeds the supported range.
     */
    private fun checkRange() {
        if (mag.size > MAX_MAG_LENGTH || mag.size == MAX_MAG_LENGTH && mag[0] < 0)
            reportOverflow()
    }
}