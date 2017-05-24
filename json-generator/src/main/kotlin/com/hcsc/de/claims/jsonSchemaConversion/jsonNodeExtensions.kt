package com.hcsc.de.claims.jsonSchemaConversion

import com.fasterxml.jackson.databind.JsonNode

internal val JsonNode.definitions get() = getOrThrow("definitions")

internal val JsonNode.properties get() = getOrThrow("properties")

internal val JsonNode.type get() = get("type")?.asText() ?: "other"

internal val JsonNode.format get() = get("format")?.asText() ?: "other"

internal val JsonNode.maxLength get() = getOrThrow("maxLength")

internal val JsonNode.items get() = getOrThrow("items")

internal val JsonNode.maxItems get() = get("maxItems")?.asInt()

internal val JsonNode.ref get() = get("\$ref")

internal val JsonNode.oneOf get() = get("oneOf")

internal fun JsonNode.getOrThrow(key: String) = get(key) ?: throw Exception("No $key exist(s) on this node")
