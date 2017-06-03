package com.hcsc.de.claims.jsonParsingFour

interface Open

interface Close

interface WithValue {
    val value: Char
}

sealed class JsonStructure {
    abstract val id: Long
}

sealed class MainStructure : JsonStructure()

object EmptyStructureElement : MainStructure() {
    override val id: Long = 0
}

data class LiteralStructureElement(
        override val id: Long
) : MainStructure()

sealed class LiteralElement : JsonStructure(), WithValue

data class LiteralValue(
        override val id: Long,
        override val value: Char
) : LiteralElement()

data class LiteralClose(
        override val id: Long,
        override val value: Char
) : LiteralElement(), Close

data class StringStructureElement(override val id: Long) : MainStructure()

sealed class StringElement : JsonStructure()

data class StringOpen(
        override val id: Long
) : StringElement(), Open

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

sealed class ArrayElement : JsonStructure()

data class ArrayOpen(override val id: Long) : ArrayElement(), Open

data class ArrayComma(override val id: Long) : ArrayElement()

data class ArrayClose(override val id: Long) : ArrayElement(), Close

sealed class ObjectStructureElement : MainStructure()

data class OpenObjectStructure(override val id: Long) : ObjectStructureElement()

data class ObjectWithKeyStructure(override val id: Long) : ObjectStructureElement()

sealed class ObjectElement : JsonStructure()

data class ObjectOpen(override val id: Long) : ObjectElement(), Open

data class ObjectColon(override val id: Long) : ObjectElement()

data class ObjectComma(override val id: Long) : ObjectElement()

data class ObjectClose(override val id: Long) : ObjectElement(), Close