package io.ducommun.jsonParsing.structureAccumulators

import io.ducommun.jsonParsing.*

data class StringValueAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure<*>>,
        override val previousElement: StringValue,
        override val previousClosable: StringStructureElement
) : StringAccumulator<StringValue>()