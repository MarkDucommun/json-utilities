package com.hcsc.de.claims.histogrammer

import com.hcsc.de.claims.results.Result

class InvertedHistogrammer(private val histogrammer: Histogrammer<Double>) {

    fun create(firstList: List<Double>, secondList: List<Double>): Result<String, BarChart> =
            histogrammer.create(firstList, secondList.map { it * -1 })
}