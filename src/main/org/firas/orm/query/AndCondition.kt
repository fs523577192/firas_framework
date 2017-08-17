package org.firas.orm.query

/**
 *
 */
class AndCondition(val conditions: List<QueryCondition>): QueryCondition("AND") {
}