package com.damai.paypayexchangerates

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.damai.base.networks.Resource
import com.damai.base.utils.Constants.MAP_KEY_ERROR
import com.damai.base.utils.Constants.MAP_KEY_MESSAGE
import com.damai.base.utils.Constants.MAP_KEY_STATUS_CODE
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
import com.damai.domain.entities.CurrencyNameEntity
import com.damai.domain.entities.RateEntity
import com.damai.domain.models.CurrencyNamesModel
import com.damai.domain.models.ExchangeRatesModel
import com.damai.domain.models.RateModel
import com.damai.paypayexchangerates.utils.CoroutineTestRule
import com.damai.paypayexchangerates.utils.InstantExecutorExtension
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
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

    private lateinit var homeRepositoryImpl: HomeRepositoryImpl

    //region Mockk Variables
    private val homeService = mockk<HomeService>()
    private val homeCache = mockk<HomeCache>()
    private val rateDao = mockk<RateDao>(relaxed = true)
    private val currencyNameDao = mockk<CurrencyNameDao>(relaxed = true)
    private val exchangeRatesMapper = mockk<ExchangeRatesResponseToExchangeRatesModelMapper>()
    private val currencyNamesMapper = mockk<CurrencyNamesResponseToCurrencyNamesModelMapper>()
    private val currencyNameEntityToModelMapper = mockk<CurrencyNameEntityToCurrencyNamesModelMapper>()
    private val currencyNamePairToCurrencyNameEntityMapper = mockk<CurrencyNamePairToCurrencyNameEntityMapper>()
    private val rateEntityToRateModelMapper = mockk<RateEntityToRateModelMapper>()
    private val rateModelToRateEntityMapper = mockk<RateModelToRateEntityMapper>()
    //endregion `Mockk Variables`

    //region Getter Variables
    private val codeCurrencyIndonesia get() = "IDR"
    private val nameCurrencyIndonesia get() = "Indonesian Rupiah"
    private val valueCurrencyIndonesia get() = 15_000.0
    private val valueCurrencyIndonesiaUpdated get() = 15_800.0
    private val successCode get() = 200
    private val errorCode get() = 500
    private val errorMessage get() = "Error"
    private val emptyString get() = ""
    private val mapKeyError get() = MAP_KEY_ERROR
    private val mapKeyStatusCode get() = MAP_KEY_STATUS_CODE
    private val mapKeyMessage get() = MAP_KEY_MESSAGE
    //endregion `Getter Variables`

    @Before
    fun setup() {
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
        // Do nothing to be cleaned up.
    }

    //region Unit Tests - Get Latest Exchange Rates
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
            runBlocking { rateDao.getAllRateEntityList() }
        } returns listOf() /* Should return empty list from local storage. */
        every { homeCache.isExchangeRatesCacheExpired() } returns isExpired
        every { homeCache.setLatestUpdateExchangeRates(value = any()) } returns Unit
        every { exchangeRatesMapper.map(value = responseBody) } returns responseModel
        every { rateEntityToRateModelMapper.map(value = rateEntityList) } returns rateModelList
        every { rateModelToRateEntityMapper.map(value = rateModel) } returns rateEntity

        homeRepositoryImpl.getLatestExchangeRates().collectLatest {
            assertTrue(it is Resource.Success)
            val model = (it as Resource.Success).model
            assertThat(model).isNotNull
            assertThat(model!!.rates).isNotNull
            assertThat(model.rates).isNotEmpty
            assertTrue(model.rates!!.first().value == valueCurrencyIndonesia)

            verify {
                runBlocking {
                    homeService.getLatestExchangeRates(
                        appId = any(),
                        base = any()
                    )
                }
            }
            verify(atLeast = 1) { homeCache.setLatestUpdateExchangeRates(value = any()) }
            verify(atLeast = 1) { runBlocking { rateDao.getAllRateEntityList() } }
            verify(atLeast = 1) { runBlocking { rateDao.insert(rateEntity = any()) } }

            confirmVerified(
                homeService,
                homeCache,
                rateDao
            )
        }
    }

    @Test
    fun `(+) get latest exchange rate from local cache and it is not expired should success`() = runTest {
        val responseBody: ExchangeRatesResponse = mockk()
        val responseModel: ExchangeRatesModel = mockk()
        val isExpired = false
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
            runBlocking { rateDao.getAllRateEntityList() }
        } returns rateEntityList
        every { homeCache.isExchangeRatesCacheExpired() } returns isExpired
        every { homeCache.setLatestUpdateExchangeRates(value = any()) } returns Unit
        every { exchangeRatesMapper.map(value = responseBody) } returns responseModel
        every { rateEntityToRateModelMapper.map(value = rateEntityList) } returns rateModelList
        every { rateModelToRateEntityMapper.map(value = rateModel) } returns rateEntity

        homeRepositoryImpl.getLatestExchangeRates().collectLatest {
            assertTrue(it is Resource.Success)
            val model = (it as Resource.Success).model
            assertThat(model).isNotNull
            assertThat(model!!.rates).isNotNull
            assertThat(model.rates).isNotEmpty
            assertTrue(model.rates!!.first().value == valueCurrencyIndonesia)

            verify(atLeast = 1) { homeCache.isExchangeRatesCacheExpired() }
            verify(atLeast = 1) { runBlocking { rateDao.getAllRateEntityList() } }

            confirmVerified(
                homeCache,
                rateDao
            )
        }
    }

    @Test
    fun `(+) get latest exchange rate from local cache and it is expired should success`() = runTest {
        val responseBody: ExchangeRatesResponse = mockk()
        val responseModel: ExchangeRatesModel = mockk()
        val isExpired = true
        val rateModel = RateModel(
            code = codeCurrencyIndonesia,
            name = emptyString,
            value = valueCurrencyIndonesiaUpdated
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
            runBlocking { rateDao.getAllRateEntityList() }
        } returns rateEntityList
        every { homeCache.isExchangeRatesCacheExpired() } returns isExpired
        every { exchangeRatesMapper.map(value = responseBody) } returns responseModel
        every { rateEntityToRateModelMapper.map(value = rateEntityList) } returns rateModelList
        every { rateModelToRateEntityMapper.map(value = rateModel) } returns rateEntity
        every { homeCache.setLatestUpdateExchangeRates(value = any()) } returns Unit

        homeRepositoryImpl.getLatestExchangeRates().collectLatest {
            assertTrue(it is Resource.Success)
            val model = (it as Resource.Success).model
            assertThat(model).isNotNull
            assertThat(model!!.rates).isNotNull
            assertThat(model.rates).isNotEmpty
            assertTrue(model.rates!!.first().value == valueCurrencyIndonesiaUpdated)

            verify {
                runBlocking {
                    homeService.getLatestExchangeRates(
                        appId = any(),
                        base = any()
                    )
                }
            }
            verify(atLeast = 1) { homeCache.isExchangeRatesCacheExpired() }
            verify(atLeast = 1) { homeCache.setLatestUpdateExchangeRates(value = any()) }
            verify(atLeast = 1) { runBlocking { rateDao.getAllRateEntityList() } }
            verify(atLeast = 1) { runBlocking { rateDao.insert(rateEntity = any()) } }

            confirmVerified(
                homeService,
                homeCache,
                rateDao
            )
        }
    }

    @Test
    fun `(-) get latest exchange rates from API with no local cache, but error from server should return error`() = runTest {
        val responseBody: ExchangeRatesResponse = mockk()
        val responseModel: ExchangeRatesModel = mockk()

        every { responseBody.error } returns true
        every { responseBody.status } returns errorCode
        every { responseModel.status } returns errorCode
        every { responseModel.message } returns errorMessage
        every {
            runBlocking {
                homeService.getLatestExchangeRates(
                    appId = any(),
                    base = any()
                )
            }
        } returns responseBody
        every { exchangeRatesMapper.map(value = responseBody) } returns responseModel

        homeRepositoryImpl.getLatestExchangeRates().collectLatest {
            assertTrue(it is Resource.Error)
            assertTrue((it as Resource.Error).errorMessage == errorMessage)

            verify {
                runBlocking {
                    homeService.getLatestExchangeRates(
                        appId = any(),
                        base = any()
                    )
                }
            }

            confirmVerified(
                homeService
            )
        }
    }

    @Test
    fun `(-) get latest exchange rate from local cache and it is expired, but error from server should return error`() = runTest {
        val responseBody: ExchangeRatesResponse = mockk()
        val responseModel: ExchangeRatesModel = mockk()
        val isExpired = true
        val rateModel = RateModel(
            code = codeCurrencyIndonesia,
            name = emptyString,
            value = valueCurrencyIndonesiaUpdated
        )
        val rateModelList = listOf(rateModel)
        val rateEntity = RateEntity(
            code = codeCurrencyIndonesia,
            value = valueCurrencyIndonesia
        )
        val rateEntityList = listOf(rateEntity)

        every { responseBody.error } returns true
        every { responseBody.status } returns errorCode
        every { responseModel.status } returns errorCode
        every { responseModel.message } returns errorMessage
        every {
            runBlocking {
                homeService.getLatestExchangeRates(
                    appId = any(),
                    base = any()
                )
            }
        } returns responseBody
        every {
            runBlocking { rateDao.getAllRateEntityList() }
        } returns rateEntityList
        every { homeCache.isExchangeRatesCacheExpired() } returns isExpired
        every { exchangeRatesMapper.map(value = responseBody) } returns responseModel
        every { rateEntityToRateModelMapper.map(value = rateEntityList) } returns rateModelList

        homeRepositoryImpl.getLatestExchangeRates().collectLatest {
            assertTrue(it is Resource.Error)
            assertTrue((it as Resource.Error).errorMessage == errorMessage)

            verify {
                runBlocking {
                    homeService.getLatestExchangeRates(
                        appId = any(),
                        base = any()
                    )
                }
            }
            verify(atLeast = 1) { homeCache.isExchangeRatesCacheExpired() }
            verify(atLeast = 1) { runBlocking { rateDao.getAllRateEntityList() } }

            confirmVerified(
                homeService,
                homeCache,
                rateDao
            )
        }
    }
    //endregion `Unit Tests - Get Latest Exchange Rates`

    //region Unit Tests - Get Currency Names
    @Test
    fun `(+) get latest currency names from API fetch with no local cache is success`() = runTest {
        val responseBody: Map<String, String> = mockk()
        val responseModel: CurrencyNamesModel = mockk()
        val isExpired = true
        val currencyPair = Pair(codeCurrencyIndonesia, nameCurrencyIndonesia)
        val currencyPairList = listOf(currencyPair)
        val currencyNameEntity = CurrencyNameEntity(
            code = codeCurrencyIndonesia,
            name = nameCurrencyIndonesia
        )
        val currencyNameEntityList = listOf(currencyNameEntity)

        every { responseBody.containsKey(mapKeyError) } returns false
        every { responseBody.containsKey(mapKeyStatusCode) } returns false
        every { responseBody.containsKey(mapKeyMessage) } returns false
        every { responseModel.status } returns successCode
        every { responseModel.currencyList } returns currencyPairList
        every {
            runBlocking {
                homeService.getCurrencyNames(appId = any())
            }
        } returns responseBody
        every {
            runBlocking { currencyNameDao.getAllCurrencyNameEntityList() }
        } returns listOf() /* Should return empty list from local storage. */
        every { homeCache.isCurrencyNamesCacheExpired() } returns isExpired
        every { homeCache.setLatestUpdateCurrencyNames(value = any()) } returns Unit
        every { currencyNamesMapper.map(value = responseBody) } returns responseModel
        every {
            currencyNameEntityToModelMapper.map(value = currencyNameEntityList)
        } returns responseModel
        every {
            currencyNamePairToCurrencyNameEntityMapper.map(value = currencyPair)
        } returns currencyNameEntity

        homeRepositoryImpl.getCurrencyNames().collectLatest {
            assertTrue(it is Resource.Success)
            val model = (it as Resource.Success).model
            assertThat(model).isNotNull
            assertThat(model!!.currencyList).isNotNull
            assertThat(model.currencyList).isNotEmpty
            assertTrue(model.currencyList.first().first == codeCurrencyIndonesia)

            verify {
                runBlocking {
                    homeService.getCurrencyNames(appId = any())
                }
            }
            verify(atLeast = 1) { homeCache.setLatestUpdateCurrencyNames(value = any()) }
            verify(atLeast = 1) { runBlocking { currencyNameDao.getAllCurrencyNameEntityList() } }
            verify(atLeast = 1) { runBlocking { currencyNameDao.insert(currencyNameEntity = any()) } }

            confirmVerified(
                homeService,
                homeCache,
                currencyNameDao
            )
        }
    }
    //endregion `Unit Tests - Get Currency Names`
}