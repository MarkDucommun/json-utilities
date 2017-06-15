package com.hcsc.de.claims.distributions

import com.hcsc.de.claims.distributions.binDistributions.IntBinWithMembersDistribution
import com.hcsc.de.claims.distributions.bins.IntBinWithMembers
import com.hcsc.de.claims.distributions.generation.DistributionProfile
import com.hcsc.de.claims.distributions.generation.DistributionRequest
import com.hcsc.de.claims.distributions.generation.FixedWidthBinCountDistributionGenerator
import com.hcsc.de.claims.distributions.generation.FixedWidthBinDistributionGenerator
import com.hcsc.de.claims.results.succeedsAndShouldReturn
import org.junit.Test

class FixedWidthBinCountDistributionGeneratorTest {

    val subject = FixedWidthBinCountDistributionGenerator(toType = Double::toInt)

    @Test
    fun `it creates a simple fixed distribution`() {

        subject.create(DistributionRequest.FixedWidthBinCountDistributionRequest(
                listOf(1, 2, 3),
                binCount = 3
        )) succeedsAndShouldReturn DistributionProfile(
                distribution = IntBinWithMembersDistribution(listOf(
                        IntBinWithMembers(listOf(1)),
                        IntBinWithMembers(listOf(2)),
                        IntBinWithMembers(listOf(3))
                )),
                pValue = 1.0
        )
    }
}