package com.hcsc.de.claims.distributions.generation

import com.hcsc.de.claims.distributions.Distribution
import com.hcsc.de.claims.distributions.generation.DistributionRequest.FixedWidthBinDistributionRequest
import com.hcsc.de.claims.results.Failure
import com.hcsc.de.claims.results.Result


interface DistributionGenerator<numberType: Number> {

    fun profile(list: List<numberType>): Result<String, DistributionProfile<numberType>>
}

interface BinDistributionGenerator<numberType: Number, in requestType: DistributionRequest<numberType>> {

   fun create(request: requestType): Result<String, DistributionProfile<numberType>>
}

interface DualDistributionGenerator<numberType: Number> {

    fun profile(listOne: List<numberType>, listTwo: List<numberType>): Result<String, DistributionProfile<numberType>>
}

class FixedWidthBinDistributionGenerator<numberType: Number>(
        private val toType: Double.() -> numberType
) : BinDistributionGenerator<numberType, FixedWidthBinDistributionRequest<numberType>> {

    override fun create(request: FixedWidthBinDistributionRequest<numberType>): Result<String, DistributionProfile<numberType>> {

        val (list, binWidth) = request

        return if (list.isNotEmpty()) {

            val doubleList = list.map(Number::toDouble)

            val startValue = doubleList.min()!!
            val endValue = doubleList.max()!!

            TODO()

        } else {
            Failure("")
        }
    }
}

class MinimizedBinSizeDistributionGenerator<numberType: Number> : DistributionGenerator<numberType> {

    override fun profile(list: List<numberType>): Result<String, DistributionProfile<numberType>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

class IdealBinCountDistributionGenerator<numberType: Number> : DistributionGenerator<numberType> {

    override fun profile(list: List<numberType>): Result<String, DistributionProfile<numberType>> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

data class DistributionProfile<out numberType: Number>(
        val pValue: Double,
        val distribution: Distribution<numberType>
)

sealed class DistributionRequest<out numberType: Number> {

    abstract val list: List<numberType>

    data class FixedWidthBinDistributionRequest<out numberType: Number>(
            override val list: List<numberType>,
            val binWidth: numberType
    ) : DistributionRequest<numberType>()

    data class VariableWidthBinSizeDistributionRequest<out numberType: Number>(
            override val list: List<numberType>,
            val binSize: Int
    ) : DistributionRequest<numberType>()

    data class VariableWidthBinCountDistributionRequest<out numberType: Number>(
            override val list: List<numberType>,
            val binCount: Int
    ) : DistributionRequest<numberType>()

    data class FixedWidthBinCountDistributionRequest<out numberType: Number>(
            override val list: List<numberType>,
            val binCount: Int
    ) : DistributionRequest<numberType>()
}