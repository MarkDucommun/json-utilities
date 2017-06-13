package com.hcsc.de.claims.histogrammer

import com.hcsc.de.claims.results.Result

interface BarChart : Renderable {

    val name: String

    val xLabel: String

    val yLabel: String

    val dataSets: List<DataSet>
}

interface Renderable {

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