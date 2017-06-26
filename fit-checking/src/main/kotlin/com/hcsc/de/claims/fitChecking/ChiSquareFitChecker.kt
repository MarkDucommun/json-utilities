package com.hcsc.de.claims.fitChecking

import com.hcsc.de.claims.distributions.DistributionPair
import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.map

class ChiSquareFitChecker<in numberType : Number>(
        private val toType: Double.() -> numberType
) : FitChecker<numberType> {

    override fun check(listOne: List<numberType>, listTwo: List<numberType>): Result<String, Double> {
        return check(listOne, listTwo, 100)
    }

    override fun check(listOne: List<numberType>, listTwo: List<numberType>, binCount: Int): Result<String, Double> {

        return DistributionPair(listOne, listTwo)
                .chiSquaredTestOfLists(binCount, toType).map { it.pValue }
    }
}