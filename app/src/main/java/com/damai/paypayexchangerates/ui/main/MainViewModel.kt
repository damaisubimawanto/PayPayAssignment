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
    private var currentValueCurrencyBase = 1.0
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
                                /* Intermezzo, get the current value of the currency base. */
                                if (rate.code == model.base) {
                                    currentValueCurrencyBase = rate.value
                                }

                                /* Set the map item with RateModel. */
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
                            setBaseCurrencyCode(code = model.base)
                        }
                    }
                    is Resource.Error -> {

                    }
                }
            }
        }
    }

    fun doExchangeRatesCalculation(givenValue: Double) {
        if (givenValue == 0.0) return
        latestRateValue = givenValue

        viewModelScope.launch(dispatcher.main()) {
            val newList = exchangeRatePoolList.map {
                val newValue = givenValue * it.value / currentValueCurrencyBase
                it.copy(
                    value = newValue
                )
            }
            newList.let(_exchangeRateListLiveData::setValue)
        }
    }

    private fun setBaseCurrencyCode(code: String) {
        code.let(_currencyBaseLiveData::postValue)
    }

    fun changeBaseCurrency(code: String) {
        setBaseCurrencyCode(code = code)
        val rate = exchangeRatePoolList.find {
            it.code == code
        }
        rate?.let {
            currentValueCurrencyBase = it.value
            doExchangeRatesCalculation(givenValue = latestRateValue)
        }
    }
}