package org.firas.db.relational

/**
 *
 */
class Connection(
        val type: DatabaseType,
        val host: String,
        val port: Int,
        val userName: String,
        val password: String
) {
}