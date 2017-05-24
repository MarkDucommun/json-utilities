package com.hcsc.de.claims.jsonParsing

sealed class JsonStructureElement

sealed class NotWhitespaceElement : JsonStructureElement()

sealed class SimpleElement : NotWhitespaceElement()

object Literal : SimpleElement()

object CommaElement : SimpleElement()

object ColonElement : SimpleElement()

object Escape : SimpleElement()

object Whitespace : JsonStructureElement()

data class ElementStart(val element: ClosableElement) : NotWhitespaceElement()

data class ElementEnd(val element: ClosableElement) : NotWhitespaceElement()

data class StringElement(override val id: Long) : ClosableElement()

data class ObjectElement(override val id: Long) : ClosableElement()

data class ObjectKeyElement(override val id: Long) : ClosableElement()

data class ObjectValueElement(val element: ClosableElement) : ClosableElement() {
    override val id: Long = element.id
}

data class ArrayElement(override val id: Long) : ClosableElement()

data class ArrayChildElement(val element: ClosableElement) : ClosableElement() {
    override val id: Long = element.id
}

sealed class ClosableElement : NotWhitespaceElement() {
    abstract val id: Long
}