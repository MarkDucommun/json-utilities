package com.hcsc.de.claims.results

fun <failureType, successType> successType.asSuccess(): Result<failureType, successType> = Success(this)