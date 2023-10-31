package com.damai.base.bindingadapters

import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter
import com.damai.base.extensions.loadImageWithCenterCrop
import com.damai.base.utils.Constants.RANDOM_IMAGE_URL
import java.text.NumberFormat
import java.util.Locale

/**
 * Created by damai007 on 30/October/2023
 */
object ViewBindingAdapter {

    @JvmStatic
    @BindingAdapter("ratesValue")
    fun bindRatesValue(view: AppCompatTextView, ratesValue: Double) {
        val currencyFormatter = NumberFormat.getCurrencyInstance(Locale.US).apply {
            maximumFractionDigits = 5
            minimumFractionDigits = 2
        }
        val ratesText = currencyFormatter.format(ratesValue)
        val symbol = ratesText.substring(startIndex = 0, endIndex = 1)
        if (symbol.toIntOrNull() == null) {
            val ratesWithoutSymbolText = ratesText.substring(
                startIndex = 1,
                endIndex = ratesText.length
            )
            view.text = ratesWithoutSymbolText
        } else {
            view.text = ratesText
        }
    }

    @JvmStatic
    @BindingAdapter(value = ["currencyName", "currencyCode"], requireAll = true)
    fun bindCurrencyNameAndCode(view: AppCompatTextView, name: String, code: String) {
        val currencyText = "$name $code"
        view.text = currencyText
    }

    @JvmStatic
    @BindingAdapter("uniqueCode")
    fun bindLoadBackgroundImage(view: AppCompatImageView, uniqueCode: String) {
        view.loadImageWithCenterCrop(url = "$RANDOM_IMAGE_URL$uniqueCode")
    }
}