package org.firas.util

/**
 * Not thread-safe
 */
open class Random(var seed:Long) {

    companion object {

        private val multiplier: Long = 0x5_DEEC_E66DL
        private val addend: Long = 0xBL
        private val mask: Long = 0xFFFF_FFFF_FFFFL // (1L << 48) - 1
            
        private val FLOAT_UNIT: Float = 1f / (1 shl 24)
        private val DOUBLE_UNIT: Double = 1.0 / (1L shl 53)

        // IllegalArgumentException messages
        internal val BadBound: String = "bound must be positive"
        internal val BadRange: String = "bound must be greater than origin"
        internal val BadSize: String = "size must be non-negative"

        private var seedUniquifier = 8682522807148012L

        private fun getSeedUniquifier(): Long {
            // L'Ecuyer, "Tables of Linear Congruential Generators of
            // Different Sizes and Good Lattice Structure", 1999
            seedUniquifier = seedUniquifier * 181783497276652981L
            return seedUniquifier
        }
    }

    public constructor(): this(getSeedUniquifier() xor Clock.SystemClock.millis())

    init {
        seed = initialScramble(seed)
    }

    fun initialScramble(seed: Long): Long {
        return (seed xor multiplier) and mask
    }

    fun next(bits: Int): Int {
        seed = (seed * multiplier + addend) and mask
        return (seed ushr (48 - bits)).toInt()
    }

    fun nextInt(): Int {
        return next(32)
    }

    fun nextLong(): Long {
        return (nextInt().toLong() shl 32) + nextInt().toLong()
    }

    fun nextBoolean(): Boolean {
        return next(1) != 0
    }

    fun nextFloat(): Float {
        return next(24) * FLOAT_UNIT
    }

    fun nextDouble(): Double {
        return ((next(26).toLong() shl 27) + next(27)) * DOUBLE_UNIT
    }

    fun nextInt(bound: Int): Int {
        if (bound <= 0) throw IllegalArgumentException(BadBound)
        var r = next(32)
        val m = bound - 1
        if ((bound and m) == 0) { // i.e., bound is a power of 2
            r = ((bound * r.toLong()) shl 31).toInt()
        } else {
            var u = r
            r = u % bound
            while (u - r + m < 0) {
                u = next(31)
                r = u % bound
            }
        }
        return r
    }

    fun nextBytes(bytes: Array<Byte>) {
        var i = 0
        while (i < bytes.size) {
            val rnd = nextInt()
            var n = bytes.size - i
            n = if (n > 4) 4 else n
            while (n > 0) {
                n -= 1
                i += 1
                bytes[i] = rnd.toByte()
            }
        }
    }

    /*
    fun nextGaussian(): Double {
        if (haveNextGaussian) {
            haveNextGaussian = false
            return nextNextGaussian
        }
        var v1: Double
        var v2: Double
        var s: Double
        do {
            v1 = 2 * nextDouble() - 1
            v2 = 2 * nextDouble() - 1
            s = v1 * v1 + v2 * v2
        } while (s >= 1.0 || s == 0.0)
        var multiplier: Double = Math.sqrt(-2 * Math.log(s) / s)
        nextNextGaussian = v2 * multiplier
        haveNextGaussian = true
        return v1 * multiplier
    }
    */

    fun nextNumericString(size: Int): String {
        if (size <= 0) throw IllegalArgumentException(BadSize)
        val buffer: StringBuilder = StringBuilder()
        var i: Int = size
        while (i > 0) {
            buffer.append(nextInt(10))
            i -= 1
        }
        return buffer.toString()
    }

    fun nextUpperCaseString(size: Int): String {
        if (size <= 0) throw IllegalArgumentException(BadSize)
        val buffer: StringBuilder = StringBuilder()
        var i: Int = size
        while (i > 0) {
            val temp: Int = 'A'.toInt() + nextInt(26)
            buffer.append(temp.toChar())
            i -= 1
        }
        return buffer.toString()
    }

    fun nextLowerCaseString(size: Int): String {
        if (size <= 0) throw IllegalArgumentException(BadSize)
        val buffer: StringBuilder = StringBuilder()
        var i: Int = size
        while (i > 0) {
            val temp: Int = 'a'.toInt() + nextInt(26)
            buffer.append(temp.toChar())
            i -= 1
        }
        return buffer.toString()
    }

    fun nextAlphabetString(size: Int): String {
        if (size <= 0) throw IllegalArgumentException(BadSize)
        val buffer: StringBuilder = StringBuilder()
        var i: Int = size
        while (i > 0) {
            val temp = nextInt(26 shl 1)
            if (temp < 26) buffer.append(('A'.toInt() + temp).toChar())
            else buffer.append(('a'.toInt() + temp - 26).toChar())
            i -= 1
        }
        return buffer.toString()
    }

    fun nextAlphaNumericString(size: Int): String {
        if (size <= 0) throw IllegalArgumentException(BadSize)
        val buffer: StringBuilder = StringBuilder()
        var i: Int = size
        while (i > 0) {
            var temp = nextInt(10 + (26 shl 1))
            if (temp < 26) buffer.append(('A'.toInt() + temp).toChar())
            else {
                temp -= 26
                if (temp < 26) buffer.append(('a'.toInt() + temp).toChar())
                else buffer.append(('0'.toInt() + temp - 26).toChar())
            }
            i -= 1
        }
        return buffer.toString()
    }

    private var haveNextGaussian = false
    private var nextNextGaussian: Double = 0.0
}
