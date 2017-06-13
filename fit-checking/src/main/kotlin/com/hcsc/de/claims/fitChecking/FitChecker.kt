package com.hcsc.de.claims.fitChecking

import com.hcsc.de.claims.results.Result

interface FitChecker<in numberType: Number> {

    fun check(listOne: List<numberType>, listTwo: List<numberType>): Result<String, Double>

    fun check(listOne: List<numberType>, listTwo: List<numberType>, binCount: Int): Result<String, Double>
}