/*
 * Copyright (c) 1994, 2013, Oracle and/or its affiliates. All rights reserved.
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
package org.firas.lang

/**
 * The class {@code Math} contains methods for performing basic
 * numeric operations such as the elementary exponential, logarithm,
 * square root, and trigonometric functions.
 *
 * <p>Unlike some of the numeric methods of class
 * {@code StrictMath}, all implementations of the equivalent
 * functions of class {@code Math} are not defined to return the
 * bit-for-bit same results.  This relaxation permits
 * better-performing implementations where strict reproducibility is
 * not required.
 *
 * <p>By default many of the {@code Math} methods simply call
 * the equivalent method in {@code StrictMath} for their
 * implementation.  Code generators are encouraged to use
 * platform-specific native libraries or microprocessor instructions,
 * where available, to provide higher-performance implementations of
 * {@code Math} methods.  Such higher-performance
 * implementations still must conform to the specification for
 * {@code Math}.
 *
 * <p>The quality of implementation specifications concern two
 * properties, accuracy of the returned result and monotonicity of the
 * method.  Accuracy of the floating-point {@code Math} methods is
 * measured in terms of <i>ulps</i>, units in the last place.  For a
 * given floating-point format, an {@linkplain #ulp(double) ulp} of a
 * specific real number value is the distance between the two
 * floating-point values bracketing that numerical value.  When
 * discussing the accuracy of a method as a whole rather than at a
 * specific argument, the number of ulps cited is for the worst-case
 * error at any argument.  If a method always has an error less than
 * 0.5 ulps, the method always returns the floating-point number
 * nearest the exact result; such a method is <i>correctly
 * rounded</i>.  A correctly rounded method is generally the best a
 * floating-point approximation can be; however, it is impractical for
 * many floating-point methods to be correctly rounded.  Instead, for
 * the {@code Math} class, a larger error bound of 1 or 2 ulps is
 * allowed for certain methods.  Informally, with a 1 ulp error bound,
 * when the exact result is a representable number, the exact result
 * should be returned as the computed result; otherwise, either of the
 * two floating-point values which bracket the exact result may be
 * returned.  For exact results large in magnitude, one of the
 * endpoints of the bracket may be infinite.  Besides accuracy at
 * individual arguments, maintaining proper relations between the
 * method at different arguments is also important.  Therefore, most
 * methods with more than 0.5 ulp errors are required to be
 * <i>semi-monotonic</i>: whenever the mathematical function is
 * non-decreasing, so is the floating-point approximation, likewise,
 * whenever the mathematical function is non-increasing, so is the
 * floating-point approximation.  Not all approximations that have 1
 * ulp accuracy will automatically meet the monotonicity requirements.
 *
 * <p>
 * The platform uses signed two's complement integer arithmetic with
 * int and long primitive types.  The developer should choose
 * the primitive type to ensure that arithmetic operations consistently
 * produce correct results, which in some cases means the operations
 * will not overflow the range of values of the computation.
 * The best practice is to choose the primitive type and algorithm to avoid
 * overflow. In cases where the size is {@code int} or {@code long} and
 * overflow errors need to be detected, the methods {@code addExact},
 * {@code subtractExact}, {@code multiplyExact}, and {@code toIntExact}
 * throw an {@code ArithmeticException} when the results overflow.
 * For other arithmetic operations such as divide, absolute value,
 * increment, decrement, and negation overflow occurs only with
 * a specific minimum or maximum value and should be checked against
 * the minimum or maximum as appropriate.
 *
 * @author  unascribed
 * @author  Joseph D. Darcy
 */
class Math private constructor() {

    companion object {
        val E = 2.7182818284590452354
        val PI = 3.14159265358979323846

        /**
         * Returns the absolute value of an `int` value.
         * If the argument is not negative, the argument is returned.
         * If the argument is negative, the negation of the argument is returned.

         *
         * Note that if the argument is equal to the value of
         * [Int.MIN_VALUE], the most negative representable
         * `int` value, the result is that same value, which is
         * negative.

         * @param   a   the argument whose absolute value is to be determined
         * *
         * @return  the absolute value of the argument.
         */
        fun abs(a: Int): Int {
            return if (a < 0) -a else a
        }

        /**
         * Returns the absolute value of a `long` value.
         * If the argument is not negative, the argument is returned.
         * If the argument is negative, the negation of the argument is returned.

         *
         * Note that if the argument is equal to the value of
         * [Long.MIN_VALUE], the most negative representable
         * `long` value, the result is that same value, which
         * is negative.

         * @param   a   the argument whose absolute value is to be determined
         * *
         * @return  the absolute value of the argument.
         */
        fun abs(a: Long): Long {
            return if (a < 0) -a else a
        }

        /**
         * Returns the absolute value of a `float` value.
         * If the argument is not negative, the argument is returned.
         * If the argument is negative, the negation of the argument is returned.
         * Special cases:
         *  * If the argument is positive zero or negative zero, the
         * result is positive zero.
         *  * If the argument is infinite, the result is positive infinity.
         *  * If the argument is NaN, the result is NaN.
         * In other words, the result is the same as the value of the expression:
         *
         * `Float.intBitsToFloat(0x7fffffff & Float.floatToIntBits(a))`

         * @param   a   the argument whose absolute value is to be determined
         * *
         * @return  the absolute value of the argument.
         */
        fun abs(a: Float): Float {
            return if (a <= 0.0f) 0.0f - a else a
        }

        /**
         * Returns the absolute value of a `double` value.
         * If the argument is not negative, the argument is returned.
         * If the argument is negative, the negation of the argument is returned.
         * Special cases:
         *  * If the argument is positive zero or negative zero, the result
         * is positive zero.
         *  * If the argument is infinite, the result is positive infinity.
         *  * If the argument is NaN, the result is NaN.
         * In other words, the result is the same as the value of the expression:
         *
         * `Double.longBitsToDouble((Double.doubleToLongBits(a)<<1)>>>1)`

         * @param   a   the argument whose absolute value is to be determined
         * *
         * @return  the absolute value of the argument.
         */
        fun abs(a: Double): Double {
            return if (a <= 0.0) 0.0 - a else a
        }

        /**
         * Returns the greater of two `int` values. That is, the
         * result is the argument closer to the value of
         * [Int.MAX_VALUE]. If the arguments have the same value,
         * the result is that same value.

         * @param   a   an argument.
         * *
         * @param   b   another argument.
         * *
         * @return  the larger of `a` and `b`.
         */
        fun max(a: Int, b: Int): Int {
            return if (a >= b) a else b
        }

        /**
         * Returns the greater of two `long` values. That is, the
         * result is the argument closer to the value of
         * [Long.MAX_VALUE]. If the arguments have the same value,
         * the result is that same value.

         * @param   a   an argument.
         * *
         * @param   b   another argument.
         * *
         * @return  the larger of `a` and `b`.
         */
        fun max(a: Long, b: Long): Long {
            return if (a >= b) a else b
        }

        /**
         * Returns the greater of two `float` values.  That is,
         * the result is the argument closer to positive infinity. If the
         * arguments have the same value, the result is that same
         * value. If either value is NaN, then the result is NaN.  Unlike
         * the numerical comparison operators, this method considers
         * negative zero to be strictly smaller than positive zero. If one
         * argument is positive zero and the other negative zero, the
         * result is positive zero.

         * @param   a   an argument.
         * *
         * @param   b   another argument.
         * *
         * @return  the larger of `a` and `b`.
         */
        fun max(a: Float, b: Float): Float {
            if (a != a)
                return a   // a is NaN
            return if (a.compareTo(b) >= 0) a else b
        }

        /**
         * Returns the greater of two `double` values.  That
         * is, the result is the argument closer to positive infinity. If
         * the arguments have the same value, the result is that same
         * value. If either value is NaN, then the result is NaN.  Unlike
         * the numerical comparison operators, this method considers
         * negative zero to be strictly smaller than positive zero. If one
         * argument is positive zero and the other negative zero, the
         * result is positive zero.

         * @param   a   an argument.
         * *
         * @param   b   another argument.
         * *
         * @return  the larger of `a` and `b`.
         */
        fun max(a: Double, b: Double): Double {
            if (a != a)
                return a   // a is NaN
            return if (a.compareTo(b) >= 0) a else b
        }

        /**
         * Returns the smaller of two `int` values. That is,
         * the result the argument closer to the value of
         * [Int.MIN_VALUE].  If the arguments have the same
         * value, the result is that same value.

         * @param   a   an argument.
         * *
         * @param   b   another argument.
         * *
         * @return  the smaller of `a` and `b`.
         */
        fun min(a: Int, b: Int): Int {
            return if (a <= b) a else b
        }

        /**
         * Returns the smaller of two `long` values. That is,
         * the result is the argument closer to the value of
         * [Long.MIN_VALUE]. If the arguments have the same
         * value, the result is that same value.

         * @param   a   an argument.
         * *
         * @param   b   another argument.
         * *
         * @return  the smaller of `a` and `b`.
         */
        fun min(a: Long, b: Long): Long {
            return if (a <= b) a else b
        }

        /**
         * Returns the smaller of two `float` values.  That is,
         * the result is the value closer to negative infinity. If the
         * arguments have the same value, the result is that same
         * value. If either value is NaN, then the result is NaN.  Unlike
         * the numerical comparison operators, this method considers
         * negative zero to be strictly smaller than positive zero.  If
         * one argument is positive zero and the other is negative zero,
         * the result is negative zero.

         * @param   a   an argument.
         * *
         * @param   b   another argument.
         * *
         * @return  the smaller of `a` and `b`.
         */
        fun min(a: Float, b: Float): Float {
            if (a != a)
                return a   // a is NaN
            return if (a.compareTo(b) <= 0) a else b
        }

        /**
         * Returns the smaller of two `double` values.  That
         * is, the result is the value closer to negative infinity. If the
         * arguments have the same value, the result is that same
         * value. If either value is NaN, then the result is NaN.  Unlike
         * the numerical comparison operators, this method considers
         * negative zero to be strictly smaller than positive zero. If one
         * argument is positive zero and the other is negative zero, the
         * result is negative zero.

         * @param   a   an argument.
         * *
         * @param   b   another argument.
         * *
         * @return  the smaller of `a` and `b`.
         */
        fun min(a: Double, b: Double): Double {
            if (a != a)
                return a   // a is NaN
            return if (a.compareTo(b) <= 0) a else b
        }

        /**
         * Returns the sum of its arguments,
         * throwing an exception if the result overflows an {@code int}.
         *
         * @param x the first value
         * @param y the second value
         * @return the result
         * @throws ArithmeticException if the result overflows an int
         */
        fun addExact(x: Int, y: Int): Int {
            val r = x + y;
            // HD 2-12 Overflow iff both arguments have the opposite sign of the result
            if (((x xor r) and (y xor r)) < 0) {
                throw ArithmeticException("integer overflow")
            }
            return r
        }

        /**
         * Returns the sum of its arguments,
         * throwing an exception if the result overflows a {@code long}.
         *
         * @param x the first value
         * @param y the second value
         * @return the result
         * @throws ArithmeticException if the result overflows a long
         */
        fun addExact(x: Long, y: Long): Long {
            val r = x + y
            // HD 2-12 Overflow iff both arguments have the opposite sign of the result
            if (((x xor r) and (y xor r)) < 0) {
                throw ArithmeticException("long overflow")
            }
            return r
        }

        /**
         * Returns the difference of the arguments,
         * throwing an exception if the result overflows an {@code int}.
         *
         * @param x the first value
         * @param y the second value to subtract from the first
         * @return the result
         * @throws ArithmeticException if the result overflows an int
         */
        fun subtractExact(x: Int, y: Int): Int {
            val r = x - y;
            // HD 2-12 Overflow iff the arguments have different signs and
            // the sign of the result is different than the sign of x
            if (((x xor y) and (x xor r)) < 0) {
                throw ArithmeticException("integer overflow")
            }
            return r
        }

        /**
         * Returns the difference of the arguments,
         * throwing an exception if the result overflows a {@code long}.
         *
         * @param x the first value
         * @param y the second value to subtract from the first
         * @return the result
         * @throws ArithmeticException if the result overflows a long
         */
        fun subtractExact(x: Long, y: Long): Long {
            val r = x - y;
            // HD 2-12 Overflow iff the arguments have different signs and
            // the sign of the result is different than the sign of x
            if (((x xor y) and (x xor r)) < 0) {
                throw ArithmeticException("long overflow")
            }
            return r
        }

        /**
         * Returns the product of the arguments,
         * throwing an exception if the result overflows an {@code int}.
         *
         * @param x the first value
         * @param y the second value
         * @return the result
         * @throws ArithmeticException if the result overflows an int
         */
        fun multiplyExact(x: Int, y: Int): Int {
            val r = x.toLong() * y.toLong()
            val temp = r.toInt()
            if (temp.toLong() != r) {
                throw ArithmeticException("integer overflow")
            }
            return temp
        }

        /**
         * Returns the product of the arguments,
         * throwing an exception if the result overflows a {@code long}.
         *
         * @param x the first value
         * @param y the second value
         * @return the result
         * @throws ArithmeticException if the result overflows a long
         */
        fun multiplyExact(x: Long, y: Long): Long {
            val r = x * y
            val ax = Math.abs(x)
            val ay = Math.abs(y)
            if (((ax or ay) ushr 31 != 0L)) {
                // Some bits greater than 2^31 that might cause overflow
                // Check the result using the divide operator
                // and check for the special case of Long.MIN_VALUE * -1
               if (((y != 0L) && (r / y != x)) ||
                   (x == Long.MIN_VALUE && y == -1L)) {
                    throw ArithmeticException("long overflow")
                }
            }
            return r
        }

        /**
         * Returns the argument incremented by one, throwing an exception if the
         * result overflows an {@code int}.
         *
         * @param a the value to increment
         * @return the result
         * @throws ArithmeticException if the result overflows an int
         */
        fun incrementExact(a: Int): Int {
            if (a == Int.MAX_VALUE) {
                throw ArithmeticException("integer overflow")
            }
            return a + 1
        }

        /**
         * Returns the argument incremented by one, throwing an exception if the
         * result overflows a {@code long}.
         *
         * @param a the value to increment
         * @return the result
         * @throws ArithmeticException if the result overflows a long
         */
        fun incrementExact(a: Long): Long {
            if (a == Long.MAX_VALUE) {
                throw ArithmeticException("long overflow")
            }
            return a + 1L
        }

        /**
         * Returns the argument decremented by one, throwing an exception if the
         * result overflows an {@code int}.
         *
         * @param a the value to decrement
         * @return the result
         * @throws ArithmeticException if the result overflows an int
         */
        fun decrementExact(a: Int): Int {
            if (a == Int.MIN_VALUE) {
                throw ArithmeticException("integer overflow")
            }
            return a - 1
        }

        /**
         * Returns the argument decremented by one, throwing an exception if the
         * result overflows a {@code long}.
         *
         * @param a the value to decrement
         * @return the result
         * @throws ArithmeticException if the result overflows a long
         */
        fun decrementExact(a: Long): Long {
            if (a == Long.MIN_VALUE) {
                throw ArithmeticException("long overflow")
            }
            return a - 1L
        }

        /**
         * Returns the negation of the argument, throwing an exception if the
         * result overflows an {@code int}.
         *
         * @param a the value to negate
         * @return the result
         * @throws ArithmeticException if the result overflows an int
         */
        fun negateExact(a: Int): Int {
            if (a == Int.MIN_VALUE) {
                throw ArithmeticException("integer overflow")
            }
            return -a
        }

        /**
         * Returns the negation of the argument, throwing an exception if the
         * result overflows a {@code long}.
         *
         * @param a the value to negate
         * @return the result
         * @throws ArithmeticException if the result overflows a long
         */
        fun negateExact(a: Long): Long {
            if (a == Long.MIN_VALUE) {
                throw ArithmeticException("long overflow")
            }
            return -a
        }

        /**
         * Returns the value of the {@code long} argument;
         * throwing an exception if the value overflows an {@code int}.
         *
         * @param value the long value
         * @return the argument as an int
         * @throws ArithmeticException if the {@code argument} overflows an int
         */
        fun toIntExact(value: Long): Int {
            val temp = value.toInt()
            if (temp.toLong() != value) {
                throw ArithmeticException("integer overflow")
            }
            return temp
        }

        /**
         * Returns the largest (closest to positive infinity)
         * `int` value that is less than or equal to the algebraic quotient.
         * There is one special case, if the dividend is the
         * [Integer.MIN_VALUE] and the divisor is `-1`,
         * then integer overflow occurs and
         * the result is equal to the `Integer.MIN_VALUE`.
         *
         *
         * Normal integer division operates under the round to zero rounding mode
         * (truncation).  This operation instead acts under the round toward
         * negative infinity (floor) rounding mode.
         * The floor rounding mode gives different results than truncation
         * when the exact result is negative.
         *
         *  * If the signs of the arguments are the same, the results of
         * `floorDiv` and the `/` operator are the same.  <br></br>
         * For example, `floorDiv(4, 3) == 1` and `(4 / 3) == 1`.
         *  * If the signs of the arguments are different,  the quotient is negative and
         * `floorDiv` returns the integer less than or equal to the quotient
         * and the `/` operator returns the integer closest to zero.<br></br>
         * For example, `floorDiv(-4, 3) == -2`,
         * whereas `(-4 / 3) == -1`.
         *
         *
         *
         *

         * @param x the dividend
         * *
         * @param y the divisor
         * *
         * @return the largest (closest to positive infinity)
         * * `int` value that is less than or equal to the algebraic quotient.
         * *
         * @throws ArithmeticException if the divisor `y` is zero
         * *
         * @see .floorMod
         * @see .floor
         */
        fun floorDiv(x: Int, y: Int): Int {
            var r = x / y
            // if the signs are different and modulo not zero, round down
            if (x xor y < 0 && r * y != x) {
                r--
            }
            return r
        }

        /**
         * Returns the largest (closest to positive infinity)
         * `long` value that is less than or equal to the algebraic quotient.
         * There is one special case, if the dividend is the
         * [Long.MIN_VALUE] and the divisor is `-1`,
         * then integer overflow occurs and
         * the result is equal to the `Long.MIN_VALUE`.
         *
         *
         * Normal integer division operates under the round to zero rounding mode
         * (truncation).  This operation instead acts under the round toward
         * negative infinity (floor) rounding mode.
         * The floor rounding mode gives different results than truncation
         * when the exact result is negative.
         *
         *
         * For examples, see [.floorDiv].

         * @param x the dividend
         * *
         * @param y the divisor
         * *
         * @return the largest (closest to positive infinity)
         * * `long` value that is less than or equal to the algebraic quotient.
         * *
         * @throws ArithmeticException if the divisor `y` is zero
         * *
         * @see .floorMod
         * @see .floor
         */
        fun floorDiv(x: Long, y: Long): Long {
            var r = x / y
            // if the signs are different and modulo not zero, round down
            if (x xor y < 0 && r * y != x) {
                r--
            }
            return r
        }

        /**
         * Returns the floor modulus of the `int` arguments.
         *
         *
         * The floor modulus is `x - (floorDiv(x, y) * y)`,
         * has the same sign as the divisor `y`, and
         * is in the range of `-abs(y) < r < +abs(y)`.
         *
         *
         * The relationship between `floorDiv` and `floorMod` is such that:
         *
         *  * `floorDiv(x, y) * y + floorMod(x, y) == x`
         *
         *
         *
         * The difference in values between `floorMod` and
         * the `%` operator is due to the difference between
         * `floorDiv` that returns the integer less than or equal to the quotient
         * and the `/` operator that returns the integer closest to zero.
         *
         *
         * Examples:
         *
         *  * If the signs of the arguments are the same, the results
         * of `floorMod` and the `%` operator are the same.  <br></br>
         *
         *  * `floorMod(4, 3) == 1`; &nbsp; and `(4 % 3) == 1`
         *
         *  * If the signs of the arguments are different, the results differ from the `%` operator.<br></br>
         *
         *  * `floorMod(+4, -3) == -2`; &nbsp; and `(+4 % -3) == +1`
         *  * `floorMod(-4, +3) == +2`; &nbsp; and `(-4 % +3) == -1`
         *  * `floorMod(-4, -3) == -1`; &nbsp; and `(-4 % -3) == -1 `
         *
         *
         *
         *
         *
         * If the signs of arguments are unknown and a positive modulus
         * is needed it can be computed as `(floorMod(x, y) + abs(y)) % abs(y)`.

         * @param x the dividend
         * *
         * @param y the divisor
         * *
         * @return the floor modulus `x - (floorDiv(x, y) * y)`
         * *
         * @throws ArithmeticException if the divisor `y` is zero
         * *
         * @see .floorDiv
         */
        fun floorMod(x: Int, y: Int): Int {
            val r = x - floorDiv(x, y) * y
            return r
        }

        /**
         * Returns the floor modulus of the `long` arguments.
         *
         *
         * The floor modulus is `x - (floorDiv(x, y) * y)`,
         * has the same sign as the divisor `y`, and
         * is in the range of `-abs(y) < r < +abs(y)`.
         *
         *
         * The relationship between `floorDiv` and `floorMod` is such that:
         *
         *  * `floorDiv(x, y) * y + floorMod(x, y) == x`
         *
         *
         *
         * For examples, see [.floorMod].

         * @param x the dividend
         * *
         * @param y the divisor
         * *
         * @return the floor modulus `x - (floorDiv(x, y) * y)`
         * *
         * @throws ArithmeticException if the divisor `y` is zero
         * *
         * @see .floorDiv
         */
        fun floorMod(x: Long, y: Long): Long {
            return x - floorDiv(x, y) * y
        }
    }
}