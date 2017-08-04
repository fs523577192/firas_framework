package org.firas.lang

/**
 *
 */
class Integer {

    companion object {

        /**
         * The number of bits used to represent an {@code int} value in two's
         * complement binary form.
         */
        val SIZE = 32

        /**
         * The number of bytes used to represent a {@code int} value in two's
         * complement binary form.
         */
        val BYTES = 4

        /**
         * Returns an {@code int} value with at most a single one-bit, in the
         * position of the highest-order ("leftmost") one-bit in the specified
         * {@code int} value.  Returns zero if the specified value has no
         * one-bits in its two's complement binary representation, that is, if it
         * is equal to zero.
         *
         * @param i the value whose highest one bit is to be computed
         * @return an {@code int} value with a single one-bit, in the position
         *     of the highest-order one-bit in the specified value, or zero if
         *     the specified value is itself equal to zero.
         */
        fun highestOneBit(i: Int): Int {
            var i = i
            i = i or (i shr 1)
            i = i or (i shr 2)
            i = i or (i shr 4)
            i = i or (i shr 8)
            i = i or (i shr 16)
            return i - i.ushr(1)
        }

        /**
         * Returns an {@code int} value with at most a single one-bit, in the
         * position of the lowest-order ("rightmost") one-bit in the specified
         * {@code int} value.  Returns zero if the specified value has no
         * one-bits in its two's complement binary representation, that is, if it
         * is equal to zero.
         *
         * @param i the value whose lowest one bit is to be computed
         * @return an {@code int} value with a single one-bit, in the position
         *     of the lowest-order one-bit in the specified value, or zero if
         *     the specified value is itself equal to zero.
         */
        fun lowestOneBit(i: Int): Int {
            return i and -i
        }

        /**
         * Returns the number of zero bits preceding the highest-order
         * ("leftmost") one-bit in the two's complement binary representation
         * of the specified {@code int} value.  Returns 32 if the
         * specified value has no one-bits in its two's complement representation,
         * in other words if it is equal to zero.
         *
         * <p>Note that this method is closely related to the logarithm base 2.
         * For all positive {@code int} values x:
         * <ul>
         * <li>floor(log<sub>2</sub>(x)) = {@code 31 - numberOfLeadingZeros(x)}
         * <li>ceil(log<sub>2</sub>(x)) = {@code 32 - numberOfLeadingZeros(x - 1)}
         * </ul>
         *
         * @param i the value whose number of leading zeros is to be computed
         * @return the number of zero bits preceding the highest-order
         *     ("leftmost") one-bit in the two's complement binary representation
         *     of the specified {@code int} value, or 32 if the value
         *     is equal to zero.
         */
        fun numberOfLeadingZeros(i: Int): Int {
            var i = i
            if (i == 0) return SIZE
            var n = 1
            if (i.ushr(16) == 0) {
                n += 16
                i = i shl 16
            }
            if (i.ushr(24) == 0) {
                n += 8
                i = i shl 8
            }
            if (i.ushr(28) == 0) {
                n += 4
                i = i shl 4
            }
            if (i.ushr(30) == 0) {
                n += 2
                i = i shl 2
            }
            n -= i.ushr(31)
            return n
        }

        /**
         * Returns the number of zero bits following the lowest-order ("rightmost")
         * one-bit in the two's complement binary representation of the specified
         * {@code int} value.  Returns 32 if the specified value has no
         * one-bits in its two's complement representation, in other words if it is
         * equal to zero.
         *
         * @param i the value whose number of trailing zeros is to be computed
         * @return the number of zero bits following the lowest-order ("rightmost")
         *     one-bit in the two's complement binary representation of the
         *     specified {@code int} value, or 32 if the value is equal
         *     to zero.
         */
        fun numberOfTrailingZeros(i: Int): Int {
            var i = i
            // HD, Figure 5-14
            var y: Int
            if (i == 0) return 32
            var n = 31
            y = i shl 16
            if (y != 0) {
                n -= 16
                i = y
            }
            y = i shl 8
            if (y != 0) {
                n -= 8
                i = y
            }
            y = i shl 4
            if (y != 0) {
                n -= 4
                i = y
            }
            y = i shl 2
            if (y != 0) {
                n -= 2
                i = y
            }
            return n - (i shl 1).ushr(31)
        }

        /**
         * Returns the number of one-bits in the two's complement binary
         * representation of the specified {@code int} value.  This function is
         * sometimes referred to as the <i>population count</i>.
         *
         * @param i the value whose bits are to be counted
         * @return the number of one-bits in the two's complement binary
         *     representation of the specified {@code int} value.
         */
        fun bitCount(i: Int): Int {
            var i = i
            i -= (i.ushr(1) and 0x55555555)
            i = (i and 0x33333333) + (i.ushr(2) and 0x33333333)
            i += i.ushr(4) and 0x0f0f0f0f
            i += i.ushr(8)
            i += i.ushr(16)
            return i and 0x3f
        }
    }
}