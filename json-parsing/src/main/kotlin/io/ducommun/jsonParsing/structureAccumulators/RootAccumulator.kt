package io.ducommun.jsonParsing.structureAccumulators

import com.hcsc.de.claims.results.Result
import io.ducommun.jsonParsing.EmptyStructureElement
import io.ducommun.jsonParsing.JsonStructure
import io.ducommun.jsonParsing.MainStructure

object RootAccumulator : BaseAccumulator<EmptyStructureElement, EmptyStructureElement, EmptyStructureElement>() {

    override val idCounter: Long = 1

    override val structure: List<JsonStructure> = emptyList()

    override val previousElement: EmptyStructureElement = EmptyStructureElement

    override val structureStack: List<MainStructure<*>> = listOf(EmptyStructureElement)

    override val previousClosable: EmptyStructureElement = EmptyStructureElement

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {
        return when (char) {
            ' ', '\n', '\r', '\t' -> skip
            '"' -> openString()
            '[' -> openArray()
            '{' -> openObject()
            else -> openLiteral(char)
        }
    }
}