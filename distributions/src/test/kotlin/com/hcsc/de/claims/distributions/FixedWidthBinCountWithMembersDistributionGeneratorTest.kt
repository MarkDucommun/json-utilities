package com.hcsc.de.claims.distributions

import com.hcsc.de.claims.collection.helpers.nonEmptyListOf
import com.hcsc.de.claims.distributions.binDistributions.IntFixedWidthBinWithMembersDistribution
import com.hcsc.de.claims.distributions.bins.IntBinWithMembers
import com.hcsc.de.claims.distributions.generation.DistributionProfile
import com.hcsc.de.claims.distributions.generation.DistributionRequest.FixedWidthBinCountDistributionRequest
import com.hcsc.de.claims.distributions.generation.FixedWidthBinCountWithMembersDistributionGenerator
import com.hcsc.de.claims.results.succeedsAndShouldReturn
import org.junit.Test

class FixedWidthBinCountWithMembersDistributionGeneratorTest {

    val subject = FixedWidthBinCountWithMembersDistributionGenerator(toType = Double::toInt)

    @Test
    fun `it creates a simple fixed distribution`() {

        subject.create(FixedWidthBinCountDistributionRequest(
                list = listOf(1, 2, 3),
                binCount = 3
        )) succeedsAndShouldReturn DistributionProfile(
                distribution = IntFixedWidthBinWithMembersDistribution(
                        bins = listOf(
                                IntBinWithMembers(nonEmptyListOf(1)),
                                IntBinWithMembers(nonEmptyListOf(2)),
                                IntBinWithMembers(nonEmptyListOf(3))
                        ),
                        binWidth = 1),
                pValue = 1.0
        )
    }
}