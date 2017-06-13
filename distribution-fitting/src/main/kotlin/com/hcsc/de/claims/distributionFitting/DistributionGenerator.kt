package com.hcsc.de.claims.distributionFitting

import com.hcsc.de.claims.distributions.Distribution
import com.hcsc.de.claims.results.Result


interface DistributionGenerator<numberType: Number> {

    fun profile(list: List<numberType>): Result<String, DistributionProfile<numberType>>
}

data class DistributionProfile<out numberType: Number>(
        val pValue: Double,
        val distribution: Distribution<numberType>
)