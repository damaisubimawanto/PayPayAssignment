package com.damai.paypayexchangerates.ui.main

import com.damai.base.BaseActivity
import com.damai.base.extensions.addOnTextChanged
import com.damai.base.extensions.observe
import com.damai.base.extensions.setCursorAtEnd
import com.damai.base.extensions.setCustomOnClickListener
import com.damai.paypayexchangerates.R
import com.damai.paypayexchangerates.databinding.ActivityMainBinding
import com.damai.paypayexchangerates.navigations.PageNavigationApi
import com.damai.paypayexchangerates.ui.main.adapter.RatesAdapter
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    //region Variables
    private lateinit var ratesAdapter: RatesAdapter
    private val pageNavigationApi: PageNavigationApi by inject()
    //endregion `Variables`

    override val layoutResource: Int = R.layout.activity_main

    override val viewModel: MainViewModel by viewModel()

    override fun ActivityMainBinding.viewInitialization() {
        vm = viewModel
        lifecycleOwner = this@MainActivity

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
                    return@addOnTextChanged
                }
                text.toDoubleOrNull()?.let(viewModel::doExchangeRatesCalculation)
            }
        }

        btnCurrency.setCustomOnClickListener {
            pageNavigationApi.openCurrencyNameBottomSheetDialog(
                fragmentActivity = this@MainActivity
            )
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