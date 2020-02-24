package com.robot.android.currencyapp

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.robot.android.currencyapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    internal var currenciesList: RecyclerView? = null
    var binding: ActivityMainBinding? = null
    var currencyModel: CurrencyModel? = null
    var error: String? = null
    private lateinit var viewModel: CurrenciesViewModel
    var currenciesListAdapter: CurrenciesAdapter? = null

    val ratesVisibility: Int
        get() {
            return currencyModel?.let { VISIBLE } ?: GONE
        }

    val messageVisibility: Int
        get() {
            return if (error != null || currencyModel != null) VISIBLE else GONE
        }

    val progressBarVisibility: Int
        get() {
            return if (error == null && currencyModel == null) VISIBLE else GONE
        }

    val messageText: String
        get() {
            return error?.let {
                error
            } ?: "Rates"

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(CurrenciesViewModel::class.java)

        viewModel.currenciesLiveData.observe(this, Observer {
            if (currencyModel == null) {
                currencyModel = it
                binding?.viewWrapper = this
                setListAdapter(it)
            }
        })

        viewModel.serverError.observe(this, Observer {
            it?.let {
                error = it
                binding?.viewWrapper = this
            }
        })

        viewModel.recalculated.observe(this, Observer {
            resetListAdapter(it)
        })
    }

    private fun resetListAdapter(currencies: CurrencyModel?) {
        currencies?.let {
            currenciesListAdapter?.updateList(currencies)
            (currenciesList?.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(0, 0)
        }
    }

    private fun setListAdapter(currencies: CurrencyModel) {
        currencies.let {

            currenciesList = binding?.rView

            //reload View according to new Data
            binding?.viewWrapper = this

            currenciesListAdapter = CurrenciesAdapter(this, it)
            val layoutManager = LinearLayoutManager(this)
            currenciesList?.layoutManager = layoutManager
            currenciesList?.adapter = currenciesListAdapter

            currenciesListAdapter?.registerAdapterDataObserver(object :
                RecyclerView.AdapterDataObserver() {
                override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                    (currenciesList?.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                        0,
                        0
                    )
                }
            })
        }
    }

    fun amountHasChanged(amount: Double) {
        viewModel.recalculateAmounts(amount)
    }

    fun fetchCurrenciesForNewBase(baseCurrency: String, amount: Double?) {
        viewModel.getCurrencies(baseCurrency, amount)
    }
}