package com.hcsc.de.claims.jsonParsingFour.structureAccumulators

import com.hcsc.de.claims.jsonParsingFour.*

data class LiteralCloseArrayAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure<*>>,
        override val previousElement: LiteralClose,
        override val previousClosable: ArrayStructureElement
) : CloseArrayAccumulator<LiteralClose>()