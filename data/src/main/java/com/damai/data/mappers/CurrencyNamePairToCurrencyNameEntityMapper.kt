package com.damai.data.mappers

import com.damai.base.BaseMapper
import com.damai.domain.entities.CurrencyNameEntity
import com.damai.domain.models.CurrencyNamesModel

/**
 * Created by damai007 on 31/October/2023
 */
class CurrencyNamePairToCurrencyNameEntityMapper : BaseMapper<Pair<String, String>, CurrencyNameEntity>() {

    override fun map(value: Pair<String, String>): CurrencyNameEntity {
        return CurrencyNameEntity(
            code = value.first,
            name = value.second
        )
    }
}