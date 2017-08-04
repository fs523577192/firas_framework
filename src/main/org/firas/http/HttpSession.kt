package org.firas.http

abstract class HttpSession(val id: String,
        protected var maxInactiveInterval: Int, var isNew: Boolean) {

    open fun getAttribute(name: String): Any? {
        return attributes.get(name)
    }

    open fun setAttribute(name: String, value: Any) {
        attributes.put(name, value)
    }

    open fun removeAttribute(name: String) {
        attributes.remove(name)
    }

    open fun getMaxInactiveInterval(): Int {
        return maxInactiveInterval
    }

    open fun setMaxInactiveInterval(maxInactiveInterval: Int) {
        if (maxInactiveInterval <= 0)
            throw IllegalAugumentException("\"maxInactiveInterval\" is not positive")
        this.maxInactiveInterval = maxInactiveInterval
    }

    protected val attributes = HashMap<String, Any>()
}
