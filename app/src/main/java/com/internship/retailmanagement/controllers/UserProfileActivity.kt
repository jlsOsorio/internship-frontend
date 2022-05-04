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
import com.internship.retailmanagement.dataclasses.UserItem
import com.internship.retailmanagement.services.ApiService
import com.internship.retailmanagement.services.ServiceGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.IllegalStateException

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
    private lateinit var store: TextView
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
        store = binding.storeProfile
        status = binding.statusProfile

        getUser()
    }

    //Get user from API
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
                        gv.storeId = responseBody.store!!.id

                        email.text = responseBody.email
                        number.text = responseBody.id.toString()
                        name.text = responseBody.name
                        nif.text = responseBody.nif.toString()
                        address.text = responseBody.address
                        council.text = responseBody.council
                        zipCode.text = responseBody.zipCode
                        birthDate.text = responseBody.birthDate
                        phoneNumber.text = responseBody.phone
                        category.text = responseBody.category
                        store.text = responseBody.store.id.toString()
                        status.text = responseBody.status

                    }
                }

                override fun onFailure(call: Call<UserItem>, t: Throwable) {
                    t.printStackTrace()
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