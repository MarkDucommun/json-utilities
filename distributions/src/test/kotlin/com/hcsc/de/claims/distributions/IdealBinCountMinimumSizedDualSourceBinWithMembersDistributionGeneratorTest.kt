package com.hcsc.de.claims.distributions

import com.hcsc.de.claims.collection.helpers.asNonEmptyList
import com.hcsc.de.claims.collection.helpers.nonEmptyListOf
import com.hcsc.de.claims.distributions.bins.IntBinWithMembers
import com.hcsc.de.claims.distributions.generation.DistributionRequest.DualDistributionRequest.IdealBinCountDualDistributionRequest
import com.hcsc.de.claims.distributions.generation.IdealBinCountMinimumSizedDualMemberBinDistributionGenerator
import com.hcsc.de.claims.results.succeedsAnd
import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Test

class IdealBinCountMinimumSizedDualSourceBinWithMembersDistributionGeneratorTest {

    val subject = IdealBinCountMinimumSizedDualMemberBinDistributionGenerator(toType = { Math.round(this).toInt() })

    @Test
    fun `it generates the same bins for both lists when the lists are the same`() {

        val listOfOnes = List(5) { 1 }

        subject.create(IdealBinCountDualDistributionRequest(
                list = listOfOnes,
                listTwo = listOfOnes
        )) succeedsAnd { (_, dist) ->

            val expectedBin = IntBinWithMembers(members = nonEmptyListOf(1, 1, 1, 1, 1))

            assertThat(dist.binCount).isEqualTo(1)
            assertThat(dist.bins.first().sourceOneBin).isEqualToComparingFieldByFieldRecursively(expectedBin)
            assertThat(dist.bins.first().sourceTwoBin).isEqualToComparingFieldByFieldRecursively(expectedBin)
        }
    }

    @Test
    fun `it properly bins two exclusive sources`() {

        val listOfOnes = List(50) { 1 }
        val listOfTwos = List(50) { 2 }

        subject.create(IdealBinCountDualDistributionRequest(
                list = listOfOnes,
                listTwo = listOfTwos
        )) succeedsAnd { (_, dist) ->

            assertThat(dist.binCount).isEqualTo(1)
            assertThat(dist.bins.first().width).isEqualTo(1)
            assertThat(dist.bins.first().startValue).isEqualTo(1)
            assertThat(dist.bins.first().endValue).isEqualTo(2)

            assertThat(dist.bins.first().sourceOneBin.members).isEqualTo(listOfOnes)
            assertThat(dist.bins.first().sourceOneBin.width).isEqualTo(0)
            assertThat(dist.bins.first().sourceOneBin.startValue).isEqualTo(1)
            assertThat(dist.bins.first().sourceOneBin.endValue).isEqualTo(1)

            assertThat(dist.bins.first().sourceTwoBin.members).isEqualTo(listOfTwos)
            assertThat(dist.bins.first().sourceTwoBin.width).isEqualTo(0)
            assertThat(dist.bins.first().sourceTwoBin.startValue).isEqualTo(2)
            assertThat(dist.bins.first().sourceTwoBin.endValue).isEqualTo(2)
        }
    }

    @Test
    fun `it properly bins when all members of source-one are below the middle of the bounds of all members`() {

        subject.create(IdealBinCountDualDistributionRequest(
                list = List(50) { 1 }.plus(List(50) { 2 }),
                listTwo = List(50) { 1 }.plus(List(50) { 6 }),
                minimumBinSize = 1
        )) succeedsAnd { (_, dist) ->

            assertThat(dist.binCount).isEqualTo(2)

            assertThat(dist.bins[0].members).containsExactlyElementsOf(List(100) { 1 })
            assertThat(dist.bins[1].members).containsExactlyElementsOf(List(50) { 2 }.plus(List(50) { 6 }))
        }
    }

    @Test
    fun `it properly bins when all members of source-two are below the middle of the bounds of all members`() {

        subject.create(IdealBinCountDualDistributionRequest(
                list = List(50) { 1 }.plus(List(50) { 6 }),
                listTwo = List(50) { 1 }.plus(List(50) { 2 }),
                minimumBinSize = 1
        )) succeedsAnd { (_, dist) ->

            assertThat(dist.binCount).isEqualTo(2)

            assertThat(dist.bins[0].members).containsExactlyElementsOf(List(50) { 2 }.plus(List(50) { 6 }))
            assertThat(dist.bins[1].members).containsExactlyElementsOf(List(100) { 1 })
        }
    }

    @Test
    fun `it properly bins when all members of source-one are above the middle of the bounds of all members`() {

        subject.create(IdealBinCountDualDistributionRequest(
                list = List(50) { 5 }.plus(List(50) { 6 }),
                listTwo = List(50) { 1 }.plus(List(50) { 6 }),
                minimumBinSize = 1
        )) succeedsAnd { (_, dist) ->

            assertThat(dist.binCount).isEqualTo(2)

            assertThat(dist.bins[0].members).containsExactlyInAnyOrder(*List(50) { 1 }.plus(List(50) { 5 }).toTypedArray())
            assertThat(dist.bins[1].members).containsExactlyInAnyOrder(*List(100) { 6 }.toTypedArray())
        }
    }

    @Test
    fun `it respects the minimum bin size`() {
        subject.create(IdealBinCountDualDistributionRequest(
                list = List(6) { 5 }.plus(List(4) { 6 }),
                listTwo = List(6) { 1 }.plus(List(4) { 6 }),
                minimumBinSize = 5
        )) succeedsAnd { (_, dist) ->
            assertThat(dist.binCount).isEqualTo(1)
        }
    }

    @Test
    fun `can be split into two normal UnknownVariableBinWidthDistributions`() {

        val listOfOnes = List(5) { 1 }.plus(List(5) { 3 })
        val listOfTwos = List(10) { 2 }.plus(List(5) { 7 })

        subject.create(IdealBinCountDualDistributionRequest(
                list = listOfOnes,
                listTwo = listOfTwos
        )) succeedsAnd { (_, dist) ->

            assertThat(dist.bins.size).isEqualTo(2)
            assertThat(dist.bins.first().sourceOneBin.size).isEqualTo(5)
            assertThat(dist.bins.first().sourceTwoBin.size).isEqualTo(10)
            assertThat(dist.bins.last().sourceOneBin.size).isEqualTo(5)
            assertThat(dist.bins.last().sourceTwoBin.size).isEqualTo(5)
        }
    }
}