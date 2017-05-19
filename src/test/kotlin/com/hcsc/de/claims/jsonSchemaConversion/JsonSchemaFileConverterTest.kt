package com.hcsc.de.claims.jsonSchemaConversion

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.javafaker.Faker
import com.hcsc.de.claims.fileReaders.JacksonJsonFileReader
import com.hcsc.de.claims.jsonDeIdentifier.FileDeidentifier
import com.hcsc.de.claims.helpers.Failure
import com.hcsc.de.claims.helpers.Success
import com.hcsc.de.claims.jsonReduction.FieldObject
import com.hcsc.de.claims.jsonReduction.JsonSchemaFileReducer
import com.hcsc.de.claims.jsonSizing.JsonSizeAnalyzer
import com.hcsc.de.claims.jsonSizing.JsonSizeNode
import com.hcsc.de.claims.jsonSizing.JsonSizer
import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Test
import java.io.File

// TODO TEST THIS
class JsonSchemaFileConverterTest {


}