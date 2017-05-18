package com.hcsc.de.claims.fileReaders

import com.hcsc.de.claims.jsonReduction.FieldObject

class FieldObjectCsvFileByteReader(
        private val fileReader: StringFileReader,
        private val csvStringConverter: CsvStringToFieldList
) : FieldObjectCsvFileReader {

    override fun read(filePath: String): FieldObject {

        val csvString = fileReader.read(filePath)

        val fieldList = csvStringConverter.convert(csvString)

        return flatten(fieldList)
    }

    fun flatten(nestedFieldList: List<List<String>>): FieldObject {

        return nestedFieldList.map { fieldList ->

            val fieldObjectName = fieldList.first()

            val remainingFields = fieldList.subList(1, fieldList.size)

            FieldObject(name = fieldObjectName).simpleFlatten(remainingFields)
        }.sum()
    }

    fun FieldObject.simpleFlatten(fieldList: List<String>): FieldObject {

        val fieldObjectName = fieldList.first()

        val remainingFields = fieldList.subList(1, fieldList.size)

        val newFieldObject = FieldObject(name = fieldObjectName)

        return copy(properties = this.properties.plus(if (remainingFields.isNotEmpty()) {
            newFieldObject.simpleFlatten(remainingFields)
        } else {
            newFieldObject
        }))
    }

    fun List<FieldObject>.sum(): FieldObject {
        return reduce(FieldObject::merge)
    }
}

fun FieldObject.merge(other: FieldObject): FieldObject {

    val sharedProperties = properties
            .filter { (propertyName) -> other.properties.find { it.name == propertyName } != null }
            .map { property -> property.merge(other.properties.find { it.name == property.name }!!) }

    val unsharedProperties = properties.filterNot { (propertyName) -> other.properties.find { it.name == propertyName } != null }

    val unsharedOtherProperties = other.properties.filterNot { (propertyName) -> properties.find { it.name == propertyName } != null }

    val finalProperties = listOf(unsharedProperties, sharedProperties, unsharedOtherProperties).flatten()

    return this.copy(properties = finalProperties)
}