package com.damai.paypayexchangerates.modules

import com.damai.data.mappers.CurrencyNameEntityToCurrencyNamesModelMapper
import com.damai.data.mappers.CurrencyNamePairToCurrencyNameEntityMapper
import com.damai.data.mappers.CurrencyNamesResponseToCurrencyNamesModelMapper
import com.damai.data.mappers.ExchangeRatesResponseToExchangeRatesModelMapper
import com.damai.data.mappers.RateEntityToRateModelMapper
import com.damai.data.mappers.RateModelToRateEntityMapper
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
    factory {
        CurrencyNameEntityToCurrencyNamesModelMapper()
    }
    factory {
        CurrencyNamePairToCurrencyNameEntityMapper()
    }
    factory {
        RateEntityToRateModelMapper()
    }
    factory {
        RateModelToRateEntityMapper()
    }
}