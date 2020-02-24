package com.robot.android.currencyapp

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import androidx.lifecycle.LiveData
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.robot.android.currencyapp.server.CurrenciesRepository
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.json.JSONObject
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

@RunWith(JUnit4::class)
class CurrenciesViewModelTest : Spek({

    beforeEachTest {
        ArchTaskExecutor.getInstance().setDelegate(object : TaskExecutor() {
            override fun executeOnDiskIO(runnable: Runnable) {
                runnable.run()
            }

            override fun isMainThread(): Boolean {
                return true
            }

            override fun postToMainThread(runnable: Runnable) {
                runnable.run()
            }
        })
    }

    afterEachTest { ArchTaskExecutor.getInstance().setDelegate(null) }

    describe("test getFormattedDouble()") {
        it("should return amount with 2 digits after comma") {
            val amount = 1.123456

            val currencyViewModel = CurrenciesViewModel()

            assertEquals(expected = 1.12, actual = currencyViewModel.getFormattedAmount(amount))
        }
    }

    describe("test recalculateAmounts()") {

        val jsonString = """{"baseCurrency":"EUR","rates":{"AUD":1.589,"BGN":1.994,"BRL":4.223,"CAD":1.522,"CHF":1.144,"CNY":7.678,"CZK":26.128,"DKK":7.556,"GBP":0.877,"HKD":8.898,"HRK":7.45,"HUF":324.2,"IDR":16194.769,"ILS":4.1,"INR":81.291,"ISK":136.068,"JPY":124.867,"KRW":1285.212,"MXN":21.772,"MYR":4.65,"NOK":9.791,"NZD":1.65,"PHP":59.256,"PLN":4.349,"RON":4.783,"RUB":75.287,"SEK":10.633,"SGD":1.55,"THB":35.574,"USD":1.14,"ZAR":16.167}}"""

        it("should recalculate amounts in currencyModel due to amount entered for base") {

            val model = CurrencyModel(JSONObject(jsonString))
            val currencyViewModel = CurrenciesViewModel()

            currencyViewModel.cModel = model

            val rate1BeforeMethod = model.rates[1]
            val rate2BeforeMethod = model.rates[2]

            currencyViewModel.recalculated.observeOnce {
                assertEquals(expected = "CHF", actual = currencyViewModel.recalculated.value!!.rates[1].code)
                assertEquals(expected = 1.144, actual = currencyViewModel.recalculated.value!!.rates[1].currency)
                assertEquals(expected = 11.44, actual = currencyViewModel.recalculated.value!!.rates[1].amount)
                assertEquals(expected = 1, actual = currencyViewModel.recalculated.value!!.rates[1].id)
                assertEquals(expected = false, actual = rate1BeforeMethod == currencyViewModel.recalculated.value!!.rates[1])

                assertEquals(expected = "HRK", actual = currencyViewModel.recalculated.value!!.rates[2].code)
                assertEquals(expected = 7.45, actual = currencyViewModel.recalculated.value!!.rates[2].currency)
                assertEquals(expected = 74.5, actual = currencyViewModel.recalculated.value!!.rates[2].amount)
                assertEquals(expected = 2, actual = currencyViewModel.recalculated.value!!.rates[2].id)
                assertEquals(expected = false, actual = rate2BeforeMethod == currencyViewModel.recalculated.value!!.rates[2])
            }

            currencyViewModel.recalculateAmounts(10.0)
        }
    }

    describe("test getCurrencies()") {

        val jsonString = """{"baseCurrency":"EUR","rates":{"AUD":1.589,"BGN":1.994,"BRL":4.223,"CAD":1.522,"CHF":1.144,"CNY":7.678,"CZK":26.128,"DKK":7.556,"GBP":0.877,"HKD":8.898,"HRK":7.45,"HUF":324.2,"IDR":16194.769,"ILS":4.1,"INR":81.291,"ISK":136.068,"JPY":124.867,"KRW":1285.212,"MXN":21.772,"MYR":4.65,"NOK":9.791,"NZD":1.65,"PHP":59.256,"PLN":4.349,"RON":4.783,"RUB":75.287,"SEK":10.633,"SGD":1.55,"THB":35.574,"USD":1.14,"ZAR":16.167}}"""

        it("should populate cModel in currencyViewModel") {

            val testDispatcher = TestCoroutineDispatcher()
            val testScope = TestCoroutineScope(testDispatcher)

            val model = CurrencyModel(JSONObject(jsonString))

            testScope.runBlockingTest {
                val mockRepo = mock<CurrenciesRepository> {
                    onBlocking { getCurrencies("EUR") } doReturn model
                }

                val currencyViewModel = CurrenciesViewModel(mockRepo, testScope)
                currencyViewModel.currenciesRepository = mockRepo

                currencyViewModel.currenciesLiveData.observeOnce {
                    assertEquals(expected = "CHF", actual = currencyViewModel.currenciesLiveData.value!!.rates[1].code)
                    assertEquals(expected = 1.144, actual = currencyViewModel.currenciesLiveData.value!!.rates[1].currency)
                    assertEquals(expected = 0.0, actual = currencyViewModel.currenciesLiveData.value!!.rates[1].amount)
                    assertEquals(expected = 0, actual = currencyViewModel.currenciesLiveData.value!!.rates[1].id)

                    assertEquals(expected = "HRK", actual = currencyViewModel.currenciesLiveData.value!!.rates[2].code)
                    assertEquals(expected = 7.45, actual = currencyViewModel.currenciesLiveData.value!!.rates[2].currency)
                    assertEquals(expected = 0.0, actual = currencyViewModel.currenciesLiveData.value!!.rates[2].amount)
                    assertEquals(expected = 1, actual = currencyViewModel.currenciesLiveData.value!!.rates[2].id)
                }

                currencyViewModel.getCurrencies()
            }
        }
    }
})

fun <T> LiveData<T>.observeOnce(onChangeHandler: (T) -> Unit) {
    val observer = OneTimeObserver(handler = onChangeHandler)
    observe(observer, observer)
}
