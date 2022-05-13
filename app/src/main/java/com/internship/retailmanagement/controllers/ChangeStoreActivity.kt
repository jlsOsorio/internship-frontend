package com.internship.retailmanagement.controllers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.internship.retailmanagement.databinding.ActivityChangeStoreBinding

class ChangeStoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangeStoreBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityChangeStoreBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}