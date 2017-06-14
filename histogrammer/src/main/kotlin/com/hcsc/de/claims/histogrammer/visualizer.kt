package com.hcsc.de.claims.histogrammer

import com.hcsc.de.claims.distributions.generation.DoubleDataBinner
import com.hcsc.de.claims.results.map

private val histogrammer = ListHistogrammer<Double>(DoubleDataBinner(), JFreeChartCreator())

fun <numberType: Number> List<numberType>.visualize(seconds: Int = 60) {

    histogrammer.create(this.map(Number::toDouble)).map { chart ->

        chart.render()

        Thread.sleep(seconds * 1000L)

        chart.stop()
    }
}

fun <numberType: Number> visualize(listOne: List<numberType>, listTwo: List<numberType>, seconds: Int = 60) {

    histogrammer.create(listOne.map(Number::toDouble), listTwo.map(Number::toDouble)).map { chart ->

        chart.render()

        Thread.sleep(seconds * 1000L)

        chart.stop()
    }
}