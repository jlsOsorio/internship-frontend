package com.internship.retailmanagement.controllers.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.internship.retailmanagement.databinding.ActivityFirstRegisterBinding

class FirstRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFirstRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityFirstRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}