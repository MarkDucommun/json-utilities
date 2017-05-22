package com.hcsc.de.claims.jsonSizing

import com.fasterxml.jackson.databind.ObjectMapper
import com.hcsc.de.claims.fileReaders.RawByteStringFileReader
import com.hcsc.de.claims.helpers.*
import net.sourceforge.jdistlib.Normal
import net.sourceforge.jdistlib.disttest.DistributionTest
import net.sourceforge.jdistlib.generic.GenericDistribution
import org.apache.commons.math3.distribution.PoissonDistribution
import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest
import org.junit.Test
import java.io.FileWriter

class JsonSizeAnalyzerIntegrationTest {

    @Test
    fun `it reads in some files and creates a size analysis`() {

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

        val jsonSizeAnalyzer = JsonSizeAnalyzer()

        val result = jsonSizeAnalyzer.generateJsonSizeOverview(listOfJsonSizes).blockingGet()

        val overview = (result as Success).content

        val listOfTopLevelSizes = listOfJsonSizes.map { it.size.toDouble() }.toDoubleArray()

        val normal = Normal(overview.size.average.toDouble(), overview.size.standardDeviation)

        val results = DistributionTest.kolmogorov_smirnov_test(listOfTopLevelSizes, normal).toList()

        val varianceFromNormality = results.distribution

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
            }
//            is Failure -> throw RuntimeException(result.content)
//        }
//    }

    private val List<Double>.distribution: DoubleDistribution get() {

        val average = this.average()

        return DoubleDistribution(
                average = average,
                minimum = min() ?: 0.0,
                maximum = max() ?: 0.0,
                standardDeviation = map { member -> (member - average).square() }.average().sqrt()
        )
    }
}