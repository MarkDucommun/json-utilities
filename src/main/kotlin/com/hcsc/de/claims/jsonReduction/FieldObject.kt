package com.hcsc.de.claims.jsonReduction

data class FieldObject(
        val name: String = "",
        val properties: List<FieldObject> = emptyList()
)