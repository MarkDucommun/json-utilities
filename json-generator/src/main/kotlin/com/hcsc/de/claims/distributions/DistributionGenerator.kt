package com.hcsc.de.claims.distributions

import com.hcsc.de.claims.helpers.Result

interface DistributionGenerator<numberType: Number> {

    fun profile(list: List<numberType>): Result<String, DistributionProfile<numberType>>
}

data class DistributionProfile<out numberType: Number>(
        val pValue: Double,
        val distribution: Distribution<numberType>
)