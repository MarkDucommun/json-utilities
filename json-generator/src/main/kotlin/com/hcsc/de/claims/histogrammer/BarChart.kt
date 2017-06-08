package com.hcsc.de.claims.histogrammer

import com.hcsc.de.claims.helpers.Result

interface BarChart {

    val name: String

    val xLabel: String

    val yLabel: String

    val dataSets: List<DataSet>

    fun render()

    fun stop()

    fun save(path: String)
}

interface ChartCreator {

    fun createBarChart(request: ChartRequest): Result<String, BarChart>
}

data class ChartRequest(
        val name: String,
        val xLabel: String,
        val yLabel: String,
        val dataSets: List<DataSet>
)