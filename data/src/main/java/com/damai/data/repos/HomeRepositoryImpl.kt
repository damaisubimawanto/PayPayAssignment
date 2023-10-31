package com.damai.data.repos

import com.damai.base.coroutines.DispatcherProvider
import com.damai.base.extensions.mappingResponseError
import com.damai.base.networks.NetworkResource
import com.damai.base.networks.Resource
import com.damai.base.utils.Constants.API_KEY
import com.damai.base.utils.Constants.CURRENCY_BASE
import com.damai.data.apiservices.HomeService
import com.damai.data.mappers.CurrencyNamesResponseToCurrencyNamesModelMapper
import com.damai.data.mappers.ExchangeRatesResponseToExchangeRatesModelMapper
import com.damai.data.responses.CurrencyNamesResponse
import com.damai.domain.models.CurrencyNamesModel
import com.damai.domain.models.ExchangeRatesModel
import com.damai.domain.repositories.HomeRepository
import kotlinx.coroutines.flow.Flow

/**
 * Created by damai007 on 30/October/2023
 */
class HomeRepositoryImpl(
    private val homeService: HomeService,
    private val dispatcher: DispatcherProvider,
    private val exchangeRatesMapper: ExchangeRatesResponseToExchangeRatesModelMapper,
    private val currencyNamesMapper: CurrencyNamesResponseToCurrencyNamesModelMapper
) : HomeRepository {

    override fun getLatestExchangeRates(): Flow<Resource<ExchangeRatesModel>> {
        return object : NetworkResource<ExchangeRatesModel>(
            dispatcherProvider = dispatcher
        ) {
            override suspend fun remoteFetch(): ExchangeRatesModel {
                val response = homeService.getLatestExchangeRates(
                    appId = API_KEY,
                    base = CURRENCY_BASE
                )
                return exchangeRatesMapper.map(value = response)
            }

            override fun shouldFetchFromRemote(): Boolean = false

            override fun shouldSaveToLocal(): Boolean = true

            override suspend fun localFetch(): ExchangeRatesModel? {
                return super.localFetch()
            }

            override suspend fun saveLocal(data: ExchangeRatesModel) {
                super.saveLocal(data)
            }
        }.asFlow()
    }

    override fun getCurrencyNames(): Flow<Resource<CurrencyNamesModel>> {
        return object : NetworkResource<CurrencyNamesModel>(
            dispatcherProvider = dispatcher
        ) {
            override suspend fun remoteFetch(): CurrencyNamesModel {
                val response = homeService.getCurrencyNames(
                    appId = API_KEY
                )
                return currencyNamesMapper.map(
                    value = CurrencyNamesResponse().apply {
                        currencyMap = response
                        mappingResponseError(response = response)
                    }
                )
            }
        }.asFlow()
    }
}