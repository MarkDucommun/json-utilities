package com.hcsc.de.claims.jsonParsingFour.accumulators

import com.hcsc.de.claims.jsonParsingFour.EmptyStructureElement
import com.hcsc.de.claims.jsonParsingFour.JsonStructure
import com.hcsc.de.claims.jsonParsingFour.LiteralClose
import com.hcsc.de.claims.jsonParsingFour.MainStructure

data class LiteralCloseEmptyAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure>,
        override val previousElement: LiteralClose,
        override val previousClosable: EmptyStructureElement
) : EmptyAccumulator<LiteralClose>() {

    override val elementName: String = "literal"
}