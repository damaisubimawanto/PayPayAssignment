package com.damai.paypayexchangerates.modules

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.damai.base.utils.Constants.HOME_PREFERENCES
import com.damai.data.caches.HomeCache
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

/**
 * Created by damai007 on 31/October/2023
 */

val cacheModule = module {
    single<SharedPreferences> {
        androidApplication().run {
            getSharedPreferences(
                HOME_PREFERENCES,
                MODE_PRIVATE
            )
        }
    }

    factory {
        HomeCache(
            preferences = get()
        )
    }
}