package com.hcsc.de.claims.fitChecking

import com.hcsc.de.claims.distributions.DistributionPair
import com.hcsc.de.claims.results.Result

fun <numberType: Number> List<numberType>.chiSquaredTestOfLists(
        expected: List<numberType>,
        binCount: Int = 5,
        toType: Double.() -> numberType
): Result<String, ChiSquareValue> = DistributionPair(this, expected).chiSquaredTestOfLists(binCount, toType)