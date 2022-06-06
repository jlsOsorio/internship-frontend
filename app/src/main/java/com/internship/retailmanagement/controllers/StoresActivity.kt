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
import com.internship.retailmanagement.controllers.adapters.StoresAdapter
import com.internship.retailmanagement.databinding.ActivityStoresBinding
import com.internship.retailmanagement.dataclasses.stores.StoreItem
import com.internship.retailmanagement.services.ApiService
import com.internship.retailmanagement.services.ServiceGenerator
import kotlinx.android.synthetic.main.activity_users.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoresActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoresBinding
    private lateinit var storesList: MutableList<StoreItem>
    private lateinit var mAdapter: StoresAdapter
    private lateinit var gv: GlobalVar
    private lateinit var fab: FloatingActionButton
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityStoresBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        gv = application as GlobalVar

        storesList = arrayListOf()
        fab = binding.fab
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

        myRecyclerView.adapter = StoresAdapter(storesList, gv, {_,_ -> ""}, { _, _, _, _, _, _, _, _ -> "" }, { _, _ -> "" })

        //Data update on scroll
        swipeRefreshUsers.setOnRefreshListener {
            //Show data in recycler view
            getStores()
            myRecyclerView.adapter!!.notifyDataSetChanged()
        }

        getStores()

        if (gv.userRole == "SUPERVISOR")
        {
            fab.visibility = View.VISIBLE
            fab.setOnClickListener{
                executeOtherActivity(CreateStoreActivity::class.java)
            }
        }
    }

    //Get stores from API
    @Synchronized
    private fun getStores() {
        val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)

        val storesCall = serviceGenerator.getStores("Bearer ${sessionManager.fetchAuthToken()}")

        storesCall.enqueue(
            object : Callback<MutableList<StoreItem>> {
                override fun onResponse(
                    call: Call<MutableList<StoreItem>>,
                    response: Response<MutableList<StoreItem>>
                ) {
                    if (response.isSuccessful) {
                        storesList.clear()
                        storesList.addAll(response.body()!!.toMutableList())
                        mAdapter = StoresAdapter(storesList, gv, {_, id ->
                            gv.storeId = id
                            executeOtherActivity(StoreDetailsActivity::class.java)
                        }, {
                            _,
                            id, storeAddress,
                            storeCouncil,
                            storeZipCode,
                            storeContact,
                            storeStatus,
                            cashRegisters ->
                            gv.storeId = id
                            gv.storeAddress = storeAddress
                            gv.storeCouncil = storeCouncil
                            gv.storeZipCode = storeZipCode
                            gv.storeContact = storeContact
                            gv.storeStatus = storeStatus
                            gv.storeNumberCR = cashRegisters
                            executeOtherActivity(ChangeStoreActivity::class.java)
                        }, { _, id ->
                            Utils.removeItem(this@StoresActivity, "store", id, this@StoresActivity::deleteStore)
                        })
                        mAdapter.notifyDataSetChanged()
                        myRecyclerView.apply {
                            layoutManager = LinearLayoutManager(this@StoresActivity)
                            setHasFixedSize(true)
                            adapter = mAdapter
                        }
                    }
                    else
                    {
                        if (response.code() == 401)
                        {
                            val errorMessage = response.errorBody()!!.string()
                            Utils.redirectUnauthorized(this@StoresActivity, errorMessage)
                        }
                        else if (response.code() == 403)
                        {
                            val errorMessage = response.errorBody()!!.string()
                            ErrorDialog.setPermissionDialog(this@StoresActivity, errorMessage).show()
                        }
                        else if (response.code() >= 400)
                        {
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            val message: String = jsonObject.getString("message")
                            ErrorDialog.setDialog(this@StoresActivity, message).show()
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

    //Delete store
    @Synchronized
    private fun deleteStore(id: Long?) {
        val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)
        val deleteStore = serviceGenerator.deleteStore("Bearer ${sessionManager.fetchAuthToken()}", id)

        deleteStore.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(
                call: Call<ResponseBody>,
                response: Response<ResponseBody>
            ) {
                if (response.isSuccessful) {
                    Utils.removeItemSuccessDialog(this@StoresActivity)
                }
                else
                {
                    if (response.code() == 401)
                    {
                        val errorMessage = response.errorBody()!!.string()
                        Utils.redirectUnauthorized(this@StoresActivity, errorMessage)
                    }
                    else if (response.code() == 403)
                    {
                        val errorMessage = response.errorBody()!!.string()
                        ErrorDialog.setPermissionDialog(this@StoresActivity, errorMessage).show()
                    }
                    else if (response.code() >= 400)
                    {
                        val jsonObject = JSONObject(response.errorBody()!!.string())
                        val message: String = jsonObject.getString("message")
                        ErrorDialog.setDialog(this@StoresActivity, message).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("StoresActivity", "Error:" + t.message.toString())
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
            R.id.signOutMenu -> Utils.logout(this@StoresActivity)
        }
        return true
    }

    //Go to next activity
    private fun executeOtherActivity(otherActivity: Class<*>) {
        val x = Intent(this@StoresActivity, otherActivity)
        startActivity(x)
    }

    /**
     * When update or create a new store, this activity must "auto refresh" to show immediatly the changes. So the method "onRestart()",
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