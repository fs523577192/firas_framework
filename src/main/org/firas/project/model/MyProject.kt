package org.firas.project.model

import org.firas.orm.model.IdModel

class MyProject(val name: String, val description: String) : IdModel("t_project") {
}
