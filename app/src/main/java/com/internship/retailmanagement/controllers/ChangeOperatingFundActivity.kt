package com.internship.retailmanagement.controllers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.internship.retailmanagement.databinding.ActivityChangeProductBinding

class ChangeOperatingFundActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangeProductBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityChangeProductBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}