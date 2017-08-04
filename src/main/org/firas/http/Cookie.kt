package org.firas.http

class Cookie(val name: String, val value: String,
        val path: String, val maxAge: Int, val domain: String,
        val httpOnly: Boolean, val secure: Boolean) {

    constructor(name: String, value: String, path: String, maxAge: Int,
            domain: String, httpOnly: Boolean):
            this(name, value, path, maxAge, domain, httpOnly, false)

    constructor(name: String, value: String, path: String,
            maxAge: Int, domain: String):
            this(name, value, path, maxAge, domain, false)

    constructor(name: String, value: String, path: String, maxAge: Int):
            this(name, value, path, maxAge, "")

    constructor(name: String, value: String, path: String):
            this(name, value, "/", -1)

    constructor(name: String, value: String):
            this(name, value, "/")
}
