package org.firas.util.validator

/**
 *
 */
class StringValidator<T: Any>(private val onlyOneError: Boolean) : IValidator<String, T> {

    constructor(onlyOneError: Boolean, pattern: String, message: T) : this(onlyOneError) {
        this.pattern = pattern
        this.message = message
    }

    constructor(onlyOneError: Boolean, min: Int, minMessage: T) : this(onlyOneError) {
        this.min = min
        this.minMessage = minMessage
    }

    constructor(onlyOneError: Boolean, min: Int?, minMessage: T?,
                max: Int, maxMessage: T) : this(onlyOneError) {
        this.min = min
        this.minMessage = minMessage
        this.max = max
        this.maxMessage = maxMessage
    }

    override fun isValid(str: String?): Triple<Boolean, String, List<T>> {
        this.min ?.let {

        }
        this.max ?.let {

        }
        this.pattern ?.let {

        }
        return false
    }

    private var min: Int? = null
    private var max: Int? = null
    private var minMessage: T? = null
    private var maxMessage: T? = null
    private var pattern: String? = null
    private var message: T? = null
    private val messages: List<T> = ArrayList()
    private var converted: String? = null
}