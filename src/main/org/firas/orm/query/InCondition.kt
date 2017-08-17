package org.firas.orm.query

/**
 *
 */
class InCondition(val field: Any, val values: Set<Any>): QueryCondition("IN") {
}