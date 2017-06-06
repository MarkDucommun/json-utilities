package com.hcsc.de.claims.jsonParsingFour.accumulators

import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success
import com.hcsc.de.claims.jsonParsingFour.*

data class LiteralCloseOpenObjectAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure>,
        override val previousElement: LiteralClose,
        override val previousClosable: OpenObjectStructure
) : CloseOpenObjectAccumulator<LiteralClose>()