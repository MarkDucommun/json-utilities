package com.hcsc.de.claims.jsonReducing

data class FieldObject(
        val name: String = "",
        val properties: List<com.hcsc.de.claims.jsonReducing.FieldObject> = emptyList()
)