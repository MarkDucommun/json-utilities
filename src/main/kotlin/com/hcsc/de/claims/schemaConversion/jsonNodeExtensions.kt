package com.hcsc.de.claims.schemaConversion

import com.fasterxml.jackson.databind.JsonNode

val JsonNode.definitions get() = getOrThrow("definitions")

val JsonNode.properties get() = getOrThrow("properties")

val JsonNode.type get() = get("type")?.asText() ?: "other"

val JsonNode.format get() = get("format")?.asText() ?: "other"

val JsonNode.maxLength get() = getOrThrow("maxLength")

val JsonNode.items get() = getOrThrow("items")

val JsonNode.maxItems get() = get("maxItems")?.asInt()

val JsonNode.ref get() = get("\$ref")

val JsonNode.oneOf get() = get("oneOf")

fun JsonNode.getOrThrow(key: String) = get(key) ?: throw Exception("No $key exist(s) on this node")
