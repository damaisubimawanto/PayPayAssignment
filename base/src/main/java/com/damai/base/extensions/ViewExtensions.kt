package com.damai.base.extensions

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.damai.base.utils.Constants.GLIDE_CROSS_FADE

/**
 * Created by damai007 on 30/October/2023
 */

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.setCustomOnClickListener(listener: View.OnClickListener) {
    setOnClickListener(object : View.OnClickListener {
        private var lastTimeClicked = 0L

        override fun onClick(p0: View?) {
            if (System.currentTimeMillis() - lastTimeClicked > 1_500L) {
                lastTimeClicked = System.currentTimeMillis()
                listener.onClick(p0)
            }
        }
    })
}

fun AppCompatImageView.loadImageWithCenterCrop(
    url: String?
) {
    Glide.with(context)
        .load(url)
        .centerCrop()
        .transition(DrawableTransitionOptions.withCrossFade(GLIDE_CROSS_FADE))
        .into(this)
}

fun AppCompatEditText.addOnTextChanged(callback: (text: String) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            callback.invoke(p0?.toString().orEmpty().trim())
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(p0: Editable?) {}
    })
}