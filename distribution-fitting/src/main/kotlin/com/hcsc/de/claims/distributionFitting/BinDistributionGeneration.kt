package com.hcsc.de.claims.distributionFitting

import com.hcsc.de.claims.distributions.DistributedBin
import com.hcsc.de.claims.distributions.DistributedBinDistribution
import com.hcsc.de.claims.distributions.UnknownVariableBinWidthDistribution
import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.map
import com.hcsc.de.claims.results.traverse

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