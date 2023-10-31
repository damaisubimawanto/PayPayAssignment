package com.damai.paypayexchangerates.modules

import com.damai.paypayexchangerates.application.AppDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

/**
 * Created by damai007 on 30/October/2023
 */

val roomModule = module {
    single {
        AppDatabase.buildDatabase(application = androidApplication())
    }

    factory {
        get<AppDatabase>().rateDao()
    }

    factory {
        get<AppDatabase>().currencyNameDao()
    }
}