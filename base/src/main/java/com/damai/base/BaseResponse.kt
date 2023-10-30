package com.damai.base

import com.google.gson.annotations.SerializedName

/**
 * Created by damai007 on 30/October/2023
 */
open class BaseResponse {
    @SerializedName("error")
    var error: Boolean? = null

    @SerializedName("status")
    var status: Int? = null

    @SerializedName("message")
    var message: String? = null
}