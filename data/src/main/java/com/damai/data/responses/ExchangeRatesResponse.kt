package com.damai.data.responses

import com.damai.base.BaseResponse
import com.google.gson.annotations.SerializedName

/**
 * Created by damai007 on 30/October/2023
 */
class ExchangeRatesResponse : BaseResponse() {
    @SerializedName("timestamp")
    var timestamp: Long? = null

    @SerializedName("base")
    var base: String? = null

    @SerializedName("rates")
    var rates: Map<String, Double>? = null
}