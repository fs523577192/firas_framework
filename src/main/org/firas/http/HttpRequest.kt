package org.firas.http

abstract class HttpRequest(val remoteAddress: String, val remotePort: Int,
        val method: HttpMethod) {

    open fun getParameter(name: String): String? {
        return parameters.get(name)
    }

    abstract fun getSession(create: Boolean): HttpSession
    open fun getSession(): HttpSession {
        return getSession(true)
    }

    open fun getAttribute(name: String): Any? {
        return attributes.get(name)
    }

    open fun setAttribute(name: String, value: Any) {
        attributes.put(name, value)
    }

    open fun removeAttribute(name: String) {
        attributes.remove(name)
    }

    protected val attributes = HashMap<String, Any>()
    protected val parameters = HashMap<String, String>()
}
