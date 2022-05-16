package com.internship.retailmanagement.controllers.adapters.spinners

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.internship.retailmanagement.R
import com.internship.retailmanagement.dataclasses.IvaItem
import kotlinx.android.synthetic.main.iva_spinner.view.*

class IvaSpinnerAdapter(context: Context, ivaList: MutableList<IvaItem>) : ArrayAdapter<IvaItem>(context, 0, ivaList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    private fun initView(position: Int, convertView: View?, parent: ViewGroup): View {
        val iva = getItem(position)
        val view = LayoutInflater.from(context).inflate(R.layout.iva_spinner, parent, false)
        view.idIva.text = iva!!.id.toString()
        view.valueNameIva.text = iva!!.value
        view.valueIva.text = iva!!.tax.toString()

        return view
    }
}