package com.damai.base.networks

/**
 * Created by damai007 on 30/October/2023
 */
sealed class Resource<out T> {

    data class Success<T>(val model: T? = null) : Resource<T>()

    data class Error(val errorMessage: String?) : Resource<Nothing>()
}