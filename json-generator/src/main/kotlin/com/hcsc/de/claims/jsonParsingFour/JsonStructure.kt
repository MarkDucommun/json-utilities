package com.hcsc.de.claims.jsonParsingFour

import com.hcsc.de.claims.helpers.Failure
import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success

sealed class JsonStructureElement {
    abstract val id: Long
}

sealed class MainStructureElement : JsonStructureElement()

object EmptyStructureElement : MainStructureElement() {
    override val id: Long = 0
}

data class LiteralStructureElement(
        override val id: Long
) : MainStructureElement()

sealed class LiteralChildElement : JsonStructureElement() {
    abstract val value: Char
}

data class LiteralChildStructureElement(
        override val id: Long,
        override val value: Char
) : LiteralChildElement()

data class LiteralChildCloseElement(
        override val id: Long,
        override val value: Char
) : LiteralChildElement()

data class StringStructureElement(override val id: Long) : MainStructureElement()

sealed class StringChildElement : JsonStructureElement()

data class StringChildOpenElement(
        override val id: Long
) : StringChildElement()

data class StringChildStructureElement(
        override val id: Long,
        val value: Char
) : StringChildElement()

object StringEscape : StringChildElement() {
    override val id: Long = 0
}

data class StringChildCloseElement(
        override val id: Long
) : StringChildElement()

data class ArrayStructureElement(override val id: Long) : MainStructureElement()

sealed class ArrayChildElement : JsonStructureElement()

data class ArrayOpen(override val id: Long) : ArrayChildElement()

data class ArrayClose(override val id: Long) : ArrayChildElement()

data class ArrayComma(override val id: Long) : ArrayChildElement()