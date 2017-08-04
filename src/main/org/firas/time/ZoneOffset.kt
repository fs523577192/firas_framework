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
class ZoneOffset(val totalSeconds) {

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
        fun of(offsetId: String) {
            if ("Z".equals(offsetId)) return UTC
            when (offsetId.length) {
            }
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
            if (totalSeconds % (15 * SECONDS_PER_MINUTE) == 0) {
                var result = SECONDS_CACHE.get(totalSeconds)
            }
            return ZoneOffset(totalSeconds)
        }

        private fun buildId(totalSeconds: Int): String {
            if (0 == totalSeconds) return "Z"
            val absTotalSeconds = Math.abs(totalSeconds)
            val buf = StringBuilder()
            val absHours = absTotalSeconds / SECONDS_PER_HOUR
            val absMinutes = (absTotalSeconds / SECONDS_PER_MINUTE) % MINUTES_PER_HOUR
            buf.append(totalSeconds < 0 ? "-" : "+")
                    .append(absHours < 10 ? "0" : "").append(absHours)
                    .append(absMinutes < 10 ? ":0" : ":").append(absMinutes)
            val absSeconds = absTotalSeconds % SECONDS_PER_MINUTE
            if (absSeconds != 0) {
                buf.append(absSeconds < 10 ? ":0" : ":").append(absSeconds)
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
            if (precededByColon && offsetId.charAt(pos - 1) != ':') {
                throw DateTimeException("Invalid ID for ZoneOffset, colon not found when expected: " + offsetId)
            }
            val ch1 = offsetId.charAt(pos)
            val ch2 = offsetId.charAt(pos + 1)
            if (ch1 < '0' || ch1 > '9' || ch2 < '0' || ch2 > '9') {
                throw DateTimeException("Invalid ID for ZoneOffset, non numeric characters found: " + offsetId)
            }
            return (ch1 - 48) * 10 + (ch2 - 48)
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
            return hours * SECONDS_PER_HOUR + minutes * SECONDS_PER_MINUTE + seconds
        }

        /** Cache of time-zone offset by offset in seconds. */
        private val SECONDS_CACHE = MutableMap()

        /** Cache of time-zone offset by ID. */
        private val ID_CACHE = MutableMap()
    }

    init {
        id = buildId(totalSeconds);
    }

    val id: String
}
