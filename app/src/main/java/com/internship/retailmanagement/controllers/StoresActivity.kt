package com.internship.retailmanagement.controllers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.internship.retailmanagement.databinding.ActivityMainBinding
import com.internship.retailmanagement.databinding.ActivityStoresBinding

class StoresActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoresBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityStoresBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}