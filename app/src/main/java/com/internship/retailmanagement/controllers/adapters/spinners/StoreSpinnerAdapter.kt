package com.internship.retailmanagement.controllers.adapters.spinners

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.internship.retailmanagement.R
import com.internship.retailmanagement.dataclasses.StoreItem
import kotlinx.android.synthetic.main.store_spinner.view.*

class StoreSpinnerAdapter(context: Context, storeList: MutableList<StoreItem>) : ArrayAdapter<StoreItem>(context, 0, storeList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    private fun initView(position: Int, convertView: View?, parent: ViewGroup) : View {
        val store = getItem(position)
        val view = LayoutInflater.from(context).inflate(R.layout.store_spinner, parent, false)
        view.idStore.text = store!!.id.toString()
        view.addressStore.text = store.address
        view.zipCodeStore.text = store.zipCode

        return view
    }
}