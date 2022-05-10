package com.internship.retailmanagement.controllers.register

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.internship.retailmanagement.databinding.ActivityFinalRegisterBinding

class FinalRegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFinalRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityFinalRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}