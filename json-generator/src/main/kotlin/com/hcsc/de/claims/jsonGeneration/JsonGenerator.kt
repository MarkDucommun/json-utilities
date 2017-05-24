package com.hcsc.de.claims.jsonGeneration

interface JsonGenerator<in inputType> {

    fun generate(input: inputType): String
}