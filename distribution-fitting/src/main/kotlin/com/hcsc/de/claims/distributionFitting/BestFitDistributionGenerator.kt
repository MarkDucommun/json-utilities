package com.hcsc.de.claims.distributionFitting

import com.hcsc.de.claims.distributions.*
import com.hcsc.de.claims.distributions.binDistributions.BinDistribution
import com.hcsc.de.claims.distributions.bins.BinWithMembers
import com.hcsc.de.claims.distributions.generation.DistributionGenerator
import com.hcsc.de.claims.distributions.generation.DistributionProfile
import com.hcsc.de.claims.distributions.generation.doubleNormalDistribution
import com.hcsc.de.claims.distributions.generation.minimizedBinSizeBinDistribution
import com.hcsc.de.claims.distributions.parametric.DoubleParametricDistribution
import com.hcsc.de.claims.math.helpers.ceiling
import com.hcsc.de.claims.math.helpers.median
import com.hcsc.de.claims.math.helpers.mode
import com.hcsc.de.claims.math.helpers.sqrt
import com.hcsc.de.claims.results.*
import net.sourceforge.jdistlib.disttest.DistributionTest
import java.util.*

class BestFitDistributionGenerator(
        private val parametricFitter: ParametricFitter
) : DistributionGenerator<Double> {

    val random = Random()

    override fun profile(list: List<Double>): Result<String, DistributionProfile<Double>> {

        return profileWith(
                list = list,
                distributionCreators = listOf(deepNonParametricDistributionGenerator)
        )
    }

    fun profileWith(
            list: List<Double>,
            distributionCreators: List<(List<Double>) -> Result<String, Randomable<Double>>>
    ): Result<String, DistributionProfile<Double>> {

        val doubleList = list.map { it }

        val results = if (doubleList.size > 1000) {

            // TODO ensure correct sample size for statistical significance
            val randomSample = List(100) { doubleList[random.nextInt(doubleList.size)] }

            distributionCreators.map { fn ->

                randomSample.generateDistribution(fn).map {

                    DistributionGeneratorAndPValue(distribution = fn, pValue = it.pValue)
                }
            }.traverse().map { it.sortedByDescending { it.pValue } }.flatMap { it ->

                it.fold(Failure<String, DistributionAndPValue>("Something") as Result<String, DistributionAndPValue>) { acc, (fn, _) ->

                    if (acc is Success && acc.content.pValue > 0.8) acc else doubleList.generateDistribution(fn)
                }
            }

        } else {

            distributionCreators.map { fn ->
                doubleList.generateDistribution(fn)
            }.traverse().map { it.sortedByDescending { it.pValue } }.map { it.first() }
        }

        return results.map { (distribution, pValue) ->

            DistributionProfile(
                    pValue = pValue,
                    distribution = DoubleParametricDistribution(
                            average = doubleList.average(),
                            maximum = doubleList.max() ?: 0.0,
                            minimum = doubleList.min() ?: 0.0,
                            mode = doubleList.mode(),
                            median = doubleList.median(),
                            distribution = distribution
                    )
            )
        }
    }

    val gammaDistributionGenerator: (List<Double>) -> Result<String, Randomable<Double>> = { parametricFitter.gammaDistribution(it) }

    val lognormalDistributionGenerator: (List<Double>) -> Result<String, Randomable<Double>> = { parametricFitter.lognormalDistribution(it) }

    val weibullDistributionGenerator: (List<Double>) -> Result<String, Randomable<Double>> = { parametricFitter.weibullDistribution(it) }

    val normalDistributionGenerator: (List<Double>) -> Result<String, Randomable<Double>> = { Success(it.doubleNormalDistribution) }

    val deepNonParametricDistributionGenerator: (List<Double>) -> Result<String, Randomable<Double>> = {


        val unknownVariableBinWidthDistribution: BinDistribution<Double, BinWithMembers<Double>> = it.minimizedBinSizeBinDistribution(it.size.toDouble().sqrt().ceiling().toInt())

//        val result = unknownVariableBinWidthDistribution.toDistributedBinDistribution()
//
//        when (result) {
//            is Success -> Success<String, Randomable<Double>>(result.content)
//            is Failure -> Failure(result.content)
//        }

        TODO()
    }

    val shallowNonParametricDistributionGenerator: (List<Double>) -> Result<String, Randomable<Double>> = {

        Success(it.minimizedBinSizeBinDistribution(it.size.toDouble().sqrt().ceiling().toInt()))
    }

    data class DistributionGeneratorAndPValue(
            val distribution: (List<Double>) -> Result<String, Randomable<Double>>,
            val pValue: Double
    )

    data class DistributionAndPValue(
            val distribution: Randomable<Double>,
            val pValue: Double
    )

    private fun List<Double>.generateDistribution(
            distributionFn: (List<Double>) -> Result<String, Randomable<Double>>
    ): Result<String, DistributionAndPValue> {

        return distributionFn(this).map { randomable: Randomable<Double> ->

            val doubleArray = List(1000) { randomable.random() }.toDoubleArray()

            val pValue = DistributionTest.kolmogorov_smirnov_test(this.toDoubleArray(), doubleArray)[1]

            DistributionAndPValue(distribution = randomable, pValue = pValue)
        }
    }
}