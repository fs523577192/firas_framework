package org.firas.orm.query

/**
 *
 */
class OrCondition(val conditions: List<QueryCondition>): QueryCondition("OR") {
}