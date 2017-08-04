package org.firas.time

import kotlin.js.Date

/**
 *
 */
impl abstract class Clock protected constructor() {

    impl abstract fun getZone(): ZoneId

    class SystemClock private constructor(val zone: ZoneId): Clock() {

        override fun getZone(): ZoneId {
            return zone
        }

        fun millis(): Long {
            return Date().getTime().toLong()
        }
    }
}
