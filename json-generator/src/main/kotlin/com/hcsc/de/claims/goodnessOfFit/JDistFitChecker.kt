package com.hcsc.de.claims.goodnessOfFit

import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.wrapExternalLibraryUsageAsResult
import net.sourceforge.jdistlib.disttest.DistributionTest
import umontreal.ssj.gof.GofStat

class JDistFitChecker<in numberType : Number> : FitChecker<numberType> {

    override fun check(listOne: List<numberType>, listTwo: List<numberType>): Result<String, Double> {

        return wrapExternalLibraryUsageAsResult {

            val kolmogorov_smirnov_test = DistributionTest.kolmogorov_smirnov_test(
                    listOne.map(Number::toDouble).toDoubleArray(),
                    listTwo.map(Number::toDouble).toDoubleArray()
            )
            kolmogorov_smirnov_test[1]
        }
    }
}