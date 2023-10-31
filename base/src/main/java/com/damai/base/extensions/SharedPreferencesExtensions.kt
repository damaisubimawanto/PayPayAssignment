package com.damai.base.extensions

import android.content.SharedPreferences

/**
 * Created by damai007 on 31/October/2023
 */

@Throws(UnsupportedOperationException::class)
fun SharedPreferences.update(value: Pair<Any, String>) {
    val editor = this.edit()
    when (value.first) {
        is Int -> editor.putInt(value.second, value.first as Int)
        is Boolean -> editor.putBoolean(value.second, value.first as Boolean)
        is Float -> editor.putFloat(value.second, value.first as Float)
        is Long -> editor.putLong(value.second, value.first as Long)
        is String -> editor.putString(value.second, value.first as String)
        else -> throw UnsupportedOperationException("unsupported type of value extension")
    }
    editor.apply()
}

fun SharedPreferences.remove(vararg keys: String): Unit = edit().also { editor ->
    keys.forEach { key -> editor.remove(key) }
}.apply()