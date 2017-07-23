package org.firas.util.validator

/**
 *
 */
class StringValidator<T: Any> private constructor(
        private val onlyOneError: Boolean,
        private val pattern: Regex? = null,
        private val message: T? = null,
        private val max: Int? = null,
        private val maxMessage: T? = null,
        private val min: Int? = null,
        private val minMessage: T? = null
) : IValidator<MatchResult?, T> {

    constructor(onlyOneError: Boolean, pattern: Regex, message: T):
            this(onlyOneError, pattern, message, null, null, null, null)

    constructor(onlyOneError: Boolean, max: Int, maxMessage: T):
            this(onlyOneError, null, null, max, maxMessage, null, null)

    constructor(onlyOneError: Boolean, max: Int, maxMessage: T, min: Int, minMessage: T):
            this(onlyOneError, null, null, max, maxMessage, min, minMessage)

    override fun isValid(str: String?): Triple<Boolean, MatchResult?, List<T>> {
        val messages: MutableList<T> = ArrayList()
        var converted: MatchResult? = null
        var isValid = true
        this.pattern ?.let {
            if (null == str) {
                messages.add(checkNotNull(message))
                return Triple(false, null, messages)
            }
            converted = this.pattern.matchEntire(str)
            if (null == converted) {
                messages.add(checkNotNull(message))
                if (onlyOneError) return Triple(false, null, messages)
                isValid = false
            }
        }
        this.max ?.let {
            if (null != str && str.length > this.max) {
                messages.add(checkNotNull(maxMessage))
                if (onlyOneError) return Triple(false, null, messages)
                isValid = false
            }
        }
        this.min ?.let {
            if (this.min > 0 && null == str || null != str && str.length < this.min) {
                messages.add(checkNotNull(minMessage))
            }
        }
        return Triple(isValid, if (isValid) converted else null, messages)
    }

}