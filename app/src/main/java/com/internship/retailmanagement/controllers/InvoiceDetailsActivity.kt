package com.internship.retailmanagement.controllers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.internship.retailmanagement.databinding.ActivityInvoiceDetailsBinding

class InvoiceDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInvoiceDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityInvoiceDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}