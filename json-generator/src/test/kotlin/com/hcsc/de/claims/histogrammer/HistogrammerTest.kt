package com.hcsc.de.claims.histogrammer

import com.hcsc.de.claims.distributionFitting.Montreal
import com.hcsc.de.claims.distributions.DataBinner
import com.hcsc.de.claims.distributions.DoubleDataBinner
import com.hcsc.de.claims.distributions.FixedWidthBin
import com.hcsc.de.claims.distributions.RealDistributionGenerator
import com.hcsc.de.claims.fileReaders.RawByteStringFileReader
import com.hcsc.de.claims.results.Failure
import com.hcsc.de.claims.results.Success
import com.hcsc.de.claims.results.failsWithMessage
import com.hcsc.de.claims.results.succeedsAndShouldReturn
import com.nhaarman.mockito_kotlin.*
import net.sourceforge.jdistlib.Weibull
import org.junit.Ignore
import org.junit.Test


class HistogrammerTest {

    val defaultBins = emptyList<FixedWidthBin<Double>>()

    val mockBarChart: BarChart = mock()

    val mockDataBinner: DataBinner<Double> = mock {
        on { bin(any()) } doReturn Success<String, List<FixedWidthBin<Double>>>(defaultBins)
    }

    val mockChartCreator: ChartCreator = mock {
        on { createBarChart(any()) } doReturn Success(mockBarChart)
    }

    val subject = ListHistogrammer<Double>(
            dataBinner = mockDataBinner,
            chartCreator = mockChartCreator
    )

    @Test
    fun `one list - it bins the data`() {

        val list = emptyList<Double>()

        subject.create(list)

        verify(mockDataBinner).bin(list)
    }

    @Test
    fun `one list - it passes out the failure from data binner`() {

        whenever(mockDataBinner.bin(any())).thenReturn(Failure("I failed"))

        subject.create(emptyList()) failsWithMessage "I failed"
    }

    @Test
    fun `one list - it passes the binned data to our chart`() {

        val expectedBins = listOf(FixedWidthBin(1.0, 1))

        whenever(mockDataBinner.bin(any())).thenReturn(Success(expectedBins))

        subject.create(emptyList())

        verify(mockChartCreator).createBarChart(ChartRequest(
                name = "Chart",
                xLabel = "size",
                yLabel = "count",
                dataSets = listOf(DataSet(
                        name = "1",
                        datapoints = listOf(Datapoint(1.0, 1))
                ))
        ))
    }

    @Test
    fun `one list - it passes out the failure from chart creator`() {

        whenever(mockChartCreator.createBarChart(any())).thenReturn(Failure("I failed again"))

        subject.create(emptyList()) failsWithMessage "I failed again"
    }

    @Test
    fun `one list - it returns what the ChartCreator made`() {

        val expectedBarChart = mockBarChart

        whenever(mockChartCreator.createBarChart(any())).thenReturn(Success(expectedBarChart))

        subject.create(emptyList()) succeedsAndShouldReturn expectedBarChart
    }

    @Test
    fun `two lists - it bins the data`() {

        val first = listOf(1.0)
        val second = listOf(2.0)

        subject.create(first, second)

        verify(mockDataBinner).bin(first)
        verify(mockDataBinner).bin(second)
    }

    @Test
    fun `two lists - it passes out the failure from data binner`() {

        whenever(mockDataBinner.bin(any())).thenReturn(Failure("I failed"))

        subject.create(emptyList(), emptyList()) failsWithMessage "I failed"
    }

    @Test
    fun `two lists - it passes the binned data to our chart`() {

        val firstExpectedBins = listOf(FixedWidthBin(1.0, 1))
        val secondExpectedBins = listOf(FixedWidthBin(2.0, 1))

        val firstList = listOf(1.0)
        val secondList = listOf(2.0)

        whenever(mockDataBinner.bin(firstList)).thenReturn(Success(firstExpectedBins))
        whenever(mockDataBinner.bin(secondList)).thenReturn(Success(secondExpectedBins))

        subject.create(firstList, secondList)

        verify(mockChartCreator).createBarChart(ChartRequest(
                name = "Chart",
                xLabel = "size",
                yLabel = "count",
                dataSets = listOf(
                        DataSet(name = "1", datapoints = listOf(Datapoint(1.0, 1))),
                        DataSet(name = "2", datapoints = listOf(Datapoint(2.0, 1)))
                )
        ))
    }

    @Test
    fun `two lists - it passes out the failure from chart creator`() {

        whenever(mockChartCreator.createBarChart(any())).thenReturn(Failure("I failed again"))

        subject.create(emptyList()) failsWithMessage "I failed again"
    }

    @Test
    fun `two lists - it returns what the ChartCreator made`() {

        val expectedBarChart = mockBarChart

        whenever(mockChartCreator.createBarChart(any())).thenReturn(Success(expectedBarChart))

        subject.create(emptyList()) succeedsAndShouldReturn expectedBarChart
    }

    @Test
    @Ignore
    fun `spike`() {

        val list = listOfClaimSizes().map { it / 1000.0 }

        val result = RealDistributionGenerator(Montreal).profile(list)

        val distribution = when (result) {
            is Success -> result.content.distribution
            is Failure -> throw RuntimeException(result.content)
        }

        val generatedList = List(list.size) { distribution.random() }

        val histogrammer = ListHistogrammer<Double>(DoubleDataBinner(), JFreeChartCreator())

        val histogramResult = histogrammer.create(list, generatedList)

        val histogram = when (histogramResult) {
            is Success -> histogramResult.content
            is Failure -> throw RuntimeException(histogramResult.content)
        }

        histogram.render()

        Thread.sleep(60000)

        histogram.stop()
    }

    @Test
    @Ignore
    fun `another spike`() {

        val startingDistribution = Weibull(2.0, 20.0)

        val list = List(100000) { startingDistribution.random() }

        val result = RealDistributionGenerator(Montreal).profile(list)

        val distribution = when (result) {
            is Success -> result.content.distribution
            is Failure -> throw RuntimeException(result.content)
        }

        val generatedList = List(list.size) { distribution.random() }

        val histogrammer = ListHistogrammer<Double>(DoubleDataBinner(), JFreeChartCreator())

        val histogramResult = histogrammer.create(list, generatedList)

        val histogram = when (histogramResult) {
            is Success -> histogramResult.content
            is Failure -> throw RuntimeException(histogramResult.content)
        }

        histogram.render()

        Thread.sleep(60000)

        histogram.stop()

    }

    fun listOfClaimSizes(): List<Int> {

        val result = RawByteStringFileReader().read("src/test/resources/data/claim-size-list.csv")

        return when (result) {
            is Success -> result.content.split(",").map { it.toInt() }
            is Failure -> throw RuntimeException("File not found")
        }
    }
}