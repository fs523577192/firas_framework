package org.firas.orm.query

/**
 *
 */
class NotInCondition(val field: Any, val values: Set<Any>): QueryCondition("NOT IN") {
}