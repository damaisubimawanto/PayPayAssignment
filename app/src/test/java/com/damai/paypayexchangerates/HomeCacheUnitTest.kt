package com.damai.paypayexchangerates

import android.content.SharedPreferences
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.damai.base.utils.TimeHelper
import com.damai.data.caches.HomeCache
import com.damai.paypayexchangerates.utils.InstantExecutorExtension
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * Created by damai007 on 01/November/2023
 */
@RunWith(AndroidJUnit4::class)
@ExtendWith(InstantExecutorExtension::class)
@MediumTest
@Config(manifest = Config.NONE)
class HomeCacheUnitTest {

    private val preferences = mockk<SharedPreferences>(relaxed = true)
    private val editor = mockk<SharedPreferences.Editor>(relaxed = true)
    private val timeHelper = mockk<TimeHelper>(relaxed = true)

    private val homeCache = HomeCache(
        preferences = preferences
    )

    @Before
    fun setup() {
        every { preferences.edit() } returns editor
    }

    @Test
    fun `(+) set latest exchange rates updated time is saved in shared preferences`() = runTest {
        val expected = System.currentTimeMillis()
        val actual = slot<Long>()

        every { editor.putLong(any(), capture(actual)) } returns editor

        homeCache.setLatestUpdateExchangeRates(value = expected)

        verify { preferences.edit() }
        verify(atLeast = 1) {
            editor.putLong(any(), expected)
        }
        verify { editor.apply() }

        assertEquals(expected, actual.captured)

        confirmVerified(
            editor,
            preferences
        )
    }

    @Test
    fun `(+) set latest currency names updated time is saved in shared preferences`() = runTest {
        val expected = System.currentTimeMillis()
        val actual = slot<Long>()

        every { editor.putLong(any(), capture(actual)) } returns editor

        homeCache.setLatestUpdateCurrencyNames(value = expected)

        verify { preferences.edit() }
        verify(atLeast = 1) {
            editor.putLong(any(), expected)
        }
        verify { editor.apply() }

        assertEquals(expected, actual.captured)

        confirmVerified(
            editor,
            preferences
        )
    }

    @Test
    fun `(+) get latest exchange rates after 30 minutes is expired`() = runTest {
        val expected = true
        val currentTime = System.currentTimeMillis()
        val lastUpdateTime = 31L * 60_000L
        val timeBefore = currentTime - lastUpdateTime

        every { preferences.getLong(any(), 0) } returns timeBefore
        every { timeHelper.getNow() } returns currentTime

        val actual = homeCache.isExchangeRatesCacheExpired()

        assertSame(expected, actual)

        verify(atLeast = 1) {
            preferences.getLong(any(), 0)
        }

        confirmVerified(
            preferences
        )
    }

    @Test
    fun `(-) get latest exchange rates right after 30 minutes is not expired`() = runTest {
        val expected = false
        val currentTime = System.currentTimeMillis()
        val lastUpdateTime = 30L * 60_000L
        val timeBefore = currentTime - lastUpdateTime

        every { preferences.getLong(any(), 0) } returns timeBefore
        every { timeHelper.getNow() } returns currentTime

        val actual = homeCache.isExchangeRatesCacheExpired()

        assertSame(expected, actual)

        verify(atLeast = 1) {
            preferences.getLong(any(), 0)
        }

        confirmVerified(
            preferences
        )
    }

    @Test
    fun `(-) get latest exchange rates before 30 minutes is not expired`() = runTest {
        val expected = false
        val currentTime = System.currentTimeMillis()
        val lastUpdateTime = 25L * 60_000L
        val timeBefore = currentTime - lastUpdateTime

        every { preferences.getLong(any(), 0) } returns timeBefore
        every { timeHelper.getNow() } returns currentTime

        val actual = homeCache.isExchangeRatesCacheExpired()

        assertSame(expected, actual)

        verify(atLeast = 1) {
            preferences.getLong(any(), 0)
        }

        confirmVerified(
            preferences
        )
    }

    @Test
    fun `(+) get latest currency names after 30 minutes is expired`() = runTest {
        val expected = true
        val currentTime = System.currentTimeMillis()
        val lastUpdateTime = 31L * 60_000L
        val timeBefore = currentTime - lastUpdateTime

        every { preferences.getLong(any(), 0) } returns timeBefore
        every { timeHelper.getNow() } returns currentTime

        val actual = homeCache.isCurrencyNamesCacheExpired()

        assertSame(expected, actual)

        verify(atLeast = 1) {
            preferences.getLong(any(), 0)
        }

        confirmVerified(
            preferences
        )
    }

    @Test
    fun `(-) get latest currency names right after 30 minutes is not expired`() = runTest {
        val expected = false
        val currentTime = System.currentTimeMillis()
        val lastUpdateTime = 30L * 60_000L
        val timeBefore = currentTime - lastUpdateTime

        every { preferences.getLong(any(), 0) } returns timeBefore
        every { timeHelper.getNow() } returns currentTime

        val actual = homeCache.isCurrencyNamesCacheExpired()

        assertSame(expected, actual)

        verify(atLeast = 1) {
            preferences.getLong(any(), 0)
        }

        confirmVerified(
            preferences
        )
    }

    @Test
    fun `(-) get latest currency names before 30 minutes is not expired`() = runTest {
        val expected = false
        val currentTime = System.currentTimeMillis()
        val lastUpdateTime = 25L * 60_000L
        val timeBefore = currentTime - lastUpdateTime

        every { preferences.getLong(any(), 0) } returns timeBefore
        every { timeHelper.getNow() } returns currentTime

        val actual = homeCache.isCurrencyNamesCacheExpired()

        assertSame(expected, actual)

        verify(atLeast = 1) {
            preferences.getLong(any(), 0)
        }

        confirmVerified(
            preferences
        )
    }
}