package com.hcsc.de.claims.jsonParsingFour.accumulators

import com.hcsc.de.claims.jsonParsingFour.ArrayClose
import com.hcsc.de.claims.jsonParsingFour.EmptyStructureElement
import com.hcsc.de.claims.jsonParsingFour.JsonStructure
import com.hcsc.de.claims.jsonParsingFour.MainStructure

data class ArrayCloseEmptyAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val previousElement: ArrayClose,
        override val structureStack: List<MainStructure>,
        override val previousClosable: EmptyStructureElement
) : EmptyAccumulator<ArrayClose>() {

    override val elementName: String = "array"
}