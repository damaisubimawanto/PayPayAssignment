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
import com.jraska.livedata.test
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.excludeRecords
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
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

    //region Mockk Variables
    private val getLatestExchangeRatesUseCase = mockk<GetLatestExchangeRatesUseCase>()
    private val getCurrencyNamesUseCase = mockk<GetCurrencyNamesUseCase>()
    private val homeRepository = mockk<HomeRepository>()

    private val exchangeRateListObserver = mockk<Observer<List<RateModel>>>(relaxed = true)
    private val currencyBaseObserver = mockk<Observer<String>>(relaxed = true)
    private val loadingObserver = mockk<Observer<Boolean>>(relaxed = true)
    private val errorObserver = mockk<Observer<Event<String>>>(relaxed = true)
    //endregion `Mockk Variables`

    //region Getter Variables
    private val codeCurrencyIndonesia get() = "IDR"
    private val nameCurrencyIndonesia get() = "Indonesian Rupiah"
    private val valueCurrencyIndonesia get() = 15_000.0
    private val codeCurrencyJapan get() = "YEN"
    private val nameCurrencyJapan get() = "Japanese Yen"
    private val valueCurrencyJapan get() = 150.0
    private val baseCodeCurrency get() = "USD"
    private val errorMessage get() = "Error"
    //endregion `Getter Variables`

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

    //region Unit Tests - Local Database
    @Test
    fun `(+) insert new exchange rate into database should be success`() = runTest {
        val rateEntity = RateEntity(
            code = codeCurrencyIndonesia,
            value = valueCurrencyIndonesia
        )
        rateDao.insert(rateEntity = rateEntity)

        val latch = CountDownLatch(1)
        val job = async(Dispatchers.IO) {
            val savedExchangeRateList = rateDao.getAllRateEntityList()
            assertThat(savedExchangeRateList).isNotEmpty
            assertTrue(savedExchangeRateList.first().code == codeCurrencyIndonesia)
            assertTrue(savedExchangeRateList.first().value == valueCurrencyIndonesia)
            latch.countDown()
        }
        latch.await()
        job.cancelAndJoin()
    }

    @Test
    fun `(+) insert new currency name into database should be success`() = runTest {
        val currencyNameEntity = CurrencyNameEntity(
            code = codeCurrencyIndonesia,
            name = nameCurrencyIndonesia
        )
        currencyNameDao.insert(currencyNameEntity = currencyNameEntity)

        val latch = CountDownLatch(1)
        val job = async(Dispatchers.IO) {
            val savedCurrencyNameList = currencyNameDao.getAllCurrencyNameEntityList()
            assertThat(savedCurrencyNameList).isNotEmpty
            assertTrue(savedCurrencyNameList.first().code == codeCurrencyIndonesia)
            assertTrue(savedCurrencyNameList.first().name == nameCurrencyIndonesia)
            latch.countDown()
        }
        latch.await()
        job.cancelAndJoin()
    }
    //endregion `Unit Tests - Local Database`

    //region Unit Tests - Get Currency Name List
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
    //endregion `Unit Tests - Get Currency Name List`

    //region Unit Tests - Get Exchange Rate List
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
    //endregion `Unit Tests - Get Exchange Rate List`

    //region Unit Tests - Join Currency Names Into Exchanges Rates
    @Test
    fun `(+) join currency name list into exchange rates should be success`() {
        val exchangeRateList = listOf(
            RateModel(
                code = codeCurrencyIndonesia,
                name = "",
                value = valueCurrencyIndonesia
            )
        )

        val currencyNameList = listOf(
            Pair(codeCurrencyIndonesia, nameCurrencyIndonesia)
        )

        val joinedList = viewModel.joinCurrencyNamesIntoExchangeRates(
            exchangeRateList = exchangeRateList,
            currencyNameList = currencyNameList
        )
        assertThat(joinedList).isNotNull
        assertThat(joinedList?.first()).isNotNull
        assertTrue(joinedList?.first()?.code == codeCurrencyIndonesia)
        assertTrue(joinedList?.first()?.name == nameCurrencyIndonesia)
        assertTrue(joinedList?.first()?.value == valueCurrencyIndonesia)
    }

    @Test
    fun `(+) join null currency name list into exchange rates should be success`() {
        val emptyString = ""
        val exchangeRateList = listOf(
            RateModel(
                code = codeCurrencyIndonesia,
                name = emptyString,
                value = valueCurrencyIndonesia
            )
        )

        val joinedList = viewModel.joinCurrencyNamesIntoExchangeRates(
            exchangeRateList = exchangeRateList,
            currencyNameList = null
        )
        assertThat(joinedList).isNotNull
        assertThat(joinedList?.first()).isNotNull
        assertTrue(joinedList?.first()?.code == codeCurrencyIndonesia)
        assertTrue(joinedList?.first()?.name == emptyString)
        assertTrue(joinedList?.first()?.value == valueCurrencyIndonesia)
    }

    @Test
    fun `(-) join currency name list into empty exchange rates should be empty`() {
        val currencyNameList = listOf(
            Pair(codeCurrencyIndonesia, nameCurrencyIndonesia)
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
    //endregion `Unit Tests - Join Currency Names Into Exchanges Rates`

    //region Unit Tests - Update Loading Live Data in getExchangeRates()
    @Test
    fun `(+) call getExchangeRates should update loading live data`() = runTest {
        /* Defines the response body for getCurrencyNamesUseCase(). */
        val currencyNamesResponseBody: CurrencyNamesModel = mockk()
        val currencyPair = Pair(codeCurrencyIndonesia, nameCurrencyIndonesia)
        val currencyList = listOf(currencyPair)
        val currencyNamesFlowResponse = flow<Resource<CurrencyNamesModel>> {
            emit(Resource.Success(currencyNamesResponseBody))
        }

        /* Defines the response body for getLatestExchangeRatesUseCase(). */
        val exchangeRatesResponseBody: ExchangeRatesModel = mockk()
        val exchangeRateModel = RateModel(
            code = codeCurrencyIndonesia,
            name = "",
            value = valueCurrencyIndonesia
        )
        val exchangeRateList = listOf(exchangeRateModel)
        val exchangeRatesFlowResponse = flow<Resource<ExchangeRatesModel>> {
            emit(Resource.Success(exchangeRatesResponseBody))
        }

        /* Setting the answers of mockking. */
        every { currencyNamesResponseBody.currencyList } returns currencyList
        every { exchangeRatesResponseBody.rates } returns exchangeRateList
        every { exchangeRatesResponseBody.base } returns baseCodeCurrency
        every { runBlocking { getCurrencyNamesUseCase() } } returns currencyNamesFlowResponse
        every { runBlocking { getLatestExchangeRatesUseCase() } } returns exchangeRatesFlowResponse

        /* Calling the selected function. */
        viewModel.getExchangeRates()

        verify(exactly = 1) {
            loadingObserver.onChanged(any())
        }

        val testObserver = viewModel.loadingLiveData.test()
            .assertHasValue()
        val content = testObserver.value()
        assertEquals(true, content)
        assertNotEquals(false, content)

        excludeRecords { viewModel.loadingLiveData.observeForever(testObserver) }

        confirmVerified(
            loadingObserver
        )
    }

    @Test
    fun `(+) call getExchangeRates and success should update loading live data`() = runTest {
        /* Defines the response body for getCurrencyNamesUseCase(). */
        val currencyNamesResponseBody: CurrencyNamesModel = mockk()
        val currencyPair = Pair(codeCurrencyIndonesia, nameCurrencyIndonesia)
        val currencyList = listOf(currencyPair)
        val currencyNamesFlowResponse = flow<Resource<CurrencyNamesModel>> {
            emit(Resource.Success(currencyNamesResponseBody))
        }

        /* Defines the response body for getLatestExchangeRatesUseCase(). */
        val exchangeRatesResponseBody: ExchangeRatesModel = mockk()
        val exchangeRateModel = RateModel(
            code = codeCurrencyIndonesia,
            name = "",
            value = valueCurrencyIndonesia
        )
        val exchangeRateList = listOf(exchangeRateModel)
        val exchangeRatesFlowResponse = flow<Resource<ExchangeRatesModel>> {
            emit(Resource.Success(exchangeRatesResponseBody))
        }

        /* Setting the answers of mockking. */
        every { currencyNamesResponseBody.currencyList } returns currencyList
        every { exchangeRatesResponseBody.rates } returns exchangeRateList
        every { exchangeRatesResponseBody.base } returns baseCodeCurrency
        every { runBlocking { getCurrencyNamesUseCase() } } returns currencyNamesFlowResponse
        every { runBlocking { getLatestExchangeRatesUseCase() } } returns exchangeRatesFlowResponse

        /* Calling the selected function. */
        viewModel.getExchangeRates()

        getLatestExchangeRatesUseCase().collectLatest {
            delay(2_000)    /* Wait 2 secs for the loading live data is updated. */
            verify(exactly = 2) {
                loadingObserver.onChanged(any())
            }

            val testObserver = viewModel.loadingLiveData.test()
                .assertHasValue()
            val content = testObserver.value()
            assertEquals(false, content)
            assertNotEquals(true, content)

            excludeRecords { viewModel.loadingLiveData.observeForever(testObserver) }

            confirmVerified(
                loadingObserver
            )
        }
    }
    //endregion `Unit Tests - Update Loading Live Data in getExchangeRates()`

    //region Unit Tests - Update Error Live Data in getExchangeRates()
    @Test
    fun `(-) call getExchangeRates and failed should update error live data`() = runTest {
        /* Defines the response body for getCurrencyNamesUseCase(). */
        val currencyNamesResponseBody: CurrencyNamesModel = mockk()
        val currencyPair = Pair(codeCurrencyIndonesia, nameCurrencyIndonesia)
        val currencyList = listOf(currencyPair)
        val currencyNamesFlowResponse = flow<Resource<CurrencyNamesModel>> {
            emit(Resource.Success(currencyNamesResponseBody))
        }

        /* Defines the response error for getLatestExchangeRatesUseCase(). */
        val exchangeRatesFlowResponse = flow<Resource<ExchangeRatesModel>> {
            emit(Resource.Error(errorMessage))
        }

        /* Setting the answers of mockking. */
        every { currencyNamesResponseBody.currencyList } returns currencyList
        every { runBlocking { getCurrencyNamesUseCase() } } returns currencyNamesFlowResponse
        every { runBlocking { getLatestExchangeRatesUseCase() } } returns exchangeRatesFlowResponse

        /* Calling the selected function. */
        viewModel.getExchangeRates()

        getLatestExchangeRatesUseCase().collectLatest {
            delay(2_000)    /* Wait 2 secs for the error live data is updated. */
            verify(exactly = 1) {
                errorObserver.onChanged(any())
            }

            val testObserver = viewModel.errorLiveData.test()
                .assertHasValue()
            val content = testObserver.value().peekContent()
            assertEquals(errorMessage, content)

            excludeRecords { viewModel.errorLiveData.observeForever(testObserver) }

            confirmVerified(
                errorObserver
            )
        }
    }
    //endregion `Unit Tests - Update Error Live Data in getExchangeRates()`

    //region Unit Tests - Change Amount
    @Test
    fun `(+) change amount should update the currency value`() = runTest {
        val newAmount = 2.0

        val newList = listOf(
            RateModel(
                code = codeCurrencyIndonesia,
                name = nameCurrencyIndonesia,
                value = valueCurrencyIndonesia
            ),
            RateModel(
                code = codeCurrencyJapan,
                name = nameCurrencyJapan,
                value = valueCurrencyJapan
            )
        )
        /* Modify variables in viewModel for unit test scenario. */
        viewModel.unitTestChangeExchangeRatePoolList(newList = newList)
        viewModel.unitTestChangeCurrentValueCurrencyBase(newValue = 1.0)

        /* Call the selected function. */
        viewModel.doExchangeRatesCalculation(givenValue = newAmount)

        delay(2_000)
        verify(exactly = 1) {
            exchangeRateListObserver.onChanged(any())
        }

        val testObserver = viewModel.exchangeRateListLiveData.test()
            .assertHasValue()
        val content = testObserver.value()
        assertThat(content).isNotNull
        assertThat(content).isNotEmpty
        assertTrue(content.size == newList.size)
        val expectedValueIndonesia = valueCurrencyIndonesia * newAmount
        val expectedValueJapan = valueCurrencyJapan * newAmount
        assertTrue(content.first().value == expectedValueIndonesia)
        assertTrue(content[1].value == expectedValueJapan)

        excludeRecords { viewModel.exchangeRateListLiveData.observeForever(testObserver) }

        confirmVerified(
            exchangeRateListObserver
        )
    }

    @Test
    fun `(-) change amount with 0 value will not update the currency value`() = runTest {
        val newList = listOf(
            RateModel(
                code = codeCurrencyIndonesia,
                name = nameCurrencyIndonesia,
                value = valueCurrencyIndonesia
            ),
            RateModel(
                code = codeCurrencyJapan,
                name = nameCurrencyJapan,
                value = valueCurrencyJapan
            )
        )
        /* Modified variables in viewModel for unit test scenario. */
        viewModel.unitTestChangeExchangeRateListLiveData(newList = newList)

        /* Call the selected function. */
        viewModel.doExchangeRatesCalculation(givenValue = 0.0)

        val content = viewModel.exchangeRateListLiveData.value
        assertThat(content).isNotNull
        assertThat(content).isNotEmpty
        assertTrue(content!!.size == newList.size)
        assertTrue(content.first().value == valueCurrencyIndonesia)
        assertTrue(content[1].value == valueCurrencyJapan)
    }
    //endregion `Unit Tests - Change Amount`

    //region Unit Tests - Change Base Currency
    @Test
    fun `(+) set base currency should update live data`() = runTest {
        viewModel.setBaseCurrencyCode(code = codeCurrencyIndonesia)

        verify(exactly = 1) {
            currencyBaseObserver.onChanged(any())
        }

        val testObserver = viewModel.currencyBaseLiveData.test()
            .assertHasValue()
        val content = testObserver.value()
        assertEquals(codeCurrencyIndonesia, content)

        excludeRecords { viewModel.currencyBaseLiveData.observeForever(testObserver) }

        confirmVerified(
            currencyBaseObserver
        )
    }

    @Test
    fun `(+) change base currency should update the value of currency base and do the calculation`() = runTest {
        val currentAmount = 1.0
        val newList = listOf(
            RateModel(
                code = codeCurrencyIndonesia,
                name = nameCurrencyIndonesia,
                value = valueCurrencyIndonesia
            ),
            RateModel(
                code = codeCurrencyJapan,
                name = nameCurrencyJapan,
                value = valueCurrencyJapan
            )
        )
        /* Modified variables in viewModel for unit test scenario. */
        viewModel.unitTestChangeExchangeRatePoolList(newList = newList)
        viewModel.unitTestChangeExchangeRateListLiveData(newList = newList)

        /* Change the base currency to IDR. */
        viewModel.changeBaseCurrency(code = codeCurrencyIndonesia)

        delay(2_000)
        /* Verify if the observers have been any changed. */
        verify(exactly = 1) {
            currencyBaseObserver.onChanged(any())
        }
        verify(atLeast = 1) {
            exchangeRateListObserver.onChanged(any())
        }

        /* Check and assert the currentBaseLiveData. */
        val currencyBaseTestObserver = viewModel.currencyBaseLiveData.test()
            .assertHasValue()
        val currencyBaseContent = currencyBaseTestObserver.value()
        assertEquals(codeCurrencyIndonesia, currencyBaseContent)

        /* Check and assert the exchangeRateListLiveData. */
        val exchangeRateTestObserver = viewModel.exchangeRateListLiveData.test()
            .assertHasValue()
        val content = exchangeRateTestObserver.value()
        assertThat(content).isNotNull
        assertThat(content).isNotEmpty
        assertTrue(content.size == newList.size)
        val expectedValueIndonesia = valueCurrencyIndonesia * currentAmount / valueCurrencyIndonesia
        val expectedValueJapan = valueCurrencyJapan * currentAmount / valueCurrencyIndonesia
        assertTrue(content.first().value == expectedValueIndonesia)
        assertTrue(content[1].value == expectedValueJapan)

        excludeRecords {
            viewModel.currencyBaseLiveData.observeForever(currencyBaseTestObserver)
            viewModel.exchangeRateListLiveData.observeForever(exchangeRateTestObserver)
        }

        confirmVerified(
            currencyBaseObserver,
            exchangeRateListObserver
        )
    }
    //endregion `Unit Tests - Change Base Currency`
}