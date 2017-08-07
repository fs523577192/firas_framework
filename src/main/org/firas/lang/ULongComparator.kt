package org.firas.lang

class ULongComparator: Comparator<Long> {

    override fun compare(a: Long, b: Long): Int {
        if (a < 0)
            return if (b < 0) a.compareTo(b) else 1
        // a >= 0
        return if (b < 0) -1 else a.compareTo(b)
    }
}
