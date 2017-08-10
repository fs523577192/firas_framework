package org.firas.project.model

import org.firas.function.model.Subfunction
import org.firas.orm.model.IdModel

class MyInterface(
    val name: String,
    val description: String,
    val parent: Subfunction) : IdModel("t_interface") {
}
