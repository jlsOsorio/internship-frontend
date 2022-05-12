package com.internship.retailmanagement.controllers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.internship.retailmanagement.databinding.ActivityStockMovementBinding

class StockMovementActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStockMovementBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityStockMovementBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}