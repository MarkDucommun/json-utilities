package io.ducommun.jsonParsing.structureAccumulators

import com.hcsc.de.claims.results.Failure
import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.Success
import io.ducommun.jsonParsing.*

data class StringUnicodeAccumulator(
        override val idCounter: Long,
        override val structure: List<JsonStructure>,
        override val structureStack: List<MainStructure<*>>,
        override val previousElement: StringUnicode,
        override val previousClosable: StringStructureElement
) : StringAccumulator<StringUnicode>() {

    override fun processChar(char: Char): Result<String, Accumulator<*, *>> {

        return when (char) {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F',
            'a', 'b', 'c', 'd', 'e', 'f' -> {
                if (previousElement.isFinal) {
                    (previousElement.unicodeValue + char)
                            .toIntOrNull(16)
                            ?.toChar()
                            ?.let { unicodeChar -> addStringValue(unicodeChar) }
                            ?: Failure("")
                } else {
                    // TODO change to as success
                    Success<String, Accumulator<*, *>>(
                            copy(previousElement = previousElement.copy(
                                    unicodeValue = previousElement.unicodeValue + char
                            )))
                }
            }
            else -> fail("TODO!!!")
        }
    }
}