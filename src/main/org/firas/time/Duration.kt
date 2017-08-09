/*
 * Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
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
 * This file is available under and governed by the GNU General Public
 * License version 2 only, as published by the Free Software Foundation.
 * However, the following notice accompanied the original version of this
 * file:
 *
 * Copyright (c) 2007-2012, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.firas.time

import org.firas.lang.Math
import org.firas.math.BigDecimal
import org.firas.time.temporal.*

/**
 * A time-based amount of time, such as '34.5 seconds'.
 * <p>
 * This class models a quantity or amount of time in terms of seconds and nanoseconds.
 * It can be accessed using other duration-based units, such as minutes and hours.
 * In addition, the {@link ChronoUnit#DAYS DAYS} unit can be used and is treated as
 * exactly equal to 24 hours, thus ignoring daylight savings effects.
 * See {@link Period} for the date-based equivalent to this class.
 * <p>
 * A physical duration could be of infinite length.
 * For practicality, the duration is stored with constraints similar to {@link Instant}.
 * The duration uses nanosecond resolution with a maximum value of the seconds that can
 * be held in a {@code long}. This is greater than the current estimated age of the universe.
 * <p>
 * The range of a duration requires the storage of a number larger than a {@code long}.
 * To achieve this, the class stores a {@code long} representing seconds and an {@code int}
 * representing nanosecond-of-second, which will always be between 0 and 999,999,999.
 * The model is of a directed duration, meaning that the duration may be negative.
 * <p>
 * The duration is measured in "seconds", but these are not necessarily identical to
 * the scientific "SI second" definition based on atomic clocks.
 * This difference only impacts durations measured near a leap-second and should not affect
 * most applications.
 * See {@link Instant} for a discussion as to the meaning of the second and time-scales.
 *
 * <p>
 * This is a <a href="{@docRoot}/java/lang/doc-files/ValueBased.html">value-based</a>
 * class; use of identity-sensitive operations (including reference equality
 * ({@code ==}), identity hash code, or synchronization) on instances of
 * {@code Duration} may have unpredictable results and should be avoided.
 * The {@code equals} method should be used for comparisons.
 *
 * @implSpec
 * This class is immutable
 */
class Duration private constructor(val seconds: Long, val nanos: Int): Comparable<Duration> {

    companion object {
        /**
         * Constant for a duration of zero.
         */
        val ZERO = Duration(0, 0)

        /**
         * Obtains a {@code Duration} representing a number of standard 24 hour days.
         * <p>
         * The seconds are calculated based on the standard definition of a day,
         * where each day is 86400 seconds which implies a 24 hour day.
         * The nanosecond in second field is set to zero.
         *
         * @param days  the number of days, positive or negative
         * @return a {@code Duration}, not null
         * @throws ArithmeticException if the input days exceeds the capacity of {@code Duration}
         */
        fun ofDays(days: Long): Duration {
            return create(Math.multiplyExact(days, LocalTime.SECONDS_PER_DAY.toLong()), 0)
        }

        /**
         * Obtains a {@code Duration} representing a number of standard hours.
         * <p>
         * The seconds are calculated based on the standard definition of an hour,
         * where each hour is 3600 seconds.
         * The nanosecond in second field is set to zero.
         *
         * @param hours  the number of hours, positive or negative
         * @return a {@code Duration}, not null
         * @throws ArithmeticException if the input hours exceeds the capacity of {@code Duration}
         */
        fun ofHours(hours: Long): Duration {
            return create(Math.multiplyExact(hours, LocalTime.SECONDS_PER_HOUR.toLong()), 0)
        }

        /**
         * Obtains a {@code Duration} representing a number of standard minutes.
         * <p>
         * The seconds are calculated based on the standard definition of a minute,
         * where each minute is 60 seconds.
         * The nanosecond in second field is set to zero.
         *
         * @param minutes  the number of minutes, positive or negative
         * @return a {@code Duration}, not null
         * @throws ArithmeticException if the input minutes exceeds the capacity of {@code Duration}
         */
        fun ofMinutes(minutes: Long): Duration {
            return create(Math.multiplyExact(minutes, LocalTime.SECONDS_PER_MINUTE.toLong()), 0)
        }

        //-----------------------------------------------------------------------
        /**
         * Obtains a {@code Duration} representing a number of seconds.
         * <p>
         * The nanosecond in second field is set to zero.
         *
         * @param seconds  the number of seconds, positive or negative
         * @return a {@code Duration}, not null
         */
        fun ofSeconds(seconds: Long): Duration {
            return create(seconds, 0)
        }

        /**
         * Obtains a {@code Duration} representing a number of seconds and an
         * adjustment in nanoseconds.
         * <p>
         * This method allows an arbitrary number of nanoseconds to be passed in.
         * The factory will alter the values of the second and nanosecond in order
         * to ensure that the stored nanosecond is in the range 0 to 999,999,999.
         * For example, the following will result in the exactly the same duration:
         * <pre>
         *  Duration.ofSeconds(3, 1);
         *  Duration.ofSeconds(4, -999_999_999);
         *  Duration.ofSeconds(2, 1000_000_001);
         * </pre>
         *
         * @param seconds  the number of seconds, positive or negative
         * @param nanoAdjustment  the nanosecond adjustment to the number of seconds, positive or negative
         * @return a {@code Duration}, not null
         * @throws ArithmeticException if the adjustment causes the seconds to exceed the capacity of {@code Duration}
         */
        fun ofSeconds(seconds: Long, nanoAdjustment: Long): Duration {
            val secs = Math.addExact(seconds, Math.floorDiv(nanoAdjustment, LocalTime.NANOS_PER_SECOND))
            val nos = Math.floorMod(nanoAdjustment, LocalTime.NANOS_PER_SECOND).toInt()
            return create(secs, nos)
        }

        //-----------------------------------------------------------------------
        /**
         * Obtains a {@code Duration} representing a number of milliseconds.
         * <p>
         * The seconds and nanoseconds are extracted from the specified milliseconds.
         *
         * @param millis  the number of milliseconds, positive or negative
         * @return a {@code Duration}, not null
         */
        fun ofMillis(millis: Long): Duration {
            var secs = millis / 1000
            var mos = (millis % 1000).toInt()
            if (mos < 0) {
                mos += 1000
                secs--
            }
            return create(secs, mos * 1000_000)
        }

        //-----------------------------------------------------------------------
        /**
         * Obtains a {@code Duration} representing a number of nanoseconds.
         * <p>
         * The seconds and nanoseconds are extracted from the specified nanoseconds.
         *
         * @param nanos  the number of nanoseconds, positive or negative
         * @return a {@code Duration}, not null
         */
        fun ofNanos(nanos: Long): Duration {
            var secs = nanos / LocalTime.NANOS_PER_SECOND
            var nos = (nanos % LocalTime.NANOS_PER_SECOND).toInt()
            if (nos < 0) {
                nos += LocalTime.NANOS_PER_SECOND.toInt()
                secs--
            }
            return create(secs, nos)
        }

        //-----------------------------------------------------------------------
        /**
         * Obtains a {@code Duration} representing an amount in the specified unit.
         * <p>
         * The parameters represent the two parts of a phrase like '6 Hours'. For example:
         * <pre>
         *  Duration.of(3, SECONDS);
         *  Duration.of(465, HOURS);
         * </pre>
         * Only a subset of units are accepted by this method.
         * The unit must either have an {@linkplain TemporalUnit#isDurationEstimated() exact duration} or
         * be {@link ChronoUnit#DAYS} which is treated as 24 hours. Other units throw an exception.
         *
         * @param amount  the amount of the duration, measured in terms of the unit, positive or negative
         * @param unit  the unit that the duration is measured in, must have an exact duration, not null
         * @return a {@code Duration}, not null
         * @throws DateTimeException if the period unit has an estimated duration
         * @throws ArithmeticException if a numeric overflow occurs
         */
        fun of(amount: Long, unit: TemporalUnit): Duration {
            return ZERO.plus(amount, unit)
        }

        //-----------------------------------------------------------------------
        /**
         * Obtains an instance of {@code Duration} from a temporal amount.
         * <p>
         * This obtains a duration based on the specified amount.
         * A {@code TemporalAmount} represents an  amount of time, which may be
         * date-based or time-based, which this factory extracts to a duration.
         * <p>
         * The conversion loops around the set of units from the amount and uses
         * the {@linkplain TemporalUnit#getDuration() duration} of the unit to
         * calculate the total {@code Duration}.
         * Only a subset of units are accepted by this method. The unit must either
         * have an {@linkplain TemporalUnit#isDurationEstimated() exact duration}
         * or be {@link ChronoUnit#DAYS} which is treated as 24 hours.
         * If any other units are found then an exception is thrown.
         *
         * @param amount  the temporal amount to convert, not null
         * @return the equivalent duration, not null
         * @throws DateTimeException if unable to convert to a {@code Duration}
         * @throws ArithmeticException if numeric overflow occurs
         */
        fun from(amount: TemporalAmount): Duration {
            var duration: Duration = ZERO;
            for (unit in amount.getUnits()) {
                duration = duration.plus(amount.get(unit), unit)
            }
            return duration
        }

        fun create(seconds: Long, nanos: Int): Duration {
            if (0L == seconds && 0 == nanos) return ZERO
            return Duration(seconds, nanos)
        }

        /**
         * The pattern for parsing.
         */
        private val PATTERN: Regex = Regex("([-+]?)P(?:([-+]?[0-9]+)D)?" +
                "(T(?:([-+]?[0-9]+)H)?(?:([-+]?[0-9]+)M)?" +
                "(?:([-+]?[0-9]+)(?:[.,]([0-9]{0,9}))?S)?)?", RegexOption.IGNORE_CASE)

    }

    fun get(unit: TemporalUnit): Long {
        if (unit == ChronoUnit.SECONDS) return seconds
        if (unit == ChronoUnit.NANOS) return nanos.toLong()
        throw UnsupportedTemporalTypeException("Unsupported unit: " + unit)
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this duration with the specified duration added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param duration  the duration to add, positive or negative, not null
     * @return a {@code Duration} based on this duration with the specified duration added, not null
     * @throws ArithmeticException if numeric overflow occurs
     */
    fun plus(duration: Duration): Duration {
        return plus(duration.seconds, duration.nanos.toLong())
     }

    /**
     * Returns a copy of this duration with the specified duration added.
     * <p>
     * The duration amount is measured in terms of the specified unit.
     * Only a subset of units are accepted by this method.
     * The unit must either have an {@linkplain TemporalUnit#isDurationEstimated() exact duration} or
     * be {@link ChronoUnit#DAYS} which is treated as 24 hours. Other units throw an exception.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param amountToAdd  the amount of the period, measured in terms of the unit, positive or negative
     * @param unit  the unit that the period is measured in, must have an exact duration, not null
     * @return a {@code Duration} based on this duration with the specified duration added, not null
     * @throws UnsupportedTemporalTypeException if the unit is not supported
     * @throws ArithmeticException if numeric overflow occurs
     */
    fun plus(amountToAdd: Long, unit: TemporalUnit): Duration {
        if (unit == ChronoUnit.DAYS) {
            return plus(Math.multiplyExact(amountToAdd, LocalTime.SECONDS_PER_DAY.toLong()), 0L)
        }
        if (unit.isDurationEstimated()) {
            throw UnsupportedTemporalTypeException("Unit must not have an estimated duration")
        }
        if (amountToAdd == 0L) {
            return this
        }
        if (unit is ChronoUnit) {
            return when (unit) {
                ChronoUnit.NANOS -> plusNanos(amountToAdd)
                ChronoUnit.MICROS -> plusSeconds((amountToAdd / (1000_000L * 1000)) * 1000).plusNanos((amountToAdd % (1000_000L * 1000)) * 1000)
                ChronoUnit.MILLIS -> plusMillis(amountToAdd)
                ChronoUnit.SECONDS -> plusSeconds(amountToAdd)
                else -> plusSeconds(Math.multiplyExact(unit.getDuration().seconds, amountToAdd))
            }
        }
        val duration = unit.getDuration().multipliedBy(amountToAdd)
        return plusSeconds(duration.seconds).plusNanos(duration.nanos.toLong())
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this duration with the specified duration in standard 24 hour days added.
     * <p>
     * The number of days is multiplied by 86400 to obtain the number of seconds to add.
     * This is based on the standard definition of a day as 24 hours.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param daysToAdd  the days to add, positive or negative
     * @return a {@code Duration} based on this duration with the specified days added, not null
     * @throws ArithmeticException if numeric overflow occurs
     */
    fun plusDays(daysToAdd: Long): Duration {
        return plus(Math.multiplyExact(daysToAdd, LocalTime.SECONDS_PER_DAY.toLong()), 0)
    }

    /**
     * Returns a copy of this duration with the specified duration in hours added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param hoursToAdd  the hours to add, positive or negative
     * @return a {@code Duration} based on this duration with the specified hours added, not null
     * @throws ArithmeticException if numeric overflow occurs
     */
    fun plusHours(hoursToAdd: Long): Duration {
        return plus(Math.multiplyExact(hoursToAdd, LocalTime.SECONDS_PER_HOUR.toLong()), 0)
    }

    /**
     * Returns a copy of this duration with the specified duration in minutes added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param minutesToAdd  the minutes to add, positive or negative
     * @return a {@code Duration} based on this duration with the specified minutes added, not null
     * @throws ArithmeticException if numeric overflow occurs
     */
    fun plusMinutes(minutesToAdd: Long): Duration {
        return plus(Math.multiplyExact(minutesToAdd, LocalTime.SECONDS_PER_MINUTE.toLong()), 0)
    }

    /**
     * Returns a copy of this duration with the specified duration in seconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondsToAdd  the seconds to add, positive or negative
     * @return a {@code Duration} based on this duration with the specified seconds added, not null
     * @throws ArithmeticException if numeric overflow occurs
     */
    fun plusSeconds(secondsToAdd: Long): Duration {
        return plus(secondsToAdd, 0)
    }

    /**
     * Returns a copy of this duration with the specified duration in milliseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param millisToAdd  the milliseconds to add, positive or negative
     * @return a {@code Duration} based on this duration with the specified milliseconds added, not null
     * @throws ArithmeticException if numeric overflow occurs
     */
    fun plusMillis(millisToAdd: Long): Duration {
        return plus(millisToAdd / 1000, (millisToAdd % 1000) * 1000_000)
    }

    /**
     * Returns a copy of this duration with the specified duration in nanoseconds added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param nanosToAdd  the nanoseconds to add, positive or negative
     * @return a {@code Duration} based on this duration with the specified nanoseconds added, not null
     * @throws ArithmeticException if numeric overflow occurs
     */
    fun plusNanos(nanosToAdd: Long): Duration {
        return plus(0, nanosToAdd)
    }

    //-----------------------------------------------------------------------
    /**
     * Returns a copy of this duration multiplied by the scalar.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param multiplicand  the value to multiply the duration by, positive or negative
     * @return a {@code Duration} based on this duration multiplied by the specified scalar, not null
     * @throws ArithmeticException if numeric overflow occurs
     */
    fun multipliedBy(multiplicand: Long): Duration {
        if (multiplicand == 0L) return ZERO
        if (multiplicand == 1L) return this
        return create(toSeconds().multiply(BigDecimal.valueOf(multiplicand)))
    }

    override fun compareTo(other: Duration): Int {
        if (seconds > other.seconds) return 1
        if (seconds < other.seconds) return -1
        if (nanos > other.nanos) return 1
        if (nanos < other.nanos) return -1
        return 0
    }

    /**
     * Returns a copy of this duration with the specified duration added.
     * <p>
     * This instance is immutable and unaffected by this method call.
     *
     * @param secondsToAdd  the seconds to add, positive or negative
     * @param nanosToAdd  the nanos to add, positive or negative
     * @return a {@code Duration} based on this duration with the specified seconds added, not null
     * @throws ArithmeticException if numeric overflow occurs
     */
    private fun plus(secondsToAdd: Long, nanosToAdd: Long): Duration {
        if (secondsToAdd or nanosToAdd == 0L) return this
        var epochSec = Math.addExact(seconds, secondsToAdd)
        epochSec = Math.addExact(epochSec, nanosToAdd / LocalTime.NANOS_PER_SECOND)
        var nanoAdjustment = nanos + nanosToAdd % LocalTime.NANOS_PER_SECOND
        return ofSeconds(epochSec, nanoAdjustment)
    }

    /**
     * Converts this duration to the total length in seconds and
     * fractional nanoseconds expressed as a {@code BigDecimal}.
     *
     * @return the total length of the duration in seconds, with a scale of 9, not null
     */
    private fun toSeconds(): BigDecimal {
        return BigDecimal.valueOf(seconds).add(BigDecimal.valueOf(nanos, 9))
    }
}