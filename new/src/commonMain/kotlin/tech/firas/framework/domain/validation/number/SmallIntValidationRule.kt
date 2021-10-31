package tech.firas.framework.domain.validation.number

import tech.firas.framework.domain.EntityField
import tech.firas.framework.domain.validation.ValidationException
import tech.firas.framework.domain.validation.ValidationRule

class SmallIntValidationRule(entityField: EntityField? = null, message: String? = null,
        var maxInclusive: Short = Short.MAX_VALUE, var minInclusive: Short = Short.MIN_VALUE):
        ValidationRule<Short?>(entityField, message) {

    override fun supports(type: EntityField.Type): Boolean {
        return type == EntityField.Type.INTEGER
    }

    override fun convertFromString(str: String?): Short? {
        if (str == null) {
            return null
        }
        try {
            val n = str.toShort()
            if (n > maxInclusive) {
                throw ValidationException("SmallInt too large, $n > $maxInclusive")
            }
            if (n < minInclusive) {
                throw ValidationException("SmallInt too small, $n < $maxInclusive")
            }
            return n
        } catch (ex: NumberFormatException) {
            throw ValidationException("Not a SmallInt", ex)
        }
    }
}