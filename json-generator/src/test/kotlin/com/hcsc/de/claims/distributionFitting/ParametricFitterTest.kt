package com.hcsc.de.claims.distributionFitting

import com.hcsc.de.claims.helpers.ceilingOnEven
import com.hcsc.de.claims.results.succeedsAnd
import com.hcsc.de.claims.visualize
import net.sourceforge.jdistlib.Gamma
import net.sourceforge.jdistlib.LogNormal
import net.sourceforge.jdistlib.Normal
import net.sourceforge.jdistlib.Weibull
import org.assertj.core.api.KotlinAssertions.assertThat
import org.assertj.core.data.Percentage
import org.junit.Test

abstract class ParametricFitterTest {

    abstract val subject: ParametricFitter

    @Test
    fun `it can return the Weibull parameters for a group of doubles`() {

        val weibullDistributionCurve = Weibull(1.7, 20.0)

        val list = List(10000) { weibullDistributionCurve.random() }

        subject.weibullParameters(list) succeedsAnd { (shape, scale) ->

            assertThat(shape).isCloseTo(2.0, Percentage.withPercentage(5.0))
            assertThat(scale).isCloseTo(100.0, Percentage.withPercentage(5.0))
        }
    }

    @Test
    fun `it can return the Gamma parameters for a group of doubles`() {

        val gammaDistributionCurve = Gamma(180000.0, 15000.0)

        val list = List(1000) { gammaDistributionCurve.random() }

        subject.gammaParameters(list) succeedsAnd { (shape, scale) ->

            assertThat(shape).isCloseTo(180000.0, Percentage.withPercentage(10.0))
            assertThat(scale).isCloseTo(15000.0, Percentage.withPercentage(10.0))
        }
    }

    @Test
    fun `it can return the lognormal parameters for a group of doubles`() {

        val distributionCurve = LogNormal(1.0, 1.0)

        val list = List(1000) { distributionCurve.random() }

        subject.lognormalParameters(list) succeedsAnd { (a, b) ->

            assertThat(a).isCloseTo(1.0, Percentage.withPercentage(5.0))
            assertThat(b).isCloseTo(1.0, Percentage.withPercentage(5.0))
        }
    }

    @Test
    fun `it can return the mean and standard deviation for a list of doubles`() {

        val distributionCurve = Normal(2000.0, 100.0)

        val list = List(1000) { distributionCurve.random() }
                .map(Double::ceilingOnEven)
                .map(Double::toInt)
                .map(Int::toDouble)

        val normalParameters = subject.normalParameters(list)

        normalParameters succeedsAnd { (shape, scale) ->

            assertThat(shape).isCloseTo(2000.0, Percentage.withPercentage(10.0))
            assertThat(scale).isCloseTo(100.0, Percentage.withPercentage(10.0))
        }
    }
}