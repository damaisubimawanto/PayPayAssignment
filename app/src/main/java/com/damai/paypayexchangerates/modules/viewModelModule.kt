package com.damai.paypayexchangerates.modules

import com.damai.paypayexchangerates.ui.main.MainViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Created by damai007 on 30/October/2023
 */

val viewModelModule = module {
    viewModel {
        MainViewModel(
            app = androidApplication(),
            dispatcher = get(),
            getLatestExchangeRatesUseCase = get(),
            getCurrencyNamesUseCase = get()
        )
    }
}