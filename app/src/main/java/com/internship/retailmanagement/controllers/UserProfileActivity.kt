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
import com.internship.retailmanagement.services.ServiceGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.IllegalStateException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.NoSuchElementException

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
    private lateinit var userItem: UserItem

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
        //store = binding.storeProfile
        status = binding.statusProfile
        storeId = binding.idStore
        storeAddress = binding.addressStore
        storeZipCode = binding.zipCodeStore
        //userItem = UserItem(null, "","","","",0,"","","","","",null)

        getUser()
    }

    //Get user from API
    @Synchronized
    private fun getUser() {
        try {
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


                }

                override fun onFailure(call: Call<UserItem>, t: Throwable) {
                    Log.e("UserProfileActivity", "Error:" + t.message.toString())
                }
            })
        }
        catch(e: IllegalStateException)
        {
            e.printStackTrace()
            e.message.toString()
        }
        catch(e: NoSuchElementException)
        {
            e.printStackTrace()
            e.message.toString()
        }
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

    /*//Get store from API
    @Synchronized
    private fun getStore() {
        try {
            val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)
            val storeCall = serviceGenerator.getStore(gv.storeId!!)

            storeCall.enqueue(object : Callback<StoreItem> {
                override fun onResponse(
                    call: Call<StoreItem>,
                    response: Response<StoreItem>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()!!

                        storeId.text = responseBody.id.toString()
                        storeAddress.text = responseBody.address
                        storeZipCode.text = responseBody.zipCode

                        /*gv.storeId = responseBody.storeId
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
                        store.text = responseBody.storeId.toString()
                        status.text = responseBody.status*/

                    }
                }

                override fun onFailure(call: Call<StoreItem>, t: Throwable) {
                    Log.e("UserProfileActivity", "Error:" + t.message.toString())
                }
            })
        }
        catch(e: IllegalStateException)
        {
            e.message.toString()
        }
        catch(e: NoSuchElementException)
        {
            e.message.toString()
        }
    }*/

}