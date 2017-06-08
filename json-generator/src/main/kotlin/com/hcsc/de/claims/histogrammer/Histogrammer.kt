package com.hcsc.de.claims.histogrammer

import com.hcsc.de.claims.helpers.Result

interface Histogrammer<in numberType: Number> {

    fun create(list: List<numberType>): Result<String, BarChart>

    fun create(firstList: List<numberType>, secondList: List<numberType>): Result<String, BarChart>
}