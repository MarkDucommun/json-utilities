package com.hcsc.de.claims.distributions.bins

import com.hcsc.de.claims.collection.helpers.NonEmptyList

data class DoubleDualSourceBinWithMembers(
        override val sourceOneBin: BinWithMembers<Double>,
        override val sourceTwoBin: BinWithMembers<Double>
) : AutomaticDualSourceBinWithMembers<Double>(
        sourceOneBin = sourceOneBin,
        sourceTwoBin = sourceTwoBin,
        toType = Double::toDouble
)

fun dualSourceBinWithMembers(
        sourceOne: NonEmptyList<Double>,
        sourceTwo: NonEmptyList<Double>
) = DoubleDualSourceBinWithMembers(
        sourceOneBin = DoubleBinWithMembers(members = sourceOne),
        sourceTwoBin = DoubleBinWithMembers(members = sourceTwo)
)