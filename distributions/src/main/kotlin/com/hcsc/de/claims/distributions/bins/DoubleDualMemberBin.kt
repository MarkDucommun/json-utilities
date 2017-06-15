package com.hcsc.de.claims.distributions.bins

data class DoubleDualMemberBin(
        override val binOne: BinWithMembers<Double>,
        override val binTwo: BinWithMembers<Double>
) : AutomaticDualMemberBin<Double>(
        binOne = binOne,
        binTwo = binTwo,
        toType = Double::toDouble
)