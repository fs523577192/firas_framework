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

/**
 * A time-zone offset from Greenwich/UTC, such as {@code +02:00}.
 * <p>
 * A time-zone offset is the period of time that a time-zone differs from Greenwich/UTC.
 * This is usually a fixed number of hours and minutes.
 * <p>
 * Different parts of the world have different time-zone offsets.
 * The rules for how offsets vary by place and time of year are captured in the
 * {@link ZoneId} class.
 * <p>
 * For example, Paris is one hour ahead of Greenwich/UTC in winter and two hours
 * ahead in summer. The {@code ZoneId} instance for Paris will reference two
 * {@code ZoneOffset} instances - a {@code +01:00} instance for winter,
 * and a {@code +02:00} instance for summer.
 * <p>
 * In 2008, time-zone offsets around the world extended from -12:00 to +14:00.
 * To prevent any problems with that range being extended, yet still provide
 * validation, the range of offsets is restricted to -18:00 to 18:00 inclusive.
 * <p>
 * This class is designed for use with the ISO calendar system.
 * The fields of hours, minutes and seconds make assumptions that are valid for the
 * standard ISO definitions of those fields. This class may be used with other
 * calendar systems providing the definition of the time fields matches those
 * of the ISO calendar system.
 * <p>
 * Instances of {@code ZoneOffset} must be compared using {@link #equals}.
 * Implementations may choose to cache certain common offsets, however
 * applications must not rely on such caching.
 *
 * <p>
 * This is a <a href="{@docRoot}/java/lang/doc-files/ValueBased.html">value-based</a>
 * class; use of identity-sensitive operations (including reference equality
 * ({@code ==}), identity hash code, or synchronization) on instances of
 * {@code ZoneOffset} may have unpredictable results and should be avoided.
 * The {@code equals} method should be used for comparisons.
 *
 * @implSpec
 */
class ZoneOffset private constructor(val totalSeconds: Int): ZoneId() {

    companion object {
        /**
         * The time-zone offset for UTC, with an ID of "Z".
         */
        val UTC = ofTotalSeconds(0)

        /**
         * Obtains an instance of {@code ZoneOffset} using the ID.
         * <p>
         * This method parses the string ID of a {@code ZoneOffset} to
         * return an instance. The parsing accepts all the formats generated by
         * {@link #getId()}, plus some additional formats:
         * <ul>
         * <li>{@code Z} - for UTC
         * <li>{@code +h}
         * <li>{@code +hh}
         * <li>{@code +hh:mm}
         * <li>{@code -hh:mm}
         * <li>{@code +hhmm}
         * <li>{@code -hhmm}
         * <li>{@code +hh:mm:ss}
         * <li>{@code -hh:mm:ss}
         * <li>{@code +hhmmss}
         * <li>{@code -hhmmss}
         * </ul>
         * Note that &plusmn; means either the plus or minus symbol.
         * <p>
         * The ID of the returned offset will be normalized to one of the formats
         * described by {@link #getId()}.
         * <p>
         * The maximum supported range is from +18:00 to -18:00 inclusive.
         *
         * @param offsetId  the offset ID, not null
         * @return the zone-offset, not null
         * @throws DateTimeException if the offset ID is invalid
         */
        fun of(offsetId: String): ZoneOffset {
            if ("Z".equals(offsetId)) return UTC
            var hours: Int
            var minutes: Int
            val seconds: Int
            when (offsetId.length) {
                2 -> {
                    hours = parseNumber(offsetId[0] + "0" + offsetId[1], 1, false)
                    minutes = 0
                    seconds = 0
                }
                3 -> {
                    hours = parseNumber(offsetId, 1, false)
                    minutes = 0
                    seconds = 0
                }
                5 -> {
                    hours = parseNumber(offsetId, 1, false)
                    minutes = parseNumber(offsetId, 3, false)
                    seconds = 0
                }
                6 -> {
                    hours = parseNumber(offsetId, 1, false)
                    minutes = parseNumber(offsetId, 4, true)
                    seconds = 0
                }
                7 -> {
                    hours = parseNumber(offsetId, 1, false)
                    minutes = parseNumber(offsetId, 3, false)
                    seconds = parseNumber(offsetId, 5, false)
                }
                9 -> {
                    hours = parseNumber(offsetId, 1, false)
                    minutes = parseNumber(offsetId, 4, true)
                    seconds = parseNumber(offsetId, 7, true)
                }
                else -> {
                    throw DateTimeException("Invalid ID for ZoneOffset, invalid format: " + offsetId)
                }
            }
            val first = offsetId[0]
            if (first != '+' && first != '-')
                throw DateTimeException("Invalid ID for ZoneOffset, plus/minus not found when expected: " + offsetId)
            return if (first == '-') ofHoursMinutesSeconds(-hours, -minutes, -seconds)
                    else ofHoursMinutesSeconds(hours, minutes, seconds)
        }

        /**
         * Obtains an instance of {@code ZoneOffset} specifying the total offset in seconds
         * <p>
         * The offset must be in the range {@code -18:00} to {@code +18:00}, which corresponds to -64800 to +64800.
         *
         * @param totalSeconds  the total time-zone offset in seconds, from -64800 to +64800
         * @return the ZoneOffset, not null
         * @throws DateTimeException if the offset is not in the required range
         */
        fun ofTotalSeconds(totalSeconds: Int): ZoneOffset {
            if (Math.abs(totalSeconds) > MAX_SECONDS) {
                throw DateTimeException("Zone offset not in valid range: -18:00 to +18:00")
            }
            if (totalSeconds % (15 * LocalTime.SECONDS_PER_MINUTE) == 0) {
                var result = SECONDS_CACHE.get(totalSeconds)
            }
            return ZoneOffset(totalSeconds)
        }

        //-----------------------------------------------------------------------
        /**
         * Obtains an instance of {@code ZoneOffset} using an offset in hours.
         *
         * @param hours  the time-zone offset in hours, from -18 to +18
         * @return the zone-offset, not null
         * @throws DateTimeException if the offset is not in the required range
         */
        fun ofHours(hours: Int): ZoneOffset {
            return ofHoursMinutesSeconds(hours, 0, 0)
        }

        /**
         * Obtains an instance of {@code ZoneOffset} using an offset in
         * hours and minutes.
         * <p>
         * The sign of the hours and minutes components must match.
         * Thus, if the hours is negative, the minutes must be negative or zero.
         * If the hours is zero, the minutes may be positive, negative or zero.
         *
         * @param hours  the time-zone offset in hours, from -18 to +18
         * @param minutes  the time-zone offset in minutes, from 0 to &plusmn;59, sign matches hours
         * @return the zone-offset, not null
         * @throws DateTimeException if the offset is not in the required range
         */
        fun ofHoursMinutes(hours: Int, minutes: Int): ZoneOffset {
            return ofHoursMinutesSeconds(hours, minutes, 0)
        }

        /**
         * Obtains an instance of {@code ZoneOffset} using an offset in
         * hours, minutes and seconds.
         * <p>
         * The sign of the hours, minutes and seconds components must match.
         * Thus, if the hours is negative, the minutes and seconds must be negative or zero.
         *
         * @param hours  the time-zone offset in hours, from -18 to +18
         * @param minutes  the time-zone offset in minutes, from 0 to &plusmn;59, sign matches hours and seconds
         * @param seconds  the time-zone offset in seconds, from 0 to &plusmn;59, sign matches hours and minutes
         * @return the zone-offset, not null
         * @throws DateTimeException if the offset is not in the required range
         */
        fun ofHoursMinutesSeconds(hours: Int, minutes: Int, seconds: Int): ZoneOffset {
            validate(hours, minutes, seconds)
            return ofTotalSeconds(totalSeconds(hours, minutes, seconds))
        }

        private fun buildId(totalSeconds: Int): String {
            if (0 == totalSeconds) return "Z"
            val absTotalSeconds = Math.abs(totalSeconds)
            val buf = StringBuilder()
            val absHours = absTotalSeconds / LocalTime.SECONDS_PER_HOUR
            val absMinutes = (absTotalSeconds / LocalTime.SECONDS_PER_MINUTE) % LocalTime.MINUTES_PER_HOUR
            buf.append(if (totalSeconds < 0) "-" else "+")
                    .append(if (absHours < 10) "0" else "").append(absHours)
                    .append(if (absMinutes < 10) ":0" else ":").append(absMinutes)
            val absSeconds = absTotalSeconds % LocalTime.SECONDS_PER_MINUTE
            if (absSeconds != 0) {
                buf.append(if (absSeconds < 10) ":0" else ":").append(absSeconds)
            }
            return buf.toString()
        }

        /**
         * Parse a two digit zero-prefixed number.
         *
         * @param offsetId  the offset ID, not null
         * @param pos  the position to parse, valid
         * @param precededByColon  should this number be prefixed by a precededByColon
         * @return the parsed number, from 0 to 99
         */
        private fun parseNumber(offsetId: CharSequence, pos: Int, precededByColon: Boolean): Int {
            if (precededByColon && offsetId[pos - 1] != ':') {
                throw DateTimeException("Invalid ID for ZoneOffset, colon not found when expected: " + offsetId)
            }
            val ch1 = offsetId[pos]
            val ch2 = offsetId[pos + 1]
            if (ch1 < '0' || ch1 > '9' || ch2 < '0' || ch2 > '9') {
                throw DateTimeException("Invalid ID for ZoneOffset, non numeric characters found: " + offsetId)
            }
            return (ch1.toInt() - 48) * 10 + (ch2.toInt() - 48)
        }

        /**
         * Calculates the total offset in seconds.
         *
         * @param hours  the time-zone offset in hours, from -18 to +18
         * @param minutes  the time-zone offset in minutes, from 0 to &plusmn;59, sign matches hours and seconds
         * @param seconds  the time-zone offset in seconds, from 0 to &plusmn;59, sign matches hours and minutes
         * @return the total in seconds
         */
        private fun totalSeconds(hours: Int, minutes: Int, seconds: Int): Int {
            return hours * LocalTime.SECONDS_PER_HOUR +
                    minutes * LocalTime.SECONDS_PER_MINUTE + seconds
        }

        /**
         * Validates the offset fields.
         *
         * @param hours  the time-zone offset in hours, from -18 to +18
         * @param minutes  the time-zone offset in minutes, from 0 to &plusmn;59
         * @param seconds  the time-zone offset in seconds, from 0 to &plusmn;59
         * @throws DateTimeException if the offset is not in the required range
         */
        private fun validate(hours: Int, minutes: Int, seconds: Int) {
            if (hours < -18 || hours > 18) {
                throw DateTimeException("Zone offset hours not in valid range: value " + hours +
                        " is not in the range -18 to 18")
            }
            if (hours > 0) {
                if (minutes < 0 || seconds < 0) {
                    throw DateTimeException("Zone offset minutes and seconds must be positive because hours is positive")
                }
            } else if (hours < 0) {
                if (minutes > 0 || seconds > 0) {
                    throw DateTimeException("Zone offset minutes and seconds must be negative because hours is negative")
                }
            } else if ((minutes > 0 && seconds < 0) || (minutes < 0 && seconds > 0)) {
                throw DateTimeException("Zone offset minutes and seconds must have the same sign")
            }
            if (Math.abs(minutes) > 59) {
                throw DateTimeException("Zone offset minutes not in valid range: abs(value) " +
                        Math.abs(minutes) + " is not in the range 0 to 59")
            }
            if (Math.abs(seconds) > 59) {
                throw DateTimeException("Zone offset seconds not in valid range: abs(value) " +
                        Math.abs(seconds) + " is not in the range 0 to 59")
            }
            if (Math.abs(hours) == 18 && (Math.abs(minutes) > 0 || Math.abs(seconds) > 0)) {
                throw DateTimeException("Zone offset not in valid range: -18:00 to +18:00")
            }
        }

        /** The abs maximum seconds */
        private val MAX_SECONDS = 18 * LocalTime.SECONDS_PER_HOUR

        /** Cache of time-zone offset by offset in seconds. */
        private val SECONDS_CACHE = HashMap<Int, ZoneOffset>(16)

        /** Cache of time-zone offset by ID. */
        private val ID_CACHE = HashMap<String, ZoneOffset>(16)
    }

    private val id: String
    init {
        id = buildId(totalSeconds)
    }
    override fun getId(): String {
        return id
    }

}
