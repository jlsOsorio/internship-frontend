package com.internship.retailmanagement.controllers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import com.internship.retailmanagement.R
import com.internship.retailmanagement.common.ErrorDialog
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.common.Utils
import com.internship.retailmanagement.config.SessionManager
import com.internship.retailmanagement.databinding.ActivityInvoiceDetailsBinding
import com.internship.retailmanagement.dataclasses.invoices.InvoiceItem
import com.internship.retailmanagement.services.ApiService
import com.internship.retailmanagement.services.ServiceGenerator
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat

class InvoiceDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInvoiceDetailsBinding
    private lateinit var gv: GlobalVar
    private lateinit var number: TextView
    private lateinit var transaction: TextView
    private lateinit var employee: TextView
    private lateinit var cashRegister: TextView
    private lateinit var totalNoIva: TextView
    private lateinit var totalIva: TextView
    private lateinit var checkProducts: Button
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityInvoiceDetailsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        gv = application as GlobalVar

        number = binding.numberInvoice
        transaction = binding.transactionInvoice
        employee = binding.userInvoice
        cashRegister = binding.cashRegisterInvoice
        totalNoIva = binding.noIvaInvoice
        totalIva = binding.totalIvaInvoice
        checkProducts = binding.buttonInvoicedProducts
        sessionManager = SessionManager(this)

        getInvoice()

        checkProducts.setOnClickListener{
            executeOtherActivity(InvoicedProductsActivity::class.java)
        }
    }

    //Get invoice from API
    @Synchronized
    private fun getInvoice() {
        val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)
        val invoiceCall = serviceGenerator.getInvoice("Bearer ${sessionManager.fetchAuthToken()}", gv.invoiceNumber!!)

        invoiceCall.enqueue(object : Callback<InvoiceItem> {
            override fun onResponse(
                call: Call<InvoiceItem>,
                response: Response<InvoiceItem>
            ) {
                if (response.isSuccessful) {
                    val df = DecimalFormat("#.##")
                    gv.invProdsList.clear()
                    val responseBody = response.body()!!
                    number.text = responseBody.invoiceNumber.toString()
                    transaction.text = responseBody.transaction
                    employee.text = responseBody.user!!.name
                    cashRegister.text = responseBody.cashRegister!!.id.toString()
                    val totNoIvaRounded = df.format(responseBody.totalNoIva)
                    totalNoIva.text = totNoIvaRounded
                    val totIvaRounded = df.format(responseBody.totalIva)
                    totalIva.text = totIvaRounded
                    gv.invProdsList.addAll(responseBody.invoicedProducts!!.toMutableList())
                }
                else
                {
                    if (response.code() == 401 || response.code() == 403) {
                        val errorMessage = response.errorBody()!!.string()
                        ErrorDialog.setPermissionDialog(this@InvoiceDetailsActivity, errorMessage).show()
                    }
                    else if (response.code() >= 400)
                    {
                        val jsonObject = JSONObject(response.errorBody()!!.string())
                        val message: String = jsonObject.getString("message")
                        ErrorDialog.setDialog(this@InvoiceDetailsActivity, message).show()
                    }
                }
            }

            override fun onFailure(call: Call<InvoiceItem>, t: Throwable) {
                Log.e("UserProfileActivity", "Error:" + t.message.toString())
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
            R.id.profileMenu ->{
                gv.isMyProfile = true
                executeOtherActivity(UserProfileActivity::class.java)
            }
            R.id.changePasswordMenu -> executeOtherActivity(ChangePasswordActivity::class.java)
            R.id.signOutMenu -> Utils.logout(this@InvoiceDetailsActivity)
        }
        return true
    }

    //Go to next activity
    private fun executeOtherActivity(otherActivity: Class<*>) {
        val x = Intent(this@InvoiceDetailsActivity, otherActivity)
        startActivity(x)
    }
}