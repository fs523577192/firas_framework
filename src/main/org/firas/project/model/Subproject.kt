package org.firas.project.model

import org.firas.orm.model.IdModel

class Subproject(
    val name: String,
    val description: String,
    val parent: MyProject) : IdModel("t_subproject") {
}
