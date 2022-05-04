package com.internship.retailmanagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.internship.retailmanagement.controllers.SignInActivity
import com.internship.retailmanagement.databinding.ActivityScreenBinding

class ScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityScreenBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.hide()

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@ScreenActivity, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}