package com.hcsc.de.claims.kotlinJacksonHelpers

import com.fasterxml.jackson.databind.JsonNode

val JsonNode.fieldNames: List<String>
    get() = mutableListOf<String>().apply { fieldNames().forEach { this += it } }.toList()
