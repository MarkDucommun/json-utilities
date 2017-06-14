package io.ducommun.jsonParsing.structureAccumulators

import com.hcsc.de.claims.results.Failure
import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.flatMap
import io.ducommun.jsonParsing.*

data class LiteralValueAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure<*>>,
        override val previousElement: LiteralValue,
        override val previousClosable: LiteralStructureElement
) : BaseAccumulator<LiteralValue, LiteralStructureElement, LiteralElement>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {

        val newStructureStack = structureStack.dropLast(1)

        val newPreviousStructure = newStructureStack.lastOrNull() ?: EmptyStructureElement

        return when (char) {
            ' ', '\n', '\r', '\t' -> when (enclosingStructure) {
                is EmptyStructureElement -> closeLiteral()
                is ArrayStructureElement -> closeLiteral()
                is ObjectWithKeyStructure -> closeLiteral()
                else -> TODO("THIS REALLY SHOULD NEVER HAPPEN")
            }
            ',' -> when (newPreviousStructure) {
                is ArrayStructureElement -> {

                    val arrayComma = ArrayComma(id = newPreviousStructure.id)

                    val literalClose = LiteralClose(previousElement.id, previousElement.value)

                    replaceLastElementAndAddNewElementAndCloseStructure(literalClose, arrayComma)
                }
                is ObjectWithKeyStructure -> {

                    val objectComma = ObjectComma(id = newPreviousStructure.id)

                    val literalClose = LiteralClose(previousElement.id, previousElement.value)

                    replaceLastElementAndAddNewElementAndCloseStructure(literalClose, objectComma)
                }
                is OpenObjectStructure -> TODO("THIS SHOULD NEVER HAPPEN")
                is EmptyStructureElement -> TODO("THIS SHOULD NEVER HAPPEN")
                is LiteralStructureElement -> TODO("THIS SHOULD NEVER HAPPEN")
                is StringStructureElement -> TODO("THIS SHOULD NEVER HAPPEN")
            }
            ']' -> when (newPreviousStructure) {
                is ArrayStructureElement -> when (newStructureStack.dropLast(1).lastOrNull()) {
                    is EmptyStructureElement -> closeLiteralAndEnclosingArray()
                    is ArrayStructureElement -> closeLiteralAndEnclosingArray()
                    is ObjectWithKeyStructure -> closeLiteralAndEnclosingArray()
                    is LiteralStructureElement -> TODO("THIS SHOULD NEVER HAPPEN")
                    is StringStructureElement -> TODO("THIS SHOULD NEVER HAPPEN")
                    is OpenObjectStructure -> TODO("THIS SHOULD NEVER HAPPEN")
                    null -> TODO("THIS SHOULD NEVER HAPPEN")
                }
                is EmptyStructureElement -> TODO("THIS SHOULD NEVER HAPPEN")
                is LiteralStructureElement -> TODO("THIS SHOULD NEVER HAPPEN")
                is StringStructureElement -> TODO("THIS SHOULD NEVER HAPPEN")
                is OpenObjectStructure -> TODO("THIS SHOULD NEVER HAPPEN")
                is ObjectWithKeyStructure -> TODO("THIS SHOULD NEVER HAPPEN")
            }
            '}' -> when (newPreviousStructure) {
                is ObjectWithKeyStructure -> when (newStructureStack.dropLast(1).lastOrNull()) {
                    is EmptyStructureElement -> closeLiteralAndEnclosingObject()
                    is ArrayStructureElement -> closeLiteralAndEnclosingObject()
                    is ObjectWithKeyStructure -> closeLiteralAndEnclosingObject()
                    is StringStructureElement -> TODO("THIS SHOULD NEVER HAPPEN")
                    is LiteralStructureElement -> TODO("THIS SHOULD NEVER HAPPEN")
                    is OpenObjectStructure -> TODO("THIS SHOULD NEVER HAPPEN")
                    null -> TODO("THIS SHOULD NEVER HAPPEN")
                }
                is EmptyStructureElement -> TODO("THIS SHOULD NEVER HAPPEN")
                is LiteralStructureElement -> TODO("THIS SHOULD NEVER HAPPEN")
                is StringStructureElement -> TODO("THIS SHOULD NEVER HAPPEN")
                is ArrayStructureElement -> TODO("THIS SHOULD NEVER HAPPEN")
                is OpenObjectStructure -> TODO("THIS SHOULD NEVER HAPPEN")
            }
            else -> addValue(::LiteralValue, char)
        }
    }

    fun closeLiteralAndEnclosingArray(): Result<String, Accumulator<*, *>> =
            closeLiteralAndEnclosingStructure(::ArrayClose)

    fun closeLiteralAndEnclosingObject(): Result<String, Accumulator<*, *>> =
        closeLiteralAndEnclosingStructure(::ObjectClose)

    inline fun <reified elementType: Close> closeLiteralAndEnclosingStructure(
            crossinline elementConstructor: (Long) -> elementType
    ): Result<String, Accumulator<*, *>> =
            closeLiteral().flatMap { (it as BaseAccumulator<*, *, *>).closeStructure(elementConstructor) }

    fun closeLiteral(): Result<String, Accumulator<*, *>> =
            replaceLastElementAndCloseStructure(LiteralClose(
                    id = previousElement.id,
                    value = previousElement.value
            ))
}