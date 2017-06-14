package io.ducommun.jsonParsing.structureAccumulators

import io.ducommun.jsonParsing.JsonStructure
import io.ducommun.jsonParsing.MainStructure
import io.ducommun.jsonParsing.StringEscape
import io.ducommun.jsonParsing.StringStructureElement
import com.hcsc.de.claims.results.Result

data class StringEscapeAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure<*>>,
        override val previousElement: StringEscape,
        override val previousClosable: StringStructureElement
) : StringAccumulator<StringEscape>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {

        return when (char) {
            '"', '\\', '/' -> addStringValue(char)
            'b' -> addStringValue('\b')
            'n' -> addStringValue('\n')
            'f' -> addStringValue(12.toChar())
            'r' -> addStringValue('\r')
            't' -> addStringValue('\t')
            else -> fail("only quotes and slashes and control characters may follow escape characters")
        }
    }
}