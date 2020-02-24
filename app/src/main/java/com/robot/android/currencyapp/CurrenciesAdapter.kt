package com.robot.android.currencyapp

import android.app.Activity
import android.os.Handler
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


class CurrenciesAdapter(private val activity: Activity, val currencyModel: CurrencyModel) :
    RecyclerView.Adapter<CurrenciesAdapter.CurrencyViewHolder>() {

    var rates: ArrayList<Rate> = currencyModel.rates
    var baseCurrency: String = currencyModel.baseCurrency

    var isBaseCurrencyChanged = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_currency, parent, false)
        return CurrencyViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CurrencyViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            if (payloads.any { it is Change }) {

                val newData = (payloads[0] as Change).newItem
                val oldData = (payloads[0] as Change).oldItem

                //Handling of first item in list
                if (holder.adapterPosition == 0) {
                    if (isBaseCurrencyChanged) {
                        if (newData.amount != oldData.amount) {
                            holder.eText.setText(newData.amount.toString())
                            isBaseCurrencyChanged = false
                        }
                    }
                } else {
                    if (newData.amount != oldData.amount) {
                        holder.eText.setText(newData.amount.toString())
                    }
                    holder.root.setOnClickListener {
                        setNewBaseCurrency(
                            holder,
                            rates[holder.adapterPosition].code,
                            rates[holder.adapterPosition].amount
                        )
                    }
                }

                if (newData.code != oldData.code) {
                    holder.currencyCode.text = newData.code
                    holder.currencyLabel.text = Currency.getInstance(newData.code).displayName
                }

                if (holder.adapterPosition == 0) {
                    if (holder.eText.hasFocus() && newData.amount % 1 == 0.0) {
                        holder.eText.setText(newData.amount.toInt().toString())
                    }
                    holder.eText.addTextChangedListener(listenerForEditText(holder))
                    holder.eText.setSelection(holder.eText.getText().length)
                    holder.eText.inputType = InputType.TYPE_CLASS_NUMBER
                } else {
                    holder.eText.inputType = InputType.TYPE_NULL
                }
            }
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }


    override fun onBindViewHolder(holder: CurrencyViewHolder, position: Int) {
        val rate = rates[holder.adapterPosition]

        if (holder.adapterPosition == 0) {
            Picasso.get().load("https://picsum.photos/200")
                .into(holder.icon)
            holder.currencyCode.text = baseCurrency
            holder.currencyLabel.text = Currency.getInstance(baseCurrency).displayName

            holder.eText.addTextChangedListener(listenerForEditText(holder))

            if (rate.amount > 0) {
                holder.eText.setText(rate.amount.toString())
            }
        } else {
            if (holder.currencyCode.text.isNullOrEmpty()) {
                //forcing picasso load new image and not use cached as the endpoint has not changed
                Picasso.get().load("https://picsum.photos/" + (200 + position))
                    .into(holder.icon)
            }
            holder.currencyCode.text = rates[holder.adapterPosition].code
            holder.currencyLabel.text =
                Currency.getInstance(rates[holder.adapterPosition].code).displayName
            holder.eText.setText(rates[holder.adapterPosition].amount.toString())
            holder.root.setOnClickListener {
                setNewBaseCurrency(
                    holder,
                    rates[holder.adapterPosition].code,
                    rates[holder.adapterPosition].amount
                )
            }
        }
    }

    private fun setNewBaseCurrency(holder: CurrencyViewHolder, code: String, amount: Double) {
        val oldFirstRate = rates[holder.adapterPosition]
        rates.removeAt(holder.adapterPosition)
        rates.add(0, oldFirstRate)
        notifyItemMoved(holder.adapterPosition, 0)
        holder.eText.addTextChangedListener(listenerForEditText(holder))

        //delayed call for new currencies to let animation for itemChange finished
        //before populating of fields with recalculated amounts
        Handler().postDelayed({
            (activity as MainActivity).fetchCurrenciesForNewBase(
                code,
                amount
            )
        }, 200)
    }

    private fun listenerForEditText(
        holder: CurrencyViewHolder
    ): TextWatcher {
        return object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            }

            override fun beforeTextChanged(
                s: CharSequence,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun afterTextChanged(s: Editable) {
                if (holder.eText.hasFocus() && holder.adapterPosition == 0 && !isBaseCurrencyChanged) {
                    applyChanges(if (s.isNotEmpty()) s.toString() else "0")
                } else {
                    isBaseCurrencyChanged = false
                }
            }
        }
    }

    private fun applyChanges(s: String = "0") {
        (activity as MainActivity).amountHasChanged(s.toDouble())
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getItemCount(): Int {
        return if (rates.isEmpty()) 0 else rates.size
    }

    fun updateList(currencies: CurrencyModel) {
        val diffResult = DiffUtil.calculateDiff(DiffCallback(currencies.rates, this.rates))
        diffResult.dispatchUpdatesTo(this)
        if (currencies.baseCurrency != baseCurrency) {
            isBaseCurrencyChanged = true
            this.baseCurrency = currencies.baseCurrency
        }
        this.rates = currencies.rates
    }

    inner class CurrencyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var root: ConstraintLayout = itemView.findViewById(R.id.root)
        var icon: CircleImageView = itemView.findViewById(R.id.flagIcon)
        var currencyCode: TextView = itemView.findViewById(R.id.currencyCode)
        var currencyLabel: TextView = itemView.findViewById(R.id.currencyLabel)
        var eText: EditText = itemView.findViewById(R.id.eText)
    }
}