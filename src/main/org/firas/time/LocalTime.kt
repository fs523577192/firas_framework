package org.firas.time

/**
 *
 */
class LocalTime private constructor(
        val hour: Byte, val minute: Byte, val second: Byte, val nano: Int):
        Comparable<LocalTime> {

    companion object {
        internal val HOURS_PER_DAY = 24
        internal val MINUTES_PER_HOUR = 60
        internal val MINUTES_PER_DAY = MINUTES_PER_HOUR * HOURS_PER_DAY
        internal val SECONDS_PER_MINUTE = 60
        internal val SECONDS_PER_HOUR = SECONDS_PER_MINUTE * MINUTES_PER_HOUR
        internal val SECONDS_PER_DAY = SECONDS_PER_HOUR * HOURS_PER_DAY
        internal val MILLIS_PER_DAY = SECONDS_PER_DAY * 1000L
        internal val MICROS_PER_DAY = SECONDS_PER_DAY * 1000_000L
        internal val NANOS_PER_SECOND = 1000_000_000L
        internal val NANOS_PER_MINUTE = NANOS_PER_SECOND * SECONDS_PER_MINUTE
        internal val NANOS_PER_HOUR = NANOS_PER_MINUTE * MINUTES_PER_HOUR
        internal val NANOS_PER_DAY = NANOS_PER_HOUR * HOURS_PER_DAY
    }

    override fun compareTo(other: LocalTime): Int {
        var cmp = hour.compareTo(other.hour)
        if (0 == cmp) cmp = minute.compareTo(other.minute)
        if (0 == cmp) cmp = second.compareTo(other.second)
        if (0 == cmp) cmp = nano.compareTo(other.nano)
        return cmp
    }

    fun isAfter(other: LocalTime): Boolean {
        return compareTo(other) > 0
    }

    fun isBefore(other: LocalTime): Boolean {
        return compareTo(other) < 0
    }

    override fun equals(other: Any?): Boolean {
        return (other is LocalTime && hour == other.hour && minute == other.minute &&
                second == other.second && nano == other.nano)
    }

    override fun toString(): String {
        val buf = StringBuilder()
        buf.append(if (hour < 10) "0" else "").append(hour)
                .append(if (minute < 10) ":0" else ":").append(minute)
        if (second > 0 || nano > 0) {
            buf.append(if (second < 0) ":0" else ":").append(second)
            if (nano > 0) {
                buf.append('.')
                if (nano % 1000_000 == 0) {
                    buf.append((nano / 1000_000 + 1000).toString().substring(1))
                } else if (nano % 1000 == 0) {
                    buf.append((nano / 1000 + 1000_000).toString().substring(1))
                } else {
                    buf.append((nano + 1000_000_000).toString().substring(1))
                }
            }
        }
        return buf.toString()
    }
}