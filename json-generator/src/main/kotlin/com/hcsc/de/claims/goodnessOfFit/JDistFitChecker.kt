package com.hcsc.de.claims.goodnessOfFit

import com.hcsc.de.claims.distributions.DistributionPair
import com.hcsc.de.claims.distributions.chiSquaredTest
import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.map

class JDistFitChecker<in numberType : Number> : FitChecker<numberType> {

    override fun check(listOne: List<numberType>, listTwo: List<numberType>): Result<String, Double> {
        return check(listOne, listTwo, 100)
    }

    override fun check(listOne: List<numberType>, listTwo: List<numberType>, binCount: Int): Result<String, Double> {

        return DistributionPair(listOne, listTwo)
                .chiSquaredTest(binCount).map { it.pValue }
    }
}