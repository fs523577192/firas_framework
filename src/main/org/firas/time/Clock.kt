package org.firas.time

/**
 *
 */
header abstract class Clock protected constructor() {
    abstract fun getZone(): ZoneId
}