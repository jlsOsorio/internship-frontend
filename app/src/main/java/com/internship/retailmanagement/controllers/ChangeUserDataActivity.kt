package com.internship.retailmanagement.controllers

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.internship.retailmanagement.R
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.controllers.adapters.spinners.StoreSpinnerAdapter
import com.internship.retailmanagement.databinding.ActivityChangeUserDataBinding
import com.internship.retailmanagement.dataclasses.stores.StoreItem
import com.internship.retailmanagement.dataclasses.users.UserItem
import com.internship.retailmanagement.services.ApiService
import com.internship.retailmanagement.common.ErrorDialog
import com.internship.retailmanagement.common.Utils
import com.internship.retailmanagement.config.SessionManager
import com.internship.retailmanagement.dataclasses.users.UpdateUserItem
import com.internship.retailmanagement.services.ServiceGenerator
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.NumberFormatException
import java.text.ParseException
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
    private lateinit var category: Spinner
    private lateinit var status: Spinner
    private lateinit var confirm: Button
    private lateinit var stores: Spinner
    private lateinit var storesList: MutableList<StoreItem>
    private lateinit var sessionManager: SessionManager
    private val myCalendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityChangeUserDataBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        gv = application as GlobalVar

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
        sessionManager = SessionManager(this)

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

        val categories : ArrayList<String> = arrayListOf("SUPERVISOR", "EMPLOYEE")
        val statusArr : ArrayList<String> = arrayListOf("ACTIVE", "INACTIVE")

        val categoriesAdapter = ArrayAdapter(this@ChangeUserDataActivity, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, categories)
        category.adapter = categoriesAdapter
        val catPos = categories.indexOfFirst {
            it == gv.userCategory
        }
        category.setSelection(catPos)

        val statusAdapter = ArrayAdapter(this@ChangeUserDataActivity, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, statusArr)
        status.adapter = statusAdapter
        val statPos = statusArr.indexOfFirst {
            it == gv.userStatus
        }
        status.setSelection(statPos)

        category.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                gv.userCategory = categories[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        status.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                gv.userStatus = statusArr[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        confirm.setOnClickListener {
            putUser()
        }

    }

    private fun setupCustomSpinner() {
        val storePos = storesList.indexOfFirst {
            it.id == gv.storeId
        }

        val adapter = StoreSpinnerAdapter(this@ChangeUserDataActivity, storesList)
        stores.adapter = adapter

        stores.setSelection(storePos)
    }

    //Get user from API
    @Synchronized
    private fun getUser() {
        try {
            val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)
            val userCall = serviceGenerator.getUser("Bearer ${sessionManager.fetchAuthToken()}", gv.userId!!)

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
                        gv.userCategory = responseBody.category
                        gv.userStatus = responseBody.status
                    }
                    else
                    {
                        if (response.code() == 401 || response.code() == 403) {
                            val errorMessage = response.errorBody()!!.string()
                            ErrorDialog.setPermissionDialog(this@ChangeUserDataActivity, errorMessage).show()
                        }
                        else if (response.code() >= 400)
                        {
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            val message: String = jsonObject.getString("message")
                            ErrorDialog.setDialog(this@ChangeUserDataActivity, message).show()
                        }
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

    //Update user
    @Synchronized
    private fun putUser() {
        try {
            val nameStr = name.text.toString()
            val emailStr = email.text.toString()
            val phoneStr = phoneNumber.text.toString()
            val birthDateStr = birthDate.text.toString()
            val nifLong = Integer.parseInt(nif.text.toString()).toLong()
            val addressStr = address.text.toString()
            val councilStr = council.text.toString()
            val zipCodeStr = zipCode.text.toString()

            val userUpdate = UpdateUserItem(
                gv.userId,
                nameStr,
                emailStr,
                phoneStr,
                birthDateStr.toDate("dd-MM-yyyy").formatTo("yyyy-MM-dd'T'HH:mm:ss'Z'"),
                nifLong,
                gv.userCategory,
                gv.userStatus,
                addressStr,
                councilStr,
                zipCodeStr,
                gv.storeId
            )
            val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)
            val userPut = serviceGenerator.updateUser("Bearer ${sessionManager.fetchAuthToken()}", gv.userId, userUpdate)

            userPut.enqueue(object : Callback<ResponseBody?> {

                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@ChangeUserDataActivity,
                            "User updated successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        if (response.code() == 401 || response.code() == 403) {
                            val errorMessage = response.errorBody()!!.string()
                            ErrorDialog.setPermissionDialog(this@ChangeUserDataActivity, errorMessage)
                        }
                        else if (response.code() >= 400)
                        {
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            val message: String = jsonObject.getString("message")
                            ErrorDialog.setDialog(this@ChangeUserDataActivity, message)
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    Log.e("ChangeUserDataActivity", "Error:" + t.message.toString())
                }
            }
            )
        }
        catch(e: NumberFormatException)
        {
            ErrorDialog.setDialog(this@ChangeUserDataActivity, "Invalid Input").show()
        }
        catch(e: ParseException)
        {
            ErrorDialog.setDialog(this@ChangeUserDataActivity, "Please insert the date of the operation.").show()
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
                        setupCustomSpinner()
                        stores.onItemSelectedListener = object:
                            AdapterView.OnItemSelectedListener {

                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                val item = parent!!.selectedItem as StoreItem
                                gv.storeId = item.id
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                TODO("Not yet implemented")
                            }

                        }
                    }
                    else
                    {
                        if (response.code() == 401 || response.code() == 403) {
                            val errorMessage = response.errorBody()!!.string()
                            ErrorDialog.setPermissionDialog(this@ChangeUserDataActivity, errorMessage)
                        }
                        else if (response.code() >= 400)
                        {
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            val message: String = jsonObject.getString("message")
                            ErrorDialog.setDialog(this@ChangeUserDataActivity, message)
                        }
                    }
                }

                override fun onFailure(call: Call<MutableList<StoreItem>>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("ChangeUserDataActivity", "Error:" + t.message.toString())
                }
            })
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
            R.id.signOutMenu -> Utils.logout(this@ChangeUserDataActivity)
        }
        return true
    }

    //Go to next activity
    private fun executeOtherActivity(otherActivity: Class<*>) {
        val x = Intent(this@ChangeUserDataActivity, otherActivity)
        startActivity(x)
    }

}