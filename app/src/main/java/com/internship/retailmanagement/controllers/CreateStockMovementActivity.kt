package com.internship.retailmanagement.controllers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.internship.retailmanagement.databinding.ActivityCreateStockMovementBinding
import com.internship.retailmanagement.databinding.ActivityMainBinding

class CreateStockMovementActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateStockMovementBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityCreateStockMovementBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}