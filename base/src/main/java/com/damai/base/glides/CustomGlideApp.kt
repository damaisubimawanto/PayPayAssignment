package com.damai.base.glides

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions

/**
 * Created by damai007 on 30/October/2023
 */
@GlideModule
class CustomGlideApp : AppGlideModule() {

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        super.applyOptions(context, builder)
        builder.apply {
            setDefaultRequestOptions(
                RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
            )
        }
    }
}