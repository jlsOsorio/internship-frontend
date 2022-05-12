package com.internship.retailmanagement.controllers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.internship.retailmanagement.databinding.ActivityCreateInvoiceBinding

class CreateInvoiceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateInvoiceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityCreateInvoiceBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}