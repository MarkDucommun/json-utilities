package com.hcsc.de.claims.distributions

import com.hcsc.de.claims.helpers.*
import com.hcsc.de.claims.renjinWrapper.R
import net.sourceforge.jdistlib.disttest.DistributionTest
import java.util.*

class RealDistributionGenerator(
        private val r: R
) : DistributionGenerator<Double> {

    val random = Random()

    override fun profile(list: List<Double>): Result<String, DistributionProfile<Double>> {

        val doubleList = list.map { it }

        val results = if (doubleList.size > 1000) {

            val randomSample = List(100) { doubleList[random.nextInt(doubleList.size)] }

            listOf(
                    normalDistributionGenerator,
                    gammaDistributionGenerator,
                    weibullDistributionGenerator,
                    lognormalDistributionGenerator
            ).map { fn ->

                randomSample.generateDistribution(fn).map {

                    DistributionGeneratorAndPValue(distribution = fn, pValue = it.pValue)
                }
            }.traverse().map { it.sortedByDescending { it.pValue } }.flatMap { it ->

                it.fold(Failure<String, DistributionAndPValue>("Something") as Result<String, DistributionAndPValue>) { acc, (fn, _) ->

                    if (acc is Success && acc.content.pValue > 0.8) acc else doubleList.generateDistribution(fn)
                }
            }

        } else {

            listOf(
                    normalDistributionGenerator,
                    gammaDistributionGenerator,
                    weibullDistributionGenerator,
                    lognormalDistributionGenerator
            ).map { fn ->
                doubleList.generateDistribution(fn)
            }.traverse().map { it.sortedByDescending { it.pValue } }.map { it.first() }
        }

        return results.map { (distribution, pValue) ->

            DistributionProfile(
                    pValue = pValue,
                    distribution = ParametricDistribution(
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

    val gammaDistributionGenerator: (List<Double>) -> Result<String, Randomable<Double>> = { r.gammaDistribution(it) }
    val lognormalDistributionGenerator: (List<Double>) -> Result<String, Randomable<Double>> = { r.lognormalDistribution(it) }
    val weibullDistributionGenerator: (List<Double>) -> Result<String, Randomable<Double>> = { r.weibullDistribution(it) }
    val normalDistributionGenerator: (List<Double>) -> Result<String, Randomable<Double>> = { Success(it.normalDoubleDistribution) }

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