package com.internship.retailmanagement.controllers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.internship.retailmanagement.R
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.databinding.ActivityStoreDetailsBinding
import com.internship.retailmanagement.dataclasses.stores.StoreItem
import com.internship.retailmanagement.services.ApiService
import com.internship.retailmanagement.services.ServiceGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoreDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoreDetailsBinding
    private lateinit var gv: GlobalVar
    private lateinit var number: TextView
    private lateinit var address: TextView
    private lateinit var council: TextView
    private lateinit var zipCode: TextView
    private lateinit var contact: TextView
    private lateinit var cashRegisters: TextView
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityStoreDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        gv = application as GlobalVar
        number = binding.numberStore
        address = binding.addressStore
        council = binding.councilStore
        zipCode = binding.zipCodeStore
        contact = binding.contactStore
        cashRegisters = binding.cashRegisterStore
        status = binding.statusStore

        getStore()
    }

    //Get store from API
    @Synchronized
    private fun getStore() {
        val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)
        val storeCall = serviceGenerator.getStore(gv.storeId!!)

        storeCall.enqueue(object : Callback<StoreItem> {
            override fun onResponse(
                call: Call<StoreItem>,
                response: Response<StoreItem>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()!!
                    number.text = responseBody.id.toString()
                    address.text = responseBody.address
                    council.text = responseBody.council
                    zipCode.text = responseBody.zipCode
                    contact.text = responseBody.contact
                    zipCode.text = responseBody.zipCode
                    cashRegisters.text = responseBody.cashRegisters!!.size.toString()
                    status.text = responseBody.status
                }
            }

            override fun onFailure(call: Call<StoreItem>, t: Throwable) {
                Log.e("StoreDetailsActivity", "Error:" + t.message.toString())
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
            R.id.profileMenu -> null
            R.id.changePasswordMenu -> null
            R.id.signOutMenu -> null
        }
        return true
    }
}