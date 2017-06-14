package com.hcsc.de.claims.distributions

data class DistributionPair<out numberType : Number>(
        val one: List<numberType>,
        val two: List<numberType>
)