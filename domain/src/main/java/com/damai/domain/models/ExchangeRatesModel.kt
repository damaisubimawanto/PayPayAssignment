package com.damai.domain.models

import com.damai.base.BaseModel

/**
 * Created by damai007 on 30/October/2023
 */
data class ExchangeRatesModel(
    val timestamp: Long,
    val base: String,
    val rates: List<RateModel>?
) : BaseModel()

data class RateModel(
    val code: String,
    val name: String,
    val value: Double
)