package com.internship.retailmanagement.controllers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.controllers.adapters.ProductsAdapter
import com.internship.retailmanagement.controllers.adapters.UsersAdapter
import com.internship.retailmanagement.databinding.ActivityProductsBinding
import com.internship.retailmanagement.dataclasses.ProductItem
import com.internship.retailmanagement.dataclasses.StockMovItem
import com.internship.retailmanagement.dataclasses.UserItem
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

        myRecyclerView.adapter = ProductsAdapter(productsList, { _, _ -> "" }, { _, _ -> "" }, { _, _ -> "" })

        //Data update on scroll
        swipeRefreshUsers.setOnRefreshListener {
            //Show data in recycler view
            getMyData()
            myRecyclerView.adapter!!.notifyDataSetChanged()
        }

        getMyData()
    }

    //Get users from API
    @Synchronized
    private fun getMyData() {
        val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)

        val usersCall = serviceGenerator.getProducts()

        usersCall.enqueue(
            object : Callback<MutableList<ProductItem>> {
                override fun onResponse(
                    call: Call<MutableList<ProductItem>>,
                    response: Response<MutableList<ProductItem>>
                ) {
                    if (response.isSuccessful) {
                        productsList.clear()
                        productsList.addAll(response.body()!!.toMutableList())
                        mAdapter = ProductsAdapter(productsList, { _, id ->""
                            //executeOtherActivity(StockMovement::class.java, id)
                        }, { _,id->""
                            //executeOtherActivity(ChangeProductDataActivity::class.java, id)
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
                                     id: Long) {
        gv.productId = id
        val x = Intent(this@ProductsActivity, otherActivity)
        startActivity(x)
    }
}