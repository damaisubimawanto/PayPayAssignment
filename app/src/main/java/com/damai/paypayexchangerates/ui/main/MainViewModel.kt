package com.damai.paypayexchangerates.ui.main

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.damai.base.BaseViewModel
import com.damai.base.coroutines.DispatcherProvider
import com.damai.base.extensions.asLiveData
import com.damai.domain.models.RateModel
import com.damai.domain.usecases.GetLatestExchangeRatesUseCase

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

    fun getExchangeRates() {

    }
}