package com.internship.retailmanagement.controllers

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.R
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.controllers.adapters.spinners.IvaSpinnerAdapter
import com.internship.retailmanagement.databinding.ActivityCreateProductBinding
import com.internship.retailmanagement.dataclasses.IvaItem
import com.internship.retailmanagement.dataclasses.products.InsertProductItem
import com.internship.retailmanagement.services.ApiService
import com.internship.retailmanagement.common.ErrorDialog
import com.internship.retailmanagement.common.Utils
import com.internship.retailmanagement.config.SessionManager
import com.internship.retailmanagement.services.ServiceGenerator
import kotlinx.android.synthetic.main.activity_change_product.nameProduct
import kotlinx.android.synthetic.main.activity_create_product.*
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.NumberFormatException
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
    private lateinit var sessionManager: SessionManager

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
        sessionManager = SessionManager(this)

        val defaultValue : ArrayList<String> = arrayListOf("SELECT IVA TAX")

        val defaultAdapter = ArrayAdapter(this@CreateProductActivity, R.layout.support_simple_spinner_dropdown_item, defaultValue)
        ivaValue.adapter = defaultAdapter

        ivaValue.setOnTouchListener { _, _ ->
            getIvaValues()
            false
        }

        create.setOnClickListener{
            createProduct()
        }
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
                        if (response.code() == 401)
                        {
                            val errorMessage = response.errorBody()!!.string()
                            Utils.redirectUnauthorized(this@CreateProductActivity, errorMessage)
                        }
                        else if (response.code() == 403)
                        {
                            val errorMessage = response.errorBody()!!.string()
                            ErrorDialog.setPermissionDialog(this@CreateProductActivity, errorMessage).show()
                        }
                        else if (response.code() >= 400)
                        {
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            val message: String = jsonObject.getString("message")
                            ErrorDialog.setDialog(this@CreateProductActivity, message).show()
                        }
                    }
                }

                override fun onFailure(call: Call<MutableList<IvaItem>>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("CreateProductActivity", "Error:" + t.message.toString())
                }
            })
    }

    private fun setupCustomSpinner() {
        val adapter = IvaSpinnerAdapter(this@CreateProductActivity, ivaList)
        ivaValue.adapter = adapter
    }

    //Create product
    @Synchronized
    private fun createProduct() {
        try {
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
            val productCreate = serviceGenerator.addProduct("Bearer ${sessionManager.fetchAuthToken()}", productInsert)

            productCreate.enqueue(object : Callback<ResponseBody?> {

                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    if (response.isSuccessful) {
                        gv.productName = productInsert.name
                        gv.productStock = productInsert.stock
                        gv.productIva = productInsert.ivaValue
                        gv.productGrossPrice = productInsert.grossPrice
                        Toast.makeText(
                            this@CreateProductActivity,
                            "Product created successfully!",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        if (response.code() == 401)
                        {
                            val errorMessage = response.errorBody()!!.string()
                            Utils.redirectUnauthorized(this@CreateProductActivity, errorMessage)
                        }
                        else if (response.code() == 403)
                        {
                            val errorMessage = response.errorBody()!!.string()
                            ErrorDialog.setPermissionDialog(this@CreateProductActivity, errorMessage).show()
                        }
                        else if (response.code() >= 400)
                        {
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            val message: String = jsonObject.getString("message")
                            ErrorDialog.setDialog(this@CreateProductActivity, message).show()
                        }
                    }

                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    Log.e("CreateProductActivity", "Error:" + t.message.toString())
                }
            }
            )
        }
        catch (e: NumberFormatException)
        {
            ErrorDialog.setDialog(this@CreateProductActivity, "Invalid input!").show()
        }
    }

    /**
     * Override method to generate menu in action bar.
     * @param menu: menu Type.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(com.internship.retailmanagement.R.menu.menu_bar, menu)
        return true
    }

    /**
     * Override method to create conditions for every options of the menu in action bar.
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
            com.internship.retailmanagement.R.id.signOutMenu -> Utils.logout(this@CreateProductActivity)
        }
        return true
    }

    //Go to next activity
    private fun executeOtherActivity(otherActivity: Class<*>) {
        val x = Intent(this@CreateProductActivity, otherActivity)
        startActivity(x)
    }
}