package com.hcsc.de.claims.distributions

import com.hcsc.de.claims.results.succeedsAnd
import net.sourceforge.jdistlib.Weibull
import net.sourceforge.jdistlib.disttest.DistributionTest
import net.sourceforge.jdistlib.generic.GenericDistribution
import org.apache.commons.math3.distribution.WeibullDistribution
import org.junit.Test
import org.renjin.script.RenjinScriptEngineFactory

class IsNormalTest {

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

        val factory = RenjinScriptEngineFactory()

        val engine = factory.scriptEngine

        val size = 20

        val listA = List(size) { one.random() }.map { it * 10 }.map { it.toInt() }.map { it.toDouble() }

        engine.eval("library(MASS)")
        engine.eval("set.seed(101)")
        engine.eval("my_data <- rnorm(250, mean=1, sd=0.45)")
        engine.eval("fit <- fitdistr(my_data, densfun=\"normal\")")
        engine.eval("print(fit)")

        val listOne = listA.toDoubleArray()

        val listB = List(size) { two.random() }.map { it * 10 }.map { it.toInt() }.map { it.toDouble() }
        val listTwo = listB.toDoubleArray()

        val output = DistributionTest.kolmogorov_smirnov_test(listOne, listTwo)
    }

    @Test
    fun `test renjin`() {

        val factory = RenjinScriptEngineFactory()

        val engine = factory.scriptEngine

        engine.put("df", 123)

        engine.eval("print(df)")
    }

    @Test
    fun `generate normal data in kotlin, move it into R, find the mean and std deviation in R, move those back to java and test the fit`() {

        val initialDistribution = Weibull(2.0, 100.0)

        val initialList = List(10) { (initialDistribution.random()).toInt() }

        val unknownDistribution = initialList.unknownVariableBinWidthDistribution(binCount = 5)

        val generatedList: List<Int> = List(500) { unknownDistribution.random() }

        val factory = RenjinScriptEngineFactory()

        val engine = factory.scriptEngine

        engine.eval("library(fitdistrplus)")

        engine.put("my_data", initialList.map { it.toDouble() }.toDoubleArray())

        engine.eval("fit.weibull <- fitdist(my_data, distr = \"weibull\", method = \"mle\", lower = c(0, 0))")
        engine.eval("fit.gamma <- fitdist(my_data, distr = \"gamma\", method = \"mle\", lower = c(0, 0), start = list(scale = 1, shape = 1))")

        engine.eval("print(fit.weibull)")
        engine.eval("print(fit.gamma)")

        engine.eval("print(gofstat(list(fit.weibull, fit.gamma)))")
    }
}