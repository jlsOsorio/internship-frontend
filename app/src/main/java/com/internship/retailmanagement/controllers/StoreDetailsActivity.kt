package com.internship.retailmanagement.controllers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.internship.retailmanagement.databinding.ActivityMainBinding
import com.internship.retailmanagement.databinding.ActivityStoreDetailsBinding

class StoreDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStoreDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityStoreDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}