package com.internship.retailmanagement.controllers

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.controllers.adapters.spinners.CRSpinnerAdapter
import com.internship.retailmanagement.databinding.ActivityChangeOperatingFundBinding
import com.internship.retailmanagement.dataclasses.CashRegisterItem
import com.internship.retailmanagement.dataclasses.operatingfunds.InsertOpFundItem
import com.internship.retailmanagement.services.ApiService
import com.internship.retailmanagement.services.ServiceGenerator
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
            finish()
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

        val crCall = serviceGenerator.getCashRegisters()

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
                                //val text = parent!!.getItemAtPosition(1).toString()
                                val item = parent!!.selectedItem as CashRegisterItem
                                gv.opFundCashRegister = item.id
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                TODO("Not yet implemented")
                            }

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
        val userPut = serviceGenerator.updateOpFund(gv.opFundId, opFundUpdate)

        userPut.enqueue(object : Callback<ResponseBody?> {

            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                gv.opFundEntryQty = opFundUpdate.entryQty
                gv.opFundExitQty = opFundUpdate.exitQty
                gv.opFundCashRegister = opFundUpdate.cashRegisterId
                gv.opFundMoment = opFundUpdate.moment
                Toast.makeText(this@ChangeOperatingFundActivity, "Operating fund updated successfully!", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Log.e("ChangeOpFundActivity", "Error:" + t.message.toString())
            }
        }
        )
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
}