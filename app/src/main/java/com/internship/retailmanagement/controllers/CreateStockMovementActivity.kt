package com.internship.retailmanagement.controllers

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.R
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.databinding.ActivityCreateStockMovementBinding
import com.internship.retailmanagement.databinding.ActivityMainBinding
import com.internship.retailmanagement.dataclasses.products.InsertProductItem
import com.internship.retailmanagement.dataclasses.stockmovements.InsertStockMovItem
import com.internship.retailmanagement.services.ApiService
import com.internship.retailmanagement.services.ServiceGenerator
import kotlinx.android.synthetic.main.activity_change_product.*
import kotlinx.android.synthetic.main.activity_change_product.nameProduct
import kotlinx.android.synthetic.main.activity_create_product.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class CreateStockMovementActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateStockMovementBinding
    private lateinit var gv: GlobalVar
    private lateinit var movement: Spinner
    private lateinit var quantity: EditText
    private lateinit var create: Button

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityCreateStockMovementBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        gv = application as GlobalVar
        movement = binding.typeMovement
        quantity = binding.qtyProduct
        create = binding.buttonCreate

        val defaultValue : ArrayList<String> = arrayListOf("SELECT MOVEMENT TYPE")
        val movArr : ArrayList<String> = arrayListOf("IN", "OUT")

        val defaultAdapter = ArrayAdapter(this@CreateStockMovementActivity, R.layout.support_simple_spinner_dropdown_item, defaultValue)
        movement.adapter = defaultAdapter

        movement.setOnTouchListener { _, _ ->
            val statusAdapter = ArrayAdapter(this@CreateStockMovementActivity, R.layout.support_simple_spinner_dropdown_item, movArr)
            movement.adapter = statusAdapter
            false
        }

        movement.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                gv.typeMovement = movArr[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        create.setOnClickListener{
            createStockMovement()
            finish()
        }
    }

    //Create stock movement
    @Synchronized
    private fun createStockMovement() {
        val moveStr = gv.typeMovement
        val quantityStr = quantity.text.toString()

        val smInsert = InsertStockMovItem(
            quantityStr.toInt(),
            moveStr
        )

        val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)
        val smCreate = serviceGenerator.addStockMovement(gv.productId!!, smInsert)

        smCreate.enqueue(object : Callback<ResponseBody?> {

            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                Toast.makeText(this@CreateStockMovementActivity, "Stock Movement created successfully!", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Log.e("CreateStockMovementActivity", "Error:" + t.message.toString())
            }
        }
        )
    }
}