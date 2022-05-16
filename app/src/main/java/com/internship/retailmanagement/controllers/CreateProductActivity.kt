package com.internship.retailmanagement.controllers

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.R
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.controllers.adapters.spinners.IvaSpinnerAdapter
import com.internship.retailmanagement.databinding.ActivityCreateProductBinding
import com.internship.retailmanagement.dataclasses.IvaItem
import com.internship.retailmanagement.dataclasses.products.InsertProductItem
import com.internship.retailmanagement.services.ApiService
import com.internship.retailmanagement.services.ServiceGenerator
import kotlinx.android.synthetic.main.activity_change_product.nameProduct
import kotlinx.android.synthetic.main.activity_create_product.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class CreateProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateProductBinding
    private lateinit var gv: GlobalVar
    private lateinit var productName: EditText
    private lateinit var productStock: EditText
    private lateinit var ivaValue: Spinner
    private lateinit var grossPrice: EditText
    private lateinit var ivaList: MutableList<IvaItem>
    private lateinit var create: Button

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityCreateProductBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        gv = application as GlobalVar
        productName = binding.nameProduct
        productStock = binding.qtyProduct
        ivaValue = binding.ivaProduct
        grossPrice = binding.grossPriceProduct
        create = binding.buttonCreate
        ivaList = arrayListOf()

        val defaultValue : ArrayList<String> = arrayListOf("SELECT IVA TAX")

        val defaultAdapter = ArrayAdapter(this@CreateProductActivity, R.layout.support_simple_spinner_dropdown_item, defaultValue)
        ivaValue.adapter = defaultAdapter

        ivaValue.setOnTouchListener { _, _ ->
            getIvaValues()
            false
        }

        create.setOnClickListener{
            createProduct()
            finish()
        }
    }

    //Get iva taxes from API
    @Synchronized
    private fun getIvaValues() {
        val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)

        val ivaCall = serviceGenerator.getIvaValues()

        ivaCall.enqueue(
            object : Callback<MutableList<IvaItem>> {
                override fun onResponse(
                    call: Call<MutableList<IvaItem>>,
                    response: Response<MutableList<IvaItem>>
                ) {
                    if (response.isSuccessful) {
                        ivaList.clear()
                        ivaList.addAll(response.body()!!.toMutableList())
                        setupCustomSpinner()
                        ivaValue.onItemSelectedListener = object:
                            AdapterView.OnItemSelectedListener {

                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                val item = parent!!.selectedItem as IvaItem
                                gv.productIva = (item.tax!!*100).toInt()
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                TODO("Not yet implemented")
                            }

                        }
                    }
                }

                override fun onFailure(call: Call<MutableList<IvaItem>>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("ChangeProductActivity", "Error:" + t.message.toString())
                }
            })
    }

    private fun setupCustomSpinner() {
        val adapter = IvaSpinnerAdapter(this@CreateProductActivity, ivaList)
        ivaValue.adapter = adapter
    }

    @Synchronized
    private fun createProduct() {
        val nameStr = nameProduct.text.toString()
        val stockStr = qtyProduct.text.toString()
        val grossPriceStr = grossPrice.text.toString()

        val productInsert = InsertProductItem(
            nameStr,
            stockStr.toInt(),
            gv.productIva,
            grossPriceStr.toDouble(),
        )

        val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)
        val userPut = serviceGenerator.addProduct(productInsert)

        userPut.enqueue(object : Callback<ResponseBody?> {

            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                gv.productName = productInsert.name
                gv.productStock = productInsert.stock
                gv.productIva = productInsert.ivaValue
                gv.productGrossPrice = productInsert.grossPrice
                Toast.makeText(this@CreateProductActivity, "Product created successfully!", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Log.e("CreateProductActivity", "Error:" + t.message.toString())
            }
        }
        )
    }
}