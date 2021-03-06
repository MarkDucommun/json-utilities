package com.hcsc.de.claims.jsonSizing

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.hcsc.de.claims.distributionFitting.FitDistrPlus
import com.hcsc.de.claims.distributionFitting.Montreal
import com.hcsc.de.claims.distributionFitting.BestFitDistributionGenerator
import com.hcsc.de.claims.distributions.parametric.DoubleNormalDistribution
import com.hcsc.de.claims.fileReading.RawByteStringFileReader
import com.hcsc.de.claims.math.helpers.ceilingOnEven
import com.hcsc.de.claims.math.helpers.sqrt
import com.hcsc.de.claims.math.helpers.square
import com.hcsc.de.claims.results.Failure
import com.hcsc.de.claims.results.Success
import com.hcsc.de.claims.results.get
import net.sourceforge.jdistlib.Normal
import net.sourceforge.jdistlib.disttest.DistributionTest
import net.sourceforge.jdistlib.generic.GenericDistribution
import org.apache.commons.math3.stat.inference.ChiSquareTest
import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Ignore
import org.junit.Test
import java.io.FileWriter

@Ignore("Not really a part of this repo")
class JsonSizeAnalyzerIntegrationTest {

    @Test
    fun `it reads in some files and creates a size analysis`() {

        val files = listOf(
                "src/test/resources/data/deidentifiedClaims1.json"
//                "src/test/resources/data/deidentifiedClaims2.json",
//                "src/test/resources/data/deidentifiedClaims3.json",
//                "src/test/resources/data/deidentifiedClaims4.json"
        )

        val fileReader = RawByteStringFileReader()

        val rawClaims = files.flatMap { fileReader.read(it).get.split("\n") }.subList(0, 5)

        val jsonSizer = JsonSizer()

        val listOfJsonSizes = rawClaims.filterNot { it.isBlank() }.map { jsonSizer.calculateSize(it).get }

        val jsonSizeAnalyzer = SingleThreadJsonSizeAnalyzer(distributionGenerator = BestFitDistributionGenerator(Montreal))

        val result = jsonSizeAnalyzer.generateJsonSizeOverview(listOfJsonSizes).get

        println()
//        val listOfTopLevelSizes = listOfJsonSizes.map { it.size.toDouble() }
//
//        val topLevelSizeDistribution = listOfTopLevelSizes.normalDistribution
//
//        val normal = Normal(topLevelSizeDistribution.average, topLevelSizeDistribution.standardDeviation)
//
//        val period = topLevelSizeDistribution.maximum / 1000
//
//        listOfTopLevelSizes.groupBy { (it / (period)).toInt() }

        //ChiSquareTest().chiSquare()

//        val results = DistributionTest.kolmogorov_smirnov_test(listOfTopLevelSizes, normal).toList()

//        val varianceFromNormality = results.normalDistribution

        println()
//        when (result) {
//            is Success -> FileWriter("claim-overview.json").apply {

//                val claimOverview = result.content

//                val jsonSorter = JsonSizeSorter()

//                val sortedClaim = jsonSorter.sort(claimOverview)

//                val jsonPercentageSizer = JsonPercentageSizer()
//
//                when (sortedClaim) {
//                    is Success -> {
//                        val result = jsonPercentageSizer.generatePercentage(sortedClaim.content)
//
//                        write(ObjectMapper().writeValueAsString(sortedClaim.content))
//                    }
//                    is Failure -> throw RuntimeException(sortedClaim.content)
//                }


//                close()
//    }
//            is Failure -> throw RuntimeException(result.content)
//        }
    }

//    @Test
//    fun `0 is very far from normality`() {
//
//        val normal = Normal(100.0, 2.0)
//
//        val results = List(20) { List(10000) { normal.random() }.kolmogorovSmirnovTest(normal) }
//
//        val testStatisticsDistribution = results.map { it.testStatistic }.normalDistribution
//        val pValueDistribution = results.map { it.pValue }.normalDistribution
//
//        println()
//    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////

    @Test
    @Ignore("Not a part of this repo")
    fun `it cleans up legacyClaimPriorStatuses by max claimPriorWorkStatusEffectiveDate`() {

        val files = listOf(
                "src/test/resources/data/deidentifiedClaims1.json",
                "src/test/resources/data/deidentifiedClaims2.json",
                "src/test/resources/data/deidentifiedClaims3.json",
                "src/test/resources/data/deidentifiedClaims4.json"
        )

        val fileReader = RawByteStringFileReader()

        val objectMapper = ObjectMapper().registerKotlinModule()

        val jsonClaimLists = files.map {
            val result = fileReader.read(it)

            when (result) {
                is Success -> result.content.split("\n").map { objectMapper.readValue<JsonNode>(it) }
                is Failure -> throw RuntimeException(result.content)
            }
        }

        jsonClaimLists.forEach { claimList ->
            val transformedClaims = claimList.map { claim ->
//                fieldM
            }
        }
    }

    @Test
    fun `it writes a file that has a comma separated list of the root size of all the json documents`() {

        val files = listOf(
                "src/test/resources/data/deidentifiedClaims1.json",
                "src/test/resources/data/deidentifiedClaims2.json",
                "src/test/resources/data/deidentifiedClaims3.json",
                "src/test/resources/data/deidentifiedClaims4.json"
        )

        val fileReader = RawByteStringFileReader()

        val rawClaims = files.flatMap {
            val result = fileReader.read(it)

            when (result) {
                is Success -> result.content.split("\n")
                is Failure -> throw RuntimeException(result.content)
            }
        }

        val jsonSizer = JsonSizer()

        val listOfJsonSizes = rawClaims.filterNot { it.isBlank() }.map {
            val result = jsonSizer.calculateSize(it)

            when (result) {
                is Success -> result.content
                is Failure -> throw RuntimeException(result.content)
            }
        }

        FileWriter("claim-size-list.csv").apply {
            write(listOfJsonSizes.map{ it.size }.joinToString(","))
            close()
        }
    }

    fun listOfClaimSizes(): List<Int> {

        val result = RawByteStringFileReader().read("claim-size-list.csv")

        return when (result) {
            is Success -> result.content.split(",").map { it.toInt() }
            is Failure -> throw RuntimeException("Blam!")
        }
    }

    @Test
    fun `it can read in the list of claim sizes from a file`() {
        assertThat(listOfClaimSizes().first()).isEqualTo(145356)
    }

    @Test
    fun `lets find out the distribution for this set of data`() {

        val claimSizes = listOfClaimSizes().map(Int::toDouble)

        val results = BestFitDistributionGenerator(FitDistrPlus).profile(claimSizes)

        if (results is Success) {

            val distribution = results.content.distribution

            val generatedDistribution = List(1000) { distribution.random() }.map { it.ceilingOnEven() }.map { it.toInt() }.map { it.toDouble() }.toDoubleArray()

            val result1 = DistributionTest.kolmogorov_smirnov_test(claimSizes.toDoubleArray(), generatedDistribution)

            println()

        }
    }

    @Test
    @Ignore
    fun `some function groups a given distribution into 1000 buckets`() {

        val claimSizes = listOfClaimSizes().map(Int::toDouble)

        val claimSizesDistribution = claimSizes.normalDistribution

        var claimsGroupedByKilobytes = claimSizes.groupBy { (it / 1000).toInt() }

        val normal = Normal(claimSizesDistribution.average, claimSizesDistribution.standardDeviation)

        var normalDistributionBySize: Map<Int, List<Double>> = List(5000) { normal.random() }.groupBy { (it / 1000).toInt() }

        val allKeys = claimsGroupedByKilobytes.keys.plus(normalDistributionBySize.keys)

        val range = allKeys.min()!!.rangeTo(allKeys.max()!!)

        range.forEach {
            val isKeyInNormal = normalDistributionBySize.containsKey(it)
            if(!isKeyInNormal) {
                normalDistributionBySize = normalDistributionBySize.plus(it to emptyList())
            }
            val isKeyInClaims = claimsGroupedByKilobytes.containsKey(it)
            if(!isKeyInClaims) {
                claimsGroupedByKilobytes = claimsGroupedByKilobytes.plus(it to emptyList())
            }
        }

        assertThat(normalDistributionBySize).hasSameSizeAs(claimsGroupedByKilobytes)

        val claimsDistribution = claimsGroupedByKilobytes
                .map { (key, value) -> SizeCount(size = key, count = value.size.toLong() + 1)  }
                .sortedBy { it.size }
                .map { it.count }
                .toLongArray()

        val normalDistribution = normalDistributionBySize
                .map { (key, value) -> SizeCount(size = key, count = value.size.toLong() + 1)  }
                .sortedBy { it.size }
                .map { it.count }
                .toLongArray()

        val result = ChiSquareTest().chiSquareTestDataSetsComparison(claimsDistribution, normalDistribution)

        println()
    }

    data class SizeCount(
            val size: Int,
            val count: Long
    )

    private val List<Double>.normalDistribution: DoubleNormalDistribution get() {

        val average = this.average()

        return DoubleNormalDistribution(
                average = average,
                minimum = min() ?: 0.0,
                maximum = max() ?: 0.0,
                mode = 0.0,
                median = 0.0,
                standardDeviation = map { member -> (member - average).square() }.average().sqrt()
        )
    }

    private fun List<Double>.kolmogorovSmirnovTest(distribution: GenericDistribution): KSResults {
        val results = DistributionTest.kolmogorov_smirnov_test(this.toDoubleArray(), distribution)

        return KSResults(testStatistic = results[0], pValue = results[1])
    }

    data class KSResults(
            val testStatistic: Double,
            val pValue: Double
    )

}