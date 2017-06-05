package com.hcsc.de.claims.jsonParsingFour.accumulators

import com.hcsc.de.claims.jsonParsingFour.JsonStructure
import com.hcsc.de.claims.jsonParsingFour.MainStructure
import com.hcsc.de.claims.jsonParsingFour.StringOpen
import com.hcsc.de.claims.jsonParsingFour.StringStructureElement

data class StringOpenAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure>,
        override val previousElement: StringOpen,
        override val previousClosable: StringStructureElement
) : StringAccumulator<StringOpen>()