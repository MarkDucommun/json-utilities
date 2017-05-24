package com.hcsc.de.claims.jsonSchemaConversion

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
) : SchemaDetail()

data class ComplexObject(
        val properties: List<SchemaObject<*>>
) : SchemaDetail()

data class OneOf(
        val list: List<SchemaDetail>
) : SchemaDetail()