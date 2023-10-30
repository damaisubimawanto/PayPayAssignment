package com.damai.paypayexchangerates.ui.main.adapter

import androidx.recyclerview.widget.DiffUtil
import com.damai.domain.models.RateModel

/**
 * Created by damai007 on 30/October/2023
 */
object RatesComparator : DiffUtil.ItemCallback<RateModel>() {

    override fun areItemsTheSame(oldItem: RateModel, newItem: RateModel): Boolean {
        return oldItem.code == newItem.code
    }

    override fun areContentsTheSame(oldItem: RateModel, newItem: RateModel): Boolean {
        return oldItem == newItem
    }
}