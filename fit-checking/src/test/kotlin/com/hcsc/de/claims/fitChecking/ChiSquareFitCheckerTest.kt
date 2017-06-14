package com.hcsc.de.claims.fitChecking

import com.hcsc.de.claims.distributionFitting.GenericDistributionRandomable
import com.hcsc.de.claims.distributionFitting.Montreal.randomVariateGen
import com.hcsc.de.claims.histogrammer.visualize
import com.hcsc.de.claims.results.get
import com.hcsc.de.claims.results.succeedsAnd
import net.sourceforge.jdistlib.Normal
import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Ignore
import org.junit.Test
import umontreal.ssj.randvar.NormalGen

class ChiSquareFitCheckerTest {

    val subject = ChiSquareFitChecker<Double>()

    @Test
    @Ignore("No assertions, do better")
    fun `it returns a high pValue for values from the same distribution`() {

        listOf(1000, 10000, 100000).forEach { listSize ->

            val randomable = GenericDistributionRandomable(Normal(100.0, 20.0))

            val firstList = List(listSize) { randomable.random() }

            val secondList = List(listSize) { randomable.random() }

            subject.check(firstList, secondList) succeedsAnd {

                if (it < 0.5) {
                    visualize(firstList, secondList, 3)
                }

                println("$listSize: $it")

                assertThat(it).isGreaterThan(0.5)
            }
        }
    }

    @Test
    @Ignore("No assertions, do better")
    fun `it returns low pValue for values from different distributions`() {

        listOf(100000).forEach { listSize ->

            val randomableOne = GenericDistributionRandomable(Normal(100.0, 20.0))

            val randomableTwo = randomVariateGen<NormalGen>(100.0, 20.0).get

            val randomableThree = GenericDistributionRandomable(Normal(100.0, 20.0))

            val firstList = List(listSize) { randomableOne.random() }

            val secondList = List(listSize) { randomableTwo.random() }

            val thirdList = List(listSize) { randomableThree.random() }

            println("list size: $listSize - ")

            listOf(5, 7, 10, 13, 15, 20).forEach { minimumBinSize ->

                println("Minimum Bin Size: $minimumBinSize")

                println("One - Two")
                subject.check(firstList, secondList, minimumBinSize).get

                println("One - Three")
                subject.check(firstList, thirdList, minimumBinSize).get

                println("Two - Three")
                subject.check(thirdList, secondList, minimumBinSize).get
            }

            println()
        }
    }
}