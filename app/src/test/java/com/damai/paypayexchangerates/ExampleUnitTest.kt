package com.damai.paypayexchangerates

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    // input : "aaaabbcccab"
    // output : "a4b2c3a1b1"
    private fun compress(input: String): String {
        var number = 0
        var currentChar = ""
        val stringBuilder = StringBuilder()
        val lastTextPosition = input.length - 1
        for (index in input.indices) {
            val character = input.substring(index, index + 1)
            when (currentChar) {
                "" -> {
                    currentChar = character
                    number++
                }
                character -> number++
                else -> {
                    stringBuilder.append("$currentChar$number")
                    currentChar = character
                    number = 1
                }
            }
            if (index == lastTextPosition) {
                stringBuilder.append("$currentChar$number")
            }
        }
        return stringBuilder.toString()
    }

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun compressUnitTest() {
        val input = "aaaabbcccab"
        val expected = "a4b2c3a1b1"
        val actual = compress(input)

        assertEquals(expected, actual)
    }

    @Test
    fun compress2UnitTest() {
        val input = "aaaabbcccaaa"
        val expected = "a4b2c3a3"
        val actual = compress(input)

        assertEquals(expected, actual)
    }

    @Test
    fun compress3UnitTest() {
        val input = "aaaabbcccaaabbbbbb"
        val expected = "a4b2c3a3b6"
        val actual = compress(input)

        assertEquals(expected, actual)
    }
}