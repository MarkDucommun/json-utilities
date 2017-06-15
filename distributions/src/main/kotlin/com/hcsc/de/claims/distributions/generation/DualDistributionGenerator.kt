package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.results.Result

interface DualDistributionGenerator<numberType: Number> {

    fun profile(listOne: List<numberType>, listTwo: List<numberType>): Result<String, DistributionProfile<numberType>>
}