package com.internship.retailmanagement.controllers.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.internship.retailmanagement.R
import com.internship.retailmanagement.dataclasses.invoices.OrderProdItem
import kotlinx.android.synthetic.main.order_item_card.view.*
import kotlinx.android.synthetic.main.stock_movement_card.view.*

class OrderProdAdapter(private var productsList: MutableList<OrderProdItem>) :
    RecyclerView.Adapter<OrderProdAdapter.OrderProdCardViewHolder>() {

    val productsListClone: List<OrderProdItem>

    init{
        productsListClone = productsList.toMutableList() //set list clone
    }

    class OrderProdCardViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val nameView: TextView = itemView.nameCard
        private val qtyView: TextView = itemView.qtyCard

        fun bindView(orderProdItem: OrderProdItem) {
            nameView.text = orderProdItem.productName
            qtyView.text = orderProdItem.quantity.toString()
        }
    }

    //Mandatory methods
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderProdCardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.order_item_card, parent, false
        )
        return OrderProdCardViewHolder(itemView)
    }

    override fun getItemCount() = productsList.size

    override fun onBindViewHolder(holder: OrderProdCardViewHolder, position: Int) {
        return holder.bindView(productsList[position])
    }
}