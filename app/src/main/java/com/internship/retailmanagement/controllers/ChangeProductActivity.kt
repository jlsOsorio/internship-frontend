package com.internship.retailmanagement.controllers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.controllers.adapters.spinners.IvaSpinnerAdapter
import com.internship.retailmanagement.databinding.ActivityChangeProductBinding
import com.internship.retailmanagement.dataclasses.IvaItem
import com.internship.retailmanagement.dataclasses.products.UpdateProductItem
import com.internship.retailmanagement.services.ApiService
import com.internship.retailmanagement.common.ErrorDialog
import com.internship.retailmanagement.common.Utils
import com.internship.retailmanagement.config.SessionManager
import com.internship.retailmanagement.services.ServiceGenerator
import kotlinx.android.synthetic.main.activity_change_product.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.NumberFormatException

class ChangeProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangeProductBinding
    private lateinit var gv: GlobalVar
    private lateinit var productName: EditText
    private lateinit var ivaValue: Spinner
    private lateinit var grossPrice: EditText
    private lateinit var ivaList: MutableList<IvaItem>
    private lateinit var confirm: Button
    private lateinit var sessionManager: SessionManager

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
        sessionManager = SessionManager(this)

        productName.setText(gv.productName!!.substring(0, 15))
        grossPrice.setText(gv.productGrossPrice.toString())

        getIvaValues()

        confirm.setOnClickListener {
            if (productName.text.length > 15)
            {
                productName.error = "Name should not have more than 15 characters"
                return@setOnClickListener
            }
            putProduct()
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

        val ivaCall = serviceGenerator.getIvaValues("Bearer ${sessionManager.fetchAuthToken()}")

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
                    else
                    {
                        if (response.code() == 401 || response.code() == 403) {
                            val errorMessage = response.errorBody()!!.string()
                            ErrorDialog.setPermissionDialog(this@ChangeProductActivity, errorMessage).show()
                        }
                        else if (response.code() >= 400)
                        {
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            val message: String = jsonObject.getString("message")
                            ErrorDialog.setDialog(this@ChangeProductActivity, message).show()
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
        try {
            val nameStr = nameProduct.text.toString()
            val grossPriceStr = grossPrice.text.toString()

            val productUpdate = UpdateProductItem(
                nameStr,
                gv.productIva,
                grossPriceStr.toDouble(),
            )

            val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)
            val userPut = serviceGenerator.updateProduct("Bearer ${sessionManager.fetchAuthToken()}", gv.productId, productUpdate)


            userPut.enqueue(object : Callback<ResponseBody?> {

                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        gv.productName = productUpdate.name
                        gv.productIva = productUpdate.ivaValue
                        gv.productGrossPrice = productUpdate.grossPrice
                        Toast.makeText(
                            this@ChangeProductActivity,
                            "Product updated successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        if (response.code() == 401 || response.code() == 403) {
                            val errorMessage = response.errorBody()!!.string()
                            ErrorDialog.setPermissionDialog(this@ChangeProductActivity, errorMessage).show()
                        }
                        else if (response.code() >= 400)
                        {
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            val message: String = jsonObject.getString("message")
                            ErrorDialog.setDialog(this@ChangeProductActivity, message).show()
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    Log.e("ChangeProductActivity", "Error:" + t.message.toString())
                }
            }
            )
        }
        catch(e: NumberFormatException)
        {
            ErrorDialog.setDialog(this@ChangeProductActivity, "Invalid input!").show()
        }
    }

    /**
     * Overwrite method to generate menu in action bar.
     * @param menu: menu Type.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(com.internship.retailmanagement.R.menu.menu_bar, menu)
        return true
    }

    /**
     * Overwrite method to create conditions for every options of the menu in action bar.
     * @param item MenuItem type
     * @return boolean value
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            com.internship.retailmanagement.R.id.profileMenu ->{
                gv.isMyProfile = true
                executeOtherActivity(UserProfileActivity::class.java)
            }
            com.internship.retailmanagement.R.id.changePasswordMenu -> executeOtherActivity(ChangePasswordActivity::class.java)
            com.internship.retailmanagement.R.id.signOutMenu -> Utils.logout(this@ChangeProductActivity)
        }
        return true
    }

    //Go to next activity
    private fun executeOtherActivity(otherActivity: Class<*>) {
        val x = Intent(this@ChangeProductActivity, otherActivity)
        startActivity(x)
    }
}