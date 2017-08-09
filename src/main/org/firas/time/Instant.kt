package org.firas.time

import org.firas.lang.Math
import org.firas.time.temporal.*

/**
 *
 */
class Instant private constructor(val epochSecond: Long, val nanos: Int):
        Temporal(), Comparable<Instant> {

    companion object {
        val EPOCH = Instant(0L, 0)
        val MAX_SECOND = 31556889864403199L
        val MIN_SECOND = -31557014167219200L

        //-----------------------------------------------------------------------
        /**
         * Obtains an instance of {@code Instant} using seconds from the
         * epoch of 1970-01-01T00:00:00Z.
         * <p>
         * The nanosecond field is set to zero.
         *
         * @param epochSecond  the number of seconds from 1970-01-01T00:00:00Z
         * @return an instant, not null
         * @throws DateTimeException if the instant exceeds the maximum or minimum instant
         */
        fun ofEpochSecond(epochSecond: Long): Instant {
            return create(epochSecond, 0)
        }

        /**
         * Obtains an instance of {@code Instant} using seconds from the
         * epoch of 1970-01-01T00:00:00Z and nanosecond fraction of second.
         * <p>
         * This method allows an arbitrary number of nanoseconds to be passed in.
         * The factory will alter the values of the second and nanosecond in order
         * to ensure that the stored nanosecond is in the range 0 to 999,999,999.
         * For example, the following will result in the exactly the same instant:
         * <pre>
         *  Instant.ofEpochSecond(3, 1);
         *  Instant.ofEpochSecond(4, -999_999_999);
         *  Instant.ofEpochSecond(2, 1000_000_001);
         * </pre>
         *
         * @param epochSecond  the number of seconds from 1970-01-01T00:00:00Z
         * @param nanoAdjustment  the nanosecond adjustment to the number of seconds, positive or negative
         * @return an instant, not null
         * @throws DateTimeException if the instant exceeds the maximum or minimum instant
         * @throws ArithmeticException if numeric overflow occurs
         */
        fun ofEpochSecond(epochSecond: Long, nanoAdjustment: Long): Instant {
            val secs = Math.addExact(epochSecond, Math.floorDiv(nanoAdjustment,
                    LocalTime.NANOS_PER_SECOND))
            val nos = Math.floorMod(nanoAdjustment, LocalTime.NANOS_PER_SECOND).toInt()
            return create(secs, nos)
        }

        /**
         * Obtains an instance of {@code Instant} using milliseconds from the
         * epoch of 1970-01-01T00:00:00Z.
         * <p>
         * The seconds and nanoseconds are extracted from the specified milliseconds.
         *
         * @param epochMilli  the number of milliseconds from 1970-01-01T00:00:00Z
         * @return an instant, not null
         * @throws DateTimeException if the instant exceeds the maximum or minimum instant
         */
        fun ofEpochMilli(epochMilli: Long): Instant {
            val secs = Math.floorDiv(epochMilli, 1000)
            val mos = Math.floorMod(epochMilli, 1000).toInt()
            return create(secs, mos * 1000_000)
        }

        //-----------------------------------------------------------------------
        /**
         * Obtains an instance of {@code Instant} using seconds and nanoseconds.
         *
         * @param seconds  the length of the duration in seconds
         * @param nanoOfSecond  the nano-of-second, from 0 to 999,999,999
         * @throws DateTimeException if the instant exceeds the maximum or minimum instant
         */
        private fun create(seconds: Long, nanoOfSecond: Int): Instant {
            if ((seconds or nanoOfSecond.toLong()) == 0L) {
                return EPOCH
            }
            if (seconds < MIN_SECOND || seconds > MAX_SECOND) {
                throw DateTimeException("Instant exceeds minimum or maximum instant")
            }
            return Instant(seconds, nanoOfSecond)
        }
    }

    /**
     * Checks if the specified unit is supported.
     * <p>
     * This checks if the specified unit can be added to, or subtracted from, this date-time.
     * If false, then calling the {@link #plus(long, TemporalUnit)} and
     * {@link #minus(long, TemporalUnit) minus} methods will throw an exception.
     * <p>
     * If the unit is a {@link ChronoUnit} then the query is implemented here.
     * The supported units are:
     * <ul>
     * <li>{@code NANOS}
     * <li>{@code MICROS}
     * <li>{@code MILLIS}
     * <li>{@code SECONDS}
     * <li>{@code MINUTES}
     * <li>{@code HOURS}
     * <li>{@code HALF_DAYS}
     * <li>{@code DAYS}
     * </ul>
     * All other {@code ChronoUnit} instances will return false.
     * <p>
     * If the unit is not a {@code ChronoUnit}, then the result of this method
     * is obtained by invoking {@code TemporalUnit.isSupportedBy(Temporal)}
     * passing {@code this} as the argument.
     * Whether the unit is supported is determined by the unit.
     *
     * @param unit  the unit to check, null returns false
     * @return true if the unit can be added/subtracted, false if not
     */
    override fun isSupported(unit: TemporalUnit): Boolean {
        if (unit is ChronoUnit) return unit.isTimeBase() || unit == ChronoUnit.DAYS
        return unit.isSupportedBy(this)
    }

    //-----------------------------------------------------------------------
    /**
     * Checks if the specified field is supported.
     * <p>
     * This checks if this instant can be queried for the specified field.
     * If false, then calling the {@link #range(TemporalField) range},
     * {@link #get(TemporalField) get} and {@link #with(TemporalField, long)}
     * methods will throw an exception.
     * <p>
     * If the field is a {@link ChronoField} then the query is implemented here.
     * The supported fields are:
     * <ul>
     * <li>{@code NANO_OF_SECOND}
     * <li>{@code MICRO_OF_SECOND}
     * <li>{@code MILLI_OF_SECOND}
     * <li>{@code INSTANT_SECONDS}
     * </ul>
     * All other {@code ChronoField} instances will return false.
     * <p>
     * If the field is not a {@code ChronoField}, then the result of this method
     * is obtained by invoking {@code TemporalField.isSupportedBy(TemporalAccessor)}
     * passing {@code this} as the argument.
     * Whether the field is supported is determined by the field.
     *
     * @param field  the field to check, null returns false
     * @return true if the field is supported on this instant, false if not
     */
    override fun isSupported(field: TemporalField): Boolean {
        if (field is ChronoField) {
            return field == ChronoField.INSTANT_SECONDS ||
                    field == ChronoField.NANO_OF_SECOND ||
                    field == ChronoField.MICRO_OF_SECOND ||
                    field ==ChronoField.MILLI_OF_SECOND
        }
        return field.isSupportedBy(this)
    }

    /**
     * Returns a copy of this instant with the specified amount added.
     * <p>
     * This returns an {@code Instant}, based on this one, with the amount
     * in terms of the unit added. If it is not possible to add the amount, because the
     * unit is not supported or for some other reason, an exception is thrown.
     * <p>
     * If the field is a {@link ChronoUnit} then the addition is implemented here.
     * The supported fields behave as follows:
     * <ul>
     * <li>{@code NANOS} -
     *  Returns a {@code Instant} with the specified number of nanoseconds added.
     *  This is equivalent to {@link #plusNanos(long)}.
     * <li>{@code MICROS} -
     *  Returns a {@code Instant} with the specified number of microseconds added.
     *  This is equivalent to {@link #plusNanos(long)} with the amount
     *  multiplied by 1,000.
     * <li>{@code MILLIS} -
     *  Returns a {@code Instant} with the specified number of milliseconds added.
     *  This is equivalent to {@link #plusNanos(long)} with the amount
     *  multiplied by 1,000,000.
     * <li>{@code SECONDS} -
     *  Returns a {@code Instant} with the specified number of seconds added.
     *  This is equivalent to {@link #plusSeconds(long)}.
     * <li>{@code MINUTES} -
     *  Returns a {@code Instant} with the specified number of minutes added.
     *  This is equivalent to {@link #plusSeconds(long)} with the amount
     *  multiplied by 60.
     * <li>{@code HOURS} -
     *  Returns a {@code Instant} with the specified number of hours added.
     *  This is equivalent to {@link #plusSeconds(long)} with the amount
     *  multiplied by 3,600.
     * <li>{@code HALF_DAYS} -
     *  Returns a {@code Instant} with the specified number of half-days added.
     *  This is equivalent to {@link #plusSeconds(long)} with the amount
     *  multiplied by 43,200 (12 hours).
     * <li>{@code DAYS} -
     *  Returns a {@code Instant} with the specified number of days added.
     *  This is equivalent to {@link #plusSeconds(long)} with the amount
     *  multiplied by 86,400 (24 hours).
     * </ul>
     * <p>
     * All other {@code ChronoUnit} instances will throw an {@code UnsupportedTemporalTypeException}.
     * <p>
     * If the field is not a {@code ChronoUnit}, then the result of this method
     * is obtained by invoking {@code TemporalUnit.addTo(Temporal, long)}
     * passing {@code this} as the argument. In this case, the unit determines
     * whether and how to perform the addition.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amountToAdd  the amount of the unit to add to the result, may be negative
     * @param unit  the unit of the amount to add, not null
     * @return an {@code Instant} based on this instant with the specified amount added, not null
     * @throws DateTimeException if the addition cannot be made
     * @throws UnsupportedTemporalTypeException if the unit is not supported
     * @throws ArithmeticException if numeric overflow occurs
     */
    override fun plus(amountToAdd: Long, unit: TemporalUnit): Temporal {
        if (unit is ChronoUnit) {
            when (unit) {
                ChronoUnit.NANOS -> return plusNanos(amountToAdd)
                ChronoUnit.MICROS -> return plus(amountToAdd / 1000_000, (amountToAdd % 1000_000) * 1000)
                ChronoUnit.MILLIS -> return plusMillis(amountToAdd)
                ChronoUnit.SECONDS -> return plusSeconds(amountToAdd)
                ChronoUnit.MINUTES -> return plusSeconds(Math.multiplyExact(amountToAdd,
                        LocalTime.SECONDS_PER_MINUTE.toLong()))
                ChronoUnit.HOURS -> return plusSeconds(Math.multiplyExact(amountToAdd,
                        LocalTime.SECONDS_PER_HOUR.toLong()))
                ChronoUnit.HALF_DAYS -> return plusSeconds(Math.multiplyExact(amountToAdd,
                        LocalTime.SECONDS_PER_DAY.toLong().shr(1)))
                ChronoUnit.DAYS -> return plusSeconds(Math.multiplyExact(amountToAdd,
                        LocalTime.SECONDS_PER_DAY.toLong()))
            }
            throw UnsupportedTemporalTypeException("Unsupported unit: " + unit)
        }
        return unit.addTo(this, amountToAdd)
    }

    override fun until(endExclusive: Temporal, unit: TemporalUnit): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLong(field: TemporalField): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun getEpochSecond(): Long {
        return epochSecond
    }

    fun getNanos(): Int {
        return nanos
    }

    fun toEpochMillis(): Long {
        return epochSecond * 1000 + nanos / 1000_000
    }

    override fun compareTo(other: Instant): Int {
        if (epochSecond > other.epochSecond) return 1
        if (epochSecond < other.epochSecond) return -1
        if (nanos > other.epochSecond) return 1
        if (nanos < other.epochSecond) return -1
        return 0
    }

    fun isAfter(other: Instant): Boolean {
        return epochSecond > other.epochSecond || (epochSecond == other.epochSecond && nanos > other.nanos)
    }

    fun isBefore(other: Instant): Boolean {
        return epochSecond < other.epochSecond || (epochSecond == other.epochSecond && nanos < other.nanos)
    }

    /**
     * Returns a copy of this instant with the specified field set to a new value.
     * <p>
     * This returns an {@code Instant}, based on this one, with the value
     * for the specified field changed.
     * If it is not possible to set the value, because the field is not supported or for
     * some other reason, an exception is thrown.
     * <p>
     * If the field is a {@link ChronoField} then the adjustment is implemented here.
     * The supported fields behave as follows:
     * <ul>
     * <li>{@code NANO_OF_SECOND} -
     *  Returns an {@code Instant} with the specified nano-of-second.
     *  The epoch-second will be unchanged.
     * <li>{@code MICRO_OF_SECOND} -
     *  Returns an {@code Instant} with the nano-of-second replaced by the specified
     *  micro-of-second multiplied by 1,000. The epoch-second will be unchanged.
     * <li>{@code MILLI_OF_SECOND} -
     *  Returns an {@code Instant} with the nano-of-second replaced by the specified
     *  milli-of-second multiplied by 1,000,000. The epoch-second will be unchanged.
     * <li>{@code INSTANT_SECONDS} -
     *  Returns an {@code Instant} with the specified epoch-second.
     *  The nano-of-second will be unchanged.
     * </ul>
     * <p>
     * In all cases, if the new value is outside the valid range of values for the field
     * then a {@code DateTimeException} will be thrown.
     * <p>
     * All other {@code ChronoField} instances will throw an {@code UnsupportedTemporalTypeException}.
     * <p>
     * If the field is not a {@code ChronoField}, then the result of this method
     * is obtained by invoking {@code TemporalField.adjustInto(Temporal, long)}
     * passing {@code this} as the argument. In this case, the field determines
     * whether and how to adjust the instant.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param field  the field to set in the result, not null
     * @param newValue  the new value of the field in the result
     * @return an {@code Instant} based on {@code this} with the specified field set, not null
     * @throws DateTimeException if the field cannot be set
     * @throws UnsupportedTemporalTypeException if the field is not supported
     * @throws ArithmeticException if numeric overflow occurs
     */
    override fun with(field: TemporalField, newValue: Long): Temporal {
        if (field is ChronoField) {
            field.checkValidValue(newValue)
            when (field) {
                ChronoField.MILLI_OF_SECOND -> {
                    val nval = newValue.toInt() * 1000_000
                    return if (nval != nanos) create(epochSecond, nval) else this
                }
                ChronoField.MICRO_OF_SECOND -> {
                    val nval = newValue.toInt() * 1000
                    return if (nval != nanos) create(epochSecond, nval) else this
                }
                ChronoField.NANO_OF_SECOND -> {
                    return if (newValue != nanos.toLong()) create(epochSecond, newValue.toInt()) else this
                }
                ChronoField.INSTANT_SECONDS -> {
                    return if (newValue != epochSecond) create(newValue, nanos) else this
                }
            }
            throw UnsupportedTemporalTypeException("Unsupported field: " + field)
        }
        return field.adjustInto(this, newValue)
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this instant with the specified amount added.
     * <p>
     * This returns an {@code Instant}, based on this one, with the specified amount added.
     * The amount is typically {@link Duration} but may be any other type implementing
     * the {@link TemporalAmount} interface.
     * <p>
     * The calculation is delegated to the amount object by calling
     * {@link TemporalAmount#addTo(Temporal)}. The amount implementation is free
     * to implement the addition in any way it wishes, however it typically
     * calls back to {@link #plus(long, TemporalUnit)}. Consult the documentation
     * of the amount implementation to determine if it can be successfully added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amountToAdd  the amount to add, not null
     * @return an {@code Instant} based on this instant with the addition made, not null
     * @throws DateTimeException if the addition cannot be made
     * @throws ArithmeticException if numeric overflow occurs
     */
    override fun plus(amount: TemporalAmount): Instant {
        return amount.addTo(this) as Instant
    }

    override fun equals(other: Any?): Boolean {
        return other is Instant && epochSecond == other.epochSecond && nanos == other.nanos
    }

    /**
     * Returns a copy of this instant with the specified duration in seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondsToAdd  the seconds to add, positive or negative
     * @return an {@code Instant} based on this instant with the specified seconds added, not null
     * @throws DateTimeException if the result exceeds the maximum or minimum instant
     * @throws ArithmeticException if numeric overflow occurs
     */
    fun plusSeconds(secondsToAdd: Long): Instant {
        return plus(secondsToAdd, 0)
    }

    /**
     * Returns a copy of this instant with the specified duration in milliseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param millisToAdd  the milliseconds to add, positive or negative
     * @return an {@code Instant} based on this instant with the specified milliseconds added, not null
     * @throws DateTimeException if the result exceeds the maximum or minimum instant
     * @throws ArithmeticException if numeric overflow occurs
     */
    fun plusMillis(millisToAdd: Long): Instant {
        return plus(millisToAdd / 1000, (millisToAdd % 1000) * 1000_000)
    }

    /**
     * Returns a copy of this instant with the specified duration in nanoseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanosToAdd  the nanoseconds to add, positive or negative
     * @return an {@code Instant} based on this instant with the specified nanoseconds added, not null
     * @throws DateTimeException if the result exceeds the maximum or minimum instant
     * @throws ArithmeticException if numeric overflow occurs
     */
    fun plusNanos(nanosToAdd: Long): Instant {
        return plus(0, nanosToAdd)
    }

    /**
     * Returns a copy of this instant with the specified duration added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondsToAdd  the seconds to add, positive or negative
     * @param nanosToAdd  the nanos to add, positive or negative
     * @return an {@code Instant} based on this instant with the specified seconds added, not null
     * @throws DateTimeException if the result exceeds the maximum or minimum instant
     * @throws ArithmeticException if numeric overflow occurs
     */
    private fun plus(secondsToAdd: Long, nanosToAdd: Long): Instant {
        if (secondsToAdd.or(nanosToAdd) == 0L) {
            return this
        }
        var epochSec = Math.addExact(epochSecond, secondsToAdd)
        epochSec = Math.addExact(epochSec, nanosToAdd / LocalTime.NANOS_PER_SECOND)
        val nanosToAdd = nanosToAdd % LocalTime.NANOS_PER_SECOND
        val nanoAdjustment = nanos + nanosToAdd  // safe int+NANOS_PER_SECOND
        return ofEpochSecond(epochSec, nanoAdjustment)
    }
}