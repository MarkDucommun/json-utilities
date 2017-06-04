package com.hcsc.de.claims.jsonParsingFour.accumulators

import com.hcsc.de.claims.jsonParsingFour.JsonStructure
import com.hcsc.de.claims.jsonParsingFour.LiteralClose

data class LiteralCloseEmptyAccumulator(
        override val idCounter: Long,
        override val previousElement: LiteralClose,
        override val structure: List<JsonStructure>
) : EmptyAccumulator<LiteralClose>() {

    override val elementName: String = "literal"
}