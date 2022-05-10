package com.internship.retailmanagement.controllers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.internship.retailmanagement.databinding.ActivityProductsBinding

class CreateProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityProductsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}