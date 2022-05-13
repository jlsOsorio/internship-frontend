package com.internship.retailmanagement.controllers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.internship.retailmanagement.databinding.ActivityCreateStoreBinding

class CreateStoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateStoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityCreateStoreBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}