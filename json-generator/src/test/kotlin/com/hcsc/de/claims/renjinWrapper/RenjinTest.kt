package com.hcsc.de.claims.renjinWrapper

import com.hcsc.de.claims.failsAnd
import com.hcsc.de.claims.succeedsAnd
import net.sourceforge.jdistlib.Weibull
import org.assertj.core.api.KotlinAssertions.assertThat
import org.assertj.core.data.Percentage
import org.junit.Test

class RenjinTest {


    val subject = Renjin

    @Test
    fun `it can return the Weibull parameters for a group of doubles`() {

        val weibullDistributionCurve = Weibull(2.0, 100.0)

        val list = List(1000) { weibullDistributionCurve.random() }

        subject.weibullParameters(list) succeedsAnd { (shape, scale) ->

            assertThat(shape).isCloseTo(2.0, Percentage.withPercentage(2.0))
            assertThat(scale).isCloseTo(100.0, Percentage.withPercentage(2.0))
        }
    }

    @Test
    fun `it fails gracefully`() {

        val list = List(1000) { -1.0 }

        subject.weibullParameters(list) failsAnd { message ->

               assertThat(message).contains("must be positive")
        }
    }
}