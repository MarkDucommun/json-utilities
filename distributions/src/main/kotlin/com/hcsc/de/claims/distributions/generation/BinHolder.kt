package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.distributions.bins.BinWithMembers
import com.hcsc.de.claims.distributions.bins.DualSourceBinWithMembers

data class BinHolder(
        val isComplete: Boolean = false,
        val bins: List<DualSourceBinWithMembers<Double, BinWithMembers<Double>>>
)