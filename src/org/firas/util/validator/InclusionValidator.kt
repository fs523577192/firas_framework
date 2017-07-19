package org.firas.util.validator

/**
 *
 */
class InclusionValidator<T: Any>(
        private val onlyOneError: Boolean,
        private val domain: Array<String>,
        private val message: T
) : IValidator<String?, T> {

    override fun isValid(str: String?): Triple<Boolean, String?, List<T>> {
        val messages: MutableList<T> = ArrayList()
        if (null != str && domain.any { str.equals(it) }) {
            return Triple(true, str, messages)
        }
        messages.add(message)
        return Triple(false, null, messages)
    }
}