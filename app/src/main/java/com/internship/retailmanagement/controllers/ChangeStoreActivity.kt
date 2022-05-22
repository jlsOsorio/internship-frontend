package com.internship.retailmanagement.controllers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.R
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.databinding.ActivityChangeStoreBinding
import com.internship.retailmanagement.dataclasses.stores.UpdateStoreItem
import com.internship.retailmanagement.services.ApiService
import com.internship.retailmanagement.services.ServiceGenerator
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangeStoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangeStoreBinding
    private lateinit var gv: GlobalVar
    private lateinit var storeAddress: EditText
    private lateinit var storeCouncil: EditText
    private lateinit var storeZipCode: EditText
    private lateinit var storeContact: EditText
    private lateinit var storeNumberCR: EditText
    private lateinit var storeStatus: Spinner
    private lateinit var confirm: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityChangeStoreBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        gv = application as GlobalVar
        storeAddress = binding.addressStore
        storeCouncil = binding.councilStore
        storeZipCode = binding.zipCodeStore
        storeContact = binding.contactStore
        storeNumberCR = binding.cashRegisterStore
        storeStatus = binding.statusStore
        confirm = binding.buttonConfirm

        storeAddress.setText(gv.storeAddress)
        storeCouncil.setText(gv.storeCouncil)
        storeZipCode.setText(gv.storeZipCode)
        storeContact.setText(gv.storeContact)
        storeNumberCR.setText(gv.storeNumberCR.toString())

        val statusArr : ArrayList<String> = arrayListOf("ACTIVE", "INACTIVE")

        val statusAdapter = ArrayAdapter(this@ChangeStoreActivity, R.layout.support_simple_spinner_dropdown_item, statusArr)
        storeStatus.adapter = statusAdapter
        val statPos = statusArr.indexOfFirst {
            it == gv.storeStatus
        }
        storeStatus.setSelection(statPos)

        storeStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                gv.storeStatus = statusArr[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        confirm.setOnClickListener {
            putStore()
            finish()
        }
    }

    //Update store
    @Synchronized
    private fun putStore() {
        val addressStr = storeAddress.text.toString()
        val councilStr = storeCouncil.text.toString()
        val zipCodeStr = storeZipCode.text.toString()
        val contactStr = storeContact.text.toString()
        val numberCR = storeNumberCR.text.toString()

        val storeUpdate = UpdateStoreItem(
            addressStr,
            councilStr,
            zipCodeStr,
            contactStr,
            gv.storeStatus,
            numberCR.toInt()
        )

        val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)
        val storePut = serviceGenerator.updateStore(gv.storeId, storeUpdate)

        storePut.enqueue(object : Callback<ResponseBody?> {

            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                Toast.makeText(this@ChangeStoreActivity, "Store updated successfully!", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Log.e("ChangeUserDataActivity", "Error:" + t.message.toString())
            }
        }
        )
    }

    /**
     * Overwrite method to generate menu in action bar.
     * @param menu: menu Type.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(com.internship.retailmanagement.R.menu.menu_bar, menu)
        return true
    }

    /**
     * Overwrite method to create conditions for every options of the menu in action bar.
     * @param item MenuItem type
     * @return boolean value
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            com.internship.retailmanagement.R.id.profileMenu -> null
            com.internship.retailmanagement.R.id.changePasswordMenu -> null
            com.internship.retailmanagement.R.id.signOutMenu -> null
        }
        return true
    }
}