package com.hcsc.de.claims.jsonSizing

import com.hcsc.de.claims.helpers.Result
import com.hcsc.de.claims.helpers.Success
import com.hcsc.de.claims.helpers.flatMap
import com.hcsc.de.claims.helpers.traverse

class JsonSizeSorter {

    fun sort(input: JsonSizeOverview): Result<String, JsonSizeOverview> {

        return when (input) {
            is JsonSizeLeafOverview -> Success(input)
            is JsonSizeObjectOverview -> input.children.map { sort(it) }.traverse().flatMap { sortedChildren: List<JsonSizeOverview> ->
                Success<String, JsonSizeOverview>(input.copy(children = sortedChildren.sortedByDescending { it.size.average }))
            }
            is JsonSizeArrayOverview -> sort(input.averageChild).flatMap { averageChild ->
                Success<String, JsonSizeOverview>(input.copy(averageChild = averageChild))
            }
        }
    }
}