package com.hcsc.de.claims.histogrammer

import com.hcsc.de.claims.distributions.binDistributions.UnknownDualMemberVariableBinWidthDistribution
import com.hcsc.de.claims.results.Result

class DistributionHistogrammer<in numberType : Number>(
        private val chartCreator: ChartCreator
) {

    fun create(dualDistribution: UnknownDualMemberVariableBinWidthDistribution<numberType>): Result<String, BarChart> {

        return chartCreator.createBarChart(ChartRequest(
                name = "Blah",
                xLabel = "startValue",
                yLabel = "Count",
                dataSets = listOf(
                        DataSet("One", dualDistribution.bins.map { Datapoint(xValue = it.startValue.toDouble(), count = it.memberOneCount) }),
                        DataSet("Two", dualDistribution.bins.map { Datapoint(xValue = it.startValue.toDouble(), count = it.memberTwoCount * -1) })
                )
        ))
    }
}