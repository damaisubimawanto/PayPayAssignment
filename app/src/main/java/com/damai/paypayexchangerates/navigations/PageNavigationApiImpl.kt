package com.damai.paypayexchangerates.navigations

import androidx.fragment.app.FragmentActivity
import com.damai.base.utils.Constants.TAG_CURRENCY_NAME_LIST
import com.damai.paypayexchangerates.ui.currencylist.CurrencyListBottomSheetDialog

/**
 * Created by damai007 on 31/October/2023
 */
class PageNavigationApiImpl : PageNavigationApi {

    override fun openCurrencyNameBottomSheetDialog(
        fragmentActivity: FragmentActivity
    ) {
        val fragment = CurrencyListBottomSheetDialog()
        fragment.show(
            fragmentActivity.supportFragmentManager,
            TAG_CURRENCY_NAME_LIST
        )
    }
}