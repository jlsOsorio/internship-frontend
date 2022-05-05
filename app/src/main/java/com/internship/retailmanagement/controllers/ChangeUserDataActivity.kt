package com.internship.retailmanagement.controllers

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.internship.retailmanagement.R
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.databinding.ActivityChangeUserDataBinding
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
    private lateinit var store: EditText
    private lateinit var status: EditText
    val myCalendar = Calendar.getInstance()

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
        store = binding.storeProfile
        status = binding.statusProfile

        email.setText(gv.userEmail)
        name.setText(gv.userName)
        nif.setText(gv.userNif.toString())
        address.setText(gv.userAddress)
        council.setText(gv.userCouncil)
        zipCode.setText(gv.userZipCode)
        birthDate.setText(gv.userBirthDate!!.formatTo("dd-MM-yyyy"))
        phoneNumber.setText(gv.userPhone)
        category.setText(gv.userCategory)
        store.setText(gv.storeId.toString())
        status.setText(gv.userStatus)


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

    private fun updateLabel() {
        val myFormat = "dd-MM-yyyy"
        val dateFormat = SimpleDateFormat(myFormat, Locale.US)
        birthDate.setText(dateFormat.format(myCalendar.time))
    }
}