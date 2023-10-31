package com.damai.paypayexchangerates.ui.currencylist

import com.damai.base.BaseBottomSheetDialogFragment
import com.damai.base.extensions.getScreenHeight
import com.damai.base.extensions.observe
import com.damai.paypayexchangerates.R
import com.damai.paypayexchangerates.databinding.FragmentCurrencyListBinding
import com.damai.paypayexchangerates.ui.currencylist.adapter.CurrencyNameAdapter
import com.damai.paypayexchangerates.ui.main.MainViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

/**
 * Created by damai007 on 31/October/2023
 */
class CurrencyListBottomSheetDialog : BaseBottomSheetDialogFragment<FragmentCurrencyListBinding, MainViewModel>() {

    //region Variables
    private lateinit var currencyNameAdapter: CurrencyNameAdapter
    //endregion `Variables`

    override val layoutResource: Int = R.layout.fragment_currency_list

    override val viewModel: MainViewModel by activityViewModel()

    override fun onStart() {
        super.onStart()
        setBottomSheetFullScreen(
            height = getScreenHeight(),
            bottomSheet = binding.root,
            skipCollapsed = true
        )
    }

    override fun FragmentCurrencyListBinding.viewInitialization() {
        with(rvCurrencyNames) {
            currencyNameAdapter = CurrencyNameAdapter { currencyCode ->
                viewModel.setBaseCurrencyCode(code = currencyCode)
                dialog?.dismiss()
            }
            adapter = currencyNameAdapter
        }
    }

    override fun FragmentCurrencyListBinding.setupObservers() {
        observe(viewModel.exchangeRateListLiveData) {
            currencyNameAdapter.submitList(it)
        }
    }
}