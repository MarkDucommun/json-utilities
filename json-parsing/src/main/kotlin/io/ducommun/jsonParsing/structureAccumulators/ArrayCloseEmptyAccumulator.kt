package io.ducommun.jsonParsing.structureAccumulators

import io.ducommun.jsonParsing.*

data class ArrayCloseEmptyAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure<*>>,
        override val previousElement: ArrayClose,
        override val previousClosable: EmptyStructureElement
) : EmptyAccumulator<ArrayClose>() {

    override val elementName: String = "array"
}