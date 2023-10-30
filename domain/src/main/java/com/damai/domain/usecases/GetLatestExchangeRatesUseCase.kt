package com.damai.domain.usecases

import com.damai.base.networks.FlowUseCase
import com.damai.base.networks.Resource
import com.damai.domain.models.ExchangeRatesModel
import com.damai.domain.repositories.HomeRepository
import kotlinx.coroutines.flow.Flow

/**
 * Created by damai007 on 30/October/2023
 */
class GetLatestExchangeRatesUseCase(
    private val homeRepository: HomeRepository
): FlowUseCase<Nothing, ExchangeRatesModel>() {

    override suspend fun execute(parameters: Nothing?): Flow<Resource<ExchangeRatesModel>> {
        return homeRepository.getLatestExchangeRates()
    }
}