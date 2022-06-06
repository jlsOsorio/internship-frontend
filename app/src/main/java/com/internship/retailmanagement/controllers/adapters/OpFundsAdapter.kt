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
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.dataclasses.operatingfunds.OpFundItem
import kotlinx.android.synthetic.main.operating_fund_card.view.*
import java.text.SimpleDateFormat
import java.util.*

class OpFundsAdapter(private var opFundsList: MutableList<OpFundItem>, private val gv: GlobalVar, private val editListener: (OpFundItem, Long, Double, Double, Long, String) -> Unit, private val removeListener: (OpFundItem, Long) -> Unit) :
    RecyclerView.Adapter<OpFundsAdapter.OpFundCardViewHolder>(), Filterable {
    private val opFundsListClone: List<OpFundItem>

    init{
        opFundsListClone = opFundsList.toMutableList() //set list clone
    }

    class OpFundCardViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val dateView: TextView = itemView.dateCard
        private val entryView: TextView = itemView.entryCard
        private val exitView: TextView = itemView.exitCard
        private val editView: ImageView = itemView.updateCardOpFund
        private val removeView: ImageView = itemView.removeCard

        fun bindView(gv: GlobalVar, opFundItem: OpFundItem, editListener: (OpFundItem, Long, Double, Double, Long, String) -> Unit, removeListener: (OpFundItem, Long) -> Unit) {

            editView.visibility = View.GONE
            removeView.visibility = View.GONE

            dateView.text = opFundItem.moment!!.toDate().formatTo("dd-MM-yyyy HH:mm:ss")
            entryView.text = opFundItem.entryQty.toString()
            exitView.text = opFundItem.exitQty.toString()

            if (gv.userRole == "SUPERVISOR")
            {
                editView.visibility = View.VISIBLE
                removeView.visibility = View.VISIBLE

                editView.setOnClickListener{
                    editListener(opFundItem, opFundItem.id!!, opFundItem.entryQty!!, opFundItem.exitQty!!, opFundItem.cashRegister!!.id!!, opFundItem.moment) //Go to changing content activity of this specific user
                }

                removeView.setOnClickListener{
                    removeListener(opFundItem, opFundItem.id!!)
                }
            }
        }


        /**
         * Method to parse a string that represents UTC date ("yyyy-MM-dd'T'HH:mm:ss'Z'") to Date type
         * @param dateFormat    string UTC date
         * @param timeZone      timeZone UTC
         * @return Date
         */
        private fun String.toDate(dateFormat: String = "yyyy-MM-dd'T'HH:mm:ss'Z'", timeZone: TimeZone = TimeZone.getTimeZone("UTC")): Date {
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
        private fun Date.formatTo(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault()): String {
            val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
            formatter.timeZone = timeZone
            return formatter.format(this)
        }
    }

    /**
     * Method to parse a string that represents UTC date ("yyyy-MM-dd'T'HH:mm:ss'Z'") to Date type
     * @param dateFormat    string UTC date
     * @param timeZone      timeZone UTC
     * @return Date
     */
    fun String.toDate(dateFormat: String = "yyyy-MM-dd'T'HH:mm:ss'Z'", timeZone: TimeZone = TimeZone.getTimeZone("UTC")): Date {
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

    //Mandatory methods
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OpFundCardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.operating_fund_card, parent, false
        )
        return OpFundCardViewHolder(itemView)
    }

    override fun getItemCount() = opFundsList.size

    override fun onBindViewHolder(holder: OpFundCardViewHolder, position: Int) {
        return holder.bindView(gv, opFundsList[position], editListener, removeListener)
    }

    override fun getFilter(): Filter = OpFundFilter()
    private inner class OpFundFilter : Filter() {
        @SuppressLint("DefaultLocale")
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = mutableListOf<OpFundItem>()
            if (constraint == null || constraint.isEmpty())
                filteredList.addAll(opFundsListClone)
            else {
                val filterPattern = constraint.toString().lowercase().trim()
                for (opFund in opFundsListClone) {
                    if (opFund.moment!!.toDate().formatTo("dd-MM-yyyy HH:mm:ss").lowercase()
                            .contains(filterPattern)
                    ) // .contains or .startsWith
                        filteredList.add(opFund)
                }
            }
            val filterRes = FilterResults()
            filterRes.values = filteredList
            return filterRes
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            opFundsList.clear()
            opFundsList.addAll(results?.values as List<OpFundItem>)
            notifyDataSetChanged()
        }
    }

}