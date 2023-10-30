package com.damai.data.mappers

import com.damai.base.BaseMapper
import com.damai.base.extensions.getResponseErrorMessage
import com.damai.base.extensions.getResponseStatus
import com.damai.base.extensions.orZero
import com.damai.data.responses.ExchangeRatesResponse
import com.damai.domain.models.ExchangeRatesModel
import com.damai.domain.models.RateModel

/**
 * Created by damai007 on 30/October/2023
 */
class ExchangeRatesResponseToExchangeRatesModelMapper : BaseMapper<ExchangeRatesResponse, ExchangeRatesModel>() {

    override fun map(value: ExchangeRatesResponse): ExchangeRatesModel {
        return ExchangeRatesModel(
            timestamp = value.timestamp.orZero(),
            base = value.base.orEmpty(),
            rates = value.rates?.map {
                RateModel(
                    code = it.key,
                    name = "",
                    value = it.value
                )
            }
        ).also {
            it.status = value.getResponseStatus()
            it.message = value.getResponseErrorMessage()
        }
    }
}