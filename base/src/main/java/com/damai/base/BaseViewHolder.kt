package com.damai.base

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by damai007 on 30/October/2023
 */
abstract class BaseViewHolder<VB: ViewDataBinding, DATA> constructor(
    protected val binding: VB
) : RecyclerView.ViewHolder(binding.root) {

    abstract fun bind(data: DATA, position: Int)
}