package com.hcsc.de.claims

import com.hcsc.de.claims.helpers.Result

interface Translator<in inType, outType> {

    fun translate(input: inType): Result<String, outType>
}