package com.internship.retailmanagement.controllers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.internship.retailmanagement.databinding.ActivityChangeOperatingFundBinding

class ChangeOperatingFundActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangeOperatingFundBinding
    private lateinit var entryQty: EditText
    private lateinit var exitQty: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityChangeOperatingFundBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}