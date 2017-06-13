package io.ducommun.jsonParsing.literalAccumulators

import io.ducommun.jsonParsing.TrueNode

class TrueAccumulator(
        previousChar: Char
) : WordAccumulator(previousChar = previousChar, word = "true", node = TrueNode) {

    override fun copy(previousChar: Char): WordAccumulator {
        return TrueAccumulator(previousChar = previousChar)
    }
}