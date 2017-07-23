package org.firas.project.model

class MyFunction(
    val name: String,
    val description: String,
    val parent: Subproject) : IdModel("t_function") {
}
