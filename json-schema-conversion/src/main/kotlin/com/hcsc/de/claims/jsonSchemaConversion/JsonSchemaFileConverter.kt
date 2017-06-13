package com.hcsc.de.claims.jsonSchemaConversion

import com.hcsc.de.claims.fileReaders.JsonFileReader
import com.hcsc.de.claims.results.Success


class JsonSchemaFileConverter(
        private val reader: JsonFileReader,
        private val listConverter: ListConverter
) {

    fun convert(filePath: String): SchemaObject<*> {

        val baseNode = reader.read(filePath = filePath)

        val convertedDefinitions = listConverter.convert(node = (baseNode as Success).content.definitions)

        val replacedConvertedDefinitions = convertedDefinitions.map {
            it.recursivelyReplaceReferences(definitions = convertedDefinitions)
        }

        val initialSchema = listConverter.convert(node = baseNode.content.properties).first()

        return initialSchema.recursivelyReplaceReferences(definitions = replacedConvertedDefinitions)
    }

    // TODO extract this!
    private fun SchemaObject<*>.recursivelyReplaceReferences(
            definitions: List<SchemaObject<*>>
    ): SchemaObject<*> {

        return copy(detail = when (this.detail) {
            is ComplexObject -> this.detail.transformProperties { it.recursivelyReplaceReferences(definitions) }
            is ArrayDetail -> this.detail.transformItemType { itemType ->
                when (itemType) {
                    is Reference -> definitions.findSchemaDetail(itemType.type)
                    is ComplexObject -> itemType.transformProperties { it.recursivelyReplaceReferences(definitions) }
                    else -> itemType
                }
            }
            is OneOf -> this.detail.transformList { item ->
                when (item) {
                    is Reference -> definitions.findSchemaDetail(item.type)
                    else -> this.detail
                }
            }
            is Reference -> definitions.findSchemaDetail(this.detail.type)
            else -> this.detail
        })
    }

    private fun ArrayDetail.transformItemType(transform: (SchemaDetail) -> SchemaDetail): SchemaDetail {
        return copy(itemType = transform(itemType))
    }

    private fun ComplexObject.transformProperties(transform: (SchemaObject<*>) -> SchemaObject<*>): ComplexObject {
        return ComplexObject(properties = this.properties.map(transform))
    }

    private fun OneOf.transformList(transform: (SchemaDetail) -> SchemaDetail): OneOf {
        return OneOf(list = this.list.map(transform))
    }

    private fun List<SchemaObject<*>>.findSchemaDetail(string: String): SchemaDetail =
            this.find { string.contains(it.name) }?.detail ?: throw Exception("Reference matching $string was not found")
}