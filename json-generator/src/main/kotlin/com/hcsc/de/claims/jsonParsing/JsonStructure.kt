package com.hcsc.de.claims.jsonParsing

sealed class JsonStructureElement

sealed class NotWhitespaceElement : JsonStructureElement()

sealed class SimpleElement : NotWhitespaceElement()

data class Literal(val char: Char) : SimpleElement()

object CommaElement : SimpleElement()

object ColonElement : SimpleElement()

object Escape : SimpleElement()

object Whitespace : JsonStructureElement()

sealed class ElementWrapper : NotWhitespaceElement() { abstract val element: ClosableElement }

data class ElementStart(override val element: ClosableElement) : ElementWrapper()

data class ElementEnd(override val element: ClosableElement) : ElementWrapper()

sealed class ClosableValueElement : ClosableElement()

sealed class CloseableWrapper : ClosableElement() { abstract val element: ClosableElement }

data class StringElement(override val id: Long) : ClosableValueElement()

data class ObjectElement(override val id: Long) : ClosableValueElement()

data class ObjectKeyElement(override val id: Long) : ClosableElement()

data class ObjectValueElement(override val element: ClosableValueElement) : CloseableWrapper() {
    override val id: Long = element.id
}

data class ArrayElement(override val id: Long) : ClosableValueElement()

data class ArrayChildElement(override val element: ClosableValueElement) : CloseableWrapper() {
    override val id: Long = element.id
}

sealed class ClosableElement : NotWhitespaceElement() {
    abstract val id: Long
}