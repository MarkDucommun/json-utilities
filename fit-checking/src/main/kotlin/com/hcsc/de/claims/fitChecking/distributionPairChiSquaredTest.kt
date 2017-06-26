package com.hcsc.de.claims.fitChecking

import com.hcsc.de.claims.distributions.DistributionPair
import com.hcsc.de.claims.distributions.generation.idealBinCountMinimumSizedDualSourceBinWithMembersDistribution
import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.flatMap

fun <numberType : Number> DistributionPair<numberType>.chiSquaredTestOfLists(
        binCount: Int = 10, toType: Double.() -> numberType
): Result<String, ChiSquareValue> =
        idealBinCountMinimumSizedDualSourceBinWithMembersDistribution(binCount, toType = toType).flatMap {
            val (distOne, distTwo) = it.asTwoDistributions
            distOne.chiSquaredTest(distTwo)
        }