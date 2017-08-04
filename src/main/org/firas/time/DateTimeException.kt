package org.firas.time

/**
 *
 */
open class DateTimeException(
        override val message: String?, override val cause: Throwable?):
        RuntimeException(message) {

    constructor(message: String?): this(message, null)
}