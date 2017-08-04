package org.firas.time

import kotlin.js.Date

/**
 *
 */
impl abstract class Clock protected constructor() {

    impl abstract fun getZone(): ZoneId
    impl abstract fun instant(): Instant
    impl open fun millis(): Long {
        return instant().toEpochMillis()
    }

    impl class SystemClock private constructor(private val zone: ZoneId): Clock() {

        override fun getZone(): ZoneId {
            return zone
        }

        override fun millis(): Long {
            return Date().getTime().toLong()
        }

        override fun instant(): Instant {
            return Instant.ofEpochMilli(millis())
        }
    }
}
