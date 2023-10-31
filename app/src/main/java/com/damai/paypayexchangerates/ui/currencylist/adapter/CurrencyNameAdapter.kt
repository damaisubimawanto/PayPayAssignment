package com.damai.paypayexchangerates.ui.currencylist.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import com.damai.base.BaseViewHolder
import com.damai.base.extensions.setCustomOnClickListener
import com.damai.domain.models.RateModel
import com.damai.paypayexchangerates.R
import com.damai.paypayexchangerates.databinding.ItemRecyclerCurrencyNameBinding
import com.damai.paypayexchangerates.ui.main.adapter.RatesComparator

/**
 * Created by damai007 on 31/October/2023
 */
class CurrencyNameAdapter(
    private val callback: (currencyCode: String) -> Unit
) : ListAdapter<RateModel, CurrencyNameAdapter.CurrencyNameVH>(
    RatesComparator
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyNameVH {
        val binding = DataBindingUtil.inflate<ItemRecyclerCurrencyNameBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_recycler_currency_name,
            parent,
            false
        )
        return CurrencyNameVH(binding = binding)
    }

    override fun onBindViewHolder(holder: CurrencyNameVH, position: Int) {
        holder.bind(
            data = currentList[position],
            position = position
        )
    }

    inner class CurrencyNameVH(
        binding: ItemRecyclerCurrencyNameBinding
    ) : BaseViewHolder<ItemRecyclerCurrencyNameBinding, RateModel>(binding = binding) {

        override fun bind(data: RateModel, position: Int) {
            with(binding) {
                model = data
                if (hasPendingBindings()) {
                    executePendingBindings()
                }

                clMainItem.setCustomOnClickListener {
                    callback.invoke(data.code)
                }
            }
        }
    }
}