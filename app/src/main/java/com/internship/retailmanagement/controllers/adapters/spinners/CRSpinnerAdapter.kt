package com.internship.retailmanagement.controllers.adapters.spinners

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.internship.retailmanagement.R
import com.internship.retailmanagement.dataclasses.CashRegisterItem
import kotlinx.android.synthetic.main.cr_spinner.view.*

class CRSpinnerAdapter(context: Context, cashRegList: MutableList<CashRegisterItem>) : ArrayAdapter<CashRegisterItem>(context, 0, cashRegList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    private fun initView(position: Int, convertView: View?, parent: ViewGroup): View {
        val cashRegister = getItem(position)
        val view = LayoutInflater.from(context).inflate(R.layout.cr_spinner, parent, false)
        view.numberCR.text = cashRegister!!.id.toString()
        return view
    }
}