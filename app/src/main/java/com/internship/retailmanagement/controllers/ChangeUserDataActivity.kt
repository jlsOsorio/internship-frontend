package com.internship.retailmanagement.controllers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.internship.retailmanagement.databinding.ActivityChangeUserDataBinding

class ChangeUserDataActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangeUserDataBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityChangeUserDataBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}