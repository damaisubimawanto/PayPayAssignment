package com.damai.paypayexchangerates.modules

import com.damai.domain.usecases.GetCurrencyNamesUseCase
import com.damai.domain.usecases.GetLatestExchangeRatesUseCase
import org.koin.dsl.module

/**
 * Created by damai007 on 30/October/2023
 */

val useCaseModule = module {
    single {
        GetLatestExchangeRatesUseCase(
            homeRepository = get()
        )
    }
    single {
        GetCurrencyNamesUseCase(
            homeRepository = get()
        )
    }
}