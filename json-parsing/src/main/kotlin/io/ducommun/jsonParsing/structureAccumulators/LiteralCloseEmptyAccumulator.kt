package io.ducommun.jsonParsing.structureAccumulators

import io.ducommun.jsonParsing.EmptyStructureElement
import io.ducommun.jsonParsing.JsonStructure
import io.ducommun.jsonParsing.LiteralClose
import io.ducommun.jsonParsing.MainStructure

data class LiteralCloseEmptyAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure<*>>,
        override val previousElement: LiteralClose,
        override val previousClosable: EmptyStructureElement
) : EmptyAccumulator<LiteralClose>() {

    override val elementName: String = "literal"
}