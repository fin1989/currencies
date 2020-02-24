package com.robot.android.currencyapp

import org.json.JSONObject

class CurrencyModel() {
    var baseCurrency: String = ""
    val rates = ArrayList<Rate>()

    constructor(json : JSONObject) : this() {
        baseCurrency = json.optString("baseCurrency")

        rates.add(Rate(-1,baseCurrency,1.0))

        val rts = json.optJSONObject("rates")
        var index = 0
        val iter = rts?.keys()
        while (iter?.hasNext() == true) {
            val key = iter.next()
            rates.add(Rate(index, key, rts.optDouble(key)))
            index++
        }
    }
}

class Rate {
    var id: Int = 0
    var code: String
    var currency: Double
    var amount: Double

    constructor(id: Int, code: String, currency: Double) {
        this.id = id
        this.code = code
        this.currency = currency

        amount = 0.0
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Rate) {
            this.id == other.id &&
                    this.code == other.code &&
                    this.currency == other.currency &&
                    this.amount == other.amount
        } else {
            false
        }

    }
}

data class Change(val oldItem: Rate, val newItem: Rate)