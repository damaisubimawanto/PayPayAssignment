package com.damai.paypayexchangerates.modules

import com.damai.data.mappers.CurrencyNamesResponseToCurrencyNamesModelMapper
import com.damai.data.mappers.ExchangeRatesResponseToExchangeRatesModelMapper
import org.koin.dsl.module

/**
 * Created by damai007 on 30/October/2023
 */

val mapperModule = module {
    factory {
        ExchangeRatesResponseToExchangeRatesModelMapper()
    }
    factory {
        CurrencyNamesResponseToCurrencyNamesModelMapper()
    }
}