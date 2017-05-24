package com.hcsc.de.claims.jsonParsingFour

import com.hcsc.de.claims.helpers.*
import com.hcsc.de.claims.jsonParsingFour.structureAccumulators.Accumulator
import com.hcsc.de.claims.jsonParsingFour.structureAccumulators.EmptyAccumulator
import com.hcsc.de.claims.jsonParsingFour.structureAccumulators.LiteralValueAccumulator
import com.hcsc.de.claims.jsonParsingFour.structureAccumulators.RootAccumulator

class JsonStructureParser {

    fun parse(string: String): Result<String, List<JsonStructure>> {

        return string.toCharArray().fold(rootAccumulatorResult()) { accumulatorResult, char ->

            accumulatorResult.flatMap { it.processChar(char) }

        }.flatMap {

            when (it.previousClosable) {
                is LiteralStructureElement -> if (it.structureStack.size > 2) {
                    Failure<String, Accumulator<*, *>>("Invalid JSON - must close all open elements")
                } else {
                    Success<String, Accumulator<*, *>>(it)
                }
                else -> if (it.structureStack.size > 1) {
                    Failure<String, Accumulator<*, *>>("Invalid JSON - must close all open elements")
                } else {
                    Success<String, Accumulator<*, *>>(it)
                }
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
}