package org.firas.util

/**
 *
 */
class Arrays {

    companion object {

        fun fill(a: IntArray, element: Int, fromIndex: Int, toIndex: Int) {
            for (i in fromIndex .. (toIndex - 1)) a[i] = element
        }

        fun fill(a: IntArray, element: Int, fromIndex: Int = 0) {
            fill(a, element, fromIndex, a.size)
        }

        fun copyOf(original: IntArray, newLength: Int): IntArray {
            return original.copyOf(newLength)
        }

        fun copyOfRange(original: IntArray, from: Int, to: Int): IntArray {
            return original.copyOfRange(from, to)
        }
    }
}