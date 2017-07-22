package org.firas.function.model

class Subfunction(
    val name: String,
    val description: String,
    val parent: MyFunction) : IdModel("t_subfunction") {
}
