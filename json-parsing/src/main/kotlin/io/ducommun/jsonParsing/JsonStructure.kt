package io.ducommun.jsonParsing

interface Open<out structureType : MainStructure<childType>, childType : JsonStructure> : JsonStructure {

    val structureConstructor: (Long, List<childType>) -> structureType
}

interface Close : JsonStructure

interface Comma : JsonStructure

interface WithValue : JsonStructure {
    val value: Char
}

interface JsonStructure {
    val id: Long
}

sealed class MainStructure<out childType : JsonStructure> : JsonStructure {

    abstract val children: List<childType>
}

object EmptyStructureElement : MainStructure<EmptyStructureElement>() {

    override val id: Long = 0
    override val children: List<EmptyStructureElement> = emptyList()
}

data class LiteralStructureElement(
        override val id: Long,
        override val children: List<LiteralElement> = emptyList()
) : MainStructure<LiteralElement>()

sealed class LiteralElement : JsonStructure, WithValue

data class LiteralValue(
        override val id: Long,
        override val value: Char
) : LiteralElement(), Open<LiteralStructureElement, LiteralElement>, Close {

    override val structureConstructor: (Long, List<LiteralElement>) -> LiteralStructureElement = ::LiteralStructureElement
}

data class LiteralClose(
        override val id: Long,
        override val value: Char
) : LiteralElement(), Close, Open<LiteralStructureElement, LiteralElement> {

    override val structureConstructor: (Long, List<LiteralElement>) -> LiteralStructureElement = ::LiteralStructureElement
}

data class StringStructureElement(
        override val id: Long,
        override val children: List<StringValue>
) : MainStructure<StringValue>()

sealed class StringElement : JsonStructure

data class StringOpen(
        override val id: Long
) : StringElement(), Open<StringStructureElement, StringValue> {

    override val structureConstructor: (Long, List<StringValue>) -> StringStructureElement
        get() = ::StringStructureElement
}

data class StringValue(
        override val id: Long,
        override val value: Char
) : StringElement(), WithValue

object StringEscape : StringElement() {
    override val id: Long = 0
}

data class StringUnicode(
        override val id: Long,
        val unicodeValue: String
) : StringElement() {
    val isFinal: Boolean = unicodeValue.length == 3
}

data class StringClose(
        override val id: Long
) : StringElement(), Close

data class ArrayStructureElement(
        override val id: Long,
        override val children: List<MainStructure<*>>
) : MainStructure<MainStructure<*>>()

sealed class ArrayElement : JsonStructure

data class ArrayOpen(override val id: Long) : ArrayElement(), Open<ArrayStructureElement, MainStructure<*>> {
    override val structureConstructor: (Long, List<MainStructure<*>>) -> ArrayStructureElement
        get() = ::ArrayStructureElement
}

data class ArrayComma(override val id: Long) : ArrayElement(), Comma

data class ArrayClose(override val id: Long) : ArrayElement(), Close

sealed class ObjectStructureElement : MainStructure<ObjectChildElement<*>>()

data class ObjectChildElement<out childType : MainStructure<*>>(
        override val id: Long,
        val key: StringStructureElement,
        val value: childType
) : JsonStructure

data class OpenObjectStructure(
        override val id: Long,
        override val children: List<ObjectChildElement<*>>
) : io.ducommun.jsonParsing.ObjectStructureElement()

data class ObjectWithKeyStructure(
        override val id: Long,
        override val children: List<ObjectChildElement<*>>
) : io.ducommun.jsonParsing.ObjectStructureElement()

sealed class ObjectElement : JsonStructure

data class ObjectOpen(override val id: Long) : io.ducommun.jsonParsing.ObjectElement(), Open<OpenObjectStructure, ObjectChildElement<*>> {

    override val structureConstructor: (Long, List<ObjectChildElement<*>>) -> OpenObjectStructure
        get() = ::OpenObjectStructure
}

data class ObjectColon(override val id: Long) : io.ducommun.jsonParsing.ObjectElement()

data class ObjectComma(override val id: Long) : io.ducommun.jsonParsing.ObjectElement(), Comma

data class ObjectClose(override val id: Long) : io.ducommun.jsonParsing.ObjectElement(), Close