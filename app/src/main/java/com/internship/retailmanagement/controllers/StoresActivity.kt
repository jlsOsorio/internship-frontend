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
import com.internship.retailmanagement.controllers.adapters.StoresAdapter
import com.internship.retailmanagement.databinding.ActivityStoresBinding
import com.internship.retailmanagement.dataclasses.CashRegisterItem
import com.internship.retailmanagement.dataclasses.stores.StoreItem
import com.internship.retailmanagement.services.ApiService
import com.internship.retailmanagement.services.ServiceGenerator
import kotlinx.android.synthetic.main.activity_users.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoresActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoresBinding
    private lateinit var storesList: MutableList<StoreItem>
    private lateinit var mAdapter: StoresAdapter
    private lateinit var gv: GlobalVar
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityStoresBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        gv = application as GlobalVar

        storesList = arrayListOf()
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

        myRecyclerView.adapter = StoresAdapter(storesList, { _, _, _, _, _, _, _, _ -> "" }, { _, _ -> "" })

        //Data update on scroll
        swipeRefreshUsers.setOnRefreshListener {
            //Show data in recycler view
            getStores()
            myRecyclerView.adapter!!.notifyDataSetChanged()
        }

        getStores()

        fab.setOnClickListener{
            executeOtherActivity(CreateStoreActivity::class.java,0,"","", "", "", "", 0)
        }
    }

    //Get stores from API
    @Synchronized
    private fun getStores() {
        val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)

        val storesCall = serviceGenerator.getStores()

        storesCall.enqueue(
            object : Callback<MutableList<StoreItem>> {
                override fun onResponse(
                    call: Call<MutableList<StoreItem>>,
                    response: Response<MutableList<StoreItem>>
                ) {
                    if (response.isSuccessful) {
                        storesList.clear()
                        storesList.addAll(response.body()!!.toMutableList())
                        mAdapter = StoresAdapter(storesList, {
                            _,
                            id, storeAddress,
                            storeCouncil,
                            storeZipCode,
                            storeContact,
                            storeStatus,
                            cashRegisters ->
                            executeOtherActivity(ChangeStoreActivity::class.java, id, storeAddress, storeCouncil, storeZipCode, storeContact, storeStatus, cashRegisters)
                        }, { _, id ->""
                            //executeOtherActivity(UserProfileActivity::class.java, id)
                        })
                        mAdapter.notifyDataSetChanged()
                        myRecyclerView.apply {
                            layoutManager = LinearLayoutManager(this@StoresActivity)
                            setHasFixedSize(true)
                            adapter = mAdapter
                        }
                    }
                }

                override fun onFailure(call: Call<MutableList<StoreItem>>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("StoresActivity", "Error:" + t.message.toString())
                }
            })
        swipeRefreshUsers.isRefreshing = false
    }

   /* //Get cash registers from API
    @Synchronized
    private fun getCashRegisters() {
        val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)

        val crCall = serviceGenerator.getCashRegisters()

        crCall.enqueue(
            object : Callback<MutableList<CashRegisterItem>> {
                override fun onResponse(
                    call: Call<MutableList<CashRegisterItem>>,
                    response: Response<MutableList<CashRegisterItem>>
                ) {
                    if (response.isSuccessful) {
                        crList.clear()
                        crList.addAll(response.body()!!.toMutableList())
                        gv.storeNumberCR = crList.size

                        }
                    }

                override fun onFailure(call: Call<MutableList<CashRegisterItem>>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("ChangeOpFundActivity", "Error:" + t.message.toString())
                }
            })
    }*/

    //Save user email in global var to set it in the update page
    private fun executeOtherActivity(otherActivity: Class<*>,
                                     id: Long,
                                     storeAddress: String,
                                     storeCouncil: String,
                                     storeZipCode: String,
                                     storeContact: String,
                                     storeStatus: String,
                                     cashRegisters: Int)
    {
        gv.storeId = id
        gv.storeAddress = storeAddress
        gv.storeCouncil = storeCouncil
        gv.storeZipCode = storeZipCode
        gv.storeContact = storeContact
        gv.storeStatus = storeStatus
        gv.storeNumberCR = cashRegisters
        val x = Intent(this@StoresActivity, otherActivity)
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
            R.id.profileMenu -> executeOtherActivity(UserProfileActivity::class.java, gv.userId!!, gv.storeAddress!!, gv.storeCouncil!!, gv.storeZipCode!!, gv.storeContact!!, gv.storeStatus!!, gv.storeNumberCR!!)
            R.id.changePasswordMenu -> executeOtherActivity(ChangePasswordActivity::class.java, gv.userId!!, gv.storeAddress!!, gv.storeCouncil!!, gv.storeZipCode!!, gv.storeContact!!, gv.storeStatus!!, gv.storeNumberCR!!)
            R.id.signOutMenu -> null
        }
        return true
    }
}