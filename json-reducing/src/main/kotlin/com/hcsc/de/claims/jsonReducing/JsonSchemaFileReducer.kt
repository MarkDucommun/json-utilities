package com.hcsc.de.claims.jsonReducing

import com.hcsc.de.claims.jsonSchemaConversion.ArrayDetail
import com.hcsc.de.claims.jsonSchemaConversion.ComplexObject
import com.hcsc.de.claims.jsonSchemaConversion.OneOf
import com.hcsc.de.claims.jsonSchemaConversion.SchemaObject

class JsonSchemaFileReducer {

    fun reduce(schemaObject: SchemaObject<*>, fieldObject: FieldObject): SchemaObject<*> {

        return when (schemaObject.detail) {
            is ComplexObject -> schemaObject.copy(detail = (schemaObject.detail as ComplexObject).copy(
                    properties = (schemaObject.detail as ComplexObject).properties.reduceProperties(fieldObject = fieldObject)
            ))
            is ArrayDetail -> {
                when ((schemaObject.detail as ArrayDetail).itemType) {
                    is ComplexObject -> schemaObject.copy(detail = (schemaObject.detail as ArrayDetail).copy(
                            itemType = ((schemaObject.detail as ArrayDetail).itemType as ComplexObject).copy(
                                    properties = ((schemaObject.detail as ArrayDetail).itemType as ComplexObject).properties.reduceProperties(fieldObject = fieldObject)
                            )
                    ))
                    else -> schemaObject
                }
            }
            is OneOf -> {
                if (schemaObject.name == "ClaimDetail" && fieldObject.name == "MedicalClaim") {
                    schemaObject.copy(detail = (schemaObject.detail as OneOf).copy(
                            list = (schemaObject.detail as OneOf).list.map { detail ->
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