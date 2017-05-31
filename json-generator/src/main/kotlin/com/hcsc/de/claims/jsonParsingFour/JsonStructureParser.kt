package com.hcsc.de.claims.jsonParsingFour

import com.hcsc.de.claims.helpers.*

class JsonStructureParser {

    fun parse(string: String): Result<String, List<JsonStructureElement>> {

        return string.toCharArray().fold(rootAccumulatorResult()) { accumulatorResult, char ->

            accumulatorResult.flatMap { it.processChar(char) }

        }.flatMap {

            when (it) {
                is EmptyAccumulator -> Success<String, List<JsonStructureElement>>(it.structure)
                is LiteralChildAccumulator -> Success<String, List<JsonStructureElement>>(it.structure)
                else -> Failure<String, List<JsonStructureElement>>("Invalid JSON - must close all open elements")
            }
        }
    }

    fun rootAccumulatorResult(): Result<String, Accumulator<JsonStructureElement, MainStructureElement?>> =
            Success(RootAccumulator)
}