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
import com.internship.retailmanagement.controllers.adapters.OpFundsAdapter
import com.internship.retailmanagement.databinding.ActivityOperatingFundsBinding
import com.internship.retailmanagement.dataclasses.operatingfunds.OpFundItem
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

        myRecyclerView.adapter = OpFundsAdapter(opFundsList, { _, _, _, _, _, _ -> "" }, { _, _ -> "" })

        //Data update on scroll
        swipeRefreshUsers.setOnRefreshListener {
            //Show data in recycler view
            getOperatingFunds()
            myRecyclerView.adapter!!.notifyDataSetChanged()
        }

        getOperatingFunds()

        fab.setOnClickListener{
            executeOtherActivity(CreateOperatingFundActivity::class.java, 0, 0.0, 0.0, 0, "")
        }
    }

    //Get operating funds from API
    @Synchronized
    private fun getOperatingFunds() {
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
                        mAdapter = OpFundsAdapter(opFundsList, { _, id, entryQty, exitQty, crId, moment ->
                            executeOtherActivity(ChangeOperatingFundActivity::class.java, id, entryQty, exitQty, crId, moment)
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

    private fun executeOtherActivity(otherActivity: Class<*>, id: Long, entryQty: Double, exitQty: Double, cashRegId: Long, moment: String) {
        gv.opFundId = id
        gv.opFundEntryQty = entryQty
        gv.opFundExitQty = exitQty
        gv.opFundCashRegister = cashRegId
        gv.opFundMoment = moment
        val x = Intent(this@OperatingFundsActivity, otherActivity)
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
     * When update or create a new operating fund, this activity must "auto refresh" to show immediatly the changes. So the method "onRestart()",
     * which is a method that is called an activity is finished and the app goes back to the previous activity, was rewritten this way.
     */
    override fun onRestart() {
        super.onRestart();
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
        overridePendingTransition(0, 0);

    }
}