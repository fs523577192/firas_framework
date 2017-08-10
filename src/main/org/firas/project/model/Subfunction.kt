package org.firas.function.model

import org.firas.orm.model.IdModel
import org.firas.project.model.MyFunction

class Subfunction(
    val name: String,
    val description: String,
    val parent: MyFunction) : IdModel("t_subfunction") {
}
