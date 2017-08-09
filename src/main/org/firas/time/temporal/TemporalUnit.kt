package org.firas.time.temporal

import org.firas.time.Duration
import org.firas.time.DateTimeException



/**
 *
 */
interface TemporalUnit {

    /**
     * Checks if this unit represents a component of a date.
     * <p>
     * A date is time-based if it can be used to imply meaning from a date.
     * It must have a {@linkplain #getDuration() duration} that is an integral
     * multiple of the length of a standard day.
     * Note that it is valid for both {@code isDateBased()} and {@code isTimeBased()}
     * to return false, such as when representing a unit like 36 hours.
     *
     * @return true if this unit is a component of a date
     */
    fun isDateBase(): Boolean

    /**
     * Checks if this unit represents a component of a time.
     * <p>
     * A unit is time-based if it can be used to imply meaning from a time.
     * It must have a {@linkplain #getDuration() duration} that divides into
     * the length of a standard day without remainder.
     * Note that it is valid for both {@code isDateBased()} and {@code isTimeBased()}
     * to return false, such as when representing a unit like 36 hours.
     *
     * @return true if this unit is a component of a time
     */
    fun isTimeBase(): Boolean

    /**
     * Gets the duration of this unit, which may be an estimate.
     * <p>
     * All units return a duration measured in standard nanoseconds from this method.
     * The duration will be positive and non-zero.
     * For example, an hour has a duration of {@code 60 * 60 * 1,000,000,000ns}.
     * <p>
     * Some units may return an accurate duration while others return an estimate.
     * For example, days have an estimated duration due to the possibility of
     * daylight saving time changes.
     * To determine if the duration is an estimate, use {@link #isDurationEstimated()}.
     *
     * @return the duration of this unit, which may be an estimate, not null
     */
    fun getDuration(): Duration

    /**
     * Checks if the duration of the unit is an estimate.
     * <p>
     * All units have a duration, however the duration is not always accurate.
     * For example, days have an estimated duration due to the possibility of
     * daylight saving time changes.
     * This method returns true if the duration is an estimate and false if it is
     * accurate. Note that accurate/estimated ignores leap seconds.
     *
     * @return true if the duration is estimated, false if accurate
     */
    fun isDurationEstimated(): Boolean

    /**
     * Checks if this unit is supported by the specified temporal object.
     * <p>
     * This checks that the implementing date-time can add/subtract this unit.
     * This can be used to avoid throwing an exception.
     * <p>
     * This default implementation derives the value using
     * {@link Temporal#plus(long, TemporalUnit)}.
     *
     * @param temporal  the temporal object to check, not null
     * @return true if the unit is supported
     */
    fun isSupportedBy(temporal: Temporal): Boolean

    /**
     * Returns a copy of the specified temporal object with the specified period added.
     *
     *
     * The period added is a multiple of this unit. For example, this method
     * could be used to add "3 days" to a date by calling this method on the
     * instance representing "days", passing the date and the period "3".
     * The period to be added may be negative, which is equivalent to subtraction.
     *
     *
     * There are two equivalent ways of using this method.
     * The first is to invoke this method directly.
     * The second is to use [Temporal.plus]:
     * <pre>
     * // these two lines are equivalent, but the second approach is recommended
     * temporal = thisUnit.addTo(temporal);
     * temporal = temporal.plus(thisUnit);
    </pre> *
     * It is recommended to use the second approach, `plus(TemporalUnit)`,
     * as it is a lot clearer to read in code.
     *
     *
     * Implementations should perform any queries or calculations using the units
     * available in [ChronoUnit] or the fields available in [ChronoField].
     * If the unit is not supported an `UnsupportedTemporalTypeException` must be thrown.
     *
     *
     * Implementations must not alter the specified temporal object.
     * Instead, an adjusted copy of the original must be returned.
     * This provides equivalent, safe behavior for immutable and mutable implementations.

     * @param <R>  the type of the Temporal object
     * *
     * @param temporal  the temporal object to adjust, not null
     * *
     * @param amount  the amount of this unit to add, positive or negative
     * *
     * @return the adjusted temporal object, not null
     * *
     * @throws DateTimeException if the period cannot be added
     * *
     * @throws UnsupportedTemporalTypeException if the unit is not supported by the temporal
    </R> */
    fun <R : Temporal> addTo(temporal: R, amount: Long): R
}