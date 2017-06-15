package com.hcsc.de.claims.distributions.bins

data class IntBinWithMembers(
        override val members: List<Int>
) : AutomaticBinWithMembers<Int>(
        rawMembers = members,
        toType = Double::toInt
)