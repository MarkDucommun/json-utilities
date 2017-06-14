package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.distributions.bins.BinWithMembers
import com.hcsc.de.claims.distributions.bins.DualMemberBin

data class BinHolder(
        val isComplete: Boolean = false,
        val bins: List<DualMemberBin<Double, BinWithMembers<Double>>>
)