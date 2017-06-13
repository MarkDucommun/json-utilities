package io.ducommun.jsonParsing.structureAccumulators

import io.ducommun.jsonParsing.JsonStructure
import io.ducommun.jsonParsing.MainStructure
import io.ducommun.jsonParsing.OpenObjectStructure
import io.ducommun.jsonParsing.StringClose

data class StringCloseOpenObjectAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure<*>>,
        override val previousElement: StringClose,
        override val previousClosable: OpenObjectStructure
) : CloseOpenObjectAccumulator<StringClose>()