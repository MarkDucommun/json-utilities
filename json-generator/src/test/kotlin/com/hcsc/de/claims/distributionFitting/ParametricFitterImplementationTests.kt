package com.hcsc.de.claims.distributionFitting

class FitDistrPlusTest : ParametricFitterTest() {

    override val subject: ParametricFitter = FitDistrPlus
}

class MontrealTest : ParametricFitterTest() {

    override val subject: ParametricFitter = Montreal
}