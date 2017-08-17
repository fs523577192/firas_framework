package org.firas.orm.query

/**
 *
 */
class EqualCondition(val field: Any, val value: Any): QueryCondition("=") {
}