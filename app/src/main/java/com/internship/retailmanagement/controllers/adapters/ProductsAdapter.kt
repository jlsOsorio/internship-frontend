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
import com.internship.retailmanagement.dataclasses.products.ProductItem
import kotlinx.android.synthetic.main.product_card.view.*
import java.text.DecimalFormat

class ProductsAdapter(private var productsList: MutableList<ProductItem>, private val gv: GlobalVar, private val infoListener: (ProductItem, Long) -> Unit, private val stockMovListener: (ProductItem, Long) -> Unit, private val editListener: (ProductItem, Long, String, Int, Double) -> Unit, private val removeListener: (ProductItem, Long) -> Unit) :
    RecyclerView.Adapter<ProductsAdapter.ProductCardViewHolder>(), Filterable {

    val productsListClone: List<ProductItem>

    init{
        productsListClone = productsList.toMutableList() //set list clone
    }

    class ProductCardViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private val nameView: TextView = itemView.nameCard
        private val stockView: TextView = itemView.stockCard
        private val grossPriceView: TextView = itemView.grossPriceCard
        private val stockMovViewSuper: ImageView = itemView.stockMovCardSuper
        private val stockMovViewNormal: ImageView = itemView.stockMovCardNormal
        private val editView: ImageView = itemView.updateCard
        private val removeView: ImageView = itemView.removeCard
        val df = DecimalFormat("#.##")

        fun bindView(gv: GlobalVar, productItem: ProductItem, infoListener: (ProductItem, Long) -> Unit, stockMovListener: (ProductItem, Long) -> Unit, editListener: (ProductItem, Long, String, Int, Double) -> Unit, removeListener: (ProductItem, Long) -> Unit) {

            stockMovViewSuper.visibility = View.GONE
            editView.visibility = View.GONE
            removeView.visibility = View.GONE

            stockView.text = productItem.stock.toString()
            val grossPriceRounded = df.format(productItem.grossPrice)
            grossPriceView.text = grossPriceRounded.toString()

            if (productItem.name!!.length > 14)
            {
                nameView.text = productItem.name.substring(0, 15)
            }
            else
            {
                nameView.text = productItem.name
            }

            if (gv.userRole == "SUPERVISOR")
            {
                stockMovViewSuper.visibility = View.VISIBLE
                editView.visibility = View.VISIBLE
                removeView.visibility = View.VISIBLE

                editView.setOnClickListener{
                    editListener(productItem, productItem.id!!, productItem.name, productItem.ivaValue!!.toInt(), productItem.grossPrice!!) //Go to changing content activity of this specific user
                }

                removeView.setOnClickListener{
                    removeListener(productItem, productItem.id!!)
                }

                stockMovViewSuper.setOnClickListener{
                    stockMovListener(productItem, productItem.id!!)
                }
            }
            else
            {
                stockMovViewNormal.visibility = View.VISIBLE

                stockMovViewNormal.setOnClickListener{
                    stockMovListener(productItem, productItem.id!!)
                }
            }

            itemView.setOnClickListener{
                infoListener(productItem, productItem.id!!)
            }
        }
    }

    //Mandatory methods
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductCardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.product_card, parent, false
        )
        return ProductCardViewHolder(itemView)
    }

    override fun getItemCount() = productsList.size

    override fun onBindViewHolder(holder: ProductCardViewHolder, position: Int) {
        return holder.bindView(gv, productsList[position], infoListener, stockMovListener, editListener, removeListener)
    }

    override fun getFilter(): Filter = ProductFilter()
    private inner class ProductFilter : Filter() {
        @SuppressLint("DefaultLocale")
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = mutableListOf<ProductItem>()
            if (constraint == null || constraint.isEmpty())
                filteredList.addAll(productsListClone)
            else {
                val filterPattern = constraint.toString().lowercase().trim()
                for (product in productsListClone) {
                    if (product.name!!.lowercase()
                            .contains(filterPattern)
                    ) // .contains or .startsWith
                        filteredList.add(product)
                }
            }
            val filterRes = FilterResults()
            filterRes.values = filteredList
            return filterRes
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            productsList.clear()
            productsList.addAll(results?.values as List<ProductItem>)
            notifyDataSetChanged()
        }
    }
}