package tech.firas.framework.domain.validation.number

import tech.firas.framework.domain.EntityField
import tech.firas.framework.domain.validation.ValidationException
import tech.firas.framework.domain.validation.ValidationRule

class FloatValidationRule(entityField: EntityField? = null, message: String? = null,
        var max: Float = Float.MAX_VALUE, var min: Float = Float.MIN_VALUE,
        var includeMax: Boolean = true, var includeMin: Boolean = true):
        ValidationRule<Float?>(entityField, message) {

    override fun supports(type: EntityField.Type): Boolean {
        return type == EntityField.Type.FLOAT
    }

    override fun convertFromString(str: String?): Float? {
        if (str == null) {
            return null
        }
        try {
            val n = str.toFloat()
            if (n > max) {
                throw ValidationException("Float too large, $n > $max")
            }
            if (!includeMax && n == max) {
                throw ValidationException("Float too large, $n >= $max")
            }
            if (n < min) {
                throw ValidationException("Float too small, $n < $max")
            }
            if (!includeMin && n == min) {
                throw ValidationException("Float too small, $n <= $max")
            }
            return n
        } catch (ex: NumberFormatException) {
            throw ValidationException("Not a float", ex)
        }
    }
}