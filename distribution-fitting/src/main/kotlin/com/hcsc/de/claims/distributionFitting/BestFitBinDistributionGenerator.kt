package com.hcsc.de.claims.distributionFitting

import com.hcsc.de.claims.distributions.Distribution
import com.hcsc.de.claims.distributions.binDistributions.BinDistribution
import com.hcsc.de.claims.distributions.binDistributions.DistributedBinDistribution
import com.hcsc.de.claims.distributions.bins.BinWithMembers
import com.hcsc.de.claims.distributions.bins.DistributedBin
import com.hcsc.de.claims.distributions.generation.DistributionGenerator
import com.hcsc.de.claims.distributions.generation.DistributionProfile
import com.hcsc.de.claims.results.Result
import com.hcsc.de.claims.results.map
import com.hcsc.de.claims.results.traverse

class BestFitBinDistributionGenerator<numberType : Number> : DistributionGenerator<
        numberType,
        DistributionProfile<numberType, Distribution<numberType>>,
        Distribution<numberType>
        > {

    override fun profile(list: List<numberType>): Result<String, DistributionProfile<numberType, Distribution<numberType>>> {

        TODO("not implemented")
    }


}

fun BinDistribution<Double, BinWithMembers<Double>>.toDistributedBinDistribution(): Result<String, DistributedBinDistribution<Double>> {

    val generator = BestFitDistributionGenerator(FitDistrPlus)

    return bins
            .map { bin ->

                val profileWith = generator
                        .profileWith(
                                list = bin.members,
                                distributionCreators = listOf(
                                        generator.normalDistributionGenerator,
                                        generator.gammaDistributionGenerator,
                                        generator.weibullDistributionGenerator,
                                        generator.lognormalDistributionGenerator
                                ))
                profileWith
                        .map {
                            DistributedBin(
                                    size = bin.size,
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