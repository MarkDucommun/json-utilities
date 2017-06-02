package com.hcsc.de.claims.jsonParsingFour

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

sealed class LiteralElement : JsonStructure() {
    abstract val value: Char
}

data class LiteralValue(
        override val id: Long,
        override val value: Char
) : LiteralElement()

data class LiteralClose(
        override val id: Long,
        override val value: Char
) : LiteralElement()

data class StringStructureElement(override val id: Long) : MainStructure()

sealed class StringElement : JsonStructure()

data class StringOpen(
        override val id: Long
) : StringElement()

data class StringValue(
        override val id: Long,
        val value: Char
) : StringElement()

object StringEscape : StringElement() {
    override val id: Long = 0
}

data class StringClose(
        override val id: Long
) : StringElement()

data class ArrayStructureElement(override val id: Long) : MainStructure()

sealed class ArrayElement : JsonStructure()

data class ArrayOpen(override val id: Long) : ArrayElement()

data class ArrayClose(override val id: Long) : ArrayElement()

data class ArrayComma(override val id: Long) : ArrayElement()

data class ObjectStructureElement(override val id: Long): MainStructure()

sealed class ObjectElement : JsonStructure()

data class ObjectOpen(override val id: Long) : ObjectElement()

data class ObjectColon(override val id: Long) : ObjectElement()

data class ObjectComma(override val id: Long) : ObjectElement()

data class ObjectClose(override val id: Long) : ObjectElement()