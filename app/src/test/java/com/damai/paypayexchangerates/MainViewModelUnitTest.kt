package com.damai.paypayexchangerates

import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.damai.base.networks.Resource
import com.damai.base.utils.Event
import com.damai.domain.daos.CurrencyNameDao
import com.damai.domain.daos.RateDao
import com.damai.domain.entities.CurrencyNameEntity
import com.damai.domain.entities.RateEntity
import com.damai.domain.models.CurrencyNamesModel
import com.damai.domain.models.ExchangeRatesModel
import com.damai.domain.models.RateModel
import com.damai.domain.repositories.HomeRepository
import com.damai.domain.usecases.GetCurrencyNamesUseCase
import com.damai.domain.usecases.GetLatestExchangeRatesUseCase
import com.damai.paypayexchangerates.application.AppDatabase
import com.damai.paypayexchangerates.ui.main.MainViewModel
import com.damai.paypayexchangerates.utils.CoroutineTestRule
import com.damai.paypayexchangerates.utils.InstantExecutorExtension
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
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
import java.util.concurrent.CountDownLatch

/**
 * Created by damai007 on 01/November/2023
 */
@RunWith(AndroidJUnit4::class)
@ExtendWith(InstantExecutorExtension::class)
@MediumTest
@Config(manifest = Config.NONE)
class MainViewModelUnitTest {

    @get:Rule
    val coroutineRule = CoroutineTestRule()

    private lateinit var database: AppDatabase
    private lateinit var rateDao: RateDao
    private lateinit var currencyNameDao: CurrencyNameDao
    private lateinit var viewModel: MainViewModel

    private val getLatestExchangeRatesUseCase = mockk<GetLatestExchangeRatesUseCase>()
    private val getCurrencyNamesUseCase = mockk<GetCurrencyNamesUseCase>()
    private val homeRepository = mockk<HomeRepository>()

    private val exchangeRateListObserver = mockk<Observer<List<RateModel>>>(relaxed = true)
    private val currencyBaseObserver = mockk<Observer<String>>(relaxed = true)
    private val loadingObserver = mockk<Observer<Boolean>>(relaxed = true)
    private val errorObserver = mockk<Observer<Event<String>>>(relaxed = true)

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        rateDao = database.rateDao()
        currencyNameDao = database.currencyNameDao()

        viewModel = MainViewModel(
            app = ApplicationProvider.getApplicationContext(),
            dispatcher = coroutineRule.dispatcherProvider,
            getLatestExchangeRatesUseCase = getLatestExchangeRatesUseCase,
            getCurrencyNamesUseCase = getCurrencyNamesUseCase
        )
        viewModel.exchangeRateListLiveData.observeForever(exchangeRateListObserver)
        viewModel.currencyBaseLiveData.observeForever(currencyBaseObserver)
        viewModel.loadingLiveData.observeForever(loadingObserver)
        viewModel.errorLiveData.observeForever(errorObserver)
    }

    @After
    fun cleanUp() {
        viewModel.exchangeRateListLiveData.removeObserver(exchangeRateListObserver)
        viewModel.currencyBaseLiveData.removeObserver(currencyBaseObserver)
        viewModel.loadingLiveData.removeObserver(loadingObserver)
        viewModel.errorLiveData.removeObserver(errorObserver)
        database.close()
    }

    @Test
    fun `test a`() {
        // TODO: Copy this method
    }

    @Test
    fun `(+) insert new exchange rate into database should be success`() = runTest {
        val symbolCurrency = "IDR"
        val valueCurrency = 10_000.0

        val rateEntity = RateEntity(
            code = symbolCurrency,
            value = valueCurrency
        )
        rateDao.insert(rateEntity = rateEntity)

        val latch = CountDownLatch(1)
        val job = async(Dispatchers.IO) {
            val savedExchangeRateList = rateDao.getAllRateEntityList()
            assertThat(savedExchangeRateList).isNotEmpty
            assertTrue(savedExchangeRateList.first().code == symbolCurrency)
            assertTrue(savedExchangeRateList.first().value == valueCurrency)
            latch.countDown()
        }
        latch.await()
        job.cancelAndJoin()
    }

    @Test
    fun `(+) insert new currency name into database should be success`() = runTest {
        val symbolCurrency = "IDR"
        val nameCurrency = "Indonesian Rupiah"

        val currencyNameEntity = CurrencyNameEntity(
            code = symbolCurrency,
            name = nameCurrency
        )
        currencyNameDao.insert(currencyNameEntity = currencyNameEntity)

        val latch = CountDownLatch(1)
        val job = async(Dispatchers.IO) {
            val savedCurrencyNameList = currencyNameDao.getAllCurrencyNameEntityList()
            assertThat(savedCurrencyNameList).isNotEmpty
            assertTrue(savedCurrencyNameList.first().code == symbolCurrency)
            assertTrue(savedCurrencyNameList.first().name == nameCurrency)
            latch.countDown()
        }
        latch.await()
        job.cancelAndJoin()
    }

    @Test
    fun `(+) get currency name list use case and returns success`() = runTest {
        val responseBody: CurrencyNamesModel = mockk()
        val flowResponse = flow<Resource<CurrencyNamesModel>> {
            emit(Resource.Success(responseBody))
        }

        every { runBlocking { getCurrencyNamesUseCase() } } returns flowResponse
        every { homeRepository.getCurrencyNames() } returns flowResponse

        getCurrencyNamesUseCase().collectLatest {
            assertTrue(it is Resource.Success)

            confirmVerified(
                homeRepository
            )
        }
    }

    @Test
    fun `(-) get currency name list use case but returns failed`() = runTest {
        val errorMessage = "Error"
        val flowResponse = flow<Resource<CurrencyNamesModel>> {
            emit(Resource.Error(errorMessage))
        }

        every { runBlocking { getCurrencyNamesUseCase() } } returns flowResponse
        every { homeRepository.getCurrencyNames() } returns flowResponse

        getCurrencyNamesUseCase().collectLatest {
            assertTrue(it is Resource.Error)
            assertTrue((it as Resource.Error).errorMessage == errorMessage)

            confirmVerified(
                homeRepository
            )
        }
    }

    @Test
    fun `(+) get latest exchange rate list use case and returns success`() = runTest {
        val responseBody: ExchangeRatesModel = mockk()
        val flowResponse = flow<Resource<ExchangeRatesModel>> {
            emit(Resource.Success(responseBody))
        }

        every { runBlocking { getLatestExchangeRatesUseCase() } } returns flowResponse
        every { homeRepository.getLatestExchangeRates() } returns flowResponse

        getLatestExchangeRatesUseCase().collectLatest {
            assertTrue(it is Resource.Success)

            confirmVerified(
                homeRepository
            )
        }
    }

    @Test
    fun `(-) get latest exchange rate list use case but returns failed`() = runTest {
        val errorMessage = "Error"
        val flowResponse = flow<Resource<ExchangeRatesModel>> {
            emit(Resource.Error(errorMessage))
        }

        every { runBlocking { getLatestExchangeRatesUseCase() } } returns flowResponse
        every { homeRepository.getLatestExchangeRates() } returns flowResponse

        getLatestExchangeRatesUseCase().collectLatest {
            assertTrue(it is Resource.Error)
            assertTrue((it as Resource.Error).errorMessage == errorMessage)

            confirmVerified(
                homeRepository
            )
        }
    }

    @Test
    fun `(+) join currency name list into exchange rates should be success`() {
        val symbolCurrency = "IDR"
        val nameCurrency = "Indonesian Rupiah"
        val valueCurrency = 15_000.0

        val exchangeRateList = listOf(
            RateModel(
                code = symbolCurrency,
                name = "",
                value = valueCurrency
            )
        )

        val currencyNameList = listOf(
            Pair(symbolCurrency, nameCurrency)
        )

        val joinedList = viewModel.joinCurrencyNamesIntoExchangeRates(
            exchangeRateList = exchangeRateList,
            currencyNameList = currencyNameList
        )
        assertTrue(joinedList != null)
        assertTrue(joinedList?.first() != null)
        assertTrue(joinedList?.first()?.code == symbolCurrency)
        assertTrue(joinedList?.first()?.name == nameCurrency)
        assertTrue(joinedList?.first()?.value == valueCurrency)
    }

    @Test
    fun `(+) join null currency name list into exchange rates should be success`() {
        val symbolCurrency = "IDR"
        val nameCurrency = ""
        val valueCurrency = 15_000.0

        val exchangeRateList = listOf(
            RateModel(
                code = symbolCurrency,
                name = nameCurrency,
                value = valueCurrency
            )
        )

        val joinedList = viewModel.joinCurrencyNamesIntoExchangeRates(
            exchangeRateList = exchangeRateList,
            currencyNameList = null
        )
        assertTrue(joinedList != null)
        assertTrue(joinedList?.first() != null)
        assertTrue(joinedList?.first()?.code == symbolCurrency)
        assertTrue(joinedList?.first()?.name == nameCurrency)
        assertTrue(joinedList?.first()?.value == valueCurrency)
    }

    @Test
    fun `(-) join currency name list into empty exchange rates should be empty`() {
        val symbolCurrency = "IDR"
        val nameCurrency = "Indonesian Rupiah"

        val currencyNameList = listOf(
            Pair(symbolCurrency, nameCurrency)
        )

        val joinedList = viewModel.joinCurrencyNamesIntoExchangeRates(
            exchangeRateList = null,
            currencyNameList = currencyNameList
        )
        assertTrue(joinedList == null)
    }

    @Test
    fun `(-) join null currency name list into empty exchange rates should be empty`() {
        val joinedList = viewModel.joinCurrencyNamesIntoExchangeRates(
            exchangeRateList = null,
            currencyNameList = null
        )
        assertTrue(joinedList == null)
    }
}