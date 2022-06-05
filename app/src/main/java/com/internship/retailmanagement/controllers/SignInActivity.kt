package com.internship.retailmanagement.controllers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.internship.retailmanagement.common.ErrorDialog
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.config.SessionManager
import com.internship.retailmanagement.databinding.ActivityChangeStoreBinding
import com.internship.retailmanagement.databinding.ActivitySignInBinding
import com.internship.retailmanagement.dataclasses.users.LoginRequest
import com.internship.retailmanagement.dataclasses.users.LoginResponse
import com.internship.retailmanagement.services.ApiService
import com.internship.retailmanagement.services.ServiceGenerator
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInActivity : AppCompatActivity() {
    //Initialize variables
    private lateinit var binding: ActivitySignInBinding
    private lateinit var gv: GlobalVar
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var signIn: AppCompatButton
    private lateinit var progressBar: ProgressBar
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivitySignInBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        gv = application as GlobalVar
        email = binding.emailSignIn
        password = binding.passwordSignIn
        signIn = binding.buttonSignIn
        progressBar = binding.progressBar
        sessionManager = SessionManager(this)

        gv.userToken = sessionManager.fetchAuthToken()

        if(gv.userToken != null) {
            executeOtherActivity(MainActivity::class.java)
        }

        signIn.setOnClickListener{
            val emailStr:String = email.text.toString().trim()
            val passwordStr:String = password.text.toString().trim()

            //Validations
            if (TextUtils.isEmpty(emailStr)) {
                email.error = "Email can not be empty."
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(passwordStr)){
                password.error="Password can not be empty."
                return@setOnClickListener
            }

            if (passwordStr.length < 6) {
                password.error = "Password must have, at least, 6 characters."
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE //Make progress bar appear after clicking on Sign In
            signIn.visibility = View.GONE //Make button disappear after clicking on Sign In

            login()
        }
    }

    //Login user
    @Synchronized
    private fun login() {

        val loginValues = LoginRequest(
            email.text.toString(),
            password.text.toString()
        )

        val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)
        val login = serviceGenerator.login(loginValues)

        login.enqueue(object : Callback<LoginResponse> {

            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful)
                {
                    val responseBody = response.body()!!
                    sessionManager.saveAuthToken(responseBody.token!!)
                    gv.userLoggedId = responseBody.id
                    gv.emailLoggedIn = responseBody.email
                    gv.userRole = responseBody.category
                    Toast.makeText(this@SignInActivity, "Login successful!", Toast.LENGTH_SHORT).show()
                    executeOtherActivity(MainActivity::class.java)
                    finish()
                }
                else
                {
                    if (response.code() >= 400) {
                        val jsonObject = JSONObject(response.errorBody()!!.string())
                        val message: String = jsonObject.getString("message")
                        ErrorDialog.setDialog(this@SignInActivity, message).show()
                    }

                    progressBar.visibility = View.GONE
                    signIn.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("SignInActivity", "Error:" + t.message.toString())
            }
        })
    }

    //Go to next activity
    private fun executeOtherActivity(otherActivity: Class<*>) {
        val x = Intent(this@SignInActivity, otherActivity)
        startActivity(x)
    }
}