package com.damai.data.mappers

import com.damai.base.BaseMapper
import com.damai.base.extensions.getResponseErrorMessage
import com.damai.base.extensions.getResponseStatus
import com.damai.data.responses.CurrencyNamesResponse
import com.damai.domain.models.CurrencyNamesModel

/**
 * Created by damai007 on 31/October/2023
 */
class CurrencyNamesResponseToCurrencyNamesModelMapper : BaseMapper<CurrencyNamesResponse, CurrencyNamesModel>() {

    override fun map(value: CurrencyNamesResponse): CurrencyNamesModel {
        return CurrencyNamesModel(
            currencyList = value.currencyMap?.map {
                Pair(it.key, it.value)
            } ?: listOf()
        ).also {
            it.status = value.getResponseStatus()
            it.message = value.getResponseErrorMessage()
        }
    }
}