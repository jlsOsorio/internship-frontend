package com.internship.retailmanagement.controllers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.controllers.adapters.spinners.IvaSpinnerAdapter
import com.internship.retailmanagement.databinding.ActivityChangeProductBinding
import com.internship.retailmanagement.dataclasses.IvaItem
import com.internship.retailmanagement.dataclasses.products.UpdateProductItem
import com.internship.retailmanagement.services.ApiService
import com.internship.retailmanagement.services.ServiceGenerator
import kotlinx.android.synthetic.main.activity_change_product.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangeProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangeProductBinding
    private lateinit var gv: GlobalVar
    private lateinit var productName: EditText
    private lateinit var ivaValue: Spinner
    private lateinit var grossPrice: EditText
    private lateinit var ivaList: MutableList<IvaItem>
    private lateinit var confirm: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityChangeProductBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        gv = application as GlobalVar
        productName = binding.nameProduct
        ivaValue = binding.ivaProduct
        grossPrice = binding.grossPriceProduct
        confirm = binding.buttonConfirm
        ivaList = arrayListOf()

        productName.setText(gv.productName)
        grossPrice.setText(gv.productGrossPrice.toString())

        getIvaValues()

        confirm.setOnClickListener {
            putProduct()
            finish()
        }
    }

    private fun setupCustomSpinner() {
        val ivaPos = ivaList.indexOfFirst {
            (it.tax!! * 100).toInt() == gv.productIva
        }

        val adapter = IvaSpinnerAdapter(this@ChangeProductActivity, ivaList)
        ivaValue.adapter = adapter

        ivaValue.setSelection(ivaPos)
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

    //Update product
    @Synchronized
    private fun putProduct() {
        val nameStr = nameProduct.text.toString()
        val grossPriceStr = grossPrice.text.toString()

        val productUpdate = UpdateProductItem(
            nameStr,
            gv.productIva,
            grossPriceStr.toDouble(),
        )

        val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)
        val userPut = serviceGenerator.updateProduct(gv.productId, productUpdate)

        userPut.enqueue(object : Callback<ResponseBody?> {

            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                gv.productName = productUpdate.name
                gv.productIva = productUpdate.ivaValue
                gv.productGrossPrice = productUpdate.grossPrice
                Toast.makeText(this@ChangeProductActivity, "Product updated successfully!", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Log.e("ChangeProductActivity", "Error:" + t.message.toString())
            }
        }
        )
    }
}