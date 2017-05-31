package com.hcsc.de.claims.jsonParsingAgain

interface JsonElement

interface EscapeElement

interface CommaElement

interface ColonElement

interface ClosableElement : JsonElement {
    val id: Int
}

interface LiteralElement : ClosableElement {
    val value: Char
}

interface StringElement : ClosableElement

interface ArrayElement : ClosableElement

interface ObjectElement : ClosableElement

sealed class JsonStructureElement : JsonElement

sealed class MainStructureElement : JsonStructureElement()

data class LiteralStructureElement(
        override val id: Int,
        override val value: Char
) : MainStructureElement(), LiteralElement

data class StringStructureElement(override val id: Int) : MainStructureElement(), StringElement

sealed class StringChildStructureElement : JsonStructureElement()

data class StringLiteralStructureElement(
        override val id: Int,
        override val value: Char
) : StringChildStructureElement(), LiteralElement

object StringEscapeStructureElement : StringChildStructureElement(), EscapeElement

data class ArrayStructureElement(
        override val id: Int
) : MainStructureElement(), ArrayElement

sealed class ArrayChildStructureElement : JsonStructureElement()

//data class ArrayChildMainStructureElement<mainStructureElementType : MainStructureElement>(
//    val
//) : ArrayChildStructureElement()

object ArrayChildCommaStructureElement : ArrayChildStructureElement(), CommaElement

data class ObjectStructureElement(override val id: Int) : MainStructureElement(), ObjectElement

data class Root(
        val children: List<MainStructureElement>
) : JsonStructureElement()

sealed class JsonStructureAccumulator<out previousElementType : JsonElement> {
    abstract val closableStack: List<ClosableElement>
    abstract val structureQueue: List<JsonElement>
    abstract val previousElement: previousElementType
}

data class LiteralAccumulator(
        override val closableStack: List<ClosableElement>,
        override val structureQueue: List<JsonElement>,
        override val previousElement: LiteralStructureElement
) : JsonStructureAccumulator<LiteralStructureElement>()