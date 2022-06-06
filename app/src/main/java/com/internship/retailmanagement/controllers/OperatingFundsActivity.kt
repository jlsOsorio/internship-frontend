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
import com.internship.retailmanagement.common.ErrorDialog
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.common.Utils
import com.internship.retailmanagement.config.SessionManager
import com.internship.retailmanagement.controllers.adapters.OpFundsAdapter
import com.internship.retailmanagement.databinding.ActivityOperatingFundsBinding
import com.internship.retailmanagement.dataclasses.operatingfunds.OpFundItem
import com.internship.retailmanagement.services.ApiService
import com.internship.retailmanagement.services.ServiceGenerator
import kotlinx.android.synthetic.main.activity_users.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OperatingFundsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOperatingFundsBinding
    private lateinit var opFundsList: MutableList<OpFundItem>
    private lateinit var mAdapter: OpFundsAdapter
    private lateinit var fab: FloatingActionButton
    private lateinit var gv: GlobalVar
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityOperatingFundsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        gv = application as GlobalVar

        opFundsList = arrayListOf()
        fab = binding.fab
        sessionManager = SessionManager(this)

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

        myRecyclerView.adapter = OpFundsAdapter(opFundsList, gv, { _, _, _, _, _, _ -> "" }, { _, _ -> "" })

        //Data update on scroll
        swipeRefreshUsers.setOnRefreshListener {
            //Show data in recycler view
            getMyOF()
            myRecyclerView.adapter!!.notifyDataSetChanged()
        }

        getMyOF()

        fab.setOnClickListener{
            executeOtherActivity(CreateOperatingFundActivity::class.java)
        }
    }

   /* //Get operating funds from API
    @Synchronized
    private fun getOperatingFunds() {
        val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)

        val opFundsCall = serviceGenerator.getOpFunds("Bearer ${sessionManager.fetchAuthToken()}", gv.userId!!)

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
                            gv.opFundId = id
                            gv.opFundEntryQty = entryQty
                            gv.opFundExitQty = exitQty
                            gv.opFundCashRegister = crId
                            gv.opFundMoment = moment
                            executeOtherActivity(ChangeOperatingFundActivity::class.java)
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
                    else
                    {
                        if (response.code() == 401 || response.code() == 403) {
                            val errorMessage = response.errorBody()!!.string()
                            ErrorDialog.setPermissionDialog(this@OperatingFundsActivity, errorMessage).show()
                        }
                        else if (response.code() > 403)
                        {
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            val message: String = jsonObject.getString("message")
                            ErrorDialog.setDialog(this@OperatingFundsActivity, message).show()
                        }
                    }
                }

                override fun onFailure(call: Call<MutableList<OpFundItem>>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("OperatingFundsActivity", "Error:" + t.message.toString())
                }
            })

        swipeRefreshUsers.isRefreshing = false
    }*/

    //Get user authenticated from API
    @Synchronized
    private fun getMyOF() {
        val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)
        val myOF = serviceGenerator.getMyOpFund("Bearer ${sessionManager.fetchAuthToken()}")

        myOF.enqueue(object : Callback<MutableList<OpFundItem>> {
            override fun onResponse(
                call: Call<MutableList<OpFundItem>>,
                response: Response<MutableList<OpFundItem>>
            ) {
                if (response.isSuccessful) {
                    opFundsList.clear()
                    opFundsList.addAll(response.body()!!.toMutableList())
                    mAdapter = OpFundsAdapter(opFundsList, gv, { _, id, entryQty, exitQty, crId, moment ->
                        gv.opFundId = id
                        gv.opFundEntryQty = entryQty
                        gv.opFundExitQty = exitQty
                        gv.opFundCashRegister = crId
                        gv.opFundMoment = moment
                        executeOtherActivity(ChangeOperatingFundActivity::class.java)
                    }, { _,id->
                        Utils.removeItem(this@OperatingFundsActivity, "operating fund", id, this@OperatingFundsActivity::deleteOpFund)
                    })
                    mAdapter.notifyDataSetChanged()
                    myRecyclerView.apply {
                        layoutManager = LinearLayoutManager(this@OperatingFundsActivity)
                        setHasFixedSize(true)
                        adapter = mAdapter
                    }
                }
                else
                {
                    if (response.code() == 401)
                    {
                        val errorMessage = response.errorBody()!!.string()
                        Utils.redirectUnauthorized(this@OperatingFundsActivity, errorMessage)
                    }
                    else if (response.code() == 403)
                    {
                        val errorMessage = response.errorBody()!!.string()
                        ErrorDialog.setPermissionDialog(this@OperatingFundsActivity, errorMessage).show()
                    }
                    else if (response.code() >= 400)
                    {
                        val jsonObject = JSONObject(response.errorBody()!!.string())
                        val message: String = jsonObject.getString("message")
                        ErrorDialog.setDialog(this@OperatingFundsActivity, message).show()
                    }
                }
            }

            override fun onFailure(call: Call<MutableList<OpFundItem>>, t: Throwable) {
                Log.e("OperatingFundsActivity", "Error:" + t.message.toString())
            }
        })
        swipeRefreshUsers.isRefreshing = false
    }

    //Delete operating fund
    @Synchronized
    private fun deleteOpFund(id: Long?) {
        val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)
        val deleteOF = serviceGenerator.deleteOpFund("Bearer ${sessionManager.fetchAuthToken()}", id)

        deleteOF.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    Utils.removeItemSuccessDialog(this@OperatingFundsActivity)
                }
                else
                {
                    if (response.code() == 401)
                    {
                        val errorMessage = response.errorBody()!!.string()
                        Utils.redirectUnauthorized(this@OperatingFundsActivity, errorMessage)
                    }
                    else if (response.code() == 403)
                    {
                        val errorMessage = response.errorBody()!!.string()
                        ErrorDialog.setPermissionDialog(this@OperatingFundsActivity, errorMessage).show()
                    }
                    else if (response.code() >= 400)
                    {
                        val jsonObject = JSONObject(response.errorBody()!!.string())
                        val message: String = jsonObject.getString("message")
                        ErrorDialog.setDialog(this@OperatingFundsActivity, message).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("OperatingFundsActivity", "Error:" + t.message.toString())
            }
        })
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
            R.id.signOutMenu -> Utils.logout(this@OperatingFundsActivity)
        }
        return true
    }

    //Go to next activity
    private fun executeOtherActivity(otherActivity: Class<*>) {
        val x = Intent(this@OperatingFundsActivity, otherActivity)
        startActivity(x)
    }

    /**
     * When update or create a new operating fund, this activity must "auto refresh" to show immediatly the changes. So the method "onRestart()",
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