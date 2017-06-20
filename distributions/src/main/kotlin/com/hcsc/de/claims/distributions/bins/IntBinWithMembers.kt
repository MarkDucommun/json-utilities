package com.hcsc.de.claims.distributions.bins

import com.hcsc.de.claims.collection.helpers.NonEmptyList

data class IntBinWithMembers(
        override val members: NonEmptyList<Int>
) : AutomaticBinWithMembers<Int>(
        rawMembers = members,
        toType = { Math.round(this).toInt() }
)