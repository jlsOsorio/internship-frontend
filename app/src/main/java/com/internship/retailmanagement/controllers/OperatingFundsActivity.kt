package com.internship.retailmanagement.controllers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.controllers.adapters.OpFundsAdapter
import com.internship.retailmanagement.controllers.adapters.ProductsAdapter
import com.internship.retailmanagement.databinding.ActivityOperatingFundsBinding
import com.internship.retailmanagement.dataclasses.OpFundItem
import com.internship.retailmanagement.dataclasses.ProductItem
import com.internship.retailmanagement.services.ApiService
import com.internship.retailmanagement.services.ServiceGenerator
import kotlinx.android.synthetic.main.activity_users.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OperatingFundsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOperatingFundsBinding
    private lateinit var opFundsList: MutableList<OpFundItem>
    private lateinit var mAdapter: OpFundsAdapter
    private lateinit var fab: FloatingActionButton
    private lateinit var gv: GlobalVar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityOperatingFundsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        gv = application as GlobalVar

        opFundsList = arrayListOf()
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

        myRecyclerView.adapter = OpFundsAdapter(opFundsList, { _, _ -> "" }, { _, _ -> "" })

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

        val opFundsCall = serviceGenerator.getOpFunds(gv.userId!!)

        opFundsCall.enqueue(
            object : Callback<MutableList<OpFundItem>> {
                override fun onResponse(
                    call: Call<MutableList<OpFundItem>>,
                    response: Response<MutableList<OpFundItem>>
                ) {
                    if (response.isSuccessful) {
                        opFundsList.clear()
                        opFundsList.addAll(response.body()!!.toMutableList())
                        mAdapter = OpFundsAdapter(opFundsList, { _, id ->""
                            //executeOtherActivity(StockMovement::class.java, id)
                        }, { _,id->""
                            //executeOtherActivity(ChangeProductDataActivity::class.java, id)
                        })
                        mAdapter.notifyDataSetChanged()
                        myRecyclerView.apply {
                            layoutManager = LinearLayoutManager(this@OperatingFundsActivity)
                            setHasFixedSize(true)
                            adapter = mAdapter
                        }
                    }
                }

                override fun onFailure(call: Call<MutableList<OpFundItem>>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("OperatingFundsActivity", "Error:" + t.message.toString())
                }
            })

        swipeRefreshUsers.isRefreshing = false
    }

    //Save user email in global var to set it in the update page
    private fun executeOtherActivity(otherActivity: Class<*>) {
        val x = Intent(this@OperatingFundsActivity, otherActivity)
        startActivity(x)
    }
}