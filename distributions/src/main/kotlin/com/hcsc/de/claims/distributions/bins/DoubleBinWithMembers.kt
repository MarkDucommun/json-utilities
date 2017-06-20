package com.hcsc.de.claims.distributions.bins

import com.hcsc.de.claims.collection.helpers.NonEmptyList

data class DoubleBinWithMembers(
        override val members: NonEmptyList<Double>
) : AutomaticBinWithMembers<Double>(
        rawMembers = members,
        toType = Double::toDouble
)