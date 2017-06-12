package com.hcsc.de.claims.distributions

import com.hcsc.de.claims.failsWithMessage
import com.hcsc.de.claims.get
import com.hcsc.de.claims.helpers.map
import com.hcsc.de.claims.succeedsAndShouldReturn
import org.assertj.core.api.KotlinAssertions.assertThat
import org.assertj.core.data.Percentage
import org.junit.Test

class RatioProbabilityTest {

    @Test
    fun `it fails if it is created with a value greater than one or less than zero`() {

        RatioProbability.create(1.1) failsWithMessage "Invalid - value out of bounds"
    }

    @Test
    fun `it succeeds with a valid value`() {

        val expectedRatioProbability = DoubleWithinZeroAndOne.create(value = 1.0).map { RatioProbability(ratio = it) }.get

        RatioProbability.create(1.0) succeedsAndShouldReturn expectedRatioProbability
    }

    @Test
    fun `it can generate the right probability`() {

        val ratioProbability = RatioProbability.create(0.5).get

        val trueCount = List(10000) { ratioProbability.nextChance() }.fold(0) { acc, bool -> if (bool) acc + 1 else acc }

        assertThat(trueCount).isCloseTo(5000, Percentage.withPercentage(5.0))
    }
}