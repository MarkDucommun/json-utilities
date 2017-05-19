package com.hcsc.de.claims.jsonGeneration

import com.hcsc.de.claims.jsonSchemaConversion.*
import com.hcsc.de.claims.jsonSchemaConversion.Number

internal fun SchemaDetail.toJsonable(): Jsonable<*> {

    return when (this) {
        is Text -> TextJson(value = "".padEnd(this.maxLength, 'X'))
        is Date -> TextJson(value = "1111/11/11")
        is DateTime -> TextJson(value = "1111/11/11 11:11:11 UTC")
        is Number -> TextJson(value = "100000000000.00000")
        is Integer -> TextJson(value = "10000000000")
        is ComplexObject -> ObjectJson(properties.map { it.name to it.detail.toJsonable() }.toMap())
        is ArrayDetail -> ListJson(List(maxItems?.let { it } ?: 5) { itemType.toJsonable() })
        is OneOf -> list.first().toJsonable()
        is Reference -> TODO()
    }
}