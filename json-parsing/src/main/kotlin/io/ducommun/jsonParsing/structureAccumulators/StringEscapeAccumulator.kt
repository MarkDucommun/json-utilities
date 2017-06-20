package io.ducommun.jsonParsing.structureAccumulators

import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.Success
import io.ducommun.jsonParsing.*

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
            'u' -> {
                return Success(StringUnicodeAccumulator(
                        idCounter = idCounter,
                        structure = structure,
                        structureStack = structureStack,
                        previousElement = StringUnicode(id = previousClosable.id, unicodeValue = ""),
                        previousClosable = previousClosable
                ))
            }
            else -> fail("only quotes and slashes and control characters may follow escape characters")
        }
    }
}