package io.ducommun.jsonParsing.literalAccumulators

import io.ducommun.jsonParsing.NullNode

class NullAccumulator(
        previousChar: Char
) : WordAccumulator(previousChar = previousChar, word = "null", node = NullNode) {

    override fun copy(previousChar: Char): WordAccumulator {
        return NullAccumulator(previousChar = previousChar)
    }
}