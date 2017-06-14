package io.ducommun.jsonParsing

import com.hcsc.de.claims.results.failsWithMessage
import com.hcsc.de.claims.results.succeedsAndShouldReturn
import org.junit.Test

class JsonStructureNesterTest {

    @Test
    fun `it fails if no elements are passed in`() {

        emptyList<JsonStructure>().nested failsWithMessage "Invalid JSON - no elements passed in"
    }

    @Test
    fun `it fails if only one element is passed in`() {

        listOf(StringOpen(id = 1)).nested failsWithMessage "Invalid JSON - start and end of structure don't match"
    }

    @Test
    fun `it returns an empty StringStructureElement`() {

        listOf(
                StringOpen(id = 1),
                StringClose(id = 1)
        ).nested succeedsAndShouldReturn
                StringStructureElement(id = 1, children = emptyList())
    }

    @Test
    fun `it returns a StringStructureElement with some values`() {

        listOf(
                StringOpen(id = 1),
                StringValue(id = 1, value = 'a'),
                StringClose(id = 1)
        ).nested succeedsAndShouldReturn
                StringStructureElement(id = 1, children = listOf(StringValue(id = 1, value = 'a')))
    }

    @Test
    fun `it fails appropriately when other than a string value is in a string open and close`() {

        listOf(
                StringOpen(id = 1),
                LiteralValue(id = 1, value = 'a'),
                StringClose(id = 1)
        ).nested failsWithMessage "Invalid JSON - something went wrong"
    }

    @Test
    fun `it fails appropriately when a string is not opened with a string open`() {

        listOf(
                StringValue(id = 1, value = 'a'),
                StringClose(id = 1)
        ).nested failsWithMessage "Invalid JSON - start and end of structure don't match"
    }

    @Test
    fun `it fails appropriately when a string is not closed with a string closed`() {

        listOf(
                StringOpen(id = 1),
                StringValue(id = 1, value = 'a')
        ).nested failsWithMessage "Invalid JSON - start and end of structure don't match"
    }

    @Test
    fun `it fails appropriately when a strings open and close does not have matching ids`() {

        listOf(
                StringOpen(id = 1),
                StringValue(id = 1, value = 'a'),
                StringClose(id = 2)
        ).nested failsWithMessage "Invalid JSON - start and end of structure don't match"
    }

    @Test
    fun `it fails appropriately when a string does not have matching ids`() {

        listOf(
                StringOpen(id = 1),
                StringValue(id = 2, value = 'a'),
                StringClose(id = 1)
        ).nested failsWithMessage "Invalid JSON - something went wrong"
    }

    @Test
    fun `it fails when a open and closing types don't match`() {

        listOf(
                StringOpen(id = 1),
                LiteralClose(id = 1, value = 'a')
        ).nested failsWithMessage "Invalid JSON - start and end of structure don't match"
    }

    @Test
    fun `it returns a LiteralStructureElement`() {

        listOf(
                LiteralValue(id = 1, value = 'a'),
                LiteralClose(id = 1, value = 'b')
        ).nested succeedsAndShouldReturn LiteralStructureElement(id = 1, children = listOf(
                LiteralValue(id = 1, value = 'a'),
                LiteralClose(id = 1, value = 'b')
        ))
    }

    @Test
    fun `it returns a LiteralStructureElement even if there is no close at the end of the literal value`() {

        listOf(
                LiteralValue(id = 1, value = 'a'),
                LiteralValue(id = 1, value = 'b')
        ).nested succeedsAndShouldReturn LiteralStructureElement(id = 1, children = listOf(
                LiteralValue(id = 1, value = 'a'),
                LiteralValue(id = 1, value = 'b')
        ))
    }

    @Test
    fun `it fails if literals have anything other than literals in them`() {

        listOf(
                LiteralValue(id = 1, value = 'a'),
                StringValue(id = 1, value = 'b'),
                LiteralClose(id = 1, value = 'b')
        ).nested failsWithMessage "Invalid JSON - something went wrong"
    }

    @Test
    fun `it fails if literals do not have matching ids`() {

        listOf(
                LiteralValue(id = 1, value = 'a'),
                LiteralValue(id = 2, value = 'b'),
                LiteralValue(id = 1, value = 'b')
        ).nested failsWithMessage "Invalid JSON - something went wrong"
    }

    @Test
    fun `it returns an empty ArrayStructureElement`() {

        listOf(
                ArrayOpen(id = 1),
                ArrayClose(id = 1)
        ).nested succeedsAndShouldReturn
                ArrayStructureElement(id = 1, children = emptyList())
    }

    @Test
    fun `it returns an array element with one string ArrayStructureElement`() {

        listOf(
                ArrayOpen(id = 1),
                StringOpen(id = 2),
                StringClose(id = 2),
                ArrayClose(id = 1)
        ).nested succeedsAndShouldReturn
                ArrayStructureElement(id = 1, children = listOf(
                        StringStructureElement(id = 2, children = emptyList())
                ))
    }

    @Test
    fun `it returns an array element with two children ArrayStructureElement`() {

        listOf(
                ArrayOpen(id = 1),
                StringOpen(id = 2),
                StringClose(id = 2),
                ArrayComma(id = 1),
                StringOpen(id = 3),
                StringClose(id = 3),
                ArrayClose(id = 1)
        ).nested succeedsAndShouldReturn
                ArrayStructureElement(id = 1, children = listOf(
                        StringStructureElement(id = 2, children = emptyList()),
                        StringStructureElement(id = 3, children = emptyList())
                ))
    }

    @Test
    fun `it returns array elements array element children`() {

        listOf(
                ArrayOpen(id = 1),
                ArrayOpen(id = 2),
                StringOpen(id = 3),
                StringClose(id = 3),
                ArrayComma(id = 2),
                StringOpen(id = 4),
                StringClose(id = 4),
                ArrayClose(id = 2),
                ArrayComma(id = 1),
                ArrayOpen(id = 5),
                StringOpen(id = 6),
                StringClose(id = 6),
                ArrayComma(id = 5),
                StringOpen(id = 7),
                StringClose(id = 7),
                ArrayClose(id = 5),
                ArrayClose(id = 1)
        ).nested succeedsAndShouldReturn
                ArrayStructureElement(id = 1, children = listOf(
                        ArrayStructureElement(id = 2, children = listOf(
                                StringStructureElement(id = 3, children = emptyList()),
                                StringStructureElement(id = 4, children = emptyList())
                        )),
                        ArrayStructureElement(id = 5, children = listOf(
                                StringStructureElement(id = 6, children = emptyList()),
                                StringStructureElement(id = 7, children = emptyList())
                        ))
                ))
    }

    @Test
    fun `it fails if nothing comes before a comma`() {

        listOf(
                ArrayOpen(id = 1),
                ArrayComma(id = 1),
                StringOpen(id = 3),
                StringClose(id = 3),
                ArrayClose(id = 1)
        ).nested failsWithMessage "Invalid JSON - array child can't be empty"
    }

    @Test
    fun `it returns an empty ObjectStructureElement`() {

        listOf(
                ObjectOpen(id = 1),
                ObjectClose(id = 1)
        ).nested succeedsAndShouldReturn
                OpenObjectStructure(id = 1, children = emptyList())
    }

    @Test
    fun `it returns an ObjectStructureElement with a single child`() {

        listOf(
                ObjectOpen(id = 1),
                StringOpen(id = 2),
                StringValue(id = 2, value = 'a'),
                StringClose(id = 2),
                ObjectColon(id = 1),
                LiteralValue(id = 3, value = '1'),
                ObjectClose(id = 1)
        ).nested succeedsAndShouldReturn
                OpenObjectStructure(id = 1, children = listOf(
                        ObjectChildElement(
                                id = 3,
                                key = StringStructureElement(id = 2, children = listOf(
                                        StringValue(id = 2, value = 'a')
                                )),
                                value = LiteralStructureElement(id = 3, children = listOf(
                                        LiteralValue(id = 3, value = '1')
                                ))
                        )
                ))
    }

    @Test
    fun `it returns an ObjectStructureElement with two children`() {

        // {"a":1,"b":2}
        listOf(
                ObjectOpen(id = 1),
                StringOpen(id = 2),
                StringValue(id = 2, value = 'a'),
                StringClose(id = 2),
                ObjectColon(id = 1),
                LiteralValue(id = 3, value = '1'),
                ObjectComma(id = 1),
                StringOpen(id = 4),
                StringValue(id = 4, value = 'b'),
                StringClose(id = 4),
                ObjectColon(id = 1),
                LiteralValue(id = 5, value = '2'),
                ObjectClose(id = 1)
        ).nested succeedsAndShouldReturn
                OpenObjectStructure(id = 1, children = listOf(
                        ObjectChildElement(
                                id = 3,
                                key = StringStructureElement(id = 2, children = listOf(
                                        StringValue(id = 2, value = 'a')
                                )),
                                value = LiteralStructureElement(id = 3, children = listOf(
                                        LiteralValue(id = 3, value = '1')
                                ))
                        ),
                        ObjectChildElement(
                                id = 5,
                                key = StringStructureElement(id = 4, children = listOf(
                                        StringValue(id = 4, value = 'b')
                                )),
                                value = LiteralStructureElement(id = 5, children = listOf(
                                        LiteralValue(id = 5, value = '2')
                                ))
                        )
                ))
    }

    @Test
    fun `it returns objects with object children`() {

        listOf(
                ObjectOpen(id = 1),

                  StringOpen(id = 2),
                  StringValue(id = 2, value = 'c'),
                  StringClose(id = 2),

                ObjectColon(id = 1),

                  ObjectOpen(id = 3),

                    StringOpen(id = 4),
                    StringValue(id = 4, value = 'a'),
                    StringClose(id = 4),

                  ObjectColon(id = 3),

                    LiteralValue(id = 5, value = '1'),

                  ObjectComma(id = 3),

                    StringOpen(id = 6),
                    StringValue(id = 6, value = 'b'),
                    StringClose(id = 6),

                  ObjectColon(id = 3),

                    LiteralValue(id = 7, value = '2'),

                  ObjectClose(id = 3),

                ObjectComma(id = 1),

                  StringOpen(id = 8),
                  StringValue(id = 8, value = 'd'),
                  StringClose(id = 8),

                ObjectColon(id = 1),

                  ObjectOpen(id = 9),

                    StringOpen(id = 10),
                    StringValue(id = 10, value = 'a'),
                    StringClose(id = 10),

                  ObjectColon(id = 9),

                    LiteralValue(id = 11, value = '1'),

                  ObjectComma(id = 9),

                    StringOpen(id = 12),
                    StringValue(id = 12, value = 'b'),
                    StringClose(id = 12),

                  ObjectColon(id = 9),

                    LiteralValue(id = 13, value = '2'),

                  ObjectClose(id = 9),

                ObjectClose(id = 1)

        ).nested succeedsAndShouldReturn
                OpenObjectStructure(id = 1, children = listOf(
                        ObjectChildElement(
                                id = 3,
                                key = StringStructureElement(id = 2, children = listOf(
                                        StringValue(id = 2, value = 'c')
                                )),
                                value = OpenObjectStructure(id = 3, children = listOf(
                                        ObjectChildElement(
                                                id = 5,
                                                key = StringStructureElement(id = 4, children = listOf(
                                                        StringValue(id = 4, value = 'a')
                                                )),
                                                value = LiteralStructureElement(id = 5, children = listOf(
                                                        LiteralValue(id = 5, value = '1')
                                                ))
                                        ),
                                        ObjectChildElement(
                                                id = 7,
                                                key = StringStructureElement(id = 6, children = listOf(
                                                        StringValue(id = 6, value = 'b')
                                                )),
                                                value = LiteralStructureElement(id = 7, children = listOf(
                                                        LiteralValue(id = 7, value = '2')
                                                ))
                                        )
                                ))
                        ),
                        ObjectChildElement(
                                id = 9,
                                key = StringStructureElement(id = 8, children = listOf(
                                        StringValue(id = 8, value = 'd')
                                )),
                                value = OpenObjectStructure(id = 9, children = listOf(
                                        ObjectChildElement(
                                                id = 11,
                                                key = StringStructureElement(id = 10, children = listOf(
                                                        StringValue(id = 10, value = 'a')
                                                )),
                                                value = LiteralStructureElement(id = 11, children = listOf(
                                                        LiteralValue(id = 11, value = '1')
                                                ))
                                        ),
                                        ObjectChildElement(
                                                id = 13,
                                                key = StringStructureElement(id = 12, children = listOf(
                                                        StringValue(id = 12, value = 'b')
                                                )),
                                                value = LiteralStructureElement(id = 13, children = listOf(
                                                        LiteralValue(id = 13, value = '2')
                                                ))
                                        )
                                ))
                        )
                ))
    }

    private val subject = JsonStructureNester()

    private val List<JsonStructure>.nested get() = subject.nest(this)
}