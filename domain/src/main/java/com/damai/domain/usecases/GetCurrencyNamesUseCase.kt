package com.damai.domain.usecases

import com.damai.base.networks.FlowUseCase
import com.damai.base.networks.Resource
import com.damai.domain.models.CurrencyNamesModel
import com.damai.domain.repositories.HomeRepository
import kotlinx.coroutines.flow.Flow

/**
 * Created by damai007 on 31/October/2023
 */
class GetCurrencyNamesUseCase(
    private val homeRepository: HomeRepository
): FlowUseCase<Nothing, CurrencyNamesModel>() {

    override suspend fun execute(parameters: Nothing?): Flow<Resource<CurrencyNamesModel>> {
        return homeRepository.getCurrencyNames()
    }
}