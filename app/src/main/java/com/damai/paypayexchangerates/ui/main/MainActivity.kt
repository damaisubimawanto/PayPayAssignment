package com.damai.paypayexchangerates.ui.main

import com.damai.base.BaseActivity
import com.damai.base.extensions.addOnTextChanged
import com.damai.base.extensions.observe
import com.damai.base.extensions.setCursorAtEnd
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
        with(etAmount) {
            setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    v.post {
                        setCursorAtEnd()
                    }
                }
            }

            addOnTextChanged { text ->
                if (text.isEmpty()) {
                    setText("1")
                    setCursorAtEnd()
                    return@addOnTextChanged
                }
                text.toDoubleOrNull()?.let(viewModel::doExchangeRatesCalculation)
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