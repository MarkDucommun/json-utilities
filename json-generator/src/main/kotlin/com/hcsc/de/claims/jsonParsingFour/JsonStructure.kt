package com.hcsc.de.claims.jsonParsingFour

interface Open<out structureType: MainStructure> : JsonStructure {
    val structureConstructor: (Long) -> structureType
}

interface Close : JsonStructure

interface WithValue : JsonStructure {
    val value: Char
}

interface JsonStructure {
    val id: Long
}

sealed class MainStructure : JsonStructure

object EmptyStructureElement : MainStructure() {
    override val id: Long = 0
}

data class LiteralStructureElement(
        override val id: Long
) : MainStructure()

sealed class LiteralElement : JsonStructure, WithValue

data class LiteralValue(
        override val id: Long,
        override val value: Char
) : LiteralElement(), Open<LiteralStructureElement> {

    override val structureConstructor: (Long) -> LiteralStructureElement
        get() = ::LiteralStructureElement
}

data class LiteralClose(
        override val id: Long,
        override val value: Char
) : LiteralElement(), Close

data class StringStructureElement(override val id: Long) : MainStructure()

sealed class StringElement : JsonStructure

data class StringOpen(
        override val id: Long
) : StringElement(), Open<StringStructureElement> {

    override val structureConstructor: (Long) -> StringStructureElement
        get() = ::StringStructureElement
}

data class StringValue(
        override val id: Long,
        override val value: Char
) : StringElement(), WithValue

object StringEscape : StringElement() {
    override val id: Long = 0
}

data class StringClose(
        override val id: Long
) : StringElement(), Close

data class ArrayStructureElement(override val id: Long) : MainStructure()

sealed class ArrayElement : JsonStructure

data class ArrayOpen(override val id: Long) : ArrayElement(), Open<ArrayStructureElement> {
    override val structureConstructor: (Long) -> ArrayStructureElement
        get() = ::ArrayStructureElement
}

data class ArrayComma(override val id: Long) : ArrayElement()

data class ArrayClose(override val id: Long) : ArrayElement(), Close

sealed class ObjectStructureElement : MainStructure()

data class OpenObjectStructure(override val id: Long) : ObjectStructureElement()

data class ObjectWithKeyStructure(override val id: Long) : ObjectStructureElement()

sealed class ObjectElement : JsonStructure

data class ObjectOpen(override val id: Long) : ObjectElement(), Open<OpenObjectStructure> {

    override val structureConstructor: (Long) -> OpenObjectStructure
        get() = ::OpenObjectStructure
}

data class ObjectColon(override val id: Long) : ObjectElement()

data class ObjectComma(override val id: Long) : ObjectElement()

data class ObjectClose(override val id: Long) : ObjectElement(), Close