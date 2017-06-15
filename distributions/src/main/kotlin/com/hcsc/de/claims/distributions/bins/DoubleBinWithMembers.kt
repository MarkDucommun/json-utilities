package com.hcsc.de.claims.distributions.bins

data class DoubleBinWithMembers(
        override val members: List<Double>
) : AutomaticBinWithMembers<Double>(
        rawMembers = members,
        toType = Double::toDouble
)