package com.hcsc.de.claims.jsonGeneration

import com.hcsc.de.claims.jsonSizing.JsonSizeOverview

class JsonOverviewBasedJsonGenerator<numberType: Number> : JsonGenerator<JsonSizeOverview<numberType>> {

    override fun generate(input: JsonSizeOverview<numberType>): String {
        TODO()
    }
}