package com.robot.android.currencyapp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.robot.android.currencyapp.server.CurrenciesRepository
import com.robot.android.currencyapp.server.ICurrrenciesService
import com.robot.android.currencyapp.server.RetrofitFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import kotlin.coroutines.CoroutineContext


class CurrenciesViewModel() : ViewModel() {

    private val parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Default
    var scope = CoroutineScope(coroutineContext)
    var currenciesRepository: CurrenciesRepository? = null

    val currenciesLiveData = MutableLiveData<CurrencyModel>()
    val recalculated = MutableLiveData<CurrencyModel>()
    val serverError = MutableLiveData<String>()

    var cModel: CurrencyModel? = null

    init {
        val reqService = RetrofitFactory().builder.create(ICurrrenciesService::class.java)
        currenciesRepository = CurrenciesRepository(reqService)
        getCurrencies()
    }

    //For unit tests to set testRepo and testScope for coroutines
    constructor(repo: CurrenciesRepository, scope: CoroutineScope) : this() {
        currenciesRepository = repo
        this.scope = scope
    }

    fun getCurrencies(baseCurrency: String = "EUR", amount: Double? = null) {
        scope.launch {
            val currencies = currenciesRepository?.getCurrencies(baseCurrency)
            currencies?.let {
                if (cModel != null) {
                    amount?.let {
                        cModel = currencies
                        recalculateAmounts(amount)
                    }
                } else {
                    cModel = currencies
                    currenciesLiveData.postValue(cModel)
                }
                if (currenciesRepository?.getResponseError() != null) {
                    serverError.postValue(currenciesRepository?.getResponseError())
                }
            } ?: serverError.postValue(currenciesRepository?.getResponseError())
        }
    }

    fun recalculateAmounts(amount: Double) {
        val newData = CurrencyModel()
        newData.baseCurrency = cModel?.baseCurrency ?: ""

        val firstRate = Rate(-1,newData.baseCurrency,1.0)

        firstRate.amount = getFormattedAmount(amount)

        newData.rates.add(firstRate)

        cModel?.rates?.forEachIndexed { index, rate ->
            if (rate.code != cModel?.baseCurrency) {
                val newRate = Rate(index, rate.code, rate.currency)
                val newAmount = amount * rate.currency
                val fAmount = getFormattedAmount(newAmount)
                newRate.amount = fAmount

                newData.rates.add(newRate)
            }
        }

        recalculated.postValue(newData)
    }

    fun getFormattedAmount(amount: Double) : Double {
        val df = DecimalFormat("#.##")
        return java.lang.Double.parseDouble(df.format(amount))
    }
}