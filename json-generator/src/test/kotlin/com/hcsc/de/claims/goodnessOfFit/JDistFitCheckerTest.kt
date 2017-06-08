package com.hcsc.de.claims.goodnessOfFit

import com.hcsc.de.claims.distributionFitting.GenericDistributionRandomable
import com.hcsc.de.claims.succeedsAnd
import net.sourceforge.jdistlib.Normal
import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Test

class JDistFitCheckerTest {

    val subject = JDistFitChecker<Double>()

    @Test
    fun `it returns a high pValue for values from the same distribution`() {

        val randomable = GenericDistributionRandomable(Normal(100.0, 20.0))

        val firstList = List(10000) { randomable.random() }

        val secondList = List(10000) { randomable.random() }

        subject.check(firstList, secondList) succeedsAnd {
            assertThat(it).isGreaterThan(0.5)
        }
    }

    @Test
    fun `it returns low pValue for values from different distributions`() {

        val randomableOne = GenericDistributionRandomable(Normal(100.0, 20.0))

        val randomableTwo = GenericDistributionRandomable(Normal(2.0, 20.0))

        val firstList = List(10000) { randomableOne.random() }

        val secondList = List(10000) { randomableTwo.random() }

        subject.check(firstList, secondList) succeedsAnd {
            println(it)
            assertThat(it).isLessThan(0.5)
        }
    }
}