package io.ducommun.jsonParsing

import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.flatMap
import io.ducommun.jsonParsing.structureAccumulators.Accumulator
import io.ducommun.jsonParsing.structureAccumulators.EmptyAccumulator
import io.ducommun.jsonParsing.structureAccumulators.LiteralValueAccumulator
import io.ducommun.jsonParsing.structureAccumulators.RootAccumulator

class JsonStructureParser {

    fun parse(string: String): com.hcsc.de.claims.results.Result<String, List<JsonStructure>> {

        return string.toCharArray().fold(rootAccumulatorResult()) { accumulatorResult, char ->

            accumulatorResult.flatMap { it.processChar(char) }

        }.flatMap {

            when (it.previousClosable) {
                is LiteralStructureElement -> if (it.structureStack.size > 2) {
                    com.hcsc.de.claims.results.Failure<String, Accumulator<*, *>>("Invalid JSON - must close all open elements")
                } else {
                    com.hcsc.de.claims.results.Success<String, Accumulator<*, *>>(it)
                }
                else -> if (it.structureStack.size > 1) {
                    com.hcsc.de.claims.results.Failure<String, Accumulator<*, *>>("Invalid JSON - must close all open elements")
                } else {
                    com.hcsc.de.claims.results.Success<String, Accumulator<*, *>>(it)
                }
            }
        }.flatMap {

            when (it) {
                is EmptyAccumulator -> com.hcsc.de.claims.results.Success<String, List<JsonStructure>>(it.structure)
                is LiteralValueAccumulator -> com.hcsc.de.claims.results.Success<String, List<JsonStructure>>(it.structure)
                else -> com.hcsc.de.claims.results.Failure<String, List<JsonStructure>>("Invalid JSON - must close all open elements")
            }
        }
    }

    fun rootAccumulatorResult(): Result<String, Accumulator<JsonStructure, MainStructure<*>>> =
            com.hcsc.de.claims.results.Success(RootAccumulator)
}