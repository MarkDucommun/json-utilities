package com.hcsc.de.claims.jsonSizing

import com.hcsc.de.claims.helpers.doOnThread
import com.hcsc.de.claims.succeedsAnd
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import net.sourceforge.jdistlib.*
import net.sourceforge.jdistlib.disttest.DistributionTest
import net.sourceforge.jdistlib.evd.Extreme
import net.sourceforge.jdistlib.generic.GenericDistribution
import org.apache.commons.math3.distribution.NormalDistribution
import org.apache.commons.math3.distribution.WeibullDistribution
import org.junit.Test
import org.rosuda.JRI.Rengine

class chiSquaredTestTest {

    @Test
    fun `it returns a high level of confidence for a simple data set`() {

        val normal = Weibull(3.0, 5.0)
        val anotherNormal = WeibullDistribution(3.0, 5.0)

        val list = List(1000000) { (normal.random()).toInt() }

        val otherList = List(1000000) { (anotherNormal.sample()).toInt() }

        DistributionPair(list, otherList).chiSquaredTest() succeedsAnd { (statistic, pValue) ->

            println("Chi Square - $statistic")
            println("P-Value - $pValue")

            println("--")
        }
    }

    @Test
    fun `it generates values that match the distribution it is created from`() {

//        val initialDistribution = Normal(180000000.0, 15000.0)
        val initialDistribution = Weibull(2.0, 10000.0)
//        val initialDistribution = F(5.0, 2.0)

        val initialList = List(10000) { (initialDistribution.random()).toInt() }

        val unknownDistribution = initialList.unknownVariableBinWidthDistribution(1000)

//        val rengine = Rengine(arrayOf("--vanilla"), false, null)

        val generatedList = List(500000) { unknownDistribution.random() }

        val initialDoubleArray = initialList.map(Int::toDouble).toDoubleArray()
        val generatedDoubleArray = generatedList.map(Int::toDouble).toDoubleArray()

        val kolmogorovResult = DistributionTest.kolmogorov_smirnov_test(
                initialDoubleArray,
                generatedDoubleArray
        )

        println("Kolmogorov - ${kolmogorovResult[0]}")
        println("P-Value - ${kolmogorovResult[1]}")
        println("-")

        val ansariResult = DistributionTest.ansari_bradley_test(
                initialDoubleArray,
                generatedDoubleArray,
                false
        )

        println("Ansari - ${ansariResult[0]}")
        println("P-Value - ${ansariResult[1]}")
        println("-")

        DistributionPair(initialList, generatedList).chiSquaredTest(100) succeedsAnd { (statistic, pValue) ->

            println("Chi-Square - $statistic")
            println("P-Value - $pValue")

            println("--")
        }
    }

    @Test
    fun `screw around`() {

        val one: GenericDistribution = Weibull(3.0, 5.0)

        val two: GenericDistribution = Weibull(3.0, 2.0)

        val size = 20

        val listA = List(size) { one.random() }.map { it * 10 }.map { it.toInt() }.map { it.toDouble() }

        val listOne = listA.toDoubleArray()

        val listB = List(size) { two.random() }.map { it * 10 }.map { it.toInt() }.map { it.toDouble() }
        val listTwo = listB.toDoubleArray()

        val output = DistributionTest.kolmogorov_smirnov_test(listOne, listTwo)

        println()
    }
}