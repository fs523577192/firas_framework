package org.firas.project.model

class MyInterface(
    val name: String,
    val description: String,
    val parent: Subfunction) : IdModel("t_interface") {
}
