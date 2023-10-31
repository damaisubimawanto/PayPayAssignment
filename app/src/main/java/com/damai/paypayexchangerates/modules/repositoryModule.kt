package com.damai.paypayexchangerates.modules

import com.damai.data.repos.HomeRepositoryImpl
import com.damai.domain.repositories.HomeRepository
import org.koin.dsl.module

/**
 * Created by damai007 on 30/October/2023
 */

val repositoryModule = module {
    single<HomeRepository> {
        HomeRepositoryImpl(
            homeService = get(),
            dispatcher = get(),
            currencyNameDao = get(),
            rateDao = get(),
            exchangeRatesMapper = get(),
            currencyNamesMapper = get(),
            currencyNameEntityToModelMapper = get(),
            currencyNamePairToCurrencyNameEntityMapper = get(),
            rateEntityToRateModelMapper = get(),
            rateModelToRateEntityMapper = get()
        )
    }
}