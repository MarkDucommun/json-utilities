package com.hcsc.de.claims.jsonGenerator

interface JsonGenerator<in inputType> {

    fun generate(input: inputType): String
}