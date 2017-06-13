package com.hcsc.de.claims.results.observableResults

import com.hcsc.de.claims.results.Result
import io.reactivex.Flowable
import io.reactivex.Single

fun <failureType, successType> List<SingleResult<failureType, successType>>
        .concat(): Flowable<Result<failureType, successType>> = Single.concat(this)

