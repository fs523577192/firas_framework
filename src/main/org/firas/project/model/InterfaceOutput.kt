package org.firas.project.model

import org.firas.orm.model.DataType

class InterfaceOutput(
    val name: String,
    val description: String,
    val dataType: DataType,
    val parent: MyInterface) : IdModel("t_interface_output") {
}
