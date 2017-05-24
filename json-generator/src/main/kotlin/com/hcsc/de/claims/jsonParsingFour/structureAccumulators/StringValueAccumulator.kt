package com.hcsc.de.claims.jsonParsingFour.structureAccumulators

import com.hcsc.de.claims.jsonParsingFour.*

data class StringValueAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure<*>>,
        override val previousElement: StringValue,
        override val previousClosable: StringStructureElement
) : StringAccumulator<StringValue>()