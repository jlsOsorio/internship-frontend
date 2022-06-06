package com.internship.retailmanagement.controllers

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.R
import androidx.appcompat.app.AppCompatActivity
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.controllers.adapters.spinners.CRSpinnerAdapter
import com.internship.retailmanagement.databinding.ActivityCreateOperatingFundBinding
import com.internship.retailmanagement.dataclasses.CashRegisterItem
import com.internship.retailmanagement.dataclasses.operatingfunds.InsertOpFundItem
import com.internship.retailmanagement.services.ApiService
import com.internship.retailmanagement.common.ErrorDialog
import com.internship.retailmanagement.common.Utils
import com.internship.retailmanagement.config.SessionManager
import com.internship.retailmanagement.services.ServiceGenerator
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class CreateOperatingFundActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateOperatingFundBinding
    private lateinit var gv: GlobalVar
    private lateinit var entryQty: EditText
    private lateinit var exitQty: EditText
    private lateinit var cashRegisters: Spinner
    private lateinit var moment: EditText
    private lateinit var crList: MutableList<CashRegisterItem>
    private lateinit var cashRegItem: CashRegisterItem
    private lateinit var create: Button
    private lateinit var sessionManager: SessionManager
    private val myCalendar = Calendar.getInstance()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityCreateOperatingFundBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        gv = application as GlobalVar
        entryQty = binding.entryQty
        exitQty = binding.exitQty
        cashRegisters = binding.cashRegisterOpFund
        moment = binding.dateOpFund
        create = binding.buttonCreate
        crList = arrayListOf()
        sessionManager = SessionManager(this)

        val date =
            OnDateSetListener { view, year, month, day ->
                myCalendar[Calendar.YEAR] = year
                myCalendar[Calendar.MONTH] = month
                myCalendar[Calendar.DAY_OF_MONTH] = day
                updateLabel()
            }

        moment.setOnClickListener {
            DatePickerDialog(
                this@CreateOperatingFundActivity,
                date,
                myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH],
            ).show()
        }

        cashRegItem = CashRegisterItem(0)

        val defaultValue : ArrayList<String> = arrayListOf("SELECT CASH REGISTER")

        val defaultAdapter = ArrayAdapter(this@CreateOperatingFundActivity, R.layout.support_simple_spinner_dropdown_item, defaultValue)
        cashRegisters.adapter = defaultAdapter

        cashRegisters.setOnTouchListener { _, _ ->
            getCashRegisters()
            false
        }

        create.setOnClickListener{
            createOpFund()
        }
    }

    private fun updateLabel() {
        val myFormat = "dd-MM-yyyy HH:mm:ss"
        val dateFormat = SimpleDateFormat(myFormat, Locale.US)
        moment.setText(dateFormat.format(myCalendar.time))
    }

    private fun setupCustomSpinner() {
        val adapter = CRSpinnerAdapter(this@CreateOperatingFundActivity, crList)
        cashRegisters.adapter = adapter
    }

    //Get cash registers from API
    @Synchronized
    private fun getCashRegisters() {
        val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)

        val crCall = serviceGenerator.getCashRegisters("Bearer ${sessionManager.fetchAuthToken()}")

        crCall.enqueue(
            object : Callback<MutableList<CashRegisterItem>> {
                override fun onResponse(
                    call: Call<MutableList<CashRegisterItem>>,
                    response: Response<MutableList<CashRegisterItem>>
                ) {
                    if (response.isSuccessful) {
                        crList.clear()
                        crList.addAll(response.body()!!.toMutableList())
                        setupCustomSpinner()
                        cashRegisters.onItemSelectedListener = object:
                            AdapterView.OnItemSelectedListener {

                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                val item = parent!!.selectedItem as CashRegisterItem
                                cashRegItem = item
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                TODO("Not yet implemented")
                            }

                        }
                    }
                    else
                    {
                        if (response.code() == 401)
                        {
                            val errorMessage = response.errorBody()!!.string()
                            Utils.redirectUnauthorized(this@CreateOperatingFundActivity, errorMessage)
                        }
                        else if (response.code() == 403)
                        {
                            val errorMessage = response.errorBody()!!.string()
                            ErrorDialog.setPermissionDialog(this@CreateOperatingFundActivity, errorMessage).show()
                        }
                        else if (response.code() >= 400)
                        {
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            val message: String = jsonObject.getString("message")
                            ErrorDialog.setDialog(this@CreateOperatingFundActivity, message).show()
                        }
                    }
                }

                override fun onFailure(call: Call<MutableList<CashRegisterItem>>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("ChangeOpFundActivity", "Error:" + t.message.toString())
                }
            })
    }

    //Create operating fund
    @Synchronized
    private fun createOpFund() {
        try {
            val entryStr = entryQty.text.toString()
            val exitStr = exitQty.text.toString()
            val momentStr = moment.text.toString()

            val opFundInsert = InsertOpFundItem(

                entryStr.toDouble(),
                exitStr.toDouble(),
                momentStr.toDate("dd-MM-yyyy HH:mm:ss").formatTo("yyyy-MM-dd'T'HH:mm:ss'Z'"),
                cashRegItem.id


            )

            val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)
            val opFundAdd = serviceGenerator.addOperatingFund("Bearer ${sessionManager.fetchAuthToken()}", opFundInsert)

            opFundAdd.enqueue(object : Callback<ResponseBody> {

                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful) {
                        gv.opFundEntryQty = opFundInsert.entryQty
                        gv.opFundExitQty = opFundInsert.exitQty
                        gv.opFundCashRegister = opFundInsert.cashRegisterId
                        gv.opFundMoment = opFundInsert.moment
                        Toast.makeText(
                            this@CreateOperatingFundActivity,
                            "Operating fund created successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        if (response.code() == 401)
                        {
                            val errorMessage = response.errorBody()!!.string()
                            Utils.redirectUnauthorized(this@CreateOperatingFundActivity, errorMessage)
                        }
                        else if (response.code() == 403)
                        {
                            val errorMessage = response.errorBody()!!.string()
                            ErrorDialog.setPermissionDialog(this@CreateOperatingFundActivity, errorMessage).show()
                        }
                        else if (response.code() >= 400)
                        {
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            val message: String = jsonObject.getString("message")
                            ErrorDialog.setDialog(this@CreateOperatingFundActivity, message).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("CreateOpFundActivity", "Error:" + t.message.toString())
                }
            }
            )
        }
        catch (e: NumberFormatException)
        {
            ErrorDialog.setDialog(this@CreateOperatingFundActivity, "Invalid input!").show()
        }
        catch (e: ParseException)
        {
            ErrorDialog.setDialog(this@CreateOperatingFundActivity, "Please insert the date of the operation.").show()
        }
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

    /**
     * Override method to generate menu in action bar.
     * @param menu: menu Type.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(com.internship.retailmanagement.R.menu.menu_bar, menu)
        return true
    }

    /**
     * Override method to create conditions for every options of the menu in action bar.
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
            com.internship.retailmanagement.R.id.signOutMenu -> Utils.logout(this@CreateOperatingFundActivity)
        }
        return true
    }

    //Go to next activity
    private fun executeOtherActivity(otherActivity: Class<*>) {
        val x = Intent(this@CreateOperatingFundActivity, otherActivity)
        startActivity(x)
    }
}