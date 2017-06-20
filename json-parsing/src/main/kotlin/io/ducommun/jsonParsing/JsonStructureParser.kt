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

    fun parse(string: String): Result<String, List<JsonStructure>> {

        return string.toCharArray().fold(rootAccumulatorResult()) { accumulatorResult, char ->

            accumulatorResult.flatMap { it.processChar(char) }

        }.flatMap {

            when (it.previousClosable) {
                is LiteralStructureElement -> it.ensureStructureStackLessThanOrEqualTo(2)
                else -> it.ensureStructureStackLessThanOrEqualTo(1)
            }
        }.flatMap {

            when (it) {
                is EmptyAccumulator -> Success<String, List<JsonStructure>>(it.structure)
                is LiteralValueAccumulator -> Success<String, List<JsonStructure>>(it.structure)
                else -> Failure<String, List<JsonStructure>>("Invalid JSON - must close all open elements")
            }
        }
    }

    fun rootAccumulatorResult(): Result<String, Accumulator<JsonStructure, MainStructure<*>>> =
            Success(RootAccumulator)

    fun Accumulator<*, *>.ensureAllStructuresAreClosed(): Result<String, Accumulator<*, *>> =

    fun Accumulator<*, *>.ensureStructureStackLessThanOrEqualTo(size: Int): Result<String, Accumulator<*, *>> {

        return if (structureStack.size > size) {
            Failure<String, Accumulator<*, *>>("Invalid JSON - must close all open elements")
        } else {
            Success<String, Accumulator<*, *>>(this)
        }
    }
}