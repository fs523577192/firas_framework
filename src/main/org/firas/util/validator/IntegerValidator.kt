package org.firas.util.validator

/**
 *
 */
open class IntegerValidator<T: Any> (
        private val onlyOneError: Boolean,
        private val message: T,
        private val min: Int? = null,
        private val minMessage: T? = null,
        private val max: Int? = null,
        private val maxMessage: T? = null
) : IValidator<Int, T> {

    init {
        if (null == min && null != minMessage ||
                null != min && null == minMessage ||
                null == max && null != maxMessage ||
                null != max && null == maxMessage) {
            throw IllegalStateException()
        }
    }

    override fun isValid(str: String?): Triple<Boolean, Int?, List<T>> {
        val messages: MutableList<T> = ArrayList()
        if (null == str) {
            messages.add(this.message)
            return Triple(false, null, messages)
        }
        try {
            val converted = str.toInt()
            var valid = true
            this.min ?.let {
                if (converted.compareTo(this.min) < 0) {
                    messages.add(checkNotNull(this.minMessage))
                    if (this.onlyOneError) return Triple(false, null, messages)
                    valid = false
                }
            }
            this.max ?.let {
                if (converted.compareTo(this.max) > 0) {
                    messages.add(checkNotNull(this.maxMessage))
                    return Triple(false, null, messages)
                }
            }
            return Triple(valid, if (valid) converted else null, messages)
        } catch (ex: NumberFormatException) {
            messages.add(this.message)
            return Triple(false, null, messages)
        }
    }

}