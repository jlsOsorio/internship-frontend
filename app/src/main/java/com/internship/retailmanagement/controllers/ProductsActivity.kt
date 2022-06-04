package com.internship.retailmanagement.controllers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.internship.retailmanagement.R
import com.internship.retailmanagement.common.ErrorDialog
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.common.Utils
import com.internship.retailmanagement.config.SessionManager
import com.internship.retailmanagement.controllers.adapters.ProductsAdapter
import com.internship.retailmanagement.databinding.ActivityProductsBinding
import com.internship.retailmanagement.dataclasses.products.ProductItem
import com.internship.retailmanagement.services.ApiService
import com.internship.retailmanagement.services.ServiceGenerator
import kotlinx.android.synthetic.main.activity_users.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductsBinding
    private lateinit var mAdapter: ProductsAdapter
    private lateinit var productsList: MutableList<ProductItem>
    private lateinit var fab: FloatingActionButton
    private lateinit var gv: GlobalVar
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityProductsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        productsList = arrayListOf()
        fab = binding.fab
        gv = application as GlobalVar
        sessionManager = SessionManager(this)

        fab.visibility = View.INVISIBLE

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

        myRecyclerView.adapter = ProductsAdapter(productsList, gv, { _, _ -> "" }, { _, _ -> "" }, { _, _, _, _, _ -> "" }, { _, _ -> "" })

        //Data update on scroll
        swipeRefreshUsers.setOnRefreshListener {
            //Show data in recycler view
            getProducts()
            myRecyclerView.adapter!!.notifyDataSetChanged()
        }

        getProducts()

        if (gv.userRole == "SUPERVISOR")
        {
            fab.visibility = View.VISIBLE
            fab.setOnClickListener{
                executeOtherActivity(CreateProductActivity::class.java)
            }
        }
    }

    //Get products from API
    @Synchronized
    private fun getProducts() {
        val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)

        val productsCall = serviceGenerator.getProducts("Bearer ${sessionManager.fetchAuthToken()}")

        productsCall.enqueue(
            object : Callback<MutableList<ProductItem>> {
                override fun onResponse(
                    call: Call<MutableList<ProductItem>>,
                    response: Response<MutableList<ProductItem>>
                ) {
                    if (response.isSuccessful) {
                        productsList.clear()
                        productsList.addAll(response.body()!!.toMutableList())
                        mAdapter = ProductsAdapter(productsList, gv, { _, id ->
                            gv.productId = id
                            executeOtherActivity(ProductDetailsActivity::class.java)
                        }, { _, id ->
                            gv.productId = id
                            executeOtherActivity(StockMovementActivity::class.java)
                        }, { _,id, name, ivaValue, grossPrice ->
                            gv.productId = id
                            gv.productName = name
                            gv.productIva = ivaValue
                            gv.productGrossPrice = grossPrice
                            executeOtherActivity(ChangeProductActivity::class.java)
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
                    else
                    {
                        if (response.code() == 401 || response.code() == 403) {
                            val errorMessage = response.errorBody()!!.string()
                            ErrorDialog.setPermissionDialog(this@ProductsActivity, errorMessage).show()
                        }
                        else if (response.code() > 403)
                        {
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            val message: String = jsonObject.getString("message")
                            ErrorDialog.setDialog(this@ProductsActivity, message).show()
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
            R.id.profileMenu ->{
                gv.isMyProfile = true
                executeOtherActivity(UserProfileActivity::class.java)
            }
            R.id.changePasswordMenu -> executeOtherActivity(ChangePasswordActivity::class.java)
            R.id.signOutMenu -> Utils.logout(this@ProductsActivity)
        }
        return true
    }

    //Go to next activity
    private fun executeOtherActivity(otherActivity: Class<*>) {
        val x = Intent(this@ProductsActivity, otherActivity)
        startActivity(x)
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