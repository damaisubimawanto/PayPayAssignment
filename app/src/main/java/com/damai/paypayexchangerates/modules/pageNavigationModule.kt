package com.damai.paypayexchangerates.modules

import com.damai.paypayexchangerates.navigations.PageNavigationApi
import com.damai.paypayexchangerates.navigations.PageNavigationApiImpl
import org.koin.dsl.module

/**
 * Created by damai007 on 30/October/2023
 */

val pageNavigationModule = module {
    factory<PageNavigationApi> {
        PageNavigationApiImpl()
    }
}