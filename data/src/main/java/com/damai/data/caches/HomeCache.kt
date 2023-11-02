package com.damai.data.caches

import android.content.SharedPreferences
import com.damai.base.BaseCache
import com.damai.base.extensions.remove
import com.damai.base.extensions.update
import com.damai.base.utils.Constants.CACHE_MINUTES
import com.damai.base.utils.TimeHelper
import java.util.concurrent.TimeUnit

/**
 * Created by damai007 on 31/October/2023
 */
class HomeCache constructor(
    private val preferences: SharedPreferences
) : BaseCache {

    fun setLatestUpdateExchangeRates(value: Long) {
        preferences.update(value to LATEST_EXCHANGE_RATES)
    }

    fun setLatestUpdateCurrencyNames(value: Long) {
        preferences.update(value to CURRENCY_NAMES)
    }

    fun isExchangeRatesCacheExpired(): Boolean {
        val lastUpdate = preferences.getLong(LATEST_EXCHANGE_RATES, 0L)
        return if (lastUpdate > 0L) {
            val timeNow = TimeHelper.getNow()
            val divideToMinutes = TimeUnit.MILLISECONDS.toMinutes(timeNow - lastUpdate)
            divideToMinutes > CACHE_MINUTES
        } else {
            true
        }
    }

    fun isCurrencyNamesCacheExpired(): Boolean {
        val lastUpdate = preferences.getLong(CURRENCY_NAMES, 0L)
        return if (lastUpdate > 0L) {
            val timeNow = TimeHelper.getNow()
            val divideToMinutes = TimeUnit.MILLISECONDS.toMinutes(timeNow - lastUpdate)
            divideToMinutes > CACHE_MINUTES
        } else {
            true
        }
    }

    override fun invalidate() {
        preferences.remove(
            LATEST_EXCHANGE_RATES,
            CURRENCY_NAMES
        )
    }

    companion object {
        private const val LATEST_EXCHANGE_RATES = "HomeCache.LATEST_EXCHANGE_RATES"
        private const val CURRENCY_NAMES = "HomeCache.CURRENCY_NAMES"
    }
}