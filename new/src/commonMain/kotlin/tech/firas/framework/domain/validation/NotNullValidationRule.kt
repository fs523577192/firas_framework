package tech.firas.framework.domain.validation

import tech.firas.framework.domain.EntityField
import tech.firas.framework.domain.EntityField.Type

class NotNullValidationRule(entityField: EntityField?, message: String?):
        ValidationRule<String>(entityField, message) {

    override fun supports(type: Type): Boolean {
        return true
    }

    override fun convertFromString(str: String?): String {
        return str ?: throw ValidationException("Violating NotNull")
    }
}