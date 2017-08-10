package org.firas.orm.model;

open class DataType(val name: String) : IdModel("t_data_type") {

    val INTERNAL_DATA_TYPES: Array<String> = arrayOf(
            "int64", "uint64", "int32", "uint32", "int16", "uint16", "int8", "uint8",
            "float64", "float32", "string", "boolean", "array", "json", "object")
}
