package tech.firas.framework.domain

import tech.firas.framework.domain.validation.ValidationRule

class EntityField(var entity: Entity? = null, var name: String? = null, var type: Type? = null,
        var lengthOrPrecision: UInt = 0u, var scale: UInt = 0u, var capacityInByte: UInt = 0u,
        var nullable: Boolean = true, var defaultValue: String = "null",
        var validationRules: List<ValidationRule<*>>? = null) {

    enum class Type {
        BOOLEAN,
        SMALLINT, // 16 bit integer
        INTEGER, // 32 bit integer
        BIGINT, // 64 bit integer
        FLOAT, // 32 bit floating point number
        DOUBLE, // 64 bit floating point number
        DECIMAL, // fixed point decimal number
        TEXT,
        LOCAL_DATE_TIME,
        LOCAL_DATE,
        LOCAL_TIME,
        DATE_TIME_WITH_TIME_ZONE,
        DATE_WITH_TIME_ZONE,
        TIME_WITH_TIME_ZONE,
        BINARY
    }
}