package com.hcsc.de.claims.jsonSizing

import com.hcsc.de.claims.distributions.NormalIntDistribution
import com.hcsc.de.claims.results.flatMap
import com.hcsc.de.claims.results.map
import com.hcsc.de.claims.results.traverse

class JsonSizeSorter {

    fun <numberType : Number> sort(input: JsonSizeOverview<numberType>): com.hcsc.de.claims.results.Result<String, JsonSizeOverview<numberType>> {

        return when (input) {
            is JsonSizeLeafOverview -> com.hcsc.de.claims.results.Success(input)
            is JsonSizeObjectOverview -> {

                val inputWithSortedChildren = input.copy(children = input.children.sortedByDescending { (it.overview.size as NormalIntDistribution).average })

                inputWithSortedChildren.children
                        .map { (overview, presence) ->
                            sort(overview).map { sortedOverview ->
                                JsonSizeObjectChild(overview = sortedOverview, presence = presence)
                            }
                        }
                        .traverse()
                        .flatMap { sortedChildrenWithRecursivelySortedChildren ->
                            val inputWithRecursivelySortedChildren = inputWithSortedChildren.copy(children = sortedChildrenWithRecursivelySortedChildren)
                            com.hcsc.de.claims.results.Success<String, JsonSizeOverview<numberType>>(inputWithRecursivelySortedChildren)
                        }
            }
            is JsonSizeArrayOverview -> sort(input.averageChild).map { averageChild -> input.copy(averageChild = averageChild) }
        }
    }
}