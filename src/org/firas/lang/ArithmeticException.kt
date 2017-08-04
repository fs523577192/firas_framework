package org.firas.lang

/**
 *
 */
open class ArithmeticException(override val message: String?):
        RuntimeException(message) {
    constructor(): this(null)
}