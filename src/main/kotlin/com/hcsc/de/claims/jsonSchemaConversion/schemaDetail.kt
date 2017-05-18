package com.hcsc.de.claims.jsonSchemaConversion

import java.util.*

sealed class SchemaDetail {
    abstract fun toJsonable(): Any
}

data class Text(
        val maxLength: Int
) : SchemaDetail() {

    override fun toJsonable(): String {

        return 1.rangeTo(random(maxLength)).fold("") { acc, _ -> acc.plus("X") }
    }
}

object Date : SchemaDetail() {

    override fun toJsonable(): String {
        return "1111/11/11"
    }
}

object DateTime : SchemaDetail() {

    override fun toJsonable(): String {
        return "1111/11/11 11:11:11 UTC"
    }
}

object Number : SchemaDetail() {

    override fun toJsonable(): String {
        return "100000000000.00000"
    }
}

object Integer : SchemaDetail() {

    override fun toJsonable(): String {
        return "10000000000"
    }
}

data class Reference(val type: String) : SchemaDetail() {

    override fun toJsonable(): String {
        throw Exception("I should have been replaced")
    }
}

data class ArrayDetail(
        val itemType: SchemaDetail,
        val maxItems: Int?
) : SchemaDetail() {

    override fun toJsonable(): List<Any> {

        return List(maxItems?.let { random(it) } ?: random(5)) {
            itemType.toJsonable()
        }
    }
}

data class ComplexObject(
        val properties: List<SchemaObject<*>>
) : SchemaDetail() {

    override fun toJsonable(): Map<String, Any> {
        return properties.map {
            val toJson = it.detail.toJsonable()
            it.name to toJson
        }.toMap()
    }
}

data class OneOf(
        val list: List<SchemaDetail>
) : SchemaDetail() {

    override fun toJsonable(): Any {
        return list.first().toJsonable()
    }
}

val random = Random()

fun random(i: Int): Int = random.nextInt(i)