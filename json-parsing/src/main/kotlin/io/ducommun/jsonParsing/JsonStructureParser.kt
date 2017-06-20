package io.ducommun.jsonParsing

import com.hcsc.de.claims.results.Failure
import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.Success
import com.hcsc.de.claims.results.flatMap
import io.ducommun.jsonParsing.structureAccumulators.Accumulator
import io.ducommun.jsonParsing.structureAccumulators.EmptyAccumulator
import io.ducommun.jsonParsing.structureAccumulators.LiteralValueAccumulator
import io.ducommun.jsonParsing.structureAccumulators.RootAccumulator

class JsonStructureParser {

    fun parse(string: String): Result<String, List<JsonStructure>> =
            string
                    .toCharArray()
                    .parseCharsToStructure()
                    .flatMap { it.ensureAllStructuresAreClosed() }
                    .flatMap { it.getStructureList() }

    fun CharArray.parseCharsToStructure(): Result<String, Accumulator<*, *>> =
            fold(rootAccumulatorResult()) { result, char -> result.flatMap { it.processChar(char) } }

    fun Accumulator<*, *>.ensureAllStructuresAreClosed(): Result<String, Accumulator<*, *>> =
            when (previousClosable) {
                is LiteralStructureElement -> ensureStructureStackLessThanOrEqualTo(2)
                else -> ensureStructureStackLessThanOrEqualTo(1)
            }

    fun Accumulator<*, *>.getStructureList(): Result<String, List<JsonStructure>> {

        return when (this) {
            is EmptyAccumulator, is LiteralValueAccumulator -> Success(structure)
            else -> Failure("Invalid JSON - must close all open elements")
        }
    }

    fun Accumulator<*, *>.ensureStructureStackLessThanOrEqualTo(size: Int): Result<String, Accumulator<*, *>> =

            if (structureStack.size > size) {
                Failure("Invalid JSON - must close all open elements")
            } else {
                Success<String, Accumulator<*, *>>(this)
            }

    fun rootAccumulatorResult(): Result<String, Accumulator<JsonStructure, MainStructure<*>>> =
            Success(RootAccumulator)
}