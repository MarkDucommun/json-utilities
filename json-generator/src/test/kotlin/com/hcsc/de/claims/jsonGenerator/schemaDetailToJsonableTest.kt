package com.hcsc.de.claims.jsonGenerator

import com.hcsc.de.claims.jsonSchemaConversion.*
import com.hcsc.de.claims.jsonSchemaConversion.Number
import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Assert
import org.junit.Test

class SchemaDetailToJsonableTest {

    @Test
    fun `it writes a Text as a string`() {

        Text(maxLength = 10).toJsonable() isText { json ->

            assertThat(json.ejected.length).isEqualTo(10)
        }
    }

    @Test
    fun `it writes Text as X`() {

        Text(maxLength = 1).toJsonable() isTextEqualTo "X"
    }

    @Test
    fun `it writes all the object type SchemaDetails`() {

        Date.toJsonable() isTextEqualTo "1111/11/11"

        DateTime.toJsonable() isTextEqualTo "1111/11/11 11:11:11 UTC"

        Number.toJsonable() isTextEqualTo "100000000000.00000"

        Integer.toJsonable() isTextEqualTo "10000000000"
    }

    @Test
    fun `it writes a simple Complex Object to json`() {

        ComplexObject(properties = listOf(
                SchemaObject("A", detail = Text(maxLength = 1)),
                SchemaObject("B", detail = Text(maxLength = 1)),
                SchemaObject("C", detail = Text(maxLength = 1))
        )).toJsonable() isObject { map ->

            assertThat(map.ejected).isEqualTo(mapOf("A" to "X", "B" to "X", "C" to "X"))
        }
    }

    @Test
    fun `it writes a complex Complex Object to json`() {

        ComplexObject(properties = listOf(
                SchemaObject(name = "A", detail = ComplexObject(
                        properties = listOf(
                                SchemaObject(name = "1", detail = Text(maxLength = 1))
                        )
                ))
        )).toJsonable() isObject { map ->

            assertThat(map.ejected).isEqualTo(mapOf("A" to mapOf("1" to "X")))
        }
    }

    @Test
    fun `it writes a simple Array to List`() {

        ArrayDetail(itemType = Text(maxLength = 1), maxItems = 3).toJsonable() isList { list ->

            assertThat(list.ejected).containsExactlyInAnyOrder("X", "X", "X")
        }
    }

    @Test
    fun `it writes a complex Array to List`() {

        val itemType = ArrayDetail(itemType = Text(maxLength = 1), maxItems = 3)

        ArrayDetail(itemType = itemType, maxItems = 1).toJsonable() isList { list ->

            assertThat(list.ejected).containsExactlyInAnyOrder(listOf("X", "X", "X"))
        }
    }

    @Test
    fun `it writes a complex Object Array to List`() {

        val itemType = ComplexObject(properties = listOf(SchemaObject(name = "A", detail = Text(maxLength = 1))))

        ArrayDetail(itemType = itemType, maxItems = 2).toJsonable() isList { list ->

            assertThat(list.ejected).containsExactlyInAnyOrder(mapOf("A" to "X"), mapOf("A" to "X"))
        }
    }

    @Test
    fun `it writes OneOf as first member of the children`() {

        OneOf(list = listOf(Text(maxLength = 1))).toJsonable() isTextEqualTo "X"
    }

    private inline fun <reified T : Jsonable<*>> Jsonable<*>.isType(onSuccess: (T) -> Unit) {
        when (this) {
            is T -> onSuccess(this)
            else -> Assert.fail("Was not ${T::class.simpleName}")
        }
    }

    private infix fun Jsonable<*>.isTextEqualTo(string: String) {
        when (this) {
            is TextJson -> assertThat(this.ejected).isEqualTo(string)
            else -> Assert.fail("Was not TextJson")
        }
    }

    private infix fun Jsonable<*>.isText(onSuccess: (TextJson) -> Unit) = this.isType(onSuccess)
    private infix fun Jsonable<*>.isList(onSuccess: (ListJson) -> Unit) = this.isType(onSuccess)
    private infix fun Jsonable<*>.isObject(onSuccess: (ObjectJson) -> Unit) = this.isType(onSuccess)
}