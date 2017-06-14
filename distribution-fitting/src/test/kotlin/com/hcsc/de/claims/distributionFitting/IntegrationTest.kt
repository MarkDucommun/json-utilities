package com.hcsc.de.claims.distributionFitting

import com.hcsc.de.claims.distributions.generation.DoubleDataBinner
import com.hcsc.de.claims.histogrammer.JFreeChartCreator
import com.hcsc.de.claims.histogrammer.ListHistogrammer
import com.hcsc.de.claims.results.get
import com.hcsc.de.claims.results.time
import com.hcsc.de.claims.results.traverse
import net.sourceforge.jdistlib.Weibull
import org.junit.Ignore
import org.junit.Test
import umontreal.ssj.probdist.WeibullDist
import umontreal.ssj.randvar.WeibullGen
import umontreal.ssj.rng.BasicRandomStreamFactory
import umontreal.ssj.rng.F2NL607

class IntegrationTest {

    @Test
    @Ignore
    fun `it can test the speed and accuracy of Java vs R`() {

        val stream = BasicRandomStreamFactory(F2NL607::class.java).newInstance()

        val weibull = WeibullDist(2.0, 1 / 100.0, 50.0)

        val gen = WeibullGen(stream, weibull)

        val montrealDistribution = MontrealRandomable(gen)

        val startingDistribution = GenericDistributionRandomable(Weibull(1.3, 50.0))

        val startingList = List(10000) { startingDistribution.random() + 5.0 }

        val renjinResult = time { FitDistrPlus.weibullParameters(startingList) }

        val montrealResult = time { Montreal.weibullParameters(startingList) }

        val histogrammer = ListHistogrammer<Double>(DoubleDataBinner(), JFreeChartCreator())

        val generatedMontreal = Montreal.weibullDistribution(startingList).get

        val generatedRenjin = FitDistrPlus.weibullDistribution(startingList).get

        val renjinList = List(10000) { generatedRenjin.random() }

        val montrealList = List(10000) { generatedMontreal.random() }

        val chartList = listOf(
                histogrammer.create(startingList, "Start"),
                histogrammer.create(renjinList, "FitDistrPlus"),
                histogrammer.create(montrealList, "Montreal")
        ).traverse().get

        chartList.map { it.render() }

        Thread.sleep(60000)

        chartList.map { it.stop() }

        println()
    }
}