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

        val sourceOneResult = sourceOneBin.split(splitPoint)
        val sourceTwoResult = sourceTwoBin.split(splitPoint)

        return when (sourceOneResult) {
            is Success -> {
                when (sourceTwoResult) {
                    is Success -> {
                        Success(SplitBinHolder<numberType, DualSourceBinWithMembers<numberType, BinWithMembers<numberType>>>(
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
                    is Failure -> {
                        when (sourceTwoResult.content) {
                            is UpperSplitFailure -> {
                                Failure(SourceTwoUpperSplitFailure as DualSourceSplitFailure)
                            }
                            is LowerSplitFailure -> {
                                Failure(SourceTwoLowerSplitFailure as DualSourceSplitFailure)
                            }
                        }
                    }
                }
            }
            is Failure -> {
                when (sourceTwoResult) {
                    is Success -> {
                        when (sourceOneResult.content) {
                            is UpperSplitFailure -> {
                                Failure(SourceOneUpperSplitFailure as DualSourceSplitFailure)
                            }
                            is LowerSplitFailure -> {
                                Failure(SourceOneLowerSplitFailure as DualSourceSplitFailure)
                            }
                        }
                    }
                    is Failure -> {

                        val failure = when {
                            sourceOneResult.content is UpperSplitFailure && sourceTwoResult.content is UpperSplitFailure -> BothSourceUpperSplitFailure
                            sourceOneResult.content is LowerSplitFailure && sourceTwoResult.content is LowerSplitFailure -> BothSourceLowerSplitFailure
                            sourceOneResult.content is UpperSplitFailure && sourceTwoResult.content is LowerSplitFailure -> BothSourceUpperLowerSplitFailure
                            else -> BothSourceLowerUpperSplitFailure
                        }

                        Failure(failure as DualSourceSplitFailure)
                    }
                }
            }
        }
    }
}

sealed class DualSourceSplitFailure

sealed class SourceOneSplitFailure<splitFailure : SplitFailure> : DualSourceSplitFailure()

sealed class SourceTwoSplitFailure<splitFailure : SplitFailure> : DualSourceSplitFailure()

sealed class BothSourceSplitFailure<sourceOneFailure : SplitFailure, sourceTwoFailure : SplitFailure> : DualSourceSplitFailure()

object SourceOneUpperSplitFailure : SourceOneSplitFailure<UpperSplitFailure>()
object SourceOneLowerSplitFailure : SourceOneSplitFailure<LowerSplitFailure>()
object SourceTwoUpperSplitFailure : SourceTwoSplitFailure<UpperSplitFailure>()
object SourceTwoLowerSplitFailure : SourceTwoSplitFailure<LowerSplitFailure>()
object BothSourceUpperSplitFailure : BothSourceSplitFailure<UpperSplitFailure, UpperSplitFailure>()
object BothSourceLowerSplitFailure : BothSourceSplitFailure<LowerSplitFailure, LowerSplitFailure>()
object BothSourceUpperLowerSplitFailure : BothSourceSplitFailure<UpperSplitFailure, LowerSplitFailure>()
object BothSourceLowerUpperSplitFailure : BothSourceSplitFailure<LowerSplitFailure, UpperSplitFailure>()
