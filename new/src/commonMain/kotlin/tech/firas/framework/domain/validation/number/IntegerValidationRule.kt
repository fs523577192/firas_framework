package tech.firas.framework.domain.validation.number

import tech.firas.framework.domain.EntityField
import tech.firas.framework.domain.validation.ValidationException
import tech.firas.framework.domain.validation.ValidationRule

class IntegerValidationRule(entityField: EntityField? = null, message: String? = null,
        var maxInclusive: Int = Int.MAX_VALUE, var minInclusive: Int = Int.MIN_VALUE):
        ValidationRule<Int?>(entityField, message) {

    override fun supports(type: EntityField.Type): Boolean {
        return type == EntityField.Type.INTEGER
    }

    override fun convertFromString(str: String?): Int? {
        if (str == null) {
            return null
        }
        try {
            val n = str.toInt()
            if (n > maxInclusive) {
                throw ValidationException("Integer too large, $n > $maxInclusive")
            }
            if (n < minInclusive) {
                throw ValidationException("Integer too small, $n < $maxInclusive")
            }
            return n
        } catch (ex: NumberFormatException) {
            throw ValidationException("Not an integer", ex)
        }
    }
}