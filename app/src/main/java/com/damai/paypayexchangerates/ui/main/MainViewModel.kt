package com.damai.paypayexchangerates.ui.main

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.damai.base.BaseViewModel
import com.damai.base.coroutines.DispatcherProvider
import com.damai.base.extensions.asLiveData
import com.damai.base.networks.Resource
import com.damai.domain.models.RateModel
import com.damai.domain.usecases.GetLatestExchangeRatesUseCase
import kotlinx.coroutines.launch

/**
 * Created by damai007 on 30/October/2023
 */
class MainViewModel(
    app: Application,
    private val dispatcher: DispatcherProvider,
    private val getLatestExchangeRatesUseCase: GetLatestExchangeRatesUseCase
) : BaseViewModel(app = app) {

    //region Live Data
    private val _exchangeRateListLiveData = MutableLiveData<List<RateModel>>()
    val exchangeRateListLiveData = _exchangeRateListLiveData.asLiveData()
    //endregion `Live Data`

    //region Variable Data
    private val exchangeRatePoolList: MutableList<RateModel> = mutableListOf()
    private var latestRateValue = 1.0
    //endregion `Variable Data`

    fun getExchangeRates() {
        viewModelScope.launch(dispatcher.io()) {
            getLatestExchangeRatesUseCase().collect { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.model?.let { model ->
                            exchangeRatePoolList.clear()
                            model.rates?.let(exchangeRatePoolList::addAll)
                            _exchangeRateListLiveData.postValue(exchangeRatePoolList)
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