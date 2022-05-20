package com.internship.retailmanagement.controllers

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.R
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.databinding.ActivityCreateStoreBinding
import com.internship.retailmanagement.dataclasses.stockmovements.InsertStockMovItem
import com.internship.retailmanagement.dataclasses.stores.UpdateStoreItem
import com.internship.retailmanagement.services.ApiService
import com.internship.retailmanagement.services.ServiceGenerator
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateStoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateStoreBinding
    private lateinit var gv: GlobalVar
    private lateinit var storeAddress: EditText
    private lateinit var storeCouncil: EditText
    private lateinit var storeZipCode: EditText
    private lateinit var storeContact: EditText
    private lateinit var storeNumberCR: EditText
    private lateinit var storeStatus: Spinner
    private lateinit var create: Button

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityCreateStoreBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        gv = application as GlobalVar
        storeAddress = binding.addressStore
        storeCouncil = binding.councilStore
        storeZipCode = binding.zipCodeStore
        storeContact = binding.contactStore
        storeNumberCR = binding.cashRegisterStore
        storeStatus = binding.statusStore
        create = binding.buttonCreate

        val defaultValue : java.util.ArrayList<String> = arrayListOf("SELECT STORE STATUS")
        val statusArr : ArrayList<String> = arrayListOf("ACTIVE", "INACTIVE")

        val defaultAdapter = ArrayAdapter(this@CreateStoreActivity, R.layout.support_simple_spinner_dropdown_item, defaultValue)
        storeStatus.adapter = defaultAdapter

        storeStatus.setOnTouchListener { _, _ ->
            val statusAdapter = ArrayAdapter(this@CreateStoreActivity, R.layout.support_simple_spinner_dropdown_item, statusArr)
            storeStatus.adapter = statusAdapter
            false
        }

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

        create.setOnClickListener {
            createStore()
            finish()
        }
    }

    //Create store
    @Synchronized
    private fun createStore() {
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
        val storeCreate = serviceGenerator.addStore(storeUpdate)
        storeCreate.enqueue(object : Callback<ResponseBody?> {

            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                Toast.makeText(this@CreateStoreActivity, "Store created successfully!", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Log.e("CreateStoreActivity", "Error:" + t.message.toString())
            }
        }
        )
    }
}