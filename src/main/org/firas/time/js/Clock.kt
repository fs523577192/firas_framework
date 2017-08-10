package org.firas.time

import kotlin.js.Date

/**
 *
 */
impl abstract class Clock protected constructor() {

    companion object {
        val systemDefault = SystemClock(ZoneId.default)
    }
    impl abstract fun getZone(): ZoneId
    impl abstract fun instant(): Instant
    impl open fun millis(): Long {
        return instant().toEpochMillis()
    }

    impl class SystemClock internal constructor(private val zone: ZoneId): Clock() {

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
