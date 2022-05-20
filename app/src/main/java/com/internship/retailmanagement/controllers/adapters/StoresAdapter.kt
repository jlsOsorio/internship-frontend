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
import com.internship.retailmanagement.dataclasses.stores.StoreItem
import kotlinx.android.synthetic.main.store_card.view.*

class StoresAdapter(private var storesList: MutableList<StoreItem>, private val infoListener: (StoreItem, Long) -> Unit, private val editListener: (StoreItem, Long, String, String, String, String, String, Int) -> Unit, private val removeListener: (StoreItem, Long) -> Unit) :
    RecyclerView.Adapter<StoresAdapter.StoreCardViewHolder>(), Filterable {

    private val storesListClone: List<StoreItem>

    init{
        storesListClone = storesList.toMutableList() //set list clone
    }

    class StoreCardViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val cityView: TextView = itemView.cityCard
        private val addressView: TextView = itemView.addressCard
        private val zipCodeView: TextView = itemView.zipCodeCard
        private val editView: ImageView = itemView.updateCard
        private val removeView: ImageView = itemView.removeCard

        fun bindView(storeItem: StoreItem, infoListener: (StoreItem, Long) -> Unit, editListener: (StoreItem, Long, String, String, String, String, String, Int) -> Unit, removeListener: (StoreItem, Long) -> Unit) {

            cityView.text = storeItem.council
            addressView.text = storeItem.address
            zipCodeView.text = storeItem.zipCode

            editView.setOnClickListener{
                editListener(
                    storeItem,
                    storeItem.id!!,
                    storeItem.address!!,
                    storeItem.council!!,
                    storeItem.zipCode!!,
                    storeItem.contact!!,
                    storeItem.status!!,
                    storeItem.cashRegisters!!.size) //Go to changing content activity of this specific user
            }

            removeView.setOnClickListener{
                removeListener(storeItem, storeItem.id!!)
            }

            itemView.setOnClickListener{
                infoListener(storeItem, storeItem.id!!)
            }
        }
    }

    //Mandatory methods
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreCardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.store_card, parent, false
        )
        return StoreCardViewHolder(itemView)
    }

    override fun getItemCount() = storesList.size

    override fun onBindViewHolder(holder: StoreCardViewHolder, position: Int) {
        return holder.bindView(storesList[position], infoListener, editListener, removeListener)
    }

    override fun getFilter(): Filter = StoreFilter()
    private inner class StoreFilter : Filter() {
        @SuppressLint("DefaultLocale")
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = mutableListOf<StoreItem>()
            if (constraint == null || constraint.isEmpty())
                filteredList.addAll(storesListClone)
            else {
                val filterPattern = constraint.toString().lowercase().trim()
                for (store in storesListClone) {
                    if (store.address!!
                            .contains(filterPattern)
                    ) // .contains or .startsWith
                        filteredList.add(store)
                }
            }
            val filterRes = FilterResults()
            filterRes.values = filteredList
            return filterRes
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            storesList.clear()
            storesList.addAll(results?.values as List<StoreItem>)
            notifyDataSetChanged()
        }
    }
}