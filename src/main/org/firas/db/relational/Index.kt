package org.firas.db.relational

/**
 *
 */
class Index(
        val type: IndexType,
        val columns: List<Column>,
        val isPrimary: Boolean = false) {

    init {
        if (!columns.isEmpty()) {
            for (i in 1 .. (columns.size - 1)) {
                if (columns[i].table != columns[0].table) {
                    throw IllegalArgumentException("The columns must be in the same table")
                }
            }
        }
    }
}