package com.damai.data.mappers

import com.damai.base.BaseMapper
import com.damai.domain.entities.CurrencyNameEntity
import com.damai.domain.models.CurrencyNamesModel

/**
 * Created by damai007 on 31/October/2023
 */
class CurrencyNameEntityToCurrencyNamesModelMapper : BaseMapper<List<CurrencyNameEntity>, CurrencyNamesModel>() {

    override fun map(value: List<CurrencyNameEntity>): CurrencyNamesModel {
        return CurrencyNamesModel(
            currencyList = value.map {
                Pair(it.code, it.name)
            }
        )
    }
}