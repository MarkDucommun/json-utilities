package io.ducommun.jsonParsing.structureAccumulators

import io.ducommun.jsonParsing.*

data class LiteralCloseArrayAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure<*>>,
        override val previousElement: LiteralClose,
        override val previousClosable: ArrayStructureElement
) : CloseArrayAccumulator<LiteralClose>()