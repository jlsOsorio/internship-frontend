package com.internship.retailmanagement.controllers

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.R
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.databinding.ActivityCreateStockMovementBinding
import com.internship.retailmanagement.dataclasses.stockmovements.InsertStockMovItem
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
import java.lang.NumberFormatException
import java.util.ArrayList

class CreateStockMovementActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateStockMovementBinding
    private lateinit var gv: GlobalVar
    private lateinit var movement: Spinner
    private lateinit var quantity: EditText
    private lateinit var create: Button
    private lateinit var sessionManager: SessionManager

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityCreateStockMovementBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        gv = application as GlobalVar
        movement = binding.typeMovement
        quantity = binding.qtyProduct
        create = binding.buttonCreate
        sessionManager = SessionManager(this)

        val defaultValue : ArrayList<String> = arrayListOf("SELECT MOVEMENT TYPE")
        val movArr : ArrayList<String> = arrayListOf("IN", "OUT")

        val defaultAdapter = ArrayAdapter(this@CreateStockMovementActivity, R.layout.support_simple_spinner_dropdown_item, defaultValue)
        movement.adapter = defaultAdapter

        movement.setOnTouchListener { _, _ ->
            val statusAdapter = ArrayAdapter(this@CreateStockMovementActivity, R.layout.support_simple_spinner_dropdown_item, movArr)
            movement.adapter = statusAdapter
            false
        }

        movement.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                gv.typeMovement = movArr[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        create.setOnClickListener{
            createStockMovement()
        }
    }

    //Create stock movement
    @Synchronized
    private fun createStockMovement() {
        try {
            val moveStr = gv.typeMovement
            val quantityStr = quantity.text.toString()

            val smInsert = InsertStockMovItem(
                quantityStr.toInt(),
                moveStr
            )

            val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)
            val smCreate = serviceGenerator.addStockMovement("Bearer ${sessionManager.fetchAuthToken()}", gv.productId!!, smInsert)

            smCreate.enqueue(object : Callback<ResponseBody?> {

                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@CreateStockMovementActivity,
                            "Stock Movement created successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        if (response.code() == 401)
                        {
                            val errorMessage = response.errorBody()!!.string()
                            Utils.redirectUnauthorized(this@CreateStockMovementActivity, errorMessage)
                        }
                        else if (response.code() == 403)
                        {
                            val errorMessage = response.errorBody()!!.string()
                            ErrorDialog.setPermissionDialog(this@CreateStockMovementActivity, errorMessage).show()
                        }
                        else if (response.code() >= 400)
                        {
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            val message: String = jsonObject.getString("message")
                            ErrorDialog.setDialog(this@CreateStockMovementActivity, message).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    Log.e("CreateStockMovActivity", "Error:" + t.message.toString())
                }
            }
            )
        }
        catch (e: NumberFormatException)
        {
            ErrorDialog.setDialog(this@CreateStockMovementActivity, "Invalid input!").show()
        }
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
            com.internship.retailmanagement.R.id.signOutMenu -> Utils.logout(this@CreateStockMovementActivity)
        }
        return true
    }

    //Go to next activity
    private fun executeOtherActivity(otherActivity: Class<*>) {
        val x = Intent(this@CreateStockMovementActivity, otherActivity)
        startActivity(x)
    }
}