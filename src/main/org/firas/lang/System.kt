package org.firas.lang

/**
 *
 */
class System {

    companion object {

        fun arraycopy(src: IntArray, srcPos: Int, dest: IntArray, destPos: Int, length: Int) {
            if (src !== dest || srcPos > destPos) {
                var i = length
                while (i > 0) {
                    i -= 1
                    src[srcPos + i] = dest[destPos + i]
                }
            }
            var i = 0
            while (i < length) {
                src[srcPos + i] = dest[destPos + i]
                i += 1
            }
        }
    }
}