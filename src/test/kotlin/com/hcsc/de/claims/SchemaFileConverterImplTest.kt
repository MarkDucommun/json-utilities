package com.hcsc.de.claims

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Test
import java.io.FileWriter
import kotlin.Double.Companion.NaN

class SchemaFileConverterImplTest {

    val objectMapper: ObjectMapper = ObjectMapper().registerModule(KotlinModule())
    val fileReader = JacksonFileReader()
    val nodeConverter = NodeConverterImpl()
    val listConverter = ListConverterImpl(nodeConverter = nodeConverter)
    val schemaFileConverter = SchemaFileConverterImpl(
            reader = fileReader,
            listConverter = listConverter
    )

    @Test
    fun `converts the simplest objects - string`() {
        val testNode: JsonNode = mapOf(
                "type" to "string",
                "maxLength" to 30
        ).convert()

        val schema = nodeConverter.convert(testNode)

        assertThat(schema).isEqualTo(Text(maxLength = 30))
    }

    @Test
    fun `converts the simplest objects - date`() {
        val testNode: JsonNode = mapOf(
                "type" to "string",
                "format" to "date"
        ).convert()

        val schema = nodeConverter.convert(testNode)

        assertThat(schema).isEqualTo(Date)
    }

    @Test
    fun `converts the simplest objects - date-time`() {
        val testNode: JsonNode = mapOf(
                "type" to "string",
                "format" to "date-time"
        ).convert()

        val schema = nodeConverter.convert(testNode)

        assertThat(schema).isEqualTo(DateTime)
    }

    @Test
    fun `converts the simplest objects - number`() {
        val testNode: JsonNode = mapOf(
                "type" to "number"
        ).convert()

        val schema = nodeConverter.convert(testNode)

        assertThat(schema).isEqualTo(Number)
    }

    @Test
    fun `converts the simplest objects - integer`() {
        val testNode: JsonNode = mapOf(
                "type" to "integer"
        ).convert()

        val schema = nodeConverter.convert(testNode)

        assertThat(schema).isEqualTo(Integer)
    }

    @Test
    fun `converts a simple object`() {

        val testNode: JsonNode = mapOf(
                "type" to "object",
                "properties" to mapOf(
                        "fieldA" to mapOf("type" to "string", "maxLength" to 30),
                        "fieldB" to mapOf("type" to "string", "maxLength" to 15)
                )
        ).convert()

        val schema = nodeConverter.convert(testNode)

        assertThat(schema).isEqualTo(ComplexObject(
                properties = listOf(
                        SchemaObject(name = "fieldA", detail = Text(maxLength = 30)),
                        SchemaObject(name = "fieldB", detail = Text(maxLength = 15))
                )
        ))
    }

    @Test
    fun `it converts a simple array`() {
        val testNode: JsonNode = mapOf(
                "type" to "array",
                "items" to mapOf(
                        "type" to "string",
                        "maxLength" to 15
                )
        ).convert()

        val schema = nodeConverter.convert(testNode)

        assertThat(schema).isEqualTo(Array(
                itemType = Text(maxLength = 15),
                maxItems = null
        ))
    }

    @Test
    fun `it converts a simple array with maxItems`() {
        val testNode: JsonNode = mapOf(
                "type" to "array",
                "items" to mapOf(
                        "type" to "string",
                        "maxLength" to 15
                ),
                "maxItems" to 100
        ).convert()

        val schema = nodeConverter.convert(testNode)

        assertThat(schema).isEqualTo(Array(
                itemType = Text(maxLength = 15),
                maxItems = 100
        ))
    }

    @Test
    fun `it converts a more complex array`() {
        val testNode: JsonNode = mapOf(
                "type" to "array",
                "items" to mapOf(
                        "type" to "object",
                        "properties" to mapOf(
                                "fieldA" to mapOf("type" to "string", "maxLength" to 30),
                                "fieldB" to mapOf("type" to "string", "maxLength" to 15)
                        )
                ),
                "maxItems" to 100
        ).convert()

        val schema = nodeConverter.convert(testNode)

        assertThat(schema).isEqualTo(Array(
                itemType = ComplexObject(
                        properties = listOf(
                                SchemaObject(name = "fieldA", detail = Text(maxLength = 30)),
                                SchemaObject(name = "fieldB", detail = Text(maxLength = 15))
                        )
                ),
                maxItems = 100
        ))
    }

    @Test
    fun `it converts a Reference`() {
        val testNode: JsonNode = mapOf("\$ref" to "definition").convert()

        val schema = nodeConverter.convert(testNode)

        assertThat(schema).isEqualTo(Reference(type = "definition"))
    }

    @Test
    fun `it converts a OneOf`() {
        val testNode: JsonNode = mapOf(
                "oneOf" to listOf(
                        mapOf("\$ref" to "definition"),
                        mapOf("type" to "string", "maxLength" to 15)
                )
        ).convert()

        val schema = nodeConverter.convert(testNode)

        assertThat(schema).isEqualTo(OneOf(
                list = listOf(
                        Reference(type = "definition"),
                        Text(maxLength = 15)
                )
        ))
    }

    @Test
    fun `a test`() {

        val result = FieldObject(name = "A").simpleFlatten(listOf("B", "C"))

        assertThat(result).isEqualTo(FieldObject(
                name = "A",
                properties = listOf(FieldObject(
                        name = "B",
                        properties = listOf(FieldObject(
                                name = "C",
                                properties = emptyList()))))))
    }

    @Test
    fun `merge`() {

        val result = listOf(
                FieldObject(name = "A", properties = listOf(FieldObject(name = "B", properties = listOf(FieldObject(name = "C", properties = emptyList()))))),
                FieldObject(name = "A", properties = listOf(FieldObject(name = "B", properties = listOf(FieldObject(name = "D", properties = emptyList()))))),
                FieldObject(name = "A", properties = listOf(FieldObject(name = "E", properties = emptyList())))
        ).sum()

        assertThat(result).isEqualTo(FieldObject(name = "A", properties = listOf(
                FieldObject(name = "B", properties = listOf(
                        FieldObject(name = "C"),
                        FieldObject(name = "D")
                )),
                FieldObject(name = "E")
        )))
    }

    @Test
    fun `test`() {

        val list = listOf(listOf("A", "B"), listOf("A", "C"))

        val fieldObject = flatten(list)

        assertThat(fieldObject).isEqualTo(FieldObject(
                name = "A",
                properties = listOf(FieldObject(name = "B"), FieldObject(name = "C"))
        ))
    }

    @Test
    fun `simple schema modifier`() {

        val schema = SchemaObject(name = "B", detail = Number)

        val fields = FieldObject(name = "B")

        val newSchema = SchemaReducerImpl().reduce(schemaObject = schema, fieldObject = fields)

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

        val newSchema = SchemaReducerImpl().reduce(schemaObject = schema, fieldObject = fields)

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

        val newSchema = SchemaReducerImpl().reduce(schemaObject = schema, fieldObject = fields)

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
                SchemaObject(name = "D", detail = Array(
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

        val newSchema = SchemaReducerImpl().reduce(schemaObject = schema, fieldObject = fields)

        assertThat(newSchema).isEqualTo(SchemaObject(name = "A", detail = ComplexObject(properties = listOf(
                SchemaObject(name = "D", detail = Array(
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
                SchemaObject(name = "D", detail = Array(
                        itemType = Number,
                        maxItems = null
                ))
        )))

        val fields = FieldObject(name = "A", properties = listOf(
                FieldObject(name = "D")
        ))

        val newSchema = SchemaReducerImpl().reduce(schemaObject = schema, fieldObject = fields)

        assertThat(newSchema).isEqualTo(SchemaObject(name = "A", detail = ComplexObject(properties = listOf(
                SchemaObject(name = "D", detail = Array(
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

        val newSchema = SchemaReducerImpl().reduce(schemaObject = schema, fieldObject = fields)

        assertThat(newSchema).isEqualTo(SchemaObject(name = "A", detail = ComplexObject(properties = listOf(
                SchemaObject(name = "ClaimDetail", detail = OneOf(list = listOf(
                        ComplexObject(properties = listOf(
                                SchemaObject(name = "E", detail = Number)
                        ))
                )))
        ))))
    }

    @Test
    fun `it can size JSON objects`() {

        val jsonString: String = mapOf("top" to mapOf("A" to "XXXX", "B" to "XXXX")).writeAsString()

        val sizeDescription = jsonString.understandJsonSize()

        assertThat(sizeDescription).isEqualTo(PreJsonSizeNode(
                name = "top",
                size = 23,
                children = listOf(
                        PreJsonSizeNode(name = "A", size = 6),
                        PreJsonSizeNode(name = "B", size = 6)
                )
        ))
    }


    @Test
    fun `it can size JSON objects with arrays`() {

        val jsonString: String = mapOf("top" to listOf("XXXX", "XXXXXXX")).writeAsString()

        val sizeDescription = jsonString.understandJsonSize()

        assertThat(sizeDescription).isEqualTo(PreJsonSizeNode(
                name = "top",
                size = 18,
                children = listOf(
                        PreJsonSizeNode(name = "0", size = 6),
                        PreJsonSizeNode(name = "1", size = 9)
                )
        ))
    }

    @Test
    fun `it can size JSON objects with arrays of complex objects`() {

        val jsonString: String = mapOf("top" to listOf(
                mapOf("A" to "XXXX", "B" to "XXXXX"),
                mapOf("C" to "XXXX", "D" to "XXXXXX"))).writeAsString()

        val sizeDescription = jsonString.understandJsonSize()

        assertThat(sizeDescription).isEqualTo(PreJsonSizeNode(
                name = "top",
                size = 63,
                children = listOf(
                        PreJsonSizeNode(name = "0", size = 25,
                                children = listOf(
                                        PreJsonSizeNode(name = "A", size = 6),
                                        PreJsonSizeNode(name = "B", size = 8)
                                )),
                        PreJsonSizeNode(name = "1", size = 35,
                                children = listOf(
                                        PreJsonSizeNode(name = "C", size = 10),
                                        PreJsonSizeNode(name = "D", size = 14)
                                ))
                )
        ))
    }

    @Test
    fun `it can sum a list of jsonSizeNodes to create an averaged node`() {
        val node1 = PreJsonSizeNode(
                name = "top",
                size = 48,
                children = listOf(
                        PreJsonSizeNode(name = "0", size = 20,
                                children = listOf(
                                        PreJsonSizeNode(name = "A", size = 4),
                                        PreJsonSizeNode(name = "B", size = 5)
                                )),
                        PreJsonSizeNode(name = "1", size = 25,
                                children = listOf(
                                        PreJsonSizeNode(name = "C", size = 6),
                                        PreJsonSizeNode(name = "D", size = 8)
                                ))
                )
        )

        val node2 = PreJsonSizeNode(
                name = "top",
                size = 63,
                children = listOf(
                        PreJsonSizeNode(name = "0", size = 25,
                                children = listOf(
                                        PreJsonSizeNode(name = "A", size = 6),
                                        PreJsonSizeNode(name = "B", size = 8)
                                )),
                        PreJsonSizeNode(name = "1", size = 35,
                                children = listOf(
                                        PreJsonSizeNode(name = "C", size = 10),
                                        PreJsonSizeNode(name = "D", size = 14)
                                ))
                )
        )

        val averagedNode = listOf(node1, node2).generateAveragedNode()

        assertThat(averagedNode).isEqualTo(
                PreJsonSizeNode(
                        name = "top",
                        size = 63,
                        children = listOf(
                                PreJsonSizeNode(name = "0", size = 25,
                                        children = listOf(
                                                PreJsonSizeNode(name = "A", size = 6),
                                                PreJsonSizeNode(name = "B", size = 8)
                                        )),
                                PreJsonSizeNode(name = "1", size = 35,
                                        children = listOf(
                                                PreJsonSizeNode(name = "C", size = 10),
                                                PreJsonSizeNode(name = "D", size = 14)
                                        ))
                        )
                )
        )
    }

    @Test
    fun `it converts the thing`() {

        val schema = schemaFileConverter.convert("/Users/xpdesktop/workspace/demo/fake-claims-generator/src/main/resources/cts-schema.json")

        val cinqFieldsFilePath = "/Users/xpdesktop/workspace/demo/fake-claims-generator/src/main/resources/cinq-fields.csv"

//        val fieldSetRows = cinqFieldsFilePath.readAndConvertCsvToFieldSetList()

//        val reducedSchemaFields = flatten(fieldSetRows)

//        val reducedSchema = SchemaReducerImpl().reduce(schema, reducedSchemaFields)

        val schemaString = schema.toJson()

        val objectMapper = ObjectMapper()

        val node = objectMapper.readValue<JsonNode>(schemaString)

        val a = node.get("InsuranceClaim").get("ClaimDetail").get("ServiceLine").map { objectMapper.writeValueAsString(it).length }.average()

        println()
//        FileWriter("schema-${System.currentTimeMillis()}.json").apply {
//            write(schemaString)
//            close()
//        }
    }

    @Test
    fun `it writes a Text as a string`() {

        val json = Text(maxLength = 10).toJsonable()

        assertThat(json.length).isEqualTo(10)
    }

    @Test
    fun `it writes Text as X`() {
        assertThat(Text(maxLength = 1).toJsonable()).isEqualTo("X")
    }

    @Test
    fun `it writes all the object type SchemaDetails`() {
        assertThat(Date.toJsonable()).isEqualTo("1111/11/11")
        assertThat(DateTime.toJsonable()).isEqualTo("1111/11/11 11:11:11 UTC")
        assertThat(Number.toJsonable()).isEqualTo("100000000000.00000")
        assertThat(Integer.toJsonable()).isEqualTo("10000000000")
    }

    @Test
    fun `it writes a simple Complex Object to json`() {
        val map = ComplexObject(properties = listOf(
                SchemaObject("A", detail = Text(maxLength = 1)),
                SchemaObject("B", detail = Text(maxLength = 1)),
                SchemaObject("C", detail = Text(maxLength = 1))
        )).toJsonable()

        assertThat(map).isEqualTo(mapOf("A" to "X", "B" to "X", "C" to "X"))
    }

    @Test
    fun `it writes a complex Complex Object to json`() {
        val map = ComplexObject(properties = listOf(
                SchemaObject(name = "A", detail = ComplexObject(
                        properties = listOf(
                                SchemaObject(name = "1", detail = Text(maxLength = 1))
                        )
                ))
        )).toJsonable()

        assertThat(map).isEqualTo(mapOf("A" to mapOf("1" to "X")))
    }

    @Test
    fun `it writes a simple Array to List`() {
        val list = Array(itemType = Text(maxLength = 1), maxItems = 3).toJsonable()

        assertThat(list).containsExactlyInAnyOrder("X", "X", "X")
    }

    @Test
    fun `it writes a complex Array to List`() {
        val list = Array(itemType = Array(itemType = Text(maxLength = 1), maxItems = 3), maxItems = 1).toJsonable()

        assertThat(list).containsExactlyInAnyOrder(listOf("X", "X", "X"))
    }

    @Test
    fun `it writes a complex Object Array to List`() {

        val itemType = ComplexObject(properties = listOf(SchemaObject(name = "A", detail = Text(maxLength = 1))))

        val list = Array(itemType = itemType, maxItems = 2).toJsonable()

        assertThat(list).containsExactlyInAnyOrder(mapOf("A" to "X"), mapOf("A" to "X"))
    }

    @Test
    fun `it writes OneOf to something?`() {
        val thing = OneOf(list = listOf(
                Text(maxLength = 1)
        )).toJsonable()

        assertThat(thing).isEqualTo("X")
    }

    @Test
    fun `it converts a SchemaObject`() {
        val json = SchemaObject(name = "thing", detail = Text(maxLength = 5)).toJson()

        assertThat(json).isEqualTo("{\"thing\":\"XXXXX\"}")
    }

    private inline fun <T, reified U : Any> T.convert() = objectMapper.convertValue(this, U::class.java)

    private fun <T> T.writeAsString() = objectMapper.writeValueAsString(this)
}