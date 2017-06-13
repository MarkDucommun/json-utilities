package com.hcsc.de.claims.results.observableResults

import com.hcsc.de.claims.results.Result
import io.reactivex.Observable
import io.reactivex.Single

typealias ObservableResult<failureType, successType> = Observable<Result<failureType, successType>>

typealias SingleResult<failureType, successType> = Single<Result<failureType, successType>>
