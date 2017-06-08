package com.hcsc.de.claims.goodnessOfFit

import com.hcsc.de.claims.distributions.DistributionPair
import com.hcsc.de.claims.distributions.asTwoDistributions
import com.hcsc.de.claims.distributions.chiSquaredTest
import com.hcsc.de.claims.distributions.unknownDualMemberVariableBinWidthDistribution
import com.hcsc.de.claims.helpers.*
import net.sourceforge.jdistlib.disttest.DistributionTest
import umontreal.ssj.gof.GofFormat
import umontreal.ssj.gof.GofStat

class JDistFitChecker<in numberType : Number> : FitChecker<numberType> {

    override fun check(listOne: List<numberType>, listTwo: List<numberType>): Result<String, Double> {

        return DistributionPair(listOne, listTwo)
                .chiSquaredTest(100).map { it.pValue }
    }
}