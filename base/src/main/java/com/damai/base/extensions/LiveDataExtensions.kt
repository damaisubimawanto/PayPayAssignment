package com.damai.base.extensions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * Created by damai007 on 30/October/2023
 */

fun <T> MutableLiveData<T>.asLiveData() = this as LiveData<T>

fun <T> MutableLiveData<List<T>>.getMutableList(): MutableList<T> {
    return value?.toMutableList() ?: mutableListOf()
}