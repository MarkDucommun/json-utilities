package com.hcsc.de.claims.jsonReduction

import com.hcsc.de.claims.jsonSchemaConversion.ArrayDetail
import com.hcsc.de.claims.jsonSchemaConversion.ComplexObject
import com.hcsc.de.claims.jsonSchemaConversion.OneOf
import com.hcsc.de.claims.jsonSchemaConversion.SchemaObject

class JsonSchemaFileReducer {

    fun reduce(schemaObject: SchemaObject<*>, fieldObject: FieldObject): SchemaObject<*> {

        return when (schemaObject.detail) {
            is ComplexObject -> schemaObject.copy(detail = schemaObject.detail.copy(
                    properties = schemaObject.detail.properties.reduceProperties(fieldObject = fieldObject)
            ))
            is ArrayDetail -> {
                when (schemaObject.detail.itemType) {
                    is ComplexObject -> schemaObject.copy(detail = schemaObject.detail.copy(
                            itemType = schemaObject.detail.itemType.copy(
                                    properties = schemaObject.detail.itemType.properties.reduceProperties(fieldObject = fieldObject)
                            )
                    ))
                    else -> schemaObject
                }
            }
            is OneOf -> {
                if (schemaObject.name == "ClaimDetail" && fieldObject.name == "MedicalClaim") {
                    schemaObject.copy(detail = schemaObject.detail.copy(
                            list = schemaObject.detail.list.map { detail ->
                                when (detail) {
                                    is ComplexObject -> detail.copy(properties = detail.properties.reduceProperties(fieldObject = fieldObject))
                                    else -> detail
                                }
                            }
                    ))
                } else {
                    schemaObject
                }
            }
            else -> if (schemaObject.name == fieldObject.name) {
                schemaObject
            } else {
                throw RuntimeException("Names don't match, something went wrong")
            }
        }
    }

    fun List<SchemaObject<*>>.reduceProperties(fieldObject: FieldObject): List<SchemaObject<*>> {
        return this.map { property ->
            fieldObject
                    .properties
                    .find { it.name == property.name || it.name == "MedicalClaim" && property.name == "ClaimDetail" }
                    ?.let { innerFieldObject -> reduce(property, innerFieldObject) }
        }.filterNotNull()
    }
}