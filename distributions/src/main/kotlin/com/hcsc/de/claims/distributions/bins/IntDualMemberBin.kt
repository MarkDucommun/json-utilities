package com.hcsc.de.claims.distributions.bins

data class IntDualMemberBin(
        override val binOne: BinWithMembers<Int>,
        override val binTwo: BinWithMembers<Int>
) : AutomaticDualMemberBin<Int>(
        binOne = binOne,
        binTwo = binTwo,
        toType = Double::toInt
)