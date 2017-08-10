package org.firas.project.model

import org.firas.orm.model.IdModel

class MyFunction(
    val name: String,
    val description: String,
    val parent: Subproject) : IdModel("t_function") {
}
