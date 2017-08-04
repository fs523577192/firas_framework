package org.firas.time

/**
 *
 */
header abstract class Clock protected constructor() {
    abstract fun getZone(): ZoneId
    abstract fun instant(): Instant
    open fun millis(): Long
    header class SystemClock private constructor(zone: ZoneId)
}