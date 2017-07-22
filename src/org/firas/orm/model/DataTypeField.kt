package org.firas.orm.model;

open class DataTypeField(
    val name: String,
    val dataType: DataType,
    val parent: DataType,
    val nullable: Boolean) : IdModel("t_data_type_field") {
}
