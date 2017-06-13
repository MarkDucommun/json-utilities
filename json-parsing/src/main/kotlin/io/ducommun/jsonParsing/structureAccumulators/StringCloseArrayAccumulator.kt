package io.ducommun.jsonParsing.structureAccumulators

import io.ducommun.jsonParsing.*

data class StringCloseArrayAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure<*>>,
        override val previousElement: StringClose,
        override val previousClosable: ArrayStructureElement
) : CloseArrayAccumulator<StringClose>()