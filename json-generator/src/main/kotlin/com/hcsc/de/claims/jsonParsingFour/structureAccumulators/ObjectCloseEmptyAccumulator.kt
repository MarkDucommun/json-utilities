package com.hcsc.de.claims.jsonParsingFour.structureAccumulators

import com.hcsc.de.claims.jsonParsingFour.EmptyStructureElement
import com.hcsc.de.claims.jsonParsingFour.JsonStructure
import com.hcsc.de.claims.jsonParsingFour.MainStructure
import com.hcsc.de.claims.jsonParsingFour.ObjectClose

data class ObjectCloseEmptyAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure<*>>,
        override val previousElement: ObjectClose,
        override val previousClosable: EmptyStructureElement
) : EmptyAccumulator<ObjectClose>() {

    override val elementName: String = "object"
}