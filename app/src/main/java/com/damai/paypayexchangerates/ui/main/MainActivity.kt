package com.damai.paypayexchangerates.ui.main

import android.util.Log
import com.damai.base.BaseActivity
import com.damai.base.extensions.observe
import com.damai.paypayexchangerates.R
import com.damai.paypayexchangerates.databinding.ActivityMainBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    override val layoutResource: Int = R.layout.activity_main

    override val viewModel: MainViewModel by viewModel()

    override fun ActivityMainBinding.viewInitialization() {
        // TODO("Not yet implemented")
    }

    override fun ActivityMainBinding.setupListeners() {
        // TODO("Not yet implemented")
    }

    override fun ActivityMainBinding.setupObservers() {
        observe(viewModel.exchangeRateListLiveData) {
            Log.d("zxczxc", "Exchange Rate List = ${it.joinToString()}")
        }
    }

    override fun ActivityMainBinding.onPreparationFinished() {
        viewModel.getExchangeRates()
    }
}