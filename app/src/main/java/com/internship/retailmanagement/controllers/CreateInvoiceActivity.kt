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
import androidx.appcompat.widget.AppCompatButton
import com.internship.retailmanagement.R
import com.internship.retailmanagement.common.ErrorDialog
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.common.Utils
import com.internship.retailmanagement.config.SessionManager
import com.internship.retailmanagement.controllers.adapters.spinners.CRSpinnerAdapter
import com.internship.retailmanagement.databinding.ActivityCreateInvoiceBinding
import com.internship.retailmanagement.dataclasses.CashRegisterItem
import com.internship.retailmanagement.dataclasses.invoices.InsertInvItem
import com.internship.retailmanagement.dataclasses.invoices.InvProdItem
import com.internship.retailmanagement.dataclasses.products.ProductItem
import com.internship.retailmanagement.dataclasses.stores.StoreItem
import com.internship.retailmanagement.services.ApiService
import com.internship.retailmanagement.services.ServiceGenerator
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class CreateInvoiceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateInvoiceBinding
    private lateinit var gv: GlobalVar
    private lateinit var userInvoice: EditText
    private lateinit var cashRegister: Spinner
    private lateinit var cashRegList: MutableList<CashRegisterItem>
    private lateinit var cashRegisterItem: CashRegisterItem
    private lateinit var transactions: Spinner
    private lateinit var orderProducts: AppCompatButton
    private lateinit var prodsList: MutableList<InvProdItem>
    private lateinit var checkInv: AppCompatButton
    private lateinit var sessionManager: SessionManager

    @SuppressLint("ClickableViewAccessibility", "ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityCreateInvoiceBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        gv = application as GlobalVar
        userInvoice = binding.employeeInvoice
        cashRegister = binding.cashRegisterInvoice
        transactions = binding.transactionInvoice
        orderProducts = binding.buttonInvoicedProducts
        checkInv = binding.buttonCreate
        cashRegList = arrayListOf()
        prodsList = arrayListOf()
        cashRegisterItem = CashRegisterItem(0)
        gv.invCashRegister = null
        sessionManager = SessionManager(this)

        userInvoice.setText(gv.emailLoggedIn)

        val defaultValue: ArrayList<String> = arrayListOf("SELECT CASH REGISTER")

        val defaultAdapter = ArrayAdapter(
            this@CreateInvoiceActivity,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            defaultValue
        )
        cashRegister.adapter = defaultAdapter

        cashRegister.setOnTouchListener { _, _ ->
            getStore()
            false
        }

        val defaultTransValue: ArrayList<String> = arrayListOf("SELECT TRANSACTION TYPE")
        val transArr: ArrayList<String> = arrayListOf("DEBIT", "CREDIT")

        val defaultTransAdapter = ArrayAdapter(
            this@CreateInvoiceActivity,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
            defaultTransValue
        )
        transactions.adapter = defaultTransAdapter

        transactions.setOnTouchListener { _, _ ->
            val transAdapter = ArrayAdapter(
                this@CreateInvoiceActivity,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                transArr
            )
            transactions.adapter = transAdapter
            false
        }

        transactions.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                gv.typeMovement = transArr[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        getProducts()

        orderProducts.setOnClickListener {
            executeOtherActivity(OrderProductsActivity::class.java)
        }

        checkInv.setOnClickListener {
            gv.mapProds = gv.prodsList.associateBy({ it.productName!! }, { it.quantity!! }).toMutableMap()
            createInvoice()
            gv.prodsList.clear()
        }
    }

    private fun setupCustomSpinner() {
        val adapter = CRSpinnerAdapter(this@CreateInvoiceActivity, cashRegList)
        cashRegister.adapter = adapter
    }

    //Get store from API
    @Synchronized
    private fun getStore() {
        val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)
        val storeCall = serviceGenerator.getStore("Bearer ${sessionManager.fetchAuthToken()}", gv.storeUserLogged!!)

        storeCall.enqueue(object : Callback<StoreItem> {
            override fun onResponse(
                call: Call<StoreItem>,
                response: Response<StoreItem>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()!!
                    cashRegList.clear()
                    cashRegList.addAll(responseBody.cashRegisters!!.toMutableList())
                        setupCustomSpinner()
                        cashRegister.onItemSelectedListener = object:
                            AdapterView.OnItemSelectedListener {

                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                val item = parent!!.selectedItem as CashRegisterItem
                                cashRegisterItem = item
                                gv.invCashRegister = cashRegList[position].id
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
                        Utils.redirectUnauthorized(this@CreateInvoiceActivity, errorMessage)
                    }
                    else if (response.code() == 403)
                    {
                        val errorMessage = response.errorBody()!!.string()
                        ErrorDialog.setPermissionDialog(this@CreateInvoiceActivity, errorMessage).show()
                    }
                    else if (response.code() >= 400)
                    {
                        val jsonObject = JSONObject(response.errorBody()!!.string())
                        val message: String = jsonObject.getString("message")
                        ErrorDialog.setDialog(this@CreateInvoiceActivity, message).show()
                    }
                }
            }

            override fun onFailure(call: Call<StoreItem>, t: Throwable) {
                Log.e("CreateInvoiceActivity", "Error:" + t.message.toString())
            }
        })
    }

    //Get products from API
    @Synchronized
    private fun getProducts() {
        val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)

        val productsCall = serviceGenerator.getProducts("Bearer ${sessionManager.fetchAuthToken()}")

        productsCall.enqueue(
            object : Callback<MutableList<ProductItem>> {
                override fun onResponse(
                    call: Call<MutableList<ProductItem>>,
                    response: Response<MutableList<ProductItem>>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()!!
                        gv.prodsNames.clear()

                        responseBody.forEach {
                            gv.prodsNames.add(it.name!!)
                        }
                    }
                    else
                    {
                        if (response.code() == 401)
                        {
                            val errorMessage = response.errorBody()!!.string()
                            Utils.redirectUnauthorized(this@CreateInvoiceActivity, errorMessage)
                        }
                        else if (response.code() == 403)
                        {
                            val errorMessage = response.errorBody()!!.string()
                            ErrorDialog.setPermissionDialog(this@CreateInvoiceActivity, errorMessage).show()
                        }
                        else if (response.code() >= 400)
                        {
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            val message: String = jsonObject.getString("message")
                            ErrorDialog.setDialog(this@CreateInvoiceActivity, message).show()
                        }
                    }
                }

                override fun onFailure(call: Call<MutableList<ProductItem>>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("ProductsActivity", "Error:" + t.message.toString())
                }
            })
    }

    //Create invoice
    @Synchronized
    private fun createInvoice() {

        val invInsert = InsertInvItem(
            gv.typeMovement,
            gv.userId,
            gv.invCashRegister,
            gv.mapProds
        )

        val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)
        val invoiceAdd = serviceGenerator.addInvoice("Bearer ${sessionManager.fetchAuthToken()}", invInsert)

        invoiceAdd.enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful)
                {
                    Toast.makeText(this@CreateInvoiceActivity, "Invoice created successfully!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                else
                {
                    if (response.code() == 401)
                    {
                        val errorMessage = response.errorBody()!!.string()
                        Utils.redirectUnauthorized(this@CreateInvoiceActivity, errorMessage)
                    }
                    else if (response.code() == 403)
                    {
                        val errorMessage = response.errorBody()!!.string()
                        ErrorDialog.setPermissionDialog(this@CreateInvoiceActivity, errorMessage).show()
                    }
                    else if (response.code() >= 400)
                    {
                        val jsonObject = JSONObject(response.errorBody()!!.string())
                        val message: String = jsonObject.getString("message")
                        ErrorDialog.setDialog(this@CreateInvoiceActivity, message).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("CreateOpFundActivity", "Error:" + t.message.toString())
            }
        }
        )
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
            R.id.signOutMenu -> Utils.logout(this@CreateInvoiceActivity)
        }
        return true
    }

    //Go to next activity
    private fun executeOtherActivity(otherActivity: Class<*>) {
        val x = Intent(this@CreateInvoiceActivity, otherActivity)
        startActivity(x)
    }
}