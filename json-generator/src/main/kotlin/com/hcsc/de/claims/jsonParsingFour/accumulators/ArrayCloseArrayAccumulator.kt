package com.hcsc.de.claims.jsonParsingFour.accumulators

import com.hcsc.de.claims.jsonParsingFour.ArrayClose
import com.hcsc.de.claims.jsonParsingFour.ArrayStructureElement
import com.hcsc.de.claims.jsonParsingFour.JsonStructure
import com.hcsc.de.claims.jsonParsingFour.MainStructure

data class ArrayCloseArrayAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure>,
        override val previousElement: ArrayClose,
        override val previousClosable: ArrayStructureElement
) : CloseArrayAccumulator<ArrayClose>()