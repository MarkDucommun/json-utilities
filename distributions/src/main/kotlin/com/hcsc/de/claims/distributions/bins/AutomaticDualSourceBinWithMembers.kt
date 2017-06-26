package com.hcsc.de.claims.distributions.bins

import com.hcsc.de.claims.results.*

open class AutomaticDualSourceBinWithMembers<numberType : Number>(
        override val sourceOneBin: BinWithMembers<numberType>,
        override val sourceTwoBin: BinWithMembers<numberType>,
        toType: Double.() -> numberType
) : AutomaticBinWithMembers<numberType>(
        rawMembers = sourceOneBin.plus(sourceTwoBin).members,
        toType = toType
), DualSourceBinWithMembers<numberType, BinWithMembers<numberType>> {

    override fun plus(
            other: DualSourceBinWithMembers<numberType, BinWithMembers<numberType>>
    ): DualSourceBinWithMembers<numberType, BinWithMembers<numberType>> {

        return AutomaticDualSourceBinWithMembers(
                sourceOneBin = sourceOneBin.plus(other.sourceOneBin),
                sourceTwoBin = sourceTwoBin.plus(other.sourceTwoBin),
                toType = toType
        )
    }

    override fun splitDualSourceBin(
            minimumSourceBinSize: Int,
            splitPoint: numberType
    ): Result<DualSourceSplitFailure, SplitBinHolder<numberType, DualSourceBinWithMembers<numberType, BinWithMembers<numberType>>>> {

        val sourceOneResult = sourceOneBin.split(splitPoint = splitPoint, minimumBinSize = minimumSourceBinSize)
        val sourceTwoResult = sourceTwoBin.split(splitPoint = splitPoint, minimumBinSize = minimumSourceBinSize)

        return when (sourceOneResult) {
            is Success -> {
                when (sourceTwoResult) {
                    is Success -> {
                        Success<DualSourceSplitFailure, SplitBinHolder<numberType, DualSourceBinWithMembers<numberType, BinWithMembers<numberType>>>>(SplitBinHolder<numberType, DualSourceBinWithMembers<numberType, BinWithMembers<numberType>>>(
                                upper = AutomaticDualSourceBinWithMembers(
                                        sourceOneBin = sourceOneResult.content.upper,
                                        sourceTwoBin = sourceTwoResult.content.upper,
                                        toType = toType
                                ),
                                lower = AutomaticDualSourceBinWithMembers(
                                        sourceOneBin = sourceOneResult.content.lower,
                                        sourceTwoBin = sourceTwoResult.content.lower,
                                        toType = toType
                                )
                        ))
                    }
                    is Failure -> Failure<DualSourceSplitFailure, SplitBinHolder<numberType, DualSourceBinWithMembers<numberType, BinWithMembers<numberType>>>>(SourceTwoSplitFailureWithValue(sourceTwoResult.content))
                }
            }
            is Failure -> {
                when (sourceTwoResult) {
                    is Success -> Failure<DualSourceSplitFailure, SplitBinHolder<numberType, DualSourceBinWithMembers<numberType, BinWithMembers<numberType>>>>(SourceOneSplitFailureWithValue(sourceOneResult.content))
                    is Failure -> Failure<DualSourceSplitFailure, SplitBinHolder<numberType, DualSourceBinWithMembers<numberType, BinWithMembers<numberType>>>>(BothSourceSplitFailureWithValues(sourceOneResult.content, sourceTwoResult.content))
                }
            }
        }
    }
}

sealed class DualSourceSplitFailure

sealed class SingleSourceSplitFailure : DualSourceSplitFailure() {
    abstract val failure: SplitFailure
}

data class SourceOneSplitFailureWithValue(override val failure: SplitFailure) : SingleSourceSplitFailure()
data class SourceTwoSplitFailureWithValue(override val failure: SplitFailure) : SingleSourceSplitFailure()
data class BothSourceSplitFailureWithValues(val sourceOneFailure: SplitFailure, val sourceTwoFailure: SplitFailure) : DualSourceSplitFailure()