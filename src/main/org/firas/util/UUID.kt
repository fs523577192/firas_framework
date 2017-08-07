package org.firas.util

import org.firas.lang.ULongComparator

class UUID(val mostSigBits: Long, val leastSigBits: Long): Comparable<UUID> {

    companion object {

        enum Type {
            UNKONWN,
            TIME_BASED,
            DCE,
            NAME_BASED_MD5,
            RANDOM_BASED,
            NAME_BASED_SHA1
        }

        /** Returns val represented by the specified number of hex digits. */
        private fun digits(v: Long, digits: Int): String {
            val hi = 1L.shl(digits.shl(2)) // 1L << (4 * digits)
            return hi.or(v.and(hi - 1)).toHexString().substring(1)
        }
    }

    /**
     * The version number associated with this {@code UUID}.  The version
     * number describes how this {@code UUID} was generated.
     *
     * The version number has the following meaning:
     * <ul>
     * <li>1    Time-based UUID
     * <li>2    DCE security UUID
     * <li>3    Name-based UUID
     * <li>4    Randomly generated UUID
     * </ul>
     *
     * @return  The version number of this {@code UUID}
     */
    fun version(): Int {
        return mostSigBits.shr(12).toInt().and(0xF)
    }

    /**
     * The timestamp value associated with this UUID.
     *
     * <p> The 60 bit timestamp value is constructed from the time_low,
     * time_mid, and time_hi fields of this {@code UUID}.  The resulting
     * timestamp is measured in 100-nanosecond units since midnight,
     * October 15, 1582 UTC.
     *
     * <p> The timestamp value is only meaningful in a time-based UUID, which
     * has version type 1.  If this {@code UUID} is not a time-based UUID then
     * this method throws UnsupportedOperationException.
     *
     * @throws UnsupportedOperationException
     *         If this UUID is not a version 1 UUID
     * @return The timestamp of this {@code UUID}.
     */
    fun timestamp(): Long {
        if (version() != Type.TIME_BASED.ordinal())
            throw UnsupportedOperationException(" Not a time-based UUID")
        return mostSigBits.and(0x0FFFL).shl(48).or(
                mostSigBits.shr(16).and(0xFFFFL).shl(32)).or(
                mostSigBits.ushr(32))
    }

    /**
     * https://github.com/cowtowncoder/java-uuid-generator/blob/3.0/src/main/java/com/fasterxml/uuid/UUIDComparator.java
     */
    fun compareTo(val other: UUID): Int {
        val type = version()
        var diff = type - other.version()
        if (0 != diff) return diff

        if (Type.TIME_BASED.ordinal() == type)
            diff = ULongComparator.compare(timestamp(), other.timestamp())
        else
            diff = ULongComparator.compare(mostSigBits, other.mostSigBits)

        return if (0 != diff) diff
                else ULongComparator.compare(leastSigBits, other.leastSigBits)
    }

    /**
     * Returns a {@code String} object representing this {@code UUID}.
     * @return  A string representation of this {@code UUID}
     */
    override fun toString(): String {
        return (digits(mostSigBits shr 32, 8) + "-" +
                digits(mostSigBits shr 16, 4) + "-" +
                digits(mostSigBits, 4) + "-" +
                digits(leastSigBits shr 48, 4) + "-" +
                digits(leastSigBits, 12))
    }
}
