package io.ducommun.jsonParsing.structureAccumulators

import io.ducommun.jsonParsing.EmptyStructureElement
import io.ducommun.jsonParsing.JsonStructure
import io.ducommun.jsonParsing.MainStructure
import io.ducommun.jsonParsing.ObjectClose

data class ObjectCloseEmptyAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure<*>>,
        override val previousElement: ObjectClose,
        override val previousClosable: EmptyStructureElement
) : EmptyAccumulator<ObjectClose>() {

    override val elementName: String = "object"
}