package com.hcsc.de.claims.jsonParsingFour.accumulators

import com.hcsc.de.claims.jsonParsingFour.ArrayClose
import com.hcsc.de.claims.jsonParsingFour.JsonStructure

data class ArrayCloseEmptyAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val previousElement: ArrayClose
) : EmptyAccumulator<ArrayClose>() {

    override val elementName: String = "array"
}