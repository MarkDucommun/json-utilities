package com.hcsc.de.claims.distributions.generation

data class RangeHolder(
        val low: Double,
        val middle: Double,
        val high: Double,
        val isIncomplete: Boolean = true,
        val isFinalBin: Boolean = false
)