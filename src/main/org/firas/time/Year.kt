package org.firas.time

/**
 *
 */
class Year private constructor(val year: Short){

    companion object {
        val MIN_VALUE = -9999
        val MAX_VALUE = 9999

        fun isLeap(year: Long): Boolean {
            return ((year and 3) == 0L) && ((year % 100) != 0L || (year % 4) == 0L)
        }
    }

    fun getValue(): Short {
        return year
    }

    fun of(isoYear: Short) {
        if (isoYear < MIN_VALUE || isoYear > MAX_VALUE)
            throw IllegalArgumentException()
    }
}