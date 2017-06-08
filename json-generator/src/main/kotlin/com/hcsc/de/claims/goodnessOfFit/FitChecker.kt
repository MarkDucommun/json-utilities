package com.hcsc.de.claims.goodnessOfFit

import com.hcsc.de.claims.helpers.Result

interface FitChecker<in numberType: Number> {

    fun check(listOne: List<numberType>, listTwo: List<numberType>): Result<String, Double>
}