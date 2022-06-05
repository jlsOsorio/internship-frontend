package com.internship.retailmanagement.controllers

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
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.controllers.adapters.spinners.CRSpinnerAdapter
import com.internship.retailmanagement.databinding.ActivityChangeOperatingFundBinding
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

class ChangeOperatingFundActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangeOperatingFundBinding
    private lateinit var gv: GlobalVar
    private lateinit var entryQty: EditText
    private lateinit var exitQty: EditText
    private lateinit var cashRegister: Spinner
    private lateinit var moment: EditText
    private lateinit var confirm: Button
    private lateinit var crList: MutableList<CashRegisterItem>
    private lateinit var sessionManager: SessionManager
    private val myCalendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityChangeOperatingFundBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        gv = application as GlobalVar
        entryQty = binding.entryQtyOpFund
        exitQty = binding.exitQtyOpFund
        cashRegister = binding.cashRegisterOpFund
        moment = binding.dateOpFund
        confirm = binding.buttonConfirm
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
                this@ChangeOperatingFundActivity,
                date,
                myCalendar[Calendar.YEAR],
                myCalendar[Calendar.MONTH],
                myCalendar[Calendar.DAY_OF_MONTH],
            ).show()
        }

        entryQty.setText(gv.opFundEntryQty.toString())
        exitQty.setText(gv.opFundExitQty.toString())
        moment.setText(gv.opFundMoment!!.toDate("yyyy-MM-dd'T'HH:mm:ss'Z'").formatTo("dd-MM-yyyy HH:mm:ss"))

        getCashRegisters()

        confirm.setOnClickListener {
            putOpFund()
        }

    }

    private fun updateLabel() {
        val myFormat = "dd-MM-yyyy HH:mm:ss"
        val dateFormat = SimpleDateFormat(myFormat, Locale.US)
        moment.setText(dateFormat.format(myCalendar.time))
    }

    private fun setupCustomSpinner() {
        val crPos = crList.indexOfFirst {
            it.id == gv.opFundCashRegister
        }

        val adapter = CRSpinnerAdapter(this@ChangeOperatingFundActivity, crList)
        cashRegister.adapter = adapter

        cashRegister.setSelection(crPos)
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
                        cashRegister.onItemSelectedListener = object:
                            AdapterView.OnItemSelectedListener {

                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                val item = parent!!.selectedItem as CashRegisterItem
                                gv.opFundCashRegister = item.id
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
                            ErrorDialog.setPermissionDialog(this@ChangeOperatingFundActivity, errorMessage).show()
                        }
                        else if (response.code() >= 400)
                        {
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            val message: String = jsonObject.getString("message")
                            ErrorDialog.setDialog(this@ChangeOperatingFundActivity, message).show()
                        }
                    }
                }

                override fun onFailure(call: Call<MutableList<CashRegisterItem>>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("ChangeOpFundActivity", "Error:" + t.message.toString())
                }
            })
    }

    @Synchronized
    private fun putOpFund() {
        try {


            val entryStr = entryQty.text.toString()
            val exitStr = exitQty.text.toString()
            val momentStr = moment.text.toString()

            val opFundUpdate = InsertOpFundItem(
                entryStr.toDouble(),
                exitStr.toDouble(),
                momentStr.toDate("dd-MM-yyyy HH:mm:ss").formatTo("yyyy-MM-dd'T'HH:mm:ss'Z'"),
                gv.opFundCashRegister
            )

            val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)
            val opFundPut = serviceGenerator.updateOpFund("Bearer ${sessionManager.fetchAuthToken()}",gv.opFundId, opFundUpdate)

            opFundPut.enqueue(object : Callback<ResponseBody?> {

                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        gv.opFundEntryQty = opFundUpdate.entryQty
                        gv.opFundExitQty = opFundUpdate.exitQty
                        gv.opFundCashRegister = opFundUpdate.cashRegisterId
                        gv.opFundMoment = opFundUpdate.moment
                        Toast.makeText(
                            this@ChangeOperatingFundActivity,
                            "Operating fund updated successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()

                    } else {
                        if (response.code() == 401 || response.code() == 403) {
                            val errorMessage = response.errorBody()!!.string()
                            ErrorDialog.setPermissionDialog(this@ChangeOperatingFundActivity, errorMessage).show()
                        }
                        else if (response.code() >= 400)
                        {
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            val message: String = jsonObject.getString("message")
                            ErrorDialog.setDialog(this@ChangeOperatingFundActivity, message).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    Log.e("ChangeOpFundActivity", "Error:" + t.message.toString())
                }
            }
            )
        }
        catch (e: NumberFormatException)
        {
            ErrorDialog.setDialog(this@ChangeOperatingFundActivity, "Invalid input!").show()
        }
        catch (e: ParseException)
        {
            ErrorDialog.setDialog(this@ChangeOperatingFundActivity, "Please insert the date of the operation.").show()
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
            com.internship.retailmanagement.R.id.signOutMenu -> Utils.logout(this@ChangeOperatingFundActivity)
        }
        return true
    }

    //Go to next activity
    private fun executeOtherActivity(otherActivity: Class<*>) {
        val x = Intent(this@ChangeOperatingFundActivity, otherActivity)
        startActivity(x)
    }
}