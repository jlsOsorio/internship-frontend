package com.internship.retailmanagement.controllers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.internship.retailmanagement.R
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.databinding.ActivityChangeProductBinding
import com.internship.retailmanagement.databinding.ActivityProductDetailsBinding
import com.internship.retailmanagement.dataclasses.products.ProductItem
import com.internship.retailmanagement.dataclasses.users.UserItem
import com.internship.retailmanagement.services.ApiService
import com.internship.retailmanagement.services.ServiceGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat

class ProductDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailsBinding
    private lateinit var gv: GlobalVar
    private lateinit var number: TextView
    private lateinit var name: TextView
    private lateinit var stock: TextView
    private lateinit var iva: TextView
    private lateinit var grossPrice: TextView
    private lateinit var taxedPrice: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        gv = application as GlobalVar
        number = binding.numberProduct
        name = binding.nameProduct
        stock = binding.stockProduct
        iva = binding.ivaProduct
        grossPrice = binding.grossPriceProduct
        taxedPrice = binding.taxedPriceProduct

        getProduct()
    }

    //Get product from API
    @Synchronized
    private fun getProduct() {
        val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)
        val productCall = serviceGenerator.getProduct(gv.productId!!)

        productCall.enqueue(object : Callback<ProductItem> {
            override fun onResponse(
                call: Call<ProductItem>,
                response: Response<ProductItem>
            ) {
                if (response.isSuccessful) {
                    val df = DecimalFormat("#.##")
                    val responseBody = response.body()!!
                    number.text = responseBody.id.toString()
                    name.text = responseBody.name
                    stock.text = responseBody.stock.toString()
                    iva.text = responseBody.ivaValue.toString()
                    val grossPriceRounded = df.format(responseBody.grossPrice)
                    grossPrice.text = grossPriceRounded.toString()
                    val taxedPriceRounded = df.format(responseBody.taxedPrice)
                    taxedPrice.text = taxedPriceRounded.toString()
                }


            }

            override fun onFailure(call: Call<ProductItem>, t: Throwable) {
                Log.e("ProductActivity", "Error:" + t.message.toString())
            }
        })
    }

    /**
     * Overwrite method to generate menu in action bar.
     * @param menu: menu Type.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_bar, menu)
        return true
    }

    /**
     * Overwrite method to create conditions for every options of the menu in action bar.
     * @param item MenuItem type
     * @return boolean value
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.profileMenu -> null
            R.id.changePasswordMenu -> null
            R.id.signOutMenu -> null
        }
        return true
    }
}