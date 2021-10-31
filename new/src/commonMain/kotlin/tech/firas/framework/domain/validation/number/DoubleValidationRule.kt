package tech.firas.framework.domain.validation.number

import tech.firas.framework.domain.EntityField
import tech.firas.framework.domain.validation.ValidationException
import tech.firas.framework.domain.validation.ValidationRule

class DoubleValidationRule(entityField: EntityField? = null, message: String? = null,
        var max: Double = Double.MAX_VALUE, var min: Double = Double.MIN_VALUE,
        var includeMax: Boolean = true, var includeMin: Boolean = true):
        ValidationRule<Double?>(entityField, message) {

    override fun supports(type: EntityField.Type): Boolean {
        return type == EntityField.Type.FLOAT
    }

    override fun convertFromString(str: String?): Double? {
        if (str == null) {
            return null
        }
        try {
            val n = str.toDouble()
            if (n > max) {
                throw ValidationException("Double too large, $n > $max")
            }
            if (!includeMax && n == max) {
                throw ValidationException("Double too large, $n >= $max")
            }
            if (n < min) {
                throw ValidationException("Double too small, $n < $max")
            }
            if (!includeMin && n == min) {
                throw ValidationException("Double too small, $n <= $max")
            }
            return n
        } catch (ex: NumberFormatException) {
            throw ValidationException("Not a double", ex)
        }
    }
}