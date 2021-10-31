package tech.firas.framework.domain.validation

import tech.firas.framework.domain.EntityField

class TextValidationRule(entityField: EntityField? = null, message: String? = null,
        var maxLengthInclusive: UInt = UInt.MAX_VALUE, var minLengthInclusive: UInt = 0u):
        ValidationRule<String?>(entityField, message) {

    override fun supports(type: EntityField.Type): Boolean {
        return type == EntityField.Type.TEXT
    }

    override fun convertFromString(str: String?): String? {
        val len = str?.length?.toUInt() ?: return str
        if (len > maxLengthInclusive) {
            throw ValidationException("String too long: $len > $maxLengthInclusive")
        }
        if (len < minLengthInclusive) {
            throw ValidationException("String too short: $len < $minLengthInclusive")
        }
        return str
    }
}