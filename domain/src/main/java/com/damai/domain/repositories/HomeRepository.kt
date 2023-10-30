package com.damai.domain.repositories

import com.damai.base.networks.Resource
import com.damai.domain.models.ExchangeRatesModel
import kotlinx.coroutines.flow.Flow
import kotlin.jvm.Throws

/**
 * Created by damai007 on 30/October/2023
 */
interface HomeRepository {

    @Throws(Exception::class)
    fun getLatestExchangeRates(): Flow<Resource<ExchangeRatesModel>>
}