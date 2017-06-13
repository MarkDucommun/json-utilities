package com.hcsc.de.claims.distributions

import com.hcsc.de.claims.distributionFitting.FitDistrPlus
import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.map
import com.hcsc.de.claims.results.traverse
import java.util.*

data class DistributedBinDistribution<out numberType : Number>(
        override val average: numberType,
        override val minimum: numberType,
        override val maximum: numberType,
        override val mode: numberType,
        override val median: numberType,
        override val bins: List<DistributedBin<numberType>>
) : BinDistribution<numberType> {

    private val random = Random()

    override fun random(): numberType {

        return bins[random.nextInt(bins.size)].random()
    }
}

fun UnknownVariableBinWidthDistribution<Double>.toDistributedBinDistribution(): Result<String, DistributedBinDistribution<Double>> {

    val generator = RealDistributionGenerator(FitDistrPlus)

    return bins
            .map { bin ->
                generator
                        .profileWith(
                                list = bin.members,
                                distributionCreators = listOf(
                                        generator.normalDistributionGenerator,
                                        generator.gammaDistributionGenerator,
                                        generator.weibullDistributionGenerator,
                                        generator.lognormalDistributionGenerator
                                ))
                        .map {
                            DistributedBin(
                                    count = bin.count,
                                    startValue = bin.startValue,
                                    distribution = it.distribution,
                                    pValue = it.pValue
                            )
                        }
            }
            .traverse()
            .map {
                DistributedBinDistribution(
                        average = average,
                        minimum = minimum,
                        maximum = maximum,
                        median = median,
                        mode = mode,
                        bins = it
                )
            }
}