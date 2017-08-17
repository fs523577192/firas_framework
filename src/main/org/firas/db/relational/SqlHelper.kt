package org.firas.db.relational

/**
 *
 */
class SqlHelper {

    companion object {

        enum class Operator(val value: String) {
            GT(">"),
            GTE(">="),
            LT("<"),
            LTE("<="),
            EQ("="),
            NE("<>")
        }

        class Result(tableName: String) {
            val aliasCount = HashMap<String, Int>()
            val bindAliasCount = HashMap<String, Int>()
            val fieldMap = HashMap<String, String>()
            val columnMap = HashMap<String, String>()
            val modelMap = HashMap<String, String>()
            val bind = HashMap<String, String>()
            val main: String

            init {
                main = setName(this, tableName)
            }
        }

        private fun setName(result: Result, name: String): String {
            if (!result.aliasCount.has(name)) {
                result.aliasCount.set(name, 1)
                return name + "_0"
            }
            val count = result.aliasCount.get(name)
            result.aliasCount.set(name, count + 1)
            return name + '_' + count
        }

        private fun setBind(result: Result, name: String): String {
            if (!result.bindAliasCount.has(name)) {
                result.bindAliasCount.set(name, 1)
                return name + "_0"
            }
            val count = result.bindAliasCount.get(name)
            result.bindAliasCount.set(name, count + 1)
            return name + '_' + count
        }

        fun handleColumnsOption(result: Result, options: Any,
                                fieldMapPrefix: Any,
                                tableName: String,
                                fieldNames: Any) {

        }

        fun handleWhereOption(where: Any, result: Result, tableName: String, fieldNames: Any) {

        }

        fun handleJoinOption(type: Any, registry: Any, option: Any,
                             result: Result, fieldMapPrefix: Any,
                             tableName: String, fields: Any) {

        }

        fun afterFind(result: Result, row: Any) {

        }
    }
}