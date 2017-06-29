package org.firas.util.validator

/**
 *
 */
interface IValidator<out E, out T: Any> {
    fun isValid(str: String?): Triple<Boolean, E?, List<T>>
}