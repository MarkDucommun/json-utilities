package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.distributions.bins.BinWithMembers
import com.hcsc.de.claims.distributions.bins.DualMemberBin

data class RangeHolder(
        val low: Double,
        val middle: Double,
        val high: Double,
        val bins: List<DualMemberBin<Double, BinWithMembers<Double>>>,
        val isIncomplete: Boolean = true,
        val isFinalBin: Boolean = false
)