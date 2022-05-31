package com.internship.retailmanagement.controllers.register

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import com.internship.retailmanagement.R
import com.internship.retailmanagement.common.ErrorDialog
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.common.Utils
import com.internship.retailmanagement.config.SessionManager
import com.internship.retailmanagement.controllers.ChangePasswordActivity
import com.internship.retailmanagement.controllers.MainActivity
import com.internship.retailmanagement.controllers.SignInActivity
import com.internship.retailmanagement.controllers.UserProfileActivity
import com.internship.retailmanagement.controllers.adapters.spinners.StoreSpinnerAdapter
import com.internship.retailmanagement.databinding.ActivityFinalRegisterBinding
import com.internship.retailmanagement.dataclasses.stores.StoreItem
import com.internship.retailmanagement.dataclasses.users.InsertUserItem
import com.internship.retailmanagement.services.ApiService
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
import kotlin.collections.ArrayList

class FinalRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFinalRegisterBinding
    private lateinit var gv: GlobalVar
    private lateinit var nif: EditText
    private lateinit var address: EditText
    private lateinit var council: EditText
    private lateinit var zipCode: EditText
    private lateinit var birthDate: EditText
    private lateinit var phoneNumber: EditText
    private lateinit var category: Spinner
    private lateinit var store: Spinner
    private lateinit var create: AppCompatButton
    private lateinit var storeItem: StoreItem
    private lateinit var storesList: MutableList<StoreItem>
    private lateinit var registerData: ArrayList<String>
    private lateinit var sessionManager: SessionManager
    private val myCalendar = Calendar.getInstance()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityFinalRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        gv = application as GlobalVar
        gv.userCategory = ""
        gv.storeId = null
        nif = binding.nifProfile
        address = binding.addressProfile
        council = binding.councilProfile
        zipCode = binding.zipCodeProfile
        birthDate = binding.birthDateProfile
        phoneNumber = binding.phoneNumberProfile
        category = binding.userCategory
        store = binding.userStore
        storesList = arrayListOf()
        create = binding.buttonCreate
        storeItem = StoreItem(0, "", "", "", "", "", null)
        registerData = arrayListOf()
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
                this@FinalRegisterActivity,
                date,
                myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH]
            ).show()
        }

        val defaultCategoryValue: ArrayList<String> = arrayListOf("SELECT CATEGORY")
        val categories : ArrayList<String> = arrayListOf("SUPERVISOR", "EMPLOYEE")

        registerData = intent.extras?.getStringArrayList("regDataKey")!!

        val defaultCategoryAdapter = ArrayAdapter(
            this@FinalRegisterActivity,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            defaultCategoryValue
        )
        category.adapter = defaultCategoryAdapter

        category.setOnTouchListener { _, _ ->
            val catAdapter = ArrayAdapter(
                this@FinalRegisterActivity,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                categories
            )
            category.adapter = catAdapter
            false
        }

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

        val defaultStoreValue: ArrayList<String> = arrayListOf("SELECT STORE")

        val defaultStoreAdapter = ArrayAdapter(
            this@FinalRegisterActivity,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            defaultStoreValue
        )
        store.adapter = defaultStoreAdapter

        store.setOnTouchListener { _, _ ->
            getStores()
            false
        }

        create.setOnClickListener{
            if (nif.text.isEmpty())
            {
                nif.error = "NIF can not be empty"
                return@setOnClickListener
            }

            if (address.text.isEmpty())
            {
                address.error = "Address can not be empty."
                return@setOnClickListener
            }

            if (council.text.isEmpty())
            {
                council.error = "Council can not be empty."
                return@setOnClickListener
            }

            if (zipCode.text.isEmpty())
            {
                zipCode.error = "Zip code can not be empty."
                return@setOnClickListener
            }

            if (phoneNumber.text.isEmpty())
            {
                phoneNumber.error = "Phone number can not be empty."
                return@setOnClickListener
            }

            if (gv.userCategory != "EMPLOYEE" && gv.userCategory != "SUPERVISOR" || gv.storeId == null)
            {
                ErrorDialog.setDialog(this@FinalRegisterActivity, "Invalid data. Please verify if all fields are correctly filled.").show()
                return@setOnClickListener
            }

            insertUser()
            val intent = Intent(this@FinalRegisterActivity, MainActivity::class.java)
            //Finish all activities after insert user
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }

    }

    private fun setupCustomSpinner() {
        val adapter = StoreSpinnerAdapter(this@FinalRegisterActivity, storesList)
        store.adapter = adapter
    }

    /**
     * Method to parse a string that represents UTC date ("yyyy-MM-dd'T'HH:mm:ss'Z'") to Date type
     * @param dateFormat    string UTC date
     * @param timeZone      timeZone UTC
     * @return Date
     */
    private fun String.toDate(dateFormat: String, timeZone: TimeZone = TimeZone.getTimeZone("UTC")): Date {
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
            R.id.signOutMenu -> Utils.logout(this@FinalRegisterActivity)
        }
        return true
    }

    //Go to next activity
    private fun executeOtherActivity(otherActivity: Class<*>) {
        val x = Intent(this@FinalRegisterActivity, otherActivity)
        startActivity(x)
    }

    //Get store from API
    @Synchronized
    private fun getStores() {
        val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)
        val storesCall = serviceGenerator.getStores("Bearer ${sessionManager.fetchAuthToken()}")

        storesCall.enqueue(object : Callback<MutableList<StoreItem>> {
            override fun onResponse(
                call: Call<MutableList<StoreItem>>,
                response: Response<MutableList<StoreItem>>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()!!
                    storesList.clear()
                    storesList.addAll(responseBody.toMutableList())
                    setupCustomSpinner()
                    store.onItemSelectedListener = object:
                        AdapterView.OnItemSelectedListener {

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            val item = parent!!.selectedItem as StoreItem
                            storeItem = item
                            gv.storeId = storesList[position].id
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
                        ErrorDialog.setPermissionDialog(this@FinalRegisterActivity, errorMessage).show()
                    }
                    else if (response.code() > 403)
                    {
                        val jsonObject = JSONObject(response.errorBody()!!.string())
                        val message: String = jsonObject.getString("message")
                        ErrorDialog.setDialog(this@FinalRegisterActivity, message).show()
                    }
                }
            }

            override fun onFailure(call: Call<MutableList<StoreItem>>, t: Throwable) {
                Log.e("FinalRegisterActivity", "Error:" + t.message.toString())
            }
        })
    }

    //Update user
    @Synchronized
    private fun insertUser() {
        try {
            val nameStr = registerData[0]
            val emailStr = registerData[1]
            val passwordStr = registerData[2]
            val phoneStr = phoneNumber.text.toString()
            val birthDateStr = birthDate.text.toString()
            val nifLong = Integer.parseInt(nif.text.toString()).toLong()
            val addressStr = address.text.toString()
            val councilStr = council.text.toString()
            val zipCodeStr = zipCode.text.toString()

            val userAdd = InsertUserItem(
                nameStr,
                emailStr,
                passwordStr,
                phoneStr,
                birthDateStr.toDate("dd-MM-yyyy").formatTo("yyyy-MM-dd'T'HH:mm:ss'Z'"),
                nifLong,
                gv.userCategory,
                "ACTIVE",
                addressStr,
                councilStr,
                zipCodeStr,
                gv.storeId
            )

            val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)
            val userInsert = serviceGenerator.addUser("Bearer ${sessionManager.fetchAuthToken()}", userAdd)

            userInsert.enqueue(object : Callback<ResponseBody?> {

                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@FinalRegisterActivity,
                            "User inserted successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        if (response.code() == 401 || response.code() == 403) {
                            val errorMessage = response.errorBody()!!.string()
                            ErrorDialog.setPermissionDialog(this@FinalRegisterActivity, errorMessage).show()
                        }
                        else if (response.code() > 403)
                        {
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            val message: String = jsonObject.getString("message")
                            ErrorDialog.setDialog(this@FinalRegisterActivity, message).show()
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
            ErrorDialog.setDialog(this@FinalRegisterActivity, "Invalid Input").show()
        }
        catch(e: ParseException)
        {
            ErrorDialog.setDialog(this@FinalRegisterActivity, "Please insert the date of the operation.").show()
        }
    }

}