package com.damai.base.bindingadapters

import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter

/**
 * Created by damai007 on 30/October/2023
 */
object ViewBindingAdapter {

    @JvmStatic
    @BindingAdapter("ratesValue")
    fun bindRatesValue(view: AppCompatTextView, ratesValue: Double) {
        val ratesText = ratesValue.toString()
        view.text = ratesText
    }

    @JvmStatic
    @BindingAdapter(value = ["currencyName", "currencyCode"], requireAll = true)
    fun bindCurrencyNameAndCode(view: AppCompatTextView, name: String, code: String) {
        val currencyText = "$name $code"
        view.text = currencyText
    }
}