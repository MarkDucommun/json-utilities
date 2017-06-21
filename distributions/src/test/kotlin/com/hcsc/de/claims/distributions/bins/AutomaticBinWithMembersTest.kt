package com.hcsc.de.claims.distributions.bins

import com.hcsc.de.claims.collection.helpers.nonEmptyListOf
import com.hcsc.de.claims.results.failsAndShouldReturn
import com.hcsc.de.claims.results.succeedsAnd
import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Test

class AutomaticBinWithMembersTest {

    @Test
    fun `it splits members into two bins around a split point`() {

        val subject = IntBinWithMembers(members = nonEmptyListOf(1, 3))

        val result = subject.split(splitPoint = 2)

        result succeedsAnd { (upper, lower) ->
            assertThat(lower.members).containsExactlyInAnyOrder(1)
            assertThat(upper.members).containsExactlyInAnyOrder(3)
        }
    }

    @Test
    fun `it puts members that equal the split point into the lower bin`() {

        val subject = IntBinWithMembers(members = nonEmptyListOf(1, 2, 3))

        subject.split(splitPoint = 2) succeedsAnd { (upper, lower) ->
            assertThat(lower.members).containsExactlyInAnyOrder(1, 2)
            assertThat(upper.members).containsExactlyInAnyOrder(3)
        }
    }

    @Test
    fun `it returns a UpperSplitFailure when the splitPoint is greater than or equal to all the members`() {

        val subject = IntBinWithMembers(members = nonEmptyListOf(1, 2))

        val result = subject.split(splitPoint = 2)

        result failsAndShouldReturn UpperSplitFailure
    }

    @Test
    fun `it returns a LowerSplitFailure when the splitPoint is less than all the members`() {

        val subject = IntBinWithMembers(members = nonEmptyListOf(1, 2))

        val result = subject.split(splitPoint = 0)

        result failsAndShouldReturn LowerSplitFailure
    }
}