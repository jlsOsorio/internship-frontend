package com.internship.retailmanagement.controllers.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.internship.retailmanagement.R
import com.internship.retailmanagement.dataclasses.invoices.InvoiceItem
import kotlinx.android.synthetic.main.invoice_card.view.*


class InvoicesAdapter(private var invoicesList: MutableList<InvoiceItem>, private val infoListener: (InvoiceItem, Long) -> Unit, private val printListener: (InvoiceItem, Long) -> Unit) :
    RecyclerView.Adapter<InvoicesAdapter.InvoiceCardViewHolder>(), Filterable {

    private val invoicesListClone: List<InvoiceItem>

    init{
        invoicesListClone = invoicesList.toMutableList() //set list clone
    }

    class InvoiceCardViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val numberView: TextView = itemView.invoiceCard
        private val billView: ImageView = itemView.billCard

        fun bindView(invoiceItem: InvoiceItem, infoListener: (InvoiceItem, Long) -> Unit, printListener: (InvoiceItem, Long) -> Unit) {

            numberView.text = invoiceItem.invoiceNumber.toString()

            billView.setOnClickListener{
                printListener(invoiceItem, invoiceItem.invoiceNumber!!)
            }

            itemView.setOnClickListener{
                infoListener(invoiceItem, invoiceItem.invoiceNumber!!)
            }
        }
    }

    //Mandatory methods
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceCardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.invoice_card, parent, false
        )
        return InvoiceCardViewHolder(itemView)
    }

    override fun getItemCount() = invoicesList.size

    override fun onBindViewHolder(holder: InvoiceCardViewHolder, position: Int) {
        return holder.bindView(invoicesList[position], infoListener, printListener)
    }

    override fun getFilter(): Filter = InvoiceFilter()
    private inner class InvoiceFilter : Filter() {
        @SuppressLint("DefaultLocale")
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = mutableListOf<InvoiceItem>()
            if (constraint == null || constraint.isEmpty())
                filteredList.addAll(invoicesListClone)
            else {
                val filterPattern = constraint.toString().lowercase().trim()
                for (invoice in invoicesListClone) {
                    if (invoice.transaction!!.lowercase()
                            .contains(filterPattern)
                    ) // .contains or .startsWith
                        filteredList.add(invoice)
                }
            }
            val filterRes = FilterResults()
            filterRes.values = filteredList
            return filterRes
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            invoicesList.clear()
            invoicesList.addAll(results?.values as List<InvoiceItem>)
            notifyDataSetChanged()
        }
    }

}