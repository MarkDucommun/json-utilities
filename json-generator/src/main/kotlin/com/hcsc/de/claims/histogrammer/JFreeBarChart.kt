package com.hcsc.de.claims.histogrammer

import org.jfree.chart.ChartFactory
import org.jfree.chart.ChartPanel
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.renderer.xy.StandardXYBarPainter
import org.jfree.chart.renderer.xy.XYBarRenderer
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import org.jfree.ui.ApplicationFrame
import org.jfree.ui.RefineryUtilities
import java.awt.Dimension
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

class JFreeBarChart(
        override val name: String,
        override val xLabel: String,
        override val yLabel: String,
        override val dataSets: List<DataSet>
) : BarChart {

    private val seriesToAdd = dataSets.map {

        XYSeries(it.name).apply {
            it.forEach { (xValue, count) -> add(xValue, count) }
        }
    }

    private val seriesCollection = XYSeriesCollection().apply {

        seriesToAdd.forEach { addSeries(it) }
    }

    private val chart = ChartFactory.createXYBarChart(
            name,
            xLabel,
            false,
            yLabel,
            seriesCollection,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
    ).apply {
        val xyBarRenderer = xyPlot.renderer as XYBarRenderer
        xyBarRenderer.setShadowVisible(false)
        xyBarRenderer.barPainter = StandardXYBarPainter()
    }


    private val frame = ApplicationFrame("frame").apply {

        contentPane = ChartPanel(chart).apply { preferredSize = Dimension(1000, 500) }
    }

    override fun render() {

        frame.pack()

        RefineryUtilities.centerFrameOnScreen(frame)

        frame.isVisible = true
    }

    override fun stop() {

        frame.dispose()
    }

    override fun save(path: String) {

        frame.pack()

        val container = ChartPanel(chart).apply { preferredSize = Dimension(1000, 500) }

        val image = BufferedImage(container.width, container.height, BufferedImage.TYPE_INT_RGB)

        val graphics2D = image.createGraphics()

        container.printAll(graphics2D)

        graphics2D.dispose()

        ImageIO.write(image, "png", File("$path/graph-${System.currentTimeMillis()}.png"))
    }
}