package com.hcsc.de.claims.jsonParsingFour.accumulators

import com.hcsc.de.claims.jsonParsingFour.EmptyStructureElement
import com.hcsc.de.claims.jsonParsingFour.JsonStructure
import com.hcsc.de.claims.jsonParsingFour.MainStructure
import com.hcsc.de.claims.jsonParsingFour.StringClose

data class StringCloseEmptyAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure>,
        override val previousElement: StringClose,
        override val previousClosable: EmptyStructureElement
) : EmptyAccumulator<StringClose>() {

    override val elementName: String = "string"
}