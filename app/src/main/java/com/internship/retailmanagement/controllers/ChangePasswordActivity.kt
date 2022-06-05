package com.internship.retailmanagement.controllers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.internship.retailmanagement.common.ErrorDialog
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.common.Utils
import com.internship.retailmanagement.config.SessionManager
import com.internship.retailmanagement.databinding.ActivityChangePasswordBinding
import com.internship.retailmanagement.dataclasses.users.UserPassword
import com.internship.retailmanagement.services.ApiService
import com.internship.retailmanagement.services.ServiceGenerator
import kotlinx.android.synthetic.main.activity_change_password.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var gv: GlobalVar
    private lateinit var oldPassword: EditText
    private lateinit var newPassword: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var confirmButton: AppCompatButton
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        gv = application as GlobalVar
        oldPassword = binding.oldPassword
        newPassword = binding.newPassword
        confirmPassword = binding.confirmPassword
        confirmButton = binding.buttonConfirm
        sessionManager = SessionManager(this)

        buttonConfirm.setOnClickListener{
            val oldPasswordStr:String = oldPassword.text.toString().trim()
            val newPasswordStr:String = newPassword.text.toString().trim()
            val confirmPasswordStr:String = confirmPassword.text.toString().trim()

            //Validations
            if (TextUtils.isEmpty(oldPasswordStr)) {
                oldPassword.error = "Old password can not be empty."
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(newPasswordStr)) {
                newPassword.error = "New password can not be empty."
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(confirmPasswordStr)){
                confirmPassword.error="Confirm password can not be empty."
                return@setOnClickListener
            }

            if (newPasswordStr.length < 6) {
                newPassword.error = "Password must have, at least, 6 characters."
                return@setOnClickListener
            }

            if (newPasswordStr != confirmPasswordStr) {
                confirmPassword.error = "Passwords don't match!"
                return@setOnClickListener
            }

            changePassword()
        }
    }

    //Change password
    @Synchronized
    private fun changePassword() {

        val passwordStr = newPassword.text.toString()

        val userPassword = UserPassword(
            passwordStr
        )

        val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)
        val alterPW = serviceGenerator.changePassword("Bearer ${sessionManager.fetchAuthToken()}", userPassword)

        alterPW.enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful)
                {
                    Toast.makeText(this@ChangePasswordActivity, "Password changed successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                else
                {
                    if (response.code() == 401 || response.code() == 403) {
                        val errorMessage = response.errorBody()!!.string()
                        ErrorDialog.setPermissionDialog(this@ChangePasswordActivity, errorMessage).show()
                    }
                    else if (response.code() >= 400)
                    {
                        val jsonObject = JSONObject(response.errorBody()!!.string())
                        val message: String = jsonObject.getString("message")
                        ErrorDialog.setDialog(this@ChangePasswordActivity, message).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("CreateOpFundActivity", "Error:" + t.message.toString())
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
            com.internship.retailmanagement.R.id.signOutMenu -> Utils.logout(this@ChangePasswordActivity)
        }
        return true
    }

    //Go to next activity
    private fun executeOtherActivity(otherActivity: Class<*>) {
        val x = Intent(this@ChangePasswordActivity, otherActivity)
        startActivity(x)
    }
}