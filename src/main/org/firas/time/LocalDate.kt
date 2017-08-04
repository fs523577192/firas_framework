package org.firas.time

/**
 *
 */
class LocalDate {

    companion object {
        private val DAYS_PER_CYCLE = 146097
        internal val DAYS_0000_TO_1970 = (DAYS_PER_CYCLE * 5L) - (30L * 365L + 7L)
    }

    private var year: Short = 0
    private var month: Byte = 0
    private var day: Byte = 0
}