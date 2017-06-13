package io.ducommun.jsonParsing.structureAccumulators

import io.ducommun.jsonParsing.EmptyStructureElement
import io.ducommun.jsonParsing.JsonStructure
import io.ducommun.jsonParsing.MainStructure
import io.ducommun.jsonParsing.StringClose

data class StringCloseEmptyAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure<*>>,
        override val previousElement: StringClose,
        override val previousClosable: EmptyStructureElement
) : EmptyAccumulator<StringClose>() {

    override val elementName: String = "string"
}