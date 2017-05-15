package com.hcsc.de.claims.jsonSizing

sealed class JsonSizeNode {
    abstract val name: String
    abstract val size: Int
}

data class JsonSizeLeafNode(
        override val name: String,
        override val size: Int
): JsonSizeNode()

data class JsonSizeObject(
        override val name: String,
        override val size: Int,
        val children: List<JsonSizeNode>,
        val averageChildSize: Int
): JsonSizeNode()

data class JsonSizeArray(
        override val name: String,
        override val size: Int,
        val children: List<JsonSizeNode>,
        val averageChildSize: Int
): JsonSizeNode()
