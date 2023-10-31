package com.damai.data.apiservices

import com.damai.data.responses.ExchangeRatesResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by damai007 on 30/October/2023
 */
interface HomeService {

    @GET("/api/latest.json")
    suspend fun getLatestExchangeRates(
        @Query("app_id") appId: String,
        @Query("base") base: String
    ): ExchangeRatesResponse

    @GET("/api/currencies.json")
    suspend fun getCurrencyNames(
        @Query("app_id") appId: String
    ): Map<String, String>
}