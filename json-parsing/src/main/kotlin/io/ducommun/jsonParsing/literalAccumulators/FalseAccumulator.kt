package io.ducommun.jsonParsing.literalAccumulators

import io.ducommun.jsonParsing.FalseNode

class FalseAccumulator(
        previousChar: Char
) : WordAccumulator(previousChar = previousChar, word = "false", node = FalseNode) {

    override fun copy(previousChar: Char): WordAccumulator {
        return FalseAccumulator(previousChar = previousChar)
    }
}