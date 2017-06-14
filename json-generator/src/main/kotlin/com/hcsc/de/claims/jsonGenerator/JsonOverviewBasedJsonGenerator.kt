package com.hcsc.de.claims.jsonGenerator

import com.hcsc.de.claims.jsonSizing.JsonSizeOverview


class JsonOverviewBasedJsonGenerator<in numberType: Number> : JsonGenerator<JsonSizeOverview<numberType>> {

    override fun generate(input: JsonSizeOverview<numberType>): String {
        TODO()
    }
}