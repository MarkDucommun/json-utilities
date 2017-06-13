package io.ducommun.jsonParsing

interface Open<out structureType : io.ducommun.jsonParsing.MainStructure<childType>, childType : io.ducommun.jsonParsing.JsonStructure> : io.ducommun.jsonParsing.JsonStructure {

    val structureConstructor: (Long, List<childType>) -> structureType
}

interface Close : io.ducommun.jsonParsing.JsonStructure

interface Comma : io.ducommun.jsonParsing.JsonStructure

interface WithValue : io.ducommun.jsonParsing.JsonStructure {
    val value: Char
}

interface JsonStructure {
    val id: Long
}

sealed class MainStructure<out childType : io.ducommun.jsonParsing.JsonStructure> : io.ducommun.jsonParsing.JsonStructure {

    abstract val children: List<childType>
}

object EmptyStructureElement : io.ducommun.jsonParsing.MainStructure<EmptyStructureElement>() {

    override val id: Long = 0
    override val children: List<io.ducommun.jsonParsing.EmptyStructureElement> = emptyList()
}

data class LiteralStructureElement(
        override val id: Long,
        override val children: List<io.ducommun.jsonParsing.LiteralElement> = emptyList()
) : io.ducommun.jsonParsing.MainStructure<LiteralElement>()

sealed class LiteralElement : io.ducommun.jsonParsing.JsonStructure, io.ducommun.jsonParsing.WithValue

data class LiteralValue(
        override val id: Long,
        override val value: Char
) : io.ducommun.jsonParsing.LiteralElement(), io.ducommun.jsonParsing.Open<LiteralStructureElement, LiteralElement>, io.ducommun.jsonParsing.Close {

    override val structureConstructor: (Long, List<io.ducommun.jsonParsing.LiteralElement>) -> io.ducommun.jsonParsing.LiteralStructureElement = ::LiteralStructureElement
}

data class LiteralClose(
        override val id: Long,
        override val value: Char
) : io.ducommun.jsonParsing.LiteralElement(), io.ducommun.jsonParsing.Close

data class StringStructureElement(
        override val id: Long,
        override val children: List<io.ducommun.jsonParsing.StringValue>
) : io.ducommun.jsonParsing.MainStructure<StringValue>()

sealed class StringElement : io.ducommun.jsonParsing.JsonStructure

data class StringOpen(
        override val id: Long
) : io.ducommun.jsonParsing.StringElement(), io.ducommun.jsonParsing.Open<StringStructureElement, StringValue> {

    override val structureConstructor: (Long, List<io.ducommun.jsonParsing.StringValue>) -> io.ducommun.jsonParsing.StringStructureElement
        get() = ::StringStructureElement
}

data class StringValue(
        override val id: Long,
        override val value: Char
) : io.ducommun.jsonParsing.StringElement(), io.ducommun.jsonParsing.WithValue

object StringEscape : io.ducommun.jsonParsing.StringElement() {
    override val id: Long = 0
}

data class StringClose(
        override val id: Long
) : io.ducommun.jsonParsing.StringElement(), io.ducommun.jsonParsing.Close

data class ArrayStructureElement(
        override val id: Long,
        override val children: List<io.ducommun.jsonParsing.MainStructure<*>>
) : io.ducommun.jsonParsing.MainStructure<MainStructure<*>>()

sealed class ArrayElement : io.ducommun.jsonParsing.JsonStructure

data class ArrayOpen(override val id: Long) : io.ducommun.jsonParsing.ArrayElement(), io.ducommun.jsonParsing.Open<ArrayStructureElement, MainStructure<*>> {
    override val structureConstructor: (Long, List<io.ducommun.jsonParsing.MainStructure<*>>) -> io.ducommun.jsonParsing.ArrayStructureElement
        get() = ::ArrayStructureElement
}

data class ArrayComma(override val id: Long) : io.ducommun.jsonParsing.ArrayElement(), io.ducommun.jsonParsing.Comma

data class ArrayClose(override val id: Long) : io.ducommun.jsonParsing.ArrayElement(), io.ducommun.jsonParsing.Close

sealed class ObjectStructureElement : io.ducommun.jsonParsing.MainStructure<ObjectChildElement<*>>()

data class ObjectChildElement<out childType : io.ducommun.jsonParsing.MainStructure<*>>(
        override val id: Long,
        val key: io.ducommun.jsonParsing.StringStructureElement,
        val value: childType
) : io.ducommun.jsonParsing.JsonStructure

data class OpenObjectStructure(
        override val id: Long,
        override val children: List<io.ducommun.jsonParsing.ObjectChildElement<*>>
) : io.ducommun.jsonParsing.ObjectStructureElement()

data class ObjectWithKeyStructure(
        override val id: Long,
        override val children: List<io.ducommun.jsonParsing.ObjectChildElement<*>>
) : io.ducommun.jsonParsing.ObjectStructureElement()

sealed class ObjectElement : io.ducommun.jsonParsing.JsonStructure

data class ObjectOpen(override val id: Long) : io.ducommun.jsonParsing.ObjectElement(), io.ducommun.jsonParsing.Open<OpenObjectStructure, ObjectChildElement<*>> {

    override val structureConstructor: (Long, List<io.ducommun.jsonParsing.ObjectChildElement<*>>) -> io.ducommun.jsonParsing.OpenObjectStructure
        get() = ::OpenObjectStructure
}

data class ObjectColon(override val id: Long) : io.ducommun.jsonParsing.ObjectElement()

data class ObjectComma(override val id: Long) : io.ducommun.jsonParsing.ObjectElement(), io.ducommun.jsonParsing.Comma

data class ObjectClose(override val id: Long) : io.ducommun.jsonParsing.ObjectElement(), io.ducommun.jsonParsing.Close