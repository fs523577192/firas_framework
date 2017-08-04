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
 * Copyright (c) 2011-2012, Stephen Colebourne & Michael Nascimento Santos
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
package org.firas.time.temporal

import org.firas.time.DateTimeException

/**
 * The range of valid values for a date-time field.
 * <p>
 * All {@link TemporalField} instances have a valid range of values.
 * For example, the ISO day-of-month runs from 1 to somewhere between 28 and 31.
 * This class captures that valid range.
 * <p>
 * It is important to be aware of the limitations of this class.
 * Only the minimum and maximum values are provided.
 * It is possible for there to be invalid values within the outer range.
 * For example, a weird field may have valid values of 1, 2, 4, 6, 7, thus
 * have a range of '1 - 7', despite that fact that values 3 and 5 are invalid.
 * <p>
 * Instances of this class are not tied to a specific field.
 *
 * @implSpec
 */
class ValueRange private constructor(
        val minSmallest: Long, val minLargest: Long,
        val maxSmallest: Long, val maxLargest: Long) {

    companion object {
        fun of(min: Long, max: Long): ValueRange {
            if (min > max) throw IllegalArgumentException("Minimum value must be less than maximum value")
            return ValueRange(min, min, max, max)
        }

        fun of(min: Long, maxSmallest: Long, maxLargest: Long): ValueRange {
            return of(min, min, maxSmallest, maxLargest)
        }

        fun of(minSmallest: Long, minLargest: Long, maxSmallest: Long, maxLargest: Long): ValueRange {
            if (minSmallest > minLargest) {
                throw IllegalArgumentException("Smallest minimum value must be less than largest minimum value")
            }
            if (maxSmallest > maxLargest) {
                throw IllegalArgumentException("Smallest maximum value must be less than largest maximum value")
            }
            if (minLargest > maxLargest) {
                throw IllegalArgumentException("Minimum value must be less than maximum value")
            }
            return ValueRange(minSmallest, minLargest, maxSmallest, maxLargest)
        }
    }

    fun isFixed(): Boolean {
        return minSmallest == minLargest && maxSmallest == maxLargest
    }

    fun getMinimum(): Long {
        return minSmallest
    }

    fun getLargestMinimum(): Long {
        return minLargest
    }

    fun getSmallestMaximum(): Long {
        return maxSmallest
    }

    fun getMaximum(): Long {
        return maxLargest
    }

    fun isIntValue(): Boolean {
        return getMinimum() >= Int.MIN_VALUE && getMaximum() <= Int.MAX_VALUE
    }

    fun isValidValue(value: Long): Boolean {
        return getMinimum() <= value && value <= getMaximum()
    }

    fun isValidIntValue(value: Long): Boolean {
        return isIntValue() && isValidValue(value)
    }

    /**
     * Checks that the specified value is valid.
     * <p>
     * This validates that the value is within the valid range of values.
     * The field is only used to improve the error message.
     *
     * @param value  the value to check
     * @param field  the field being checked, may be null
     * @return the value that was passed in
     * @see #isValidValue(long)
     */
    fun checkValidValue(value: Long, field: TemporalField): Long {
        if (!isValidValue(value)) {
            throw DateTimeException(genInvalidFieldMessage(field, value))
        }
        return value;
    }

    /**
     * Checks that the specified value is valid and fits in an {@code int}.
     * <p>
     * This validates that the value is within the valid range of values and that
     * all valid values are within the bounds of an {@code int}.
     * The field is only used to improve the error message.
     *
     * @param value  the value to check
     * @param field  the field being checked, may be null
     * @return the value that was passed in
     * @see #isValidIntValue(long)
     */
    fun checkValidIntValue(value: Long, field: TemporalField): Int {
        if (!isValidIntValue(value)) {
            throw DateTimeException(genInvalidFieldMessage(field, value))
        }
        return value.toInt()
    }

    override fun toString(): String {
        val buf = StringBuilder()
        buf.append(minSmallest)
        if (minSmallest != minLargest) buf.append('/').append(minLargest)
        buf.append(" - ").append(maxSmallest)
        if (maxSmallest != maxLargest) buf.append('/').append(maxLargest)
        return buf.toString()
    }

    override fun equals(obj: Any?): Boolean {
        if (this == obj) return true
        if (obj is ValueRange) {
            return obj.minSmallest == minSmallest && obj.minLargest == minLargest &&
                    obj.maxSmallest == maxSmallest && obj.maxLargest == maxLargest
        }
        return false
    }

    override fun hashCode(): Int {
        val hash = minSmallest + (minLargest shl 16) +
                (minLargest shr 48) + (maxSmallest shl 32) +
                (maxSmallest shr 32) + (maxLargest shl 48) +
                (maxLargest shr 16)
        return (hash xor (hash ushr 32)).toInt()
    }

    private fun genInvalidFieldMessage(field: TemporalField, value: Long): String {
        return "Invalid value" + (if (null != field) (" for " + field) else "") +
                " (valid values " + this + "): " + value

    }

}