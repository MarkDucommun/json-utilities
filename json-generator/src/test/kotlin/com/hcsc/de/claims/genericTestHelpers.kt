package com.hcsc.de.claims

import com.hcsc.de.claims.distributions.DoubleDataBinner
import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.histogrammer.ChartHistogrammer
import com.hcsc.de.claims.histogrammer.JFreeChartCreator

data class TimeAndResult<failureType, successType>(
        val result: Result<failureType, successType>,
        val elapsedTime: Double
)

fun <failureType, successType> time(fn: () -> Result<failureType, successType>): TimeAndResult<failureType, successType> {

    val start = System.nanoTime()

    val result = fn.invoke()

    return TimeAndResult(
            result = result,
            elapsedTime = (System.nanoTime().toDouble() - start.toDouble()) / 1000000.0
    )
}

private val histogrammer = ChartHistogrammer<Double>(DoubleDataBinner(), JFreeChartCreator())

fun <numberType: Number> List<numberType>.visualize(seconds: Int = 60) {

    val chart = histogrammer.create(this.map(Number::toDouble)).get

    chart.render()

    Thread.sleep(seconds * 1000L)

    chart.stop()
}

fun <numberType: Number> visualize(listOne: List<numberType>, listTwo: List<numberType>, seconds: Int = 60) {

    val chart = histogrammer.create(listOne.map(Number::toDouble), listTwo.map(Number::toDouble)).get

    chart.render()

    Thread.sleep(seconds * 1000L)

    chart.stop()
}

