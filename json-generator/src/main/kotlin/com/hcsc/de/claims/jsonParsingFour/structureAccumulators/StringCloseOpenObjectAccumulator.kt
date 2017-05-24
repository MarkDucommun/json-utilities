package com.hcsc.de.claims.jsonParsingFour.structureAccumulators

import com.hcsc.de.claims.jsonParsingFour.JsonStructure
import com.hcsc.de.claims.jsonParsingFour.MainStructure
import com.hcsc.de.claims.jsonParsingFour.OpenObjectStructure
import com.hcsc.de.claims.jsonParsingFour.StringClose

data class StringCloseOpenObjectAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure<*>>,
        override val previousElement: StringClose,
        override val previousClosable: OpenObjectStructure
) : CloseOpenObjectAccumulator<StringClose>()