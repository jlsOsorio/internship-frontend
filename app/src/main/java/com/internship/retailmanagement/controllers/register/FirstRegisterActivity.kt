package com.internship.retailmanagement.controllers.register

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import com.internship.retailmanagement.databinding.ActivityFirstRegisterBinding

class FirstRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFirstRegisterBinding
    private lateinit var fullName: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var next: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityFirstRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        fullName = binding.nameRegister
        email = binding.emailRegister
        password = binding.passwordRegister
        confirmPassword = binding.confirmPassword
        next = binding.buttonNext

        next.setOnClickListener{
            val registerData :ArrayList<String> = arrayListOf(fullName.text.toString(), email.text.toString(), password.text.toString())

            if(fullName.text.isEmpty())
            {
                fullName.error = "Name can not be empty."
                return@setOnClickListener
            }

            if(email.text.isEmpty())
            {
                email.error = "Email can not be empty."
                return@setOnClickListener
            }

            if(password.text.isEmpty())
            {
                password.error = "Password can not be empty."
                return@setOnClickListener
            }

            if (password.text.toString().length < 6)
            {
                password.error = "Password must have, at least, 6 characters."
                return@setOnClickListener
            }

            if (password.text.toString() != confirmPassword.text.toString())
            {
                confirmPassword.error = "Passwords don't match."
                return@setOnClickListener
            }

            val intent = Intent(this@FirstRegisterActivity, FinalRegisterActivity::class.java)
            intent.putStringArrayListExtra("regDataKey", registerData)
            startActivity(intent)
        }
    }
}