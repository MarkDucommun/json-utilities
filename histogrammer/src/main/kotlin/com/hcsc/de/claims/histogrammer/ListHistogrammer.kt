package com.hcsc.de.claims.histogrammer

import com.hcsc.de.claims.distributions.DataBinner
import com.hcsc.de.claims.distributions.FixedWidthBin
import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.flatMap
import com.hcsc.de.claims.results.map
import com.hcsc.de.claims.results.traverse

class ListHistogrammer<in numberType : Number>(
        private val dataBinner: DataBinner<Double>,
        private val chartCreator: ChartCreator
) : Histogrammer<numberType> {

    override fun create(list: List<numberType>, name: String): Result<String, BarChart> =
            createBarChart(listOf(list), name)

    override fun create(firstList: List<numberType>, secondList: List<numberType>): Result<String, BarChart> =
            listOf(
                    firstList.asBinList.map { it.asDataset("1") },
                    secondList.asBinList.map { it.asInvertedDataset("2") }
            ).traverse().flatMap { it.asBarChart("Chart") }

    private fun createBarChart(lists: List<List<numberType>>, title: String = "Chart"): Result<String, BarChart> =
            lists.asBinLists.flatMap { it.asDataSets.asBarChart(title) }

    private val List<List<numberType>>.asBinLists: Result<String, List<List<FixedWidthBin<Double>>>>
        get() = map { it.asBinList }.traverse()

    private val List<numberType>.asBinList: Result<String, List<FixedWidthBin<Double>>>
        get() = dataBinner.bin(this.map { it.toInt().toDouble() })

    private val List<List<FixedWidthBin<Double>>>.asDataSets: List<DataSet>
        get() = mapIndexed { index, bins -> bins.asDataset("${index + 1}") }

    private fun List<DataSet>.asBarChart(name: String): Result<String, BarChart>
         = chartCreator.createBarChart(ChartRequest(
                name = name,
                xLabel = "size",
                yLabel = "count",
                dataSets = this
        ))

    private fun List<FixedWidthBin<Double>>.asDataset(name: String): DataSet =
            DataSet(name = name, datapoints = map { it.asDatapoint })

    private fun List<FixedWidthBin<Double>>.asInvertedDataset(name: String): DataSet =
            DataSet(name = name, datapoints = map { it.asInvertedDatapoint })

    private val FixedWidthBin<Double>.asDatapoint: Datapoint
        get() = Datapoint(xValue = startValue, count = count)

    private val FixedWidthBin<Double>.asInvertedDatapoint: Datapoint
        get() = Datapoint(xValue = startValue, count = count * -1)
}


class InvertedHistogrammer(
        private val histogrammer: Histogrammer<Double>
) {

    fun create(firstList: List<Double>, secondList: List<Double>): Result<String, BarChart> =
            histogrammer.create(firstList, secondList.map { it * -1 })
}
