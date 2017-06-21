package com.hcsc.de.claims.distributions.bins

import com.hcsc.de.claims.collection.helpers.NonEmptyList

data class IntDualSourceBinWithMembers(
        override val sourceOneBin: BinWithMembers<Int>,
        override val sourceTwoBin: BinWithMembers<Int>
) : AutomaticDualSourceBinWithMembers<Int>(
        sourceOneBin = sourceOneBin,
        sourceTwoBin = sourceTwoBin,
        toType = Double::toInt
)

fun dualSourceBinWithMembers(
        sourceOne: NonEmptyList<Int>,
        sourceTwo: NonEmptyList<Int>
) = IntDualSourceBinWithMembers(
        sourceOneBin = IntBinWithMembers(members = sourceOne),
        sourceTwoBin = IntBinWithMembers(members = sourceTwo)
)