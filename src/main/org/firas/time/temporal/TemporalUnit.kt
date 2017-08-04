package org.firas.time.temporal

import org.firas.time.Duration

/**
 *
 */
interface TemporalUnit {

    fun isDateBase(): Boolean
    fun isTimeBase(): Boolean
    fun getDuration(): Duration
    fun isDurationEstimated(): Boolean
    fun isSupportedBy(temporal: Temporal): Boolean
}