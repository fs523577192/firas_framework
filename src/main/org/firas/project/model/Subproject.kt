package org.firas.project.model

class Subproject(
    val name: String,
    val description: String,
    val parent: MyProject) : IdModel("t_subproject") {
}
