package com.hcsc.de.claims.jsonReduction

import com.hcsc.de.claims.jsonSchemaConversion.*
import com.hcsc.de.claims.jsonSchemaConversion.Number
import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Test

class JsonSchemaFileReducerTest {

    @Test
    fun `simple schema modifier`() {

        val schema = SchemaObject(name = "B", detail = Number)

        val fields = FieldObject(name = "B")

        val newSchema = JsonSchemaFileReducer().reduce(schemaObject = schema, fieldObject = fields)

        assertThat(newSchema).isEqualTo(schema)
    }

    @Test
    fun `schema modifier`() {

        val schema = SchemaObject(name = "A", detail = ComplexObject(properties = listOf(
                SchemaObject(name = "B", detail = Number),
                SchemaObject(name = "C", detail = Number),
                SchemaObject(name = "D", detail = Number)
        )))

        val fields = FieldObject(name = "A", properties = listOf(
                FieldObject(name = "C"),
                FieldObject(name = "D")
        ))

        val newSchema = JsonSchemaFileReducer().reduce(schemaObject = schema, fieldObject = fields)

        assertThat(newSchema).isEqualTo(SchemaObject(name = "A", detail = ComplexObject(properties = listOf(
                SchemaObject(name = "C", detail = Number),
                SchemaObject(name = "D", detail = Number)
        ))))
    }

    @Test
    fun `schema modifier more`() {

        val schema = SchemaObject(name = "A", detail = ComplexObject(properties = listOf(
                SchemaObject(name = "C", detail = Number),
                SchemaObject(name = "D", detail = ComplexObject(properties = listOf(
                        SchemaObject(name = "E", detail = Number),
                        SchemaObject(name = "F", detail = Number)
                )))
        )))

        val fields = FieldObject(name = "A", properties = listOf(
                FieldObject(name = "D", properties = listOf(
                        FieldObject(name = "E")
                ))
        ))

        val newSchema = JsonSchemaFileReducer().reduce(schemaObject = schema, fieldObject = fields)

        assertThat(newSchema).isEqualTo(SchemaObject(name = "A", detail = ComplexObject(properties = listOf(
                SchemaObject(name = "D", detail = ComplexObject(properties = listOf(
                        SchemaObject(name = "E", detail = Number)
                )))
        ))))
    }

    @Test
    fun `schema modifier array`() {

        val schema = SchemaObject(name = "A", detail = ComplexObject(properties = listOf(
                SchemaObject(name = "C", detail = Number),
                SchemaObject(name = "D", detail = ArrayDetail(
                        itemType = ComplexObject(properties = listOf(
                                SchemaObject(name = "E", detail = Number),
                                SchemaObject(name = "F", detail = Number)
                        )),
                        maxItems = null
                ))
        )))

        val fields = FieldObject(name = "A", properties = listOf(
                FieldObject(name = "D", properties = listOf(
                        FieldObject(name = "E")
                ))
        ))

        val newSchema = JsonSchemaFileReducer().reduce(schemaObject = schema, fieldObject = fields)

        assertThat(newSchema).isEqualTo(SchemaObject(name = "A", detail = ComplexObject(properties = listOf(
                SchemaObject(name = "D", detail = ArrayDetail(
                        itemType = ComplexObject(properties = listOf(
                                SchemaObject(name = "E", detail = Number)
                        )),
                        maxItems = null
                ))
        ))))
    }

    @Test
    fun `schema modifier array of arrays`() {

        val schema = SchemaObject(name = "A", detail = ComplexObject(properties = listOf(
                SchemaObject(name = "C", detail = Number),
                SchemaObject(name = "D", detail = ArrayDetail(
                        itemType = Number,
                        maxItems = null
                ))
        )))

        val fields = FieldObject(name = "A", properties = listOf(
                FieldObject(name = "D")
        ))

        val newSchema = JsonSchemaFileReducer().reduce(schemaObject = schema, fieldObject = fields)

        assertThat(newSchema).isEqualTo(SchemaObject(name = "A", detail = ComplexObject(properties = listOf(
                SchemaObject(name = "D", detail = ArrayDetail(
                        itemType = Number,
                        maxItems = null
                ))
        ))))
    }

    @Test
    fun `schema modifier one of`() {

        val schema = SchemaObject(name = "A", detail = ComplexObject(properties = listOf(
                SchemaObject(name = "C", detail = Number),
                SchemaObject(name = "ClaimDetail", detail = OneOf(list = listOf(
                        ComplexObject(properties = listOf(
                                SchemaObject(name = "E", detail = Number),
                                SchemaObject(name = "F", detail = Number)
                        ))
                )))
        )))

        val fields = FieldObject(name = "A", properties = listOf(
                FieldObject(name = "MedicalClaim", properties = listOf(
                        FieldObject(name = "E")
                ))
        ))

        val newSchema = JsonSchemaFileReducer().reduce(schemaObject = schema, fieldObject = fields)

        assertThat(newSchema).isEqualTo(SchemaObject(name = "A", detail = ComplexObject(properties = listOf(
                SchemaObject(name = "ClaimDetail", detail = OneOf(list = listOf(
                        ComplexObject(properties = listOf(
                                SchemaObject(name = "E", detail = Number)
                        ))
                )))
        ))))
    }
}