package org.firas.orm.model;

import org.firas.util.validator.IValidator

open class DataTypeField<T, MT: Any>(
    val name: String,
    val dataType: DataType,
    val parent: DataType,
    val validator: IValidator<T, MT>,
    val defaultValue: T,
    val nullable: Boolean) : IdModel("t_data_type_field") {
}
