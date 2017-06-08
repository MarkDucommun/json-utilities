package com.hcsc.de.claims.histogrammer

data class DataSet(
    val name: String,
    val datapoints: List<Datapoint>
) : List<Datapoint> by datapoints