package com.internship.retailmanagement.controllers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.R
import com.internship.retailmanagement.common.ErrorDialog
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.common.Utils
import com.internship.retailmanagement.config.SessionManager
import com.internship.retailmanagement.databinding.ActivityChangeStoreBinding
import com.internship.retailmanagement.dataclasses.stores.UpdateStoreItem
import com.internship.retailmanagement.services.ApiService
import com.internship.retailmanagement.services.ServiceGenerator
import okhttp3.ResponseBody
import org.json.JSONObject
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
    private lateinit var sessionManager: SessionManager

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
        sessionManager = SessionManager(this)

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
        val storePut = serviceGenerator.updateStore("Bearer ${sessionManager.fetchAuthToken()}", gv.storeId, storeUpdate)

        storePut.enqueue(object : Callback<ResponseBody?> {

            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if (response.isSuccessful)
                {
                    Toast.makeText(this@ChangeStoreActivity, "Store updated successfully!", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    if (response.code() == 401 || response.code() == 403) {
                        val errorMessage = response.errorBody()!!.string()
                        ErrorDialog.setPermissionDialog(this@ChangeStoreActivity, errorMessage).show()
                    }
                    else if (response.code() >= 400)
                    {
                        val jsonObject = JSONObject(response.errorBody()!!.string())
                        val message: String = jsonObject.getString("message")
                        ErrorDialog.setDialog(this@ChangeStoreActivity, message).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Log.e("ChangeStoreActivity", "Error:" + t.message.toString())
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
            com.internship.retailmanagement.R.id.profileMenu ->{
                gv.isMyProfile = true
                executeOtherActivity(UserProfileActivity::class.java)
            }
            com.internship.retailmanagement.R.id.changePasswordMenu -> executeOtherActivity(ChangePasswordActivity::class.java)
            com.internship.retailmanagement.R.id.signOutMenu -> Utils.logout(this@ChangeStoreActivity)
        }
        return true
    }

    //Go to next activity
    private fun executeOtherActivity(otherActivity: Class<*>) {
        val x = Intent(this@ChangeStoreActivity, otherActivity)
        startActivity(x)
    }
}