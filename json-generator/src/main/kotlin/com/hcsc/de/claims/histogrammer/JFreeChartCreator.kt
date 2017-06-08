package com.hcsc.de.claims.histogrammer

import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success

class JFreeChartCreator : ChartCreator {

    override fun createBarChart(request: ChartRequest): Result<String, BarChart> {

        return Success(JFreeBarChart(
                name = request.name,
                xLabel = request.xLabel,
                yLabel = request.yLabel,
                dataSets = request.dataSets
        ))
    }
}