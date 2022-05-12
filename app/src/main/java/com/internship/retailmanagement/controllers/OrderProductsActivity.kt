package com.internship.retailmanagement.controllers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.internship.retailmanagement.databinding.ActivityOrderProductsBinding

class OrderProductsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderProductsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityOrderProductsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}