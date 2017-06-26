package com.hcsc.de.claims.distributionFitting

interface RandomableGenerator<numberType: Number> {

    fun generate(list: List<numberType>): RandomableProfile<numberType>
}