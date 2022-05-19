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
import com.internship.retailmanagement.dataclasses.stockmovements.StockMovItem
import kotlinx.android.synthetic.main.stock_movement_card.view.*
import java.text.SimpleDateFormat
import java.util.*

class StockMovAdapter(private var stockMovsList: MutableList<StockMovItem>) :
    RecyclerView.Adapter<StockMovAdapter.StockMovCardViewHolder>(), Filterable {

    private val stockMovsListClone: List<StockMovItem>

    init {
        stockMovsListClone = stockMovsList.toMutableList() //set list clone
    }

    class StockMovCardViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val dateView: TextView = itemView.dateCard
        private val movementView: TextView = itemView.movementCard
        private val quantityView: TextView = itemView.quantityCard

        fun bindView(
            stockMovItem: StockMovItem
        ) {

            dateView.text = stockMovItem.createdAt!!.toDate().formatTo("dd-MM-yyyy HH:mm:ss")
            movementView.text = stockMovItem.movement.toString()
            quantityView.text = stockMovItem.quantity.toString()
        }


        /**
         * Method to parse a string that represents UTC date ("yyyy-MM-dd'T'HH:mm:ss'Z'") to Date type
         * @param dateFormat    string UTC date
         * @param timeZone      timeZone UTC
         * @return Date
         */
        fun String.toDate(
            dateFormat: String = "yyyy-MM-dd'T'HH:mm:ss'Z'",
            timeZone: TimeZone = TimeZone.getTimeZone("UTC")
        ): Date {
            val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
            parser.timeZone = timeZone
            return parser.parse(this)!!
        }


        /**
         * Method to parse a Date with a pre-established format to a String with the intended format
         * @param dateFormat    string representing intended date format (Ex: "yyyy-MM-dd")
         * @param timeZone      default timeZone
         * @return String representing the date with intended format.
         */
        fun Date.formatTo(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault()): String {
            val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
            formatter.timeZone = timeZone
            return formatter.format(this)
        }
    }

    //Mandatory methods
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockMovCardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.stock_movement_card, parent, false
        )
        return StockMovCardViewHolder(itemView)
    }

    override fun getItemCount() = stockMovsList.size

    override fun onBindViewHolder(holder: StockMovCardViewHolder, position: Int) {
        return holder.bindView(stockMovsList[position])
    }

    override fun getFilter(): Filter = StockMovFilter()
    private inner class StockMovFilter : Filter() {
        @SuppressLint("DefaultLocale")
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = mutableListOf<StockMovItem>()
            if (constraint == null || constraint.isEmpty())
                filteredList.addAll(stockMovsListClone)
            else {
                val filterPattern = constraint.toString().lowercase().trim()
                for (stockMov in stockMovsListClone) {
                    if (stockMov.movement.toString().lowercase()
                            .contains(filterPattern)
                    )
                        filteredList.add(stockMov)
                }
            }
            val filterRes = FilterResults()
            filterRes.values = filteredList
            return filterRes
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            stockMovsList.clear()
            stockMovsList.addAll(results?.values as List<StockMovItem>)
            notifyDataSetChanged()
        }
    }
}