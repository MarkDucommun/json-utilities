package com.hcsc.de.claims.collection.helpers

import com.hcsc.de.claims.results.failsWithMessage
import com.hcsc.de.claims.results.get
import com.hcsc.de.claims.results.succeedsAnd
import com.hcsc.de.claims.results.succeedsAndShouldReturn
import org.assertj.core.api.KotlinAssertions.assertThat
import org.junit.Test

class SimpleNonEmptyListTest {

    @Test
    fun `it has a size`() {

        assertThat(nonEmptyListOf(1, 2, 3).size).isEqualTo(3)
    }

    @Test
    fun `first works`() {

        assertThat(nonEmptyListOf(1, 2, 3).first).isEqualTo(1)
    }


    @Test
    fun `last works`() {

        assertThat(nonEmptyListOf(1).last).isEqualTo(1)
        assertThat(nonEmptyListOf(1, 2, 3).last).isEqualTo(3)
    }

    @Test
    fun `all - returns the elements as a normal List`() {

        assertThat(nonEmptyListOf(1).all).containsExactly(1)
        assertThat(nonEmptyListOf(1, 2, 3).all).containsExactly(1, 2, 3)
    }

    @Test
    fun `get - returns a success if there is a value at the requested index`() {

        nonEmptyListOf(1, 2, 3).get(2) succeedsAndShouldReturn 3
    }

    @Test
    fun `get - returns a failure if there is no value at the request index`() {

        nonEmptyListOf(1, 2, 3).get(3) failsWithMessage "No value exists at this index"
    }

    @Test
    fun `getOrNull - returns the value if there is a value at the requested index`() {

        assertThat(nonEmptyListOf(1, 2, 3).getOrNull(2)).isEqualTo(3)
    }

    @Test
    fun `getOrNull - returns null if there is no value at the request index`() {

        assertThat(nonEmptyListOf(1, 2, 3).getOrNull(3)).isEqualTo(null)
    }

    @Test
    fun `plus - element - adds an element`() {

        assertThat(nonEmptyListOf(1, 2).plus(3)).isEqualTo(nonEmptyListOf(1, 2, 3))
    }

    @Test
    fun `plus - non empty list - adds the non empty list`() {

        assertThat(nonEmptyListOf(1, 2).plus(nonEmptyListOf(3, 4))).isEqualTo(nonEmptyListOf(1, 2, 3, 4))
    }

    @Test
    fun `plus - iterable - adds the non empty list`() {

        assertThat(nonEmptyListOf(1, 2).plus(listOf(3, 4))).isEqualTo(nonEmptyListOf(1, 2, 3, 4))
    }

    @Test
    fun `drop - returns a success if dropping the requested number of elements does not yield an empty list`() {

        nonEmptyListOf(1, 2, 3, 4).drop(2) succeedsAndShouldReturn nonEmptyListOf(3, 4)
    }

    @Test
    fun `drop - returns a failure if dropping the requested number of elements yields an empty list`() {

        nonEmptyListOf(1, 2).drop(2) failsWithMessage "List must contain at least one value"
    }


    @Test
    fun `drop last - returns a success if dropping the requested number of elements does not yield an empty list`() {

        nonEmptyListOf(1, 2, 3, 4).dropLast(2) succeedsAndShouldReturn nonEmptyListOf(1, 2)
    }

    @Test
    fun `drop last - returns a failure if dropping the requested number of elements yields an empty list`() {

        nonEmptyListOf(1, 2).drop(2) failsWithMessage "List must contain at least one value"
    }

    @Test
    fun `safe drop - returns a failure if dropping the requested number of elements yields an empty list`() {

        nonEmptyListOf(1, 2, 3, 4).drop(2) succeedsAndShouldReturn nonEmptyListOf(3, 4)
        nonEmptyListOf(1, 2).drop(2) failsWithMessage "List must contain at least one value"
    }


    @Test
    fun `safe drop last - returns a success if dropping the requested number of elements does not yield an empty list`() {

        nonEmptyListOf(1, 2, 3, 4).dropLast(2) succeedsAndShouldReturn nonEmptyListOf(1, 2)
        nonEmptyListOf(1, 2).drop(2) failsWithMessage "List must contain at least one value"
    }

    @Test
    fun `contains - it returns true if it contains the requested element`() {

        assertThat(nonEmptyListOf(1, 2, 3).contains(2)).isTrue()
    }

    @Test
    fun `contains - it returns false if it does not contains the requested element`() {

        assertThat(nonEmptyListOf(1, 2, 3).contains(4)).isFalse()
    }

    @Test
    fun `find - returns null if the condition is never satisfied`() {

        nonEmptyListOf(1, 2, 3).find { it == 4 } failsWithMessage "Element meeting criteria was not found"
    }

    @Test
    fun `find - returns the first value that satisfies the condition`() {

        nonEmptyListOf(1, 2, 3, 2).find { it == 2 } succeedsAndShouldReturn 2
    }

    @Test
    fun `as reversed - reverses the list`() {

        assertThat(nonEmptyListOf(1, 2, 3).asReversed()).isEqualTo(nonEmptyListOf(3, 2, 1))
    }

    @Test
    fun `for each - does the operation to each element`() {

        var total = 0

        nonEmptyListOf(1, 2, 3).forEach { total += it }

        assertThat(total).isEqualTo(6)
    }

    @Test
    fun `map transforms the values into a new list`() {

        assertThat(nonEmptyListOf(1, 2, 3).map(Int::toString)).isEqualTo(nonEmptyListOf("1", "2", "3"))
    }

    @Test
    fun `mapIndexed transforms the values into a new list with the indexes`() {

        val mapIndexed = nonEmptyListOf("A", "B", "C").mapIndexed { i, it -> "$it$i" }

        assertThat(mapIndexed).isEqualTo(nonEmptyListOf("A0", "B1", "C2"))
    }

    @Test
    fun `filter succeeds if more than one value remains`() {

        nonEmptyListOf(1, 2, 3).filter { it == 1 } succeedsAndShouldReturn nonEmptyListOf(1)
    }

    @Test
    fun `filter fails if no values remain`() {

        nonEmptyListOf(1, 2, 3).filter { it == 4 } failsWithMessage "List must contain at least one value"
    }

    @Test
    fun `filter not succeeds if more than one value remains`() {

        nonEmptyListOf(1, 2, 3).filterNot { it == 1 } succeedsAndShouldReturn nonEmptyListOf(2, 3)
    }

    @Test
    fun `filter not fails if no values remain`() {

        nonEmptyListOf(1, 2, 3).filterNot { it != 4 } failsWithMessage "List must contain at least one value"
    }

    @Test
    fun `reduce works`() {

        assertThat(nonEmptyListOf(1, 2, 3).reduce { acc, it -> acc + it }).isEqualTo(6)
    }

    @Test
    fun `fold works`() {

        assertThat(nonEmptyListOf(2, 4, 6).fold(true) { acc, value -> acc && value % 2 == 0 }).isEqualTo(true)
    }

    @Test
    fun `zip combines two non empty lists together`() {

        assertThat(nonEmptyListOf(1,2,3).zip(nonEmptyListOf(1,2,3))).isEqualTo(nonEmptyListOf(
                1 to 1,
                2 to 2,
                3 to 3
        ))
    }

    @Test
    fun `it fails if you do not initialize it with at least one value`() {

        nonEmptyListOf<Int>() failsWithMessage "List must contain at least one value"
    }

    @Test
    fun `it succeeds if you initialize it with at least one value`() {

        assertThat(nonEmptyListOf(1)).isEqualTo(SimpleNonEmptyList(first = 1))
        assertThat(nonEmptyListOf(1, 2)).isEqualTo(SimpleNonEmptyList(first = 1, remaining = listOf(2)))
        assertThat(nonEmptyListOf(1, 2, 3)).isEqualTo(SimpleNonEmptyList(first = 1, remaining = listOf(2, 3)))
        assertThat(nonEmptyListOf(1, 2, 3, 4)).isEqualTo(SimpleNonEmptyList(first = 1, remaining = listOf(2, 3, 4)))
        assertThat(nonEmptyListOf(1, 2, 3, 4, 5)).isEqualTo(SimpleNonEmptyList(first = 1, remaining = listOf(2, 3, 4, 5)))
        assertThat(nonEmptyListOf(1, 2, 3, 4, 5, 6)).isEqualTo(SimpleNonEmptyList(first = 1, remaining = listOf(2, 3, 4, 5, 6)))
        assertThat(nonEmptyListOf(1, 2, 3, 4, 5, 6, 7)).isEqualTo(SimpleNonEmptyList(first = 1, remaining = listOf(2, 3, 4, 5, 6, 7)))
        nonEmptyListOf(1, 2, 3, 4, 5, 6, 7, 8) succeedsAndShouldReturn SimpleNonEmptyList(first = 1, remaining = listOf(2, 3, 4, 5, 6, 7, 8))
        nonEmptyListOf(1, 2, 3, 4, 5, 6, 7, 8, 9) succeedsAndShouldReturn SimpleNonEmptyList(first = 1, remaining = listOf(2, 3, 4, 5, 6, 7, 8, 9))
    }

    @Test
    fun `returns true when to separate objects contain the same values`() {

        assertThat(nonEmptyListOf(1, 2, 3) == nonEmptyListOf(1, 2, 3)).isTrue()
    }
}