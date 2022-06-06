package com.internship.retailmanagement.controllers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.internship.retailmanagement.R
import com.internship.retailmanagement.common.ErrorDialog
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.common.Utils
import com.internship.retailmanagement.config.SessionManager
import com.internship.retailmanagement.databinding.ActivityProductDetailsBinding
import com.internship.retailmanagement.dataclasses.products.ProductItem
import com.internship.retailmanagement.services.ApiService
import com.internship.retailmanagement.services.ServiceGenerator
import org.json.JSONObject
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
    private lateinit var sessionManager: SessionManager

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
        sessionManager = SessionManager(this)

        getProduct()
    }

    //Get product from API
    @Synchronized
    private fun getProduct() {
        val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)
        val productCall = serviceGenerator.getProduct("Bearer ${sessionManager.fetchAuthToken()}", gv.productId!!)

        productCall.enqueue(object : Callback<ProductItem> {
            override fun onResponse(
                call: Call<ProductItem>,
                response: Response<ProductItem>
            ) {
                if (response.isSuccessful) {
                    try {
                        val df = DecimalFormat("#.##")
                        val responseBody = response.body()!!
                        var nameSize = responseBody.name!!.length
                        var nameStr = responseBody.name

                        if(nameSize > 15)
                        {
                            nameStr = responseBody.name.substring(0, 15)
                            var stringBeginning = 15
                            nameSize -= 15

                            while(nameSize > 15)
                            {
                                nameStr += "\n${responseBody.name.substring(stringBeginning, stringBeginning + 15)}"
                                stringBeginning += 15
                                nameSize -= 15
                            }

                            if (nameSize > 0)
                            {
                                nameStr += "\n${responseBody.name.substring(stringBeginning, stringBeginning + nameSize)}"

                                /*if(responseBody.name[responseBody.name.length-nameSize - 1] != ' ')
                                {
                                    val nameWords : ArrayList<String> = ArrayList(responseBody.name.split(" "))
                                    val lastCharsSizeToRemove = nameWords[nameWords.size - 1].length - nameSize
                                    nameStr.removeSuffix(nameWords[nameWords.size - 1])
                                    nameStr += "\n${nameWords[nameWords.size - 1]}"
                                }*/
                            }
                        }
                        number.text = responseBody.id.toString()
                        name.text = nameStr
                        stock.text = responseBody.stock.toString()
                        iva.text = responseBody.ivaValue.toString()
                        val grossPriceRounded = df.format(responseBody.grossPrice)
                        grossPrice.text = grossPriceRounded.toString()
                        val taxedPriceRounded = df.format(responseBody.taxedPrice)
                        taxedPrice.text = taxedPriceRounded.toString()
                    } catch (e: RuntimeException)
                    {
                        Log.e("ERROR:", e.message.toString())
                    }

                }
                else
                {
                    if (response.code() == 401)
                    {
                        val errorMessage = response.errorBody()!!.string()
                        Utils.redirectUnauthorized(this@ProductDetailsActivity, errorMessage)
                    }
                    else if (response.code() == 403)
                    {
                        val errorMessage = response.errorBody()!!.string()
                        ErrorDialog.setPermissionDialog(this@ProductDetailsActivity, errorMessage).show()
                    }
                    else if (response.code() >= 400)
                    {
                        val jsonObject = JSONObject(response.errorBody()!!.string())
                        val message: String = jsonObject.getString("message")
                        ErrorDialog.setDialog(this@ProductDetailsActivity, message).show()
                    }
                }
            }

            override fun onFailure(call: Call<ProductItem>, t: Throwable) {
                Log.e("ProductActivity", "Error:" + t.message.toString())
            }
        })
    }

    /**
     * Override method to generate menu in action bar.
     * @param menu: menu Type.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_bar, menu)
        return true
    }

    /**
     * Override method to create conditions for every options of the menu in action bar.
     * @param item MenuItem type
     * @return boolean value
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.profileMenu ->{
                gv.isMyProfile = true
                executeOtherActivity(UserProfileActivity::class.java)
            }
            R.id.changePasswordMenu -> executeOtherActivity(ChangePasswordActivity::class.java)
            R.id.signOutMenu -> Utils.logout(this@ProductDetailsActivity)
        }
        return true
    }

    //Go to next activity
    private fun executeOtherActivity(otherActivity: Class<*>) {
        val x = Intent(this@ProductDetailsActivity, otherActivity)
        startActivity(x)
    }
}