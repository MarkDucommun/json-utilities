package com.hcsc.de.claims.jsonParsingFour

import com.hcsc.de.claims.helpers.*

class JsonStructureParser {

    fun parse(string: String): Result<String, List<JsonStructure>> {

        return string.toCharArray().fold(rootAccumulatorResult()) { accumulatorResult, char ->

            accumulatorResult.flatMap { it.processChar(char) }

        }.flatMap {

            when (it) {
                is EmptyAccumulator -> Success<String, List<JsonStructure>>(it.structure)
                is LiteralValueAccumulator -> Success<String, List<JsonStructure>>(it.structure)
                else -> Failure<String, List<JsonStructure>>("Invalid JSON - must close all open elements")
            }
        }
    }

    fun rootAccumulatorResult(): Result<String, Accumulator<JsonStructure, MainStructure?>> =
            Success(RootAccumulator)
}