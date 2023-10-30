package com.damai.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel

/**
 * Created by damai007 on 30/October/2023
 */
abstract class BaseFragment<VB: ViewDataBinding, VM: ViewModel> : Fragment() {

    abstract val layoutResource: Int
    abstract val viewModel: VM

    private var _binding: VB? = null
    protected val binding
        get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(
            inflater,
            layoutResource,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewInitialization()
        binding.setupListeners()
        binding.setupObservers()
        binding.onPreparationFinished()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    //region Optional implementation
    open fun VB.viewInitialization() {}

    open fun VB.setupListeners() {}

    open fun VB.setupObservers() {}

    open fun VB.onPreparationFinished() {}
    //endregion `Optional implementation`
}