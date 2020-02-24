package com.robot.android.currencyapp

import androidx.recyclerview.widget.DiffUtil


class DiffCallback() : DiffUtil.Callback() {

    var oldCurrecnies = ArrayList<Rate>()
    var newCurrecnies = ArrayList<Rate>()

    constructor(newCurrecnies: ArrayList<Rate>, oldCurrecnies: ArrayList<Rate>): this(){
        this.newCurrecnies = newCurrecnies
        this.oldCurrecnies = oldCurrecnies
    }

    override fun getOldListSize(): Int {
        return oldCurrecnies.size
    }

    override fun getNewListSize(): Int {
        return newCurrecnies.size
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldCurrecnies[oldItemPosition] == (newCurrecnies[newItemPosition])
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return true
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        val oldItem = oldCurrecnies[oldItemPosition]
        val newItem = newCurrecnies[newItemPosition]

        return Change(
            oldItem,
            newItem)
    }
}