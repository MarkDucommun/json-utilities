package com.hcsc.de.claims.jsonSchemaConversion

import java.util.*

sealed class SchemaDetail

data class Text(
        val maxLength: Int
) : SchemaDetail()

object Date : SchemaDetail()

object DateTime : SchemaDetail()

object Number : SchemaDetail()

object Integer : SchemaDetail()

data class Reference(val type: String) : SchemaDetail()

data class ArrayDetail(
        val itemType: SchemaDetail,
        val maxItems: Int?
) : SchemaDetail() {

//    override fun toJsonable(): List<Any> {
//
//        return List(maxItems?.let { random(it) } ?: random(5)) {
//            itemType.toJsonable()
//        }
//    }
}

data class ComplexObject(
        val properties: List<SchemaObject<*>>
) : SchemaDetail()

data class OneOf(
        val list: List<SchemaDetail>
) : SchemaDetail() {

//    override fun toJsonable(): Any {
//        return list.first().toJsonable()
//    }
}

val random = Random()

fun random(i: Int): Int = random.nextInt(i)