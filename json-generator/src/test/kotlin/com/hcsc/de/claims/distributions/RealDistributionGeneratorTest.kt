package com.hcsc.de.claims.distributions

import com.hcsc.de.claims.distributionFitting.FitDistrPlus
import com.hcsc.de.claims.helpers.ceiling
import com.hcsc.de.claims.succeedsAnd
import com.hcsc.de.claims.visualize
import net.sourceforge.jdistlib.Gamma
import net.sourceforge.jdistlib.LogNormal
import net.sourceforge.jdistlib.Weibull
import net.sourceforge.jdistlib.disttest.DistributionTest
import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Test

class RealDistributionGeneratorTest {

    val subject = RealDistributionGenerator(parametricFitter = FitDistrPlus)

    val size = 10000

    @Test
    fun `it returns a distribution shaped like weibull if that is what we passed in`() {

        val distribution = Weibull(100.0, 2000.0)

        val list = List(size) { distribution.random() }

        subject.profile(list) succeedsAnd { distributionProfile ->

            val generatedDist = distributionProfile.distribution

            val newList = List(size) { distribution.random() }

            val generatedList = List(size) { generatedDist.random() }

            val result = DistributionTest.kolmogorov_smirnov_test(newList.toDoubleArray(), generatedList.toDoubleArray())
            //visualize(newList.map{it.ceiling()}, generatedList.map{it.ceiling()})
            assertThat(result[1]).isGreaterThan(0.05)
        }
    }

    @Test
    fun `it returns a distribution shaped like gamma if that is what we passed in`() {

        val distribution = Gamma(100.0, 20.0)

        val list = List(size) { distribution.random() }

        subject.profile(list) succeedsAnd { distributionProfile ->

            val generatedDist = distributionProfile.distribution

            val newList = List(size) { distribution.random() }

            val generatedList = List(size) { generatedDist.random() }

            val result = DistributionTest.kolmogorov_smirnov_test(newList.toDoubleArray(), generatedList.toDoubleArray())

            assertThat(result[1]).isGreaterThan(0.05)
        }
    }

    @Test
    fun `it returns a distribution shaped like lognormal if that is what we passed in`() {

        val distribution = LogNormal(100.0, 20.0)

        val list = List(size) { distribution.random() }

        subject.profile(list) succeedsAnd { distributionProfile ->

            val generatedDist = distributionProfile.distribution

            val newList = List(size) { distribution.random() }

            val generatedList = List(size) { generatedDist.random() }

            val result = DistributionTest.kolmogorov_smirnov_test(newList.toDoubleArray(), generatedList.toDoubleArray())

            assertThat(result[1]).isGreaterThan(0.05)
        }
    }
}