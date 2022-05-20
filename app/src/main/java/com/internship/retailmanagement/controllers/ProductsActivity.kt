package com.internship.retailmanagement.controllers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.internship.retailmanagement.R
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.controllers.adapters.ProductsAdapter
import com.internship.retailmanagement.databinding.ActivityProductsBinding
import com.internship.retailmanagement.dataclasses.products.ProductItem
import com.internship.retailmanagement.services.ApiService
import com.internship.retailmanagement.services.ServiceGenerator
import kotlinx.android.synthetic.main.activity_users.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductsBinding
    private lateinit var mAdapter: ProductsAdapter
    private lateinit var productsList: MutableList<ProductItem>
    private lateinit var fab: FloatingActionButton
    private lateinit var gv: GlobalVar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityProductsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        productsList = arrayListOf()
        fab = binding.fab
        gv = application as GlobalVar

        /**
         * Hide floating action button while scrolling down. Make it appear when scrolling up.
         */
        myRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy < 0 && !fab.isShown) {
                    fab.show()
                } else if (dy > 0 && fab.isShown) {
                    fab.hide()
                }
            }
        })

        myRecyclerView.adapter = ProductsAdapter(productsList, { _, _ -> "" }, { _, _ -> "" }, { _, _, _, _, _ -> "" }, { _, _ -> "" })

        //Data update on scroll
        swipeRefreshUsers.setOnRefreshListener {
            //Show data in recycler view
            getProducts()
            myRecyclerView.adapter!!.notifyDataSetChanged()
        }

        getProducts()

        fab.setOnClickListener{
            executeOtherActivity(CreateProductActivity::class.java, 0, "", -1, 0.0)
        }
    }

    //Get products from API
    @Synchronized
    private fun getProducts() {
        val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)

        val productsCall = serviceGenerator.getProducts()

        productsCall.enqueue(
            object : Callback<MutableList<ProductItem>> {
                override fun onResponse(
                    call: Call<MutableList<ProductItem>>,
                    response: Response<MutableList<ProductItem>>
                ) {
                    if (response.isSuccessful) {
                        productsList.clear()
                        productsList.addAll(response.body()!!.toMutableList())
                        mAdapter = ProductsAdapter(productsList, { _, id ->
                            executeOtherActivity(ProductDetailsActivity::class.java, id, "", 0, 0.0)
                        }, { _, id ->
                            executeOtherActivity(StockMovementActivity::class.java, id, "", 0, 0.0)
                        }, { _,id, name, ivaValue, grossPrice->
                            executeOtherActivity(ChangeProductActivity::class.java, id, name, ivaValue, grossPrice)
                        }, { _,id ->
                            "" //deleteProduct)
                        })
                        mAdapter.notifyDataSetChanged()
                        myRecyclerView.apply {
                            layoutManager = LinearLayoutManager(this@ProductsActivity)
                            setHasFixedSize(true)
                            adapter = mAdapter
                        }
                    }
                }

                override fun onFailure(call: Call<MutableList<ProductItem>>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("ProductsActivity", "Error:" + t.message.toString())
                }
            })

        swipeRefreshUsers.isRefreshing = false
    }

    //Save user email in global var to set it in the update page
    private fun executeOtherActivity(otherActivity: Class<*>,
                                     id: Long, name: String, ivaValue: Int, grossPrice: Double ) {
        gv.productId = id
        gv.productName = name
        gv.productIva = ivaValue
        gv.productGrossPrice = grossPrice
        val x = Intent(this@ProductsActivity, otherActivity)
        startActivity(x)
    }


    /**
     * Overwrite method to generate menu in action bar.
     * @param menu: menu Type.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_bar, menu)
        return true
    }

    /**
     * Overwrite method to create conditions for every options of the menu in action bar.
     * @param item MenuItem type
     * @return boolean value
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.profileMenu -> null
            R.id.changePasswordMenu -> null
            R.id.signOutMenu -> null
        }
        return true
    }

    /**
     * When update or create a new product, this activity must "auto refresh" to show immediatly the changes. So the method "onRestart()",
     * which is a method that is called an activity is finished and the app goes back to the previous activity, was rewritten this way.
     */
    override fun onRestart() {
        super.onRestart()
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
        overridePendingTransition(0, 0)

    }
}