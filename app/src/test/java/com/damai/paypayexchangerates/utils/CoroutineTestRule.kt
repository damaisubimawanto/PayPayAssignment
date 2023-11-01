package com.damai.paypayexchangerates.utils

import androidx.annotation.RestrictTo
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.damai.base.coroutines.DispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.runner.Description

/**
 * Created by damai007 on 01/November/2023
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RestrictTo(value = [RestrictTo.Scope.TESTS])
class CoroutineTestRule : InstantTaskExecutorRule() {

    val dispatcher = StandardTestDispatcher()

    val dispatcherProvider: DispatcherProvider = object : DispatcherProvider {
        override fun main(): CoroutineDispatcher = dispatcher
        override fun default(): CoroutineDispatcher = dispatcher
        override fun io(): CoroutineDispatcher = dispatcher
    }

    override fun starting(description: Description) {
        super.starting(description)
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        super.finished(description)
        Dispatchers.resetMain()
    }
}