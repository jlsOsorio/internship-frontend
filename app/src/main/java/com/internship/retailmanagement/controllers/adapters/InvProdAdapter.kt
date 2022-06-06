package com.internship.retailmanagement.controllers.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.internship.retailmanagement.R
import com.internship.retailmanagement.dataclasses.invoices.InvProdItem
import kotlinx.android.synthetic.main.invoiced_product_card.view.*
import java.text.DecimalFormat

class InvProdAdapter(private var productsList: MutableList<InvProdItem>) :
    RecyclerView.Adapter<InvProdAdapter.InvProdViewHolder>(), Filterable {

    val productsListClone: List<InvProdItem>

    init{
        productsListClone = productsList.toMutableList() //set list clone
    }

    class InvProdViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val nameView: TextView = itemView.nameCard
        private val ivaView: TextView = itemView.ivaCard
        private val quantityView: TextView = itemView.qtyCard
        private val subTotalNoIvaView: TextView = itemView.noIvaCard
        private val subTotalIvaView: TextView = itemView.totalIvaCard
        val df = DecimalFormat("#.##")

        fun bindView(invProdItem: InvProdItem) {

            nameView.text = invProdItem.productName?.substring(0, 9)
            ivaView.text = invProdItem.ivaValue.toString()
            quantityView.text = invProdItem.quantity.toString()
            val subTotNoIvaRounded = df.format(invProdItem.subTotalNoIva)
            val subTotIvaRounded = df.format(invProdItem.subTotalIva)
            subTotalNoIvaView.text = subTotNoIvaRounded.toString()
            subTotalIvaView.text = subTotIvaRounded.toString()
        }
    }

    //Mandatory methods
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvProdViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.invoiced_product_card, parent, false
        )
        return InvProdViewHolder(itemView)
    }

    override fun getItemCount() = productsList.size

    override fun onBindViewHolder(holder: InvProdViewHolder, position: Int) {
        return holder.bindView(productsList[position])
    }

    override fun getFilter(): Filter = InvProdFilter()
    private inner class InvProdFilter : Filter() {
        @SuppressLint("DefaultLocale")
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = mutableListOf<InvProdItem>()
            if (constraint == null || constraint.isEmpty())
                filteredList.addAll(productsListClone)
            else {
                val filterPattern = constraint.toString().lowercase().trim()
                for (invProd in productsListClone) {
                    if (invProd.productName!!.lowercase()
                            .contains(filterPattern)
                    ) // .contains or .startsWith
                        filteredList.add(invProd)
                }
            }
            val filterRes = FilterResults()
            filterRes.values = filteredList
            return filterRes
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            productsList.clear()
            productsList.addAll(results?.values as List<InvProdItem>)
            notifyDataSetChanged()
        }
    }
}