package com.damai.paypayexchangerates.ui.main

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.damai.base.BaseViewModel
import com.damai.base.coroutines.DispatcherProvider
import com.damai.base.extensions.asLiveData
import com.damai.base.networks.Resource
import com.damai.domain.models.RateModel
import com.damai.domain.usecases.GetCurrencyNamesUseCase
import com.damai.domain.usecases.GetLatestExchangeRatesUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Created by damai007 on 30/October/2023
 */
class MainViewModel(
    app: Application,
    private val dispatcher: DispatcherProvider,
    private val getLatestExchangeRatesUseCase: GetLatestExchangeRatesUseCase,
    private val getCurrencyNamesUseCase: GetCurrencyNamesUseCase
) : BaseViewModel(app = app) {

    //region Live Data
    private val _exchangeRateListLiveData = MutableLiveData<List<RateModel>>()
    val exchangeRateListLiveData = _exchangeRateListLiveData.asLiveData()

    private val _currencyBaseLiveData = MutableLiveData<String>()
    val currencyBaseLiveData = _currencyBaseLiveData.asLiveData()
    //endregion `Live Data`

    //region Variable Data
    private val exchangeRatePoolList: MutableList<RateModel> = mutableListOf()
    private var latestRateValue = 1.0
    //endregion `Variable Data`

    fun getExchangeRates() {
        viewModelScope.launch(dispatcher.io()) {
            val currencyNameListResource = async {
                getCurrencyNamesUseCase().first()
            }.await()
            val currencyNameList = when (currencyNameListResource) {
                is Resource.Success -> {
                    currencyNameListResource.model?.currencyList
                }
                is Resource.Error -> {
                    null
                }
            }

            getLatestExchangeRatesUseCase().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.model?.let { model ->
                            exchangeRatePoolList.clear()

                            /* We want to join the currency name from currencyNameList into
                             * the exchangeRatePoolList. */
                            model.rates?.map { rate ->
                                val currency = currencyNameList?.find {
                                    it.first == rate.code
                                }
                                if (currency == null) {
                                    rate
                                } else {
                                    rate.copy(
                                        name = currency.second
                                    )
                                }
                            }?.let(exchangeRatePoolList::addAll)    /* Added the joined list. */
                            exchangeRatePoolList.let(_exchangeRateListLiveData::postValue)

                            model.base.let(_currencyBaseLiveData::postValue)
                        }
                    }
                    is Resource.Error -> {

                    }
                }
            }
        }
    }

    fun doExchangeRatesCalculation(givenValue: Double) {
        if (latestRateValue == givenValue) return
        latestRateValue = givenValue

        viewModelScope.launch(dispatcher.main()) {
            val newList = exchangeRatePoolList.map {
                val newValue = givenValue * it.value
                it.copy(
                    value = newValue
                )
            }
            newList.let(_exchangeRateListLiveData::setValue)
        }
    }
}