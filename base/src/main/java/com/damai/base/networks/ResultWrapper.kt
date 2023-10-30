package com.damai.base.networks

import com.damai.base.BaseModel
import com.damai.base.utils.Constants
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Created by damai007 on 30/October/2023
 */
sealed class ResultWrapper<out T> {

    data class Success<out T>(val value: T) : ResultWrapper<T>()

    data class GenericError(val message: String? = null) : ResultWrapper<Nothing>()
}

internal suspend fun <T: BaseModel> safeApiCall(
    dispatcher: CoroutineDispatcher,
    apiCall: suspend () -> T?
): ResultWrapper<T> {
    return withContext(dispatcher) {
        try {
            val call = apiCall.invoke()
            when (call?.status) {
                Constants.SUCCESS_CODE -> ResultWrapper.Success(call)
                else -> {
                    ResultWrapper.GenericError(
                        message = call?.message
                    )
                }
            }
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            ResultWrapper.GenericError(message = throwable.message)
        }
    }
}