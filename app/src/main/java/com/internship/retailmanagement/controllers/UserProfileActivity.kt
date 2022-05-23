package com.internship.retailmanagement.controllers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.internship.retailmanagement.R
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.databinding.ActivityUserProfileBinding
import com.internship.retailmanagement.dataclasses.users.UserItem
import com.internship.retailmanagement.services.ApiService
import com.internship.retailmanagement.services.ErrorDialog
import com.internship.retailmanagement.services.ServiceGenerator
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class UserProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var gv: GlobalVar
    private lateinit var email: TextView
    private lateinit var number: TextView
    private lateinit var name: TextView
    private lateinit var nif: TextView
    private lateinit var address: TextView
    private lateinit var council: TextView
    private lateinit var zipCode: TextView
    private lateinit var birthDate: TextView
    private lateinit var phoneNumber: TextView
    private lateinit var category: TextView
    private lateinit var storeId: TextView
    private lateinit var storeAddress: TextView
    private lateinit var storeZipCode: TextView
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityUserProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        gv = application as GlobalVar
        email = binding.emailProfile
        number = binding.numberProfile
        name = binding.nameProfile
        nif = binding.nifProfile
        address = binding.addressProfile
        council = binding.councilProfile
        zipCode = binding.zipCodeProfile
        birthDate = binding.birthDateProfile
        phoneNumber = binding.phoneNumberProfile
        category = binding.categoryProfile
        status = binding.statusProfile
        storeId = binding.idStore
        storeAddress = binding.addressStore
        storeZipCode = binding.zipCodeStore

        getUser()
    }

    //Get user from API
    @Synchronized
    private fun getUser() {
        val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)
        val userCall = serviceGenerator.getUser(gv.userId!!)

        userCall.enqueue(object : Callback<UserItem> {
            override fun onResponse(
                call: Call<UserItem>,
                response: Response<UserItem>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()!!
                    email.text = responseBody.email
                    number.text = responseBody.id.toString()
                    name.text = responseBody.name
                    nif.text = responseBody.nif.toString()
                    address.text = responseBody.address
                    council.text = responseBody.council
                    zipCode.text = responseBody.zipCode
                    birthDate.text = responseBody.birthDate!!.toDate().formatTo("dd-MM-yyyy")
                    phoneNumber.text = responseBody.phone
                    category.text = responseBody.category
                    storeId.text = responseBody.store!!.id.toString()
                    storeAddress.text = responseBody.store.address
                    storeZipCode.text = responseBody.store.zipCode
                    status.text = responseBody.status
                }
                else
                {
                    if (response.code() >= 400) {
                        var jsonObject = JSONObject(response.errorBody()?.string())
                        val message: String = jsonObject.getString("message")
                        ErrorDialog.setDialog(this@UserProfileActivity, message)
                        finish()
                    }
                }


            }

            override fun onFailure(call: Call<UserItem>, t: Throwable) {
                Log.e("UserProfileActivity", "Error:" + t.message.toString())
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

    /**
     * Method to parse a string that represents UTC date ("yyyy-MM-dd'T'HH:mm:ss'Z'") to Date type
     * @param dateFormat    string UTC date
     * @param timeZone      timeZone UTC
     * @return Date
     */
    fun String.toDate(dateFormat: String = "yyyy-MM-dd'T'HH:mm:ss'Z'", timeZone: TimeZone = TimeZone.getTimeZone("UTC")): Date {
        val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
        parser.timeZone = timeZone
        return parser.parse(this)!!
    }


    /**
    * Method to parse a Date with a pre-established format to a String with the intended format
    * @param dateFormat    string representing intended date format (Ex: "yyyy-MM-dd")
    * @param timeZone      default timeZone
    * @return String representing the date with intended format.
    */
    fun Date.formatTo(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault()): String {
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        formatter.timeZone = timeZone
        return formatter.format(this)
    }

}