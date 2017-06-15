package com.hcsc.de.claims.histogrammer

import com.hcsc.de.claims.distributions.binDistributions.DualMemberBinsDistribution
import com.hcsc.de.claims.results.Result

class DistributionHistogrammer<numberType : Number>(
        private val chartCreator: ChartCreator
) {

    fun create(dualDistribution: DualMemberBinsDistribution<numberType>): Result<String, BarChart> {

        return chartCreator.createBarChart(ChartRequest(
                name = "Blah",
                xLabel = "startValue",
                yLabel = "Count",
                dataSets = listOf(
                        DataSet("One", dualDistribution.bins.map { Datapoint(xValue = it.startValue.toDouble(), count = it.binOne.size) }),
                        DataSet("Two", dualDistribution.bins.map { Datapoint(xValue = it.startValue.toDouble(), count = it.binTwo.size * -1) })
                )
        ))
    }
}