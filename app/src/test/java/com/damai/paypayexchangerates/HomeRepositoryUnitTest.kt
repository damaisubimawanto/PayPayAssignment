package com.damai.paypayexchangerates

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.damai.base.networks.Resource
import com.damai.data.apiservices.HomeService
import com.damai.data.caches.HomeCache
import com.damai.data.mappers.CurrencyNameEntityToCurrencyNamesModelMapper
import com.damai.data.mappers.CurrencyNamePairToCurrencyNameEntityMapper
import com.damai.data.mappers.CurrencyNamesResponseToCurrencyNamesModelMapper
import com.damai.data.mappers.ExchangeRatesResponseToExchangeRatesModelMapper
import com.damai.data.mappers.RateEntityToRateModelMapper
import com.damai.data.mappers.RateModelToRateEntityMapper
import com.damai.data.repos.HomeRepositoryImpl
import com.damai.data.responses.ExchangeRatesResponse
import com.damai.domain.daos.CurrencyNameDao
import com.damai.domain.daos.RateDao
import com.damai.domain.entities.RateEntity
import com.damai.domain.models.ExchangeRatesModel
import com.damai.domain.models.RateModel
import com.damai.paypayexchangerates.application.AppDatabase
import com.damai.paypayexchangerates.utils.CoroutineTestRule
import com.damai.paypayexchangerates.utils.InstantExecutorExtension
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * Created by damai007 on 02/November/2023
 */
@RunWith(AndroidJUnit4::class)
@ExtendWith(InstantExecutorExtension::class)
@MediumTest
@Config(manifest = Config.NONE)
class HomeRepositoryUnitTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private lateinit var database: AppDatabase
    private lateinit var rateDao: RateDao
    private lateinit var currencyNameDao: CurrencyNameDao
    private lateinit var homeRepositoryImpl: HomeRepositoryImpl

    private val homeService = mockk<HomeService>()
    private val homeCache = mockk<HomeCache>()
    private val rateDaoMock = mockk<RateDao>(relaxed = true)
    private val currencyNameDaoMock = mockk<CurrencyNameDao>(relaxed = true)
    private val exchangeRatesMapper = mockk<ExchangeRatesResponseToExchangeRatesModelMapper>()
    private val currencyNamesMapper = mockk<CurrencyNamesResponseToCurrencyNamesModelMapper>()
    private val currencyNameEntityToModelMapper = mockk<CurrencyNameEntityToCurrencyNamesModelMapper>()
    private val currencyNamePairToCurrencyNameEntityMapper = mockk<CurrencyNamePairToCurrencyNameEntityMapper>()
    private val rateEntityToRateModelMapper = mockk<RateEntityToRateModelMapper>()
    private val rateModelToRateEntityMapper = mockk<RateModelToRateEntityMapper>()

    private val codeCurrencyIndonesia get() = "IDR"
    private val nameCurrencyIndonesia get() = "Indonesian Rupiah"
    private val valueCurrencyIndonesia get() = 15_000.0
    private val successCode get() = 200
    private val emptyString get() = ""

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        rateDao = database.rateDao()
        currencyNameDao = database.currencyNameDao()

        homeRepositoryImpl = HomeRepositoryImpl(
            homeService = homeService,
            homeCache = homeCache,
            dispatcher = coroutineRule.dispatcherProvider,
            currencyNameDao = currencyNameDao,
            rateDao = rateDao,
            exchangeRatesMapper = exchangeRatesMapper,
            currencyNamesMapper = currencyNamesMapper,
            currencyNameEntityToModelMapper = currencyNameEntityToModelMapper,
            currencyNamePairToCurrencyNameEntityMapper = currencyNamePairToCurrencyNameEntityMapper,
            rateEntityToRateModelMapper = rateEntityToRateModelMapper,
            rateModelToRateEntityMapper = rateModelToRateEntityMapper
        )
    }

    @After
    fun cleanUp() {
        database.close()
    }

    @Test
    fun `(+) get latest exchange rates from API fetch with no local cache is success`() = runTest {
        val responseBody: ExchangeRatesResponse = mockk()
        val responseModel: ExchangeRatesModel = mockk()
        val isExpired = true
        val rateModel = RateModel(
            code = codeCurrencyIndonesia,
            name = emptyString,
            value = valueCurrencyIndonesia
        )
        val rateModelList = listOf(rateModel)
        val rateEntity = RateEntity(
            code = codeCurrencyIndonesia,
            value = valueCurrencyIndonesia
        )
        val rateEntityList = listOf(rateEntity)

        every { responseBody.error } returns false
        every { responseBody.status } returns successCode
        every { responseModel.status } returns successCode
        every { responseModel.rates } returns rateModelList
        every {
            runBlocking {
                homeService.getLatestExchangeRates(
                    appId = any(),
                    base = any()
                )
            }
        } returns responseBody
        every {
            runBlocking { rateDaoMock.getAllRateEntityList() }
        } returns rateEntityList
        every { homeCache.isExchangeRatesCacheExpired() } returns isExpired
        every { exchangeRatesMapper.map(value = responseBody) } returns responseModel
        every { rateEntityToRateModelMapper.map(value = rateEntityList) } returns rateModelList
        every { rateModelToRateEntityMapper.map(value = rateModel) } returns rateEntity
        every { homeCache.setLatestUpdateExchangeRates(value = any()) } returns Unit

        homeRepositoryImpl.getLatestExchangeRates().collectLatest {
            assertTrue(it is Resource.Success)

            verify {
                runBlocking {
                    homeService.getLatestExchangeRates(
                        appId = any(),
                        base = any()
                    )
                }
            }
            verify(atLeast = 1) { homeCache.setLatestUpdateExchangeRates(value = any()) }

            confirmVerified(
                homeService,
                homeCache
            )
        }
    }
}