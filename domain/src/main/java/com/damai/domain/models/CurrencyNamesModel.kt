package com.damai.domain.models

import com.damai.base.BaseModel

/**
 * Created by damai007 on 31/October/2023
 */
data class CurrencyNamesModel(
    val currencyList: List<Pair<String, String>>
) : BaseModel()
