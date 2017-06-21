package com.hcsc.de.claims.distributions.generation

sealed class DistributionRequest<out numberType: Number>{

    abstract val list: List<numberType>

    data class FixedWidthBinDistributionRequest<out numberType: Number>(
            override val list: List<numberType>,
            val binWidth: numberType
    ) : DistributionRequest<numberType>()

    data class MinimizeBinSizeDistributionRequest<out numberType: Number>(
            override val list: List<numberType>,
            val binSize: Int
    ) : DistributionRequest<numberType>()

    data class VariableWidthBinCountDistributionRequest<out numberType: Number>(
            override val list: List<numberType>,
            val binCount: Int
    ) : DistributionRequest<numberType>()

    data class FixedWidthBinCountDistributionRequest<out numberType: Number>(
            override val list: List<numberType>,
            val binCount: Int,
            val minimum: numberType? = null,
            val maximum: numberType? = null
    ) : DistributionRequest<numberType>()

    data class IdealBinCountDistributionRequest<out numberType: Number>(
            override val list: List<numberType>
    ) : DistributionRequest<numberType>()

    sealed class DualDistributionRequest<out numberType: Number> : DistributionRequest<numberType>() {

        abstract val listTwo: List<numberType>

        data class FixedWidthBinDistributionRequest<out numberType: Number>(
                override val list: List<numberType>,
                override val listTwo: List<numberType>,
                val binWidth: numberType
        ) : DualDistributionRequest<numberType>()

        data class MinimizeBinSizeDistributionRequest<out numberType: Number>(
                override val list: List<numberType>,
                override val listTwo: List<numberType>,
                val binSize: Int
        ) : DualDistributionRequest<numberType>()

        data class VariableWidthBinCountDistributionRequest<out numberType: Number>(
                override val list: List<numberType>,
                override val listTwo: List<numberType>,
                val binCount: Int
        ) : DualDistributionRequest<numberType>()

        data class FixedWidthBinCountDistributionRequest<out numberType: Number>(
                override val list: List<numberType>,
                override val listTwo: List<numberType>,
                val binCount: Int
        ) : DualDistributionRequest<numberType>()

        data class IdealBinCountDualDistributionRequest<out numberType: Number>(
                override val list: List<numberType>,
                override val listTwo: List<numberType>,
                val minimumBinSize: Int = 5
        ) : DualDistributionRequest<numberType>()
    }
}