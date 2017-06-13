package io.ducommun.jsonParsing.structureAccumulators

import io.ducommun.jsonParsing.*

data class ObjectCloseArrayAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure<*>>,
        override val previousElement: ObjectClose,
        override val previousClosable: ArrayStructureElement
) : CloseArrayAccumulator<ObjectClose>()