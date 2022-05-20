package com.internship.retailmanagement.controllers

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
import com.internship.retailmanagement.controllers.adapters.InvProdAdapter
import com.internship.retailmanagement.controllers.adapters.ProductsAdapter
import com.internship.retailmanagement.databinding.ActivityInvoicedProductsBinding
import com.internship.retailmanagement.dataclasses.InvProdItem
import com.internship.retailmanagement.dataclasses.products.ProductItem
import com.internship.retailmanagement.services.ApiService
import com.internship.retailmanagement.services.ServiceGenerator
import kotlinx.android.synthetic.main.activity_users.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InvoicedProductsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInvoicedProductsBinding
    private lateinit var mAdapter: InvProdAdapter
    private lateinit var productsList: MutableList<InvProdItem>
    private lateinit var gv: GlobalVar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityInvoicedProductsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        productsList = arrayListOf()
        gv = application as GlobalVar

        myRecyclerView.adapter = InvProdAdapter(gv.invProdsList!!)

        //Data update on scroll
        swipeRefreshUsers.setOnRefreshListener {
            //Show data in recycler view
            myRecyclerView.adapter!!.notifyDataSetChanged()
            swipeRefreshUsers.isRefreshing = false
        }
    }

    //Get products from API
    @Synchronized
    private fun getInvProds() {
        val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)

        val invProdsCall = serviceGenerator.getInvoicedProducts()

        invProdsCall.enqueue(
            object : Callback<MutableList<InvProdItem>> {
                override fun onResponse(
                    call: Call<MutableList<InvProdItem>>,
                    response: Response<MutableList<InvProdItem>>
                ) {
                    if (response.isSuccessful) {
                        productsList.clear()
                        productsList.addAll(response.body()!!.toMutableList())
                        mAdapter = InvProdAdapter(productsList)
                        mAdapter.notifyDataSetChanged()
                        myRecyclerView.apply {
                            layoutManager = LinearLayoutManager(this@InvoicedProductsActivity)
                            setHasFixedSize(true)
                            adapter = mAdapter
                        }
                    }
                }

                override fun onFailure(call: Call<MutableList<InvProdItem>>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("InvProdsActivity", "Error:" + t.message.toString())
                }
            })

        swipeRefreshUsers.isRefreshing = false
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
}