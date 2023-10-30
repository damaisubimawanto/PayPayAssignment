package com.damai.paypayexchangerates.ui.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import com.damai.base.BaseViewHolder
import com.damai.domain.models.RateModel
import com.damai.paypayexchangerates.R
import com.damai.paypayexchangerates.databinding.ItemRecyclerRatesBinding

/**
 * Created by damai007 on 30/October/2023
 */
class RatesAdapter : ListAdapter<RateModel, RatesAdapter.RatesVH>(RatesComparator) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RatesVH {
        val binding = DataBindingUtil.inflate<ItemRecyclerRatesBinding>(
            LayoutInflater.from(parent.context),
            R.layout.item_recycler_rates,
            parent,
            false
        )
        return RatesVH(binding = binding)
    }

    override fun onBindViewHolder(holder: RatesVH, position: Int) {
        holder.bind(
            data = currentList[position],
            position = position
        )
    }

    inner class RatesVH(
        binding: ItemRecyclerRatesBinding
    ) : BaseViewHolder<ItemRecyclerRatesBinding, RateModel>(binding = binding) {

        override fun bind(data: RateModel, position: Int) {
            binding.model = data
            if (binding.hasPendingBindings()) {
                binding.executePendingBindings()
            }
        }
    }
}