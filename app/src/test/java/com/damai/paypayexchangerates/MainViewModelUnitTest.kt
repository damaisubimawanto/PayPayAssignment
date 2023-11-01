package com.damai.paypayexchangerates

import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.damai.base.utils.Event
import com.damai.domain.daos.CurrencyNameDao
import com.damai.domain.daos.RateDao
import com.damai.domain.models.RateModel
import com.damai.domain.repositories.HomeRepository
import com.damai.domain.usecases.GetCurrencyNamesUseCase
import com.damai.domain.usecases.GetLatestExchangeRatesUseCase
import com.damai.paypayexchangerates.application.AppDatabase
import com.damai.paypayexchangerates.ui.main.MainViewModel
import com.damai.paypayexchangerates.utils.CoroutineTestRule
import com.damai.paypayexchangerates.utils.InstantExecutorExtension
import io.mockk.mockk
import org.junit.After
import org.junit.Before
import org.junit.Rule
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
    fun `(+) insert new exchange rate into database should be success`() {

    }

    @Test
    fun `(+) insert new currency name into database should be success`() {

    }

    @Test
    fun `(+) get currency name list use case and returns success`() {

    }

    @Test
    fun `(-) get currency name list use case but returns failed`() {

    }

    @Test
    fun `(+) get latest exchange rate list use case and returns success`() {

    }

    @Test
    fun `(-) get latest exchange rate list use case but returns failed`() {

    }

    @Test
    fun `(+) join currency name list into exchange rates should be success`() {

    }

    @Test
    fun `(+) join null currency name list into exchange rates should be success`() {

    }

    @Test
    fun `(-) join currency name list into empty exchange rates should be empty`() {

    }

    @Test
    fun `(-) join null currency name list into empty exchange rates should be empty`() {
        
    }
}