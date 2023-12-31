package com.damai.base.extensions

import com.damai.base.BaseResponse
import com.damai.base.utils.Constants.EMPTY_CODE
import com.damai.base.utils.Constants.SUCCESS_CODE

/**
 * Created by damai007 on 30/October/2023
 */

fun Int?.orZero() = this ?: 0

fun Boolean?.orFalse() = this ?: false

fun Double?.orZero() = this ?: 0.0

fun Long?.orZero() = this ?: 0L

fun BaseResponse.getResponseStatus(): Int = if (error.orFalse()) {
    status ?: EMPTY_CODE
} else {
    SUCCESS_CODE
}

fun BaseResponse.getResponseErrorMessage(): String? = if (error.orFalse()) {
    message
} else {
    null
}