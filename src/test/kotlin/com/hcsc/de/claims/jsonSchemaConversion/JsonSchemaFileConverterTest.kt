package com.hcsc.de.claims.jsonSchemaConversion

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.javafaker.Faker
import com.hcsc.de.claims.fileReaders.JacksonJsonFileReader
import com.hcsc.de.claims.jsonDeIdentifier.FileDeidentifier
import com.hcsc.de.claims.helpers.Failure
import com.hcsc.de.claims.helpers.Success
import com.hcsc.de.claims.jsonReduction.FieldObject
import com.hcsc.de.claims.jsonReduction.JsonSchemaFileReducer
import com.hcsc.de.claims.jsonSizing.JsonSizeAnalyzer
import com.hcsc.de.claims.jsonSizing.JsonSizeNode
import com.hcsc.de.claims.jsonSizing.JsonSizer
import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Test
import java.io.File

class JsonSchemaFileConverterTest {

    val objectMapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())
    val fileReader = JacksonJsonFileReader()
    val nodeConverter = NodeConverter()
    val listConverter = ListConverter(nodeConverter = nodeConverter)
    val schemaFileConverter = JsonSchemaFileConverter(
            reader = fileReader,
            listConverter = listConverter
    )

    @Test
    fun `a test`() {

//        val result = FieldObject(name = "A").simpleFlatten(listOf("B", "C"))
//
//        assertThat(result).isEqualTo(FieldObject(
//                name = "A",
//                properties = listOf(FieldObject(
//                        name = "B",
//                        properties = listOf(FieldObject(
//                                name = "C",
//                                properties = emptyList()))))))
    }

    @Test
    fun `merge`() {

//        val result = listOf(
//                FieldObject(name = "A", properties = listOf(FieldObject(name = "B", properties = listOf(FieldObject(name = "C", properties = emptyList()))))),
//                FieldObject(name = "A", properties = listOf(FieldObject(name = "B", properties = listOf(FieldObject(name = "D", properties = emptyList()))))),
//                FieldObject(name = "A", properties = listOf(FieldObject(name = "E", properties = emptyList())))
//        ).sum()
//
//        assertThat(result).isEqualTo(FieldObject(name = "A", properties = listOf(
//                FieldObject(name = "B", properties = listOf(
//                        FieldObject(name = "C"),
//                        FieldObject(name = "D")
//                )),
//                FieldObject(name = "E")
//        )))
    }

    @Test
    fun `test`() {

//        val list = listOf(listOf("A", "B"), listOf("A", "C"))
//
//        val fieldObject = flatten(list)
//
//        assertThat(fieldObject).isEqualTo(FieldObject(
//                name = "A",
//                properties = listOf(FieldObject(name = "B"), FieldObject(name = "C"))
//        ))
    }

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

    @Test
    fun `it converts the thing`() {

        val schema = schemaFileConverter.convert("/Users/xpdesktop/workspace/json-schema-parser/src/main/resources/cts-schema.json")

//        val cinqFieldsFilePath = "/Users/xpdesktop/workspace/demo/fake-claims-generator/src/main/resources/cinq-fields.csv"

//        val fieldSetRows = cinqFieldsFilePath.readAndConvertCsvToFieldSetList()

//        val reducedSchemaFields = flatten(fieldSetRows)

//        val reducedSchema = com.hcsc.de.claims.jsonReduction.JsonSchemaFileReducer().reduce(schema, reducedSchemaFields)

        val faker = Faker()

        val jsonSizer = JsonSizer()
        val jsonSizeAverager = JsonSizeAnalyzer()

        val start = System.currentTimeMillis()

        val overview = 1.rangeTo(50)
                .map { schema.toJson() }
                .map { jsonSizer.calculateSize(it) }
                .map {
                    when (it) {
                        is Success -> it.content
                        is Failure -> throw RuntimeException("Error")
                    }
                }

        val end = System.currentTimeMillis() - start

//        val node = objectMapper.readValue<JsonNode>(schemaString)
//        val a = node.get("InsuranceClaim").get("ClaimDetail").get("ServiceLine").map { objectMapper.writeValueAsString(it).length }.average()

        println()
//        FileWriter("schema-${System.currentTimeMillis()}.json").apply {
//            write(schemaString)
//            close()
//        }
    }

    @Test
    fun `look at some real claims`() {

        val jsonSizer = JsonSizer()
        val jsonSizeAverager = JsonSizeAnalyzer()

        val fiveTen = "/Users/xpdesktop/workspace/json-schema-parser/src/main/resources/yan0510.json"
        val fiveEleven = "/Users/xpdesktop/workspace/json-schema-parser/src/main/resources/yan0511.json"

        val fiveTenClaims = String(File(fiveTen).readBytes()).split("\n")
        val fiveElevenClaims = String(File(fiveEleven).readBytes()).split("\n")

        val sizes = fiveTenClaims.plus(fiveElevenClaims).map {
            jsonSizer.calculateSize(it)
        }.map { result ->
            when (result) {
                is Success -> result.content
                is Failure -> throw RuntimeException()
            }
        }

        fun List<JsonSizeNode>.generateOverview() = jsonSizeAverager.generateJsonSizeOverview(this).blockingGet()

        val overview = sizes.generateOverview()

        println()
    }

    @Test
    fun `find accumulator key histories`() {

        val fiveTen = "/Users/xpdesktop/workspace/json-schema-parser/src/main/resources/yan0510.json"
        val fiveEleven = "/Users/xpdesktop/workspace/json-schema-parser/src/main/resources/yan0511.json"

        val allClaims = String(File(fiveTen).readBytes()).split("\n").plus(String(File(fiveEleven).readBytes()).split("\n"))

        val accumulatorKeyHistories = allClaims.map { objectMapper.readValue<JsonNode>(it).get("medicalClaim").get("accumulatorKeyHistory") }

        println()
    }

    @Test
    fun `deidentify some claims`() {
        FileDeidentifier().deidentify()
    }
}