package com.internship.retailmanagement.controllers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.internship.retailmanagement.databinding.ActivityCreateOperatingFundBinding

class CreateOperatingFundActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateOperatingFundBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityCreateOperatingFundBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}