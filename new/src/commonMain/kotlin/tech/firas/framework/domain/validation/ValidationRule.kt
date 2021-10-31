package tech.firas.framework.domain.validation

import tech.firas.framework.domain.EntityField

abstract class ValidationRule<out T>(var entityField: EntityField? = null, var message: String? = null) {

    init {
        val type = entityField?.type
        if (type != null && !supports(type)) {
            throw IllegalArgumentException("Unsupported type: $type")
        }
    }

    abstract fun supports(type: EntityField.Type): Boolean

    @Throws(ValidationException::class)
    abstract fun convertFromString(str: String?): T?
}