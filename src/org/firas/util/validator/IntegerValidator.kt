package org.firas.util.validator

/**
 *
 */
class IntegerValidator<T: Any> (
        private val onlyOneError: Boolean,
        private val message: T,
        private val min: Int? = null,
        private val minMessage: T? = null,
        private val max: Int? = null,
        private val maxMessage: T? = null
) : IValidator<Int, T> {

    override fun isValid(str: String?): Triple<Boolean, Int?, List<T>> {
        val messages: MutableList<T> = ArrayList()
        if (null == str) {
            messages.add(this.message)
            return Triple(false, null, messages)
        }
        val converted = str.toInt()
        var valid = true
        this.min ?.let {
            if (converted.compareTo(checkNotNull(this.min)) < 0) {
                messages.add(checkNotNull(this.minMessage))
                if (this.onlyOneError) return Triple(false, null, messages)
                valid = false
            }
        }
        this.max ?.let {
            if (converted.compareTo(checkNotNull(this.max)) > 0) {
                messages.add(checkNotNull(this.maxMessage))
                return Triple(false, null, messages)
            }
        }
        return Triple(valid, if (valid) converted else null, messages)
    }

}