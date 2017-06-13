package io.ducommun.jsonParsing.structureAccumulators

import io.ducommun.jsonParsing.*

data class LiteralCloseOpenObjectAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure<*>>,
        override val previousElement: LiteralClose,
        override val previousClosable: OpenObjectStructure
) : CloseOpenObjectAccumulator<LiteralClose>()