package org.firas.time.temporal

import org.firas.time.DateTimeException

/**
 *
 */
class UnsupportedTemporalTypeException(
        override val message: String?, override val cause: Throwable?):
        DateTimeException(message, cause) {

    constructor(message: String?): this(message, null)
}