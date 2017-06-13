package io.ducommun.jsonParsing.structureAccumulators

import io.ducommun.jsonParsing.ArrayClose
import io.ducommun.jsonParsing.ArrayStructureElement
import io.ducommun.jsonParsing.JsonStructure
import io.ducommun.jsonParsing.MainStructure

data class ArrayCloseArrayAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure<*>>,
        override val previousElement: ArrayClose,
        override val previousClosable: ArrayStructureElement
) : CloseArrayAccumulator<ArrayClose>()