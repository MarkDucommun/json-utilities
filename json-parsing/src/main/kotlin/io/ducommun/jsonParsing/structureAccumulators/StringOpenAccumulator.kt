package io.ducommun.jsonParsing.structureAccumulators

import io.ducommun.jsonParsing.JsonStructure
import io.ducommun.jsonParsing.MainStructure
import io.ducommun.jsonParsing.StringOpen
import io.ducommun.jsonParsing.StringStructureElement

data class StringOpenAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure<*>>,
        override val previousElement: StringOpen,
        override val previousClosable: StringStructureElement
) : StringAccumulator<StringOpen>()