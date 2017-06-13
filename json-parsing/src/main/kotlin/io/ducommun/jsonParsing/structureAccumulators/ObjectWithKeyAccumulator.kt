package io.ducommun.jsonParsing.structureAccumulators

import io.ducommun.jsonParsing.*
import com.hcsc.de.claims.results.Result

data class ObjectWithKeyAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure<*>>,
        override val previousElement: StringClose,
        override val previousClosable: ObjectWithKeyStructure
) : BaseAccumulator<StringClose, ObjectWithKeyStructure, ObjectChildElement<*>>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {
        return when (char) {
            ' ', '\n', '\r', '\t' -> skip
            ':' -> addElement(::ObjectColon)
            else -> fail("colon must follow an object key")
        }
    }
}