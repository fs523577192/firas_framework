package org.firas.db.relational

/**
 *
 */
class Column(
        val table: Table,
        val name: String,
        val dataType: DataType,
        val nullable: Boolean = true,
        val defaultValue: Any?) {
}