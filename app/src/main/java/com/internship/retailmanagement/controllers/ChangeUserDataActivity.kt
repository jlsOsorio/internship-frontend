package com.internship.retailmanagement.controllers

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.internship.retailmanagement.R
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.controllers.adapters.StoreSpinnerAdapter
import com.internship.retailmanagement.databinding.ActivityChangeUserDataBinding
import com.internship.retailmanagement.dataclasses.StoreItem
import com.internship.retailmanagement.dataclasses.UserItem
import com.internship.retailmanagement.services.ApiService
import com.internship.retailmanagement.services.ServiceGenerator
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.IllegalStateException
import java.text.SimpleDateFormat
import java.util.*


class ChangeUserDataActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangeUserDataBinding
    private lateinit var gv: GlobalVar
    private lateinit var email: EditText
    private lateinit var name: EditText
    private lateinit var nif: EditText
    private lateinit var address: EditText
    private lateinit var council: EditText
    private lateinit var zipCode: EditText
    private lateinit var birthDate: EditText
    private lateinit var phoneNumber: EditText
    private lateinit var category: EditText
    private lateinit var status: EditText
    private lateinit var confirm: Button
    private lateinit var stores: Spinner
    private lateinit var storesList: MutableList<StoreItem>
    private val myCalendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityChangeUserDataBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        gv = application as GlobalVar

        Toast.makeText(this,
            "You can now edit the employee with the number ${gv.userId}", Toast.LENGTH_SHORT).show()

        email = binding.emailProfile
        name = binding.nameProfile
        nif = binding.nifProfile
        address = binding.addressProfile
        council = binding.councilProfile
        zipCode = binding.zipCodeProfile
        birthDate = binding.birthDateProfile
        phoneNumber = binding.phoneNumberProfile
        category = binding.categoryProfile
        stores = binding.storeProfile
        status = binding.statusProfile
        confirm = binding.buttonConfirm
        storesList = arrayListOf()

        val date =
            OnDateSetListener { view, year, month, day ->
                myCalendar[Calendar.YEAR] = year
                myCalendar[Calendar.MONTH] = month
                myCalendar[Calendar.DAY_OF_MONTH] = day
                updateLabel()
            }

        birthDate.setOnClickListener {
            DatePickerDialog(
                this@ChangeUserDataActivity,
                date,
                myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            ).show()
        }

        getStores()
        getUser()

        confirm.setOnClickListener {
            putMethod()
        }

    }

    private fun setupCustomSpinner() {

        val adapter = StoreSpinnerAdapter(this@ChangeUserDataActivity, storesList)
        stores.adapter = adapter
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
                        email.setText(responseBody.email)
                        name.setText(responseBody.name)
                        nif.setText(responseBody.nif.toString())
                        address.setText(responseBody.address)
                        council.setText(responseBody.council)
                        zipCode.setText(responseBody.zipCode)
                        birthDate.setText(responseBody.birthDate!!.toDate("yyyy-MM-dd'T'HH:mm:ss'Z'").formatTo("dd-MM-yyyy"))
                        phoneNumber.setText(responseBody.phone)
                        category.setText(responseBody.category)
                        status.setText(responseBody.status)
                    }
                }

                override fun onFailure(call: Call<UserItem>, t: Throwable) {
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

    @Synchronized
    private fun putMethod() {
        val nameStr = name.text.toString()
        val emailStr = email.text.toString()
        val phoneStr = phoneNumber.text.toString()
        val birthDateStr = birthDate.text.toString()
        val nifLong = Integer.parseInt(nif.text.toString()).toLong()
        val categoryStr = category.text.toString()
        val statusStr = status.text.toString()
        val addressStr = address.text.toString()
        val councilStr = council.text.toString()
        val zipCodeStr = zipCode.text.toString()
        //val storeIdLong = Integer.parseInt(store.text.toString()).toLong()

        val userUpdate = UserItem(
            gv.userId,
            nameStr,
            emailStr,
            phoneStr,
            birthDateStr.toDate("dd-MM-yyyy").formatTo("yyyy-MM-dd'T'HH:mm:ss'Z'"),
            nifLong,
            categoryStr,
            statusStr,
            addressStr,
            councilStr,
            zipCodeStr,
            gv.storeId
        )
        val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)
        val userPut = serviceGenerator.updateUser(gv.userId, userUpdate)

        userPut.enqueue(object : Callback<ResponseBody?> {

            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                Toast.makeText(this@ChangeUserDataActivity, "User updated successfully!", Toast.LENGTH_SHORT).show()
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
    fun String.toDate(dateFormat: String, timeZone: TimeZone = TimeZone.getTimeZone("UTC")): Date {
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
    private fun Date.formatTo(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault()): String {
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        formatter.timeZone = timeZone
        return formatter.format(this)
    }

    private fun updateLabel() {
        val myFormat = "dd-MM-yyyy"
        val dateFormat = SimpleDateFormat(myFormat, Locale.US)
        birthDate.setText(dateFormat.format(myCalendar.time))
    }

    //Get users from API
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
                    setupCustomSpinner()
                    stores.onItemSelectedListener = object:
                        AdapterView.OnItemSelectedListener {

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            //val text = parent!!.getItemAtPosition(1).toString()
                            val item = parent!!.selectedItem as StoreItem
                            Toast.makeText(this@ChangeUserDataActivity, item.id.toString(), Toast.LENGTH_SHORT).show()
                            gv.storeId = item.id
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            TODO("Not yet implemented")
                        }

                    }
                }
            }

            override fun onFailure(call: Call<MutableList<StoreItem>>, t: Throwable) {
                t.printStackTrace()
                Log.e("ChangeUserDataActivity", "Error:" + t.message.toString())
            }
        })
    }
}