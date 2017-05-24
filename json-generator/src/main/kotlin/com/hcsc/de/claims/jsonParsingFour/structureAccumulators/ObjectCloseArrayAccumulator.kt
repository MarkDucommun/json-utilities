package com.hcsc.de.claims.jsonParsingFour.structureAccumulators

import com.hcsc.de.claims.jsonParsingFour.*

data class ObjectCloseArrayAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure<*>>,
        override val previousElement: ObjectClose,
        override val previousClosable: ArrayStructureElement
) : CloseArrayAccumulator<ObjectClose>()