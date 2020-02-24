package com.robot.android.currencyapp

import android.content.Context
import org.json.JSONObject
import org.mockito.Mockito
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

class CurrencyModelTest : Spek({

    val jsonString = """{"baseCurrency":"EUR","rates":{"AUD":1.589,"BGN":1.994,"BRL":4.223,"CAD":1.522,"CHF":1.144,"CNY":7.678,"CZK":26.128,"DKK":7.556,"GBP":0.877,"HKD":8.898,"HRK":7.45,"HUF":324.2,"IDR":16194.769,"ILS":4.1,"INR":81.291,"ISK":136.068,"JPY":124.867,"KRW":1285.212,"MXN":21.772,"MYR":4.65,"NOK":9.791,"NZD":1.65,"PHP":59.256,"PLN":4.349,"RON":4.783,"RUB":75.287,"SEK":10.633,"SGD":1.55,"THB":35.574,"USD":1.14,"ZAR":16.167}}"""

    describe("parseCurrencies") {

        val context : Context = Mockito.mock(Context::class.java)

        it("should parse response from currencies call") {

            val model = CurrencyModel(JSONObject(jsonString))

            assertEquals(expected = "EUR", actual = model.baseCurrency)
            assertEquals(expected = 32, actual = model.rates.size)
        }

        it("should correctly create Rate Object") {
            val model = CurrencyModel(JSONObject(jsonString))
            val rate = model.rates[1]

            assertEquals(expected = "CHF", actual = rate.code)
            assertEquals(expected = 1.144, actual = rate.currency)
            assertEquals(expected = 0.0, actual = rate.amount)
            assertEquals(expected = 0, actual = rate.id)
        }

        it("should return false for equality") {

            val rate1 = Rate(0,"CHF", 1.112)
            val rate2 = Rate(0,"CHF", 1.111)

            assertEquals(false, rate1==rate2)
        }

        it("should return true for equality") {

            val rate1 = Rate(0,"CHF", 1.111)
            val rate2 = Rate(0,"CHF", 1.111)

            assertEquals(true, rate1==rate2)
        }
    }


})