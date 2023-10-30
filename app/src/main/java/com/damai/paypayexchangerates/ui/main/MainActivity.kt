package com.damai.paypayexchangerates.ui.main

import com.damai.base.BaseActivity
import com.damai.base.extensions.addOnTextChanged
import com.damai.base.extensions.observe
import com.damai.paypayexchangerates.R
import com.damai.paypayexchangerates.databinding.ActivityMainBinding
import com.damai.paypayexchangerates.ui.main.adapter.RatesAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    //region Variables
    private lateinit var ratesAdapter: RatesAdapter
    //endregion `Variables`

    override val layoutResource: Int = R.layout.activity_main

    override val viewModel: MainViewModel by viewModel()

    override fun ActivityMainBinding.viewInitialization() {
        with(rvExchangeRates) {
            ratesAdapter = RatesAdapter()
            adapter = ratesAdapter
        }
    }

    override fun ActivityMainBinding.setupListeners() {
        etAmount.addOnTextChanged { text ->
            if (text.isEmpty()) {
                etAmount.setText("1")
                return@addOnTextChanged
            }


        }
    }

    override fun ActivityMainBinding.setupObservers() {
        observe(viewModel.exchangeRateListLiveData) {
            ratesAdapter.submitList(it)
        }
    }

    override fun ActivityMainBinding.onPreparationFinished() {
        viewModel.getExchangeRates()
    }
}