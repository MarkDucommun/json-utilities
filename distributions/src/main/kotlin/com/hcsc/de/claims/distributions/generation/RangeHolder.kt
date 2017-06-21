package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.distributions.bins.BinWithMembers
import com.hcsc.de.claims.distributions.bins.DualSourceBinWithMembers

data class RangeHolder(
        val low: Double,
        val middle: Double,
        val high: Double,
        val bins: List<DualSourceBinWithMembers<Double, BinWithMembers<Double>>>,
        val isComplete: Boolean = false
)