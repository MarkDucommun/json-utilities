package com.hcsc.de.claims.distributions.bins

import com.hcsc.de.claims.collection.helpers.nonEmptyListOf
import com.hcsc.de.claims.results.failsAndShouldReturn
import com.hcsc.de.claims.results.succeedsAnd
import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Test
import org.mockito.internal.matchers.Not

class AutomaticDualSourceBinWithMembersTest {

    @Test
    fun `it splits two sources into two shared bins around a split point`() {

        val subject = dualSourceBinWithMembers(
                sourceOne = nonEmptyListOf(1, 3),
                sourceTwo = nonEmptyListOf(1, 3)
        )

        val result = subject.split(splitPoint = 2)

        result succeedsAnd { (upper, lower) ->
            assertThat(lower.members).containsExactlyInAnyOrder(1, 1)
            assertThat(upper.members).containsExactlyInAnyOrder(3, 3)
        }
    }

    @Test
    fun `it puts the members that equal the split point into the lower bin`() {

        val subject = dualSourceBinWithMembers(
                sourceOne = nonEmptyListOf(1, 2, 3),
                sourceTwo = nonEmptyListOf(1, 2, 3)
        )

        subject.split(splitPoint = 2) succeedsAnd { (upper, lower) ->
            assertThat(lower.members).containsExactlyInAnyOrder(1, 1, 2, 2)
            assertThat(upper.members).containsExactlyInAnyOrder(3, 3)
        }
    }

    @Test
    fun `it fails when source-one upper bin fails`() {

        val subject = dualSourceBinWithMembers(
                sourceOne = nonEmptyListOf(1, 2),
                sourceTwo = nonEmptyListOf(1, 4)
        )

        subject.splitDualSourceBin(splitPoint = 3, minimumSourceBinSize = 1) failsAndShouldReturn SourceOneSplitFailureWithValue(UpperSplitFailure)
    }

    @Test
    fun `it fails when source-one lower bin fails`() {

        val subject = dualSourceBinWithMembers(
                sourceOne = nonEmptyListOf(4, 5),
                sourceTwo = nonEmptyListOf(1, 4)
        )

        subject.splitDualSourceBin(splitPoint = 3, minimumSourceBinSize = 1) failsAndShouldReturn SourceOneSplitFailureWithValue(LowerSplitFailure)
    }

    @Test
    fun `it fails when source-two upper bin fails`() {

        val subject = dualSourceBinWithMembers(
                sourceOne = nonEmptyListOf(1, 5),
                sourceTwo = nonEmptyListOf(1, 2)
        )

        subject.splitDualSourceBin(splitPoint = 3, minimumSourceBinSize = 1) failsAndShouldReturn SourceTwoSplitFailureWithValue(UpperSplitFailure)
    }

    @Test
    fun `it fails when source-two lower bin fails`() {

        val subject = dualSourceBinWithMembers(
                sourceOne = nonEmptyListOf(1, 4),
                sourceTwo = nonEmptyListOf(4, 5)
        )

        subject.splitDualSourceBin(splitPoint = 3, minimumSourceBinSize = 1) failsAndShouldReturn SourceTwoSplitFailureWithValue(LowerSplitFailure)
    }

    @Test
    fun `it fails when both upper sources fail`() {

        val subject = dualSourceBinWithMembers(
                sourceOne = nonEmptyListOf(1, 2),
                sourceTwo = nonEmptyListOf(1, 2)
        )

        subject.splitDualSourceBin(splitPoint = 3, minimumSourceBinSize = 1) failsAndShouldReturn BothSourceSplitFailureWithValues(UpperSplitFailure, UpperSplitFailure)
    }

    @Test
    fun `it fails when both lower sources fail`() {

        val subject = dualSourceBinWithMembers(
                sourceOne = nonEmptyListOf(3, 4),
                sourceTwo = nonEmptyListOf(3, 4)
        )

        subject.splitDualSourceBin(splitPoint = 2, minimumSourceBinSize = 1) failsAndShouldReturn BothSourceSplitFailureWithValues(LowerSplitFailure, LowerSplitFailure)
    }

    @Test
    fun `it fails when source-one lower bin and source-two upper bin fail`() {

        val subject = dualSourceBinWithMembers(
                sourceOne = nonEmptyListOf(4, 5),
                sourceTwo = nonEmptyListOf(1, 2)
        )

        subject.splitDualSourceBin(splitPoint = 3, minimumSourceBinSize = 1) failsAndShouldReturn BothSourceSplitFailureWithValues(LowerSplitFailure, UpperSplitFailure)
    }

    @Test
    fun `it fails when source-one upper bin and source-two lower bin fail`() {

        val subject = dualSourceBinWithMembers(
                sourceOne = nonEmptyListOf(1, 2),
                sourceTwo = nonEmptyListOf(4, 5)
        )

        subject.splitDualSourceBin(splitPoint = 3, minimumSourceBinSize = 1) failsAndShouldReturn BothSourceSplitFailureWithValues(UpperSplitFailure, LowerSplitFailure)
    }

    @Test
    fun `it respects the minimum bin size when both cant be split`() {

        val subject = dualSourceBinWithMembers(
                sourceOne = nonEmptyListOf(1, 3),
                sourceTwo = nonEmptyListOf(1, 3)
        )

        subject.splitDualSourceBin(splitPoint = 2, minimumSourceBinSize = 2) failsAndShouldReturn BothSourceSplitFailureWithValues(NotEnoughMembersToSplitFailure, NotEnoughMembersToSplitFailure)
    }

    @Test
    fun `it respects the minimum bin size when upper cant be split`() {

        val subject = dualSourceBinWithMembers(
                sourceOne = nonEmptyListOf(1, 3),
                sourceTwo = nonEmptyListOf(1, 1, 3, 3)
        )

        subject.splitDualSourceBin(splitPoint = 2, minimumSourceBinSize = 2) failsAndShouldReturn SourceOneSplitFailureWithValue(NotEnoughMembersToSplitFailure)
    }

    @Test
    fun `it respects the minimum bin size when lower cant be split`() {

        val subject = dualSourceBinWithMembers(
                sourceOne = nonEmptyListOf(1, 1, 3, 3),
                sourceTwo = nonEmptyListOf(1, 3)
        )

        subject.splitDualSourceBin(splitPoint = 2, minimumSourceBinSize = 2) failsAndShouldReturn SourceTwoSplitFailureWithValue(NotEnoughMembersToSplitFailure)
    }

    @Test
    fun `it respects the minimum bin size`() {

        val subject = dualSourceBinWithMembers(
                sourceOne = nonEmptyListOf(1, 3),
                sourceTwo = nonEmptyListOf(1, 3)
        )

        subject.splitDualSourceBin(splitPoint = 2, minimumSourceBinSize = 2) failsAndShouldReturn BothSourceSplitFailureWithValues(NotEnoughMembersToSplitFailure, NotEnoughMembersToSplitFailure)
    }

    @Test
    fun `it respects the minimum bin size even more`() {

        val subject = dualSourceBinWithMembers(
                sourceOne = nonEmptyListOf(1, 1, 1, 3),
                sourceTwo = nonEmptyListOf(1, 1, 1, 3)
        )

        subject.splitDualSourceBin(splitPoint = 2, minimumSourceBinSize = 2) failsAndShouldReturn BothSourceSplitFailureWithValues(UpperSplitFailure, UpperSplitFailure)
    }
}
