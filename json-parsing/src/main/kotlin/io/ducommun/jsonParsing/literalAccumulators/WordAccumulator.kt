package io.ducommun.jsonParsing.literalAccumulators

import com.hcsc.de.claims.results.Failure
import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.Success
import com.hcsc.de.claims.results.flatMapError
import io.ducommun.jsonParsing.JsonNode

abstract class WordAccumulator(
        val previousChar: Char,
        val word: String,
        val node: JsonNode
) : Accumulator {

    val zippedWord: List<Pair<Char, Char>> = word.dropLast(1).zip(word.drop(1))

    val lastPair: Pair<Char, Char> = zippedWord.last()

    override fun addChar(char: Char): Result<String, Accumulator> {

        val initial: Result<String, Accumulator> = Failure("Invalid JSON - incorrect literal")

        return zippedWord.fold(initial) { accumulator, pair ->

            val (previousCharCandidate, charCandidate) = pair

            accumulator.flatMapError {

                val matches = previousCharCandidate == previousChar && charCandidate == char

                if (pair == lastPair && matches) {
                    Success<String, Accumulator>(LiteralCompleteAccumulator(rawNode = node))
                } else if (matches) {
                    Success<String, Accumulator>(copy(previousChar = char))
                } else {
                    Failure<String, Accumulator>(it)
                }
            }
        }
    }

    abstract fun copy(previousChar: Char): WordAccumulator
}