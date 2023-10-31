package com.damai.base

import android.app.Dialog
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * Created by damai007 on 31/October/2023
 */
abstract class BaseBottomSheetDialogFragment<VB: ViewDataBinding, VM: ViewModel> : BottomSheetDialogFragment() {

    abstract val layoutResource: Int
    abstract val viewModel: VM

    private var _binding: VB? = null
    protected val binding
        get() = requireNotNull(_binding)

    private lateinit var sheetDialog: BottomSheetDialog

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        sheetDialog = object : BottomSheetDialog(requireContext(), theme) {
            override fun onBackPressed() {
                if (childFragmentManager.backStackEntryCount > 0) {
                    childFragmentManager.popBackStack()
                } else {
                    super.onBackPressed()
                }
            }
        }

        return sheetDialog
    }

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

    //region Public Functions
    fun setBottomSheetFullScreen(height: Int, bottomSheet: View, skipCollapsed: Boolean = false) {
        setExpanded(skipCollapsed = skipCollapsed)

        val rect = Rect()
        activity?.window?.decorView?.getWindowVisibleDisplayFrame(rect)
        val statusBarHeight = rect.top

        val param = bottomSheet.layoutParams
        param.height = height - statusBarHeight
        bottomSheet.layoutParams = param
    }

    fun setExpanded(skipCollapsed: Boolean = false) {
        sheetDialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        sheetDialog.behavior.skipCollapsed = skipCollapsed
    }
    //endregion `Public Functions`
}