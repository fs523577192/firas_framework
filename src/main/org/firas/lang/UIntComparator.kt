package org.firas.lang

class UIntComparator: Comparator<Int> {

    override fun compare(a: Int, b: Int): Int {
        if (a < 0)
            return if (b < 0) (a - b) else 1
        // a >= 0
        return if (b < 0) -1 else (a - b)
    }
}
