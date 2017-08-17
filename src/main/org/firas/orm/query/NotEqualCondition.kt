package org.firas.orm.query

/**
 *
 */
class NotEqualCondition(val field: Any, val value: Any): QueryCondition("!=") {
}