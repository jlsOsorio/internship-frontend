package com.internship.retailmanagement.controllers

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.internship.retailmanagement.R
import com.internship.retailmanagement.common.ErrorDialog
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.common.Utils
import com.internship.retailmanagement.config.SessionManager
import com.internship.retailmanagement.controllers.adapters.InvoicesAdapter
import com.internship.retailmanagement.databinding.ActivityInvoicesBinding
import com.internship.retailmanagement.dataclasses.invoices.InvProdItem
import com.internship.retailmanagement.dataclasses.invoices.InvoiceItem
import com.internship.retailmanagement.services.ApiService
import com.internship.retailmanagement.services.ServiceGenerator
import kotlinx.android.synthetic.main.activity_users.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class InvoicesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInvoicesBinding
    private lateinit var invoicesList: MutableList<InvoiceItem>
    private lateinit var mAdapter: InvoicesAdapter
    private lateinit var fab: FloatingActionButton
    private lateinit var gv: GlobalVar
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityInvoicesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        invoicesList = arrayListOf()
        fab = binding.fab
        gv = application as GlobalVar
        sessionManager = SessionManager(this)

        /**
         * Hide floating action button while scrolling down. Make it appear when scrolling up.
         */
        myRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy < 0 && !fab.isShown) {
                    fab.show()
                } else if (dy > 0 && fab.isShown) {
                    fab.hide()
                }
            }
        })

        myRecyclerView.adapter = InvoicesAdapter(invoicesList, { _, _ -> "" }, { _, _ -> "" })

        //Data update on scroll
        swipeRefreshUsers.setOnRefreshListener {
            //Show data in recycler view
            getInvoices()
            myRecyclerView.adapter!!.notifyDataSetChanged()
        }

        getInvoices()

        fab.setOnClickListener{
            executeOtherActivity(CreateInvoiceActivity::class.java,0)
        }
    }

    //Get invoices from API
    @Synchronized
    private fun getInvoices() {
        val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)

        val invoicesCall = serviceGenerator.getInvoices("Bearer ${sessionManager.fetchAuthToken()}")

        invoicesCall.enqueue(
            object : Callback<MutableList<InvoiceItem>> {
                override fun onResponse(
                    call: Call<MutableList<InvoiceItem>>,
                    response: Response<MutableList<InvoiceItem>>
                ) {
                    if (response.isSuccessful) {
                        invoicesList.clear()
                        invoicesList.addAll(response.body()!!.toMutableList())
                        gv.fileInvoices = openFileOutput("invoices.txt", Context.MODE_PRIVATE)
                        val outputWriter = OutputStreamWriter(gv.fileInvoices)
                        for (invoice in invoicesList)
                        {
                            outputWriter.write(invoiceTemplate(invoice.invoiceNumber!!,
                                invoice.cashRegister!!.id!!,
                                invoice.user!!.name!!,
                                invoice.user.store!!.address,
                                invoice.user.store.zipCode,
                                invoice.user.store.contact,
                                invoice.totalIva!!,
                                invoice.transaction!!,
                                invoice.invoicedProducts!!))
                            outputWriter.flush()
                        }
                        mAdapter = InvoicesAdapter(invoicesList, { _, id ->
                            executeOtherActivity(InvoiceDetailsActivity::class.java, id)
                        }, { invoice, id ->

                            //Generate pop up dialog to show full bill info from intended one
                            val builder = AlertDialog.Builder(this@InvoicesActivity)
                            builder.setTitle(getString(R.string.invoice))
                            builder.setMessage(invoiceTemplate(id, invoice.cashRegister!!.id!!, invoice.user!!.name!!, invoice.user.store!!.address, invoice.user.store.zipCode, invoice.user.store.contact, invoice.totalIva!!, invoice.transaction!!, invoice.invoicedProducts!!))
                            builder.setPositiveButton("CLOSE") { dialogInterface: DialogInterface, _ ->
                                dialogInterface.cancel()
                            }
                            builder.show()
                        })
                        mAdapter.notifyDataSetChanged()
                        myRecyclerView.apply {
                            layoutManager = LinearLayoutManager(this@InvoicesActivity)
                            setHasFixedSize(true)
                            adapter = mAdapter
                        }
                    }
                    else
                    {
                        if (response.code() == 401)
                        {
                            val errorMessage = response.errorBody()!!.string()
                            Utils.redirectUnauthorized(this@InvoicesActivity, errorMessage)
                        }
                        else if (response.code() == 403)
                        {
                            val errorMessage = response.errorBody()!!.string()
                            ErrorDialog.setPermissionDialog(this@InvoicesActivity, errorMessage).show()
                        }
                        else if (response.code() >= 400)
                        {
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            val message: String = jsonObject.getString("message")
                            ErrorDialog.setDialog(this@InvoicesActivity, message).show()
                        }
                    }
                }

                override fun onFailure(call: Call<MutableList<InvoiceItem>>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("UsersActivity", "Error:" + t.message.toString())
                }
            })
        swipeRefreshUsers.isRefreshing = false
    }

    private fun invoiceTemplate(invNumber: Long,
                                cashRegister: Long,
                                employee: String,
                                storeAddress: String,
                                storeZipCode: String,
                                storeContact: String,
                                invTotal: Double,
                                transaction: String,
                                responseProds: MutableList<InvProdItem>) : String {

        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.US)
        val currentDate = sdf.format(Date())
        val df = DecimalFormat("#.##")
        val invTotalRounded = df.format(invTotal)
        val payment = Random.nextDouble(invTotal, invTotal + 20)
        val receivedRounded = df.format(payment)
        val changeRounded = df.format(payment-invTotal)
        val res = StringBuilder()

        res.append("============================= NEW INVOICE =============================\n")
        res.append("\nWINRETAIL")
        res.append("\nRua da Winretail, 20")
        res.append("\n1234-123")
        res.append(" Vila Nova de Gaia")
        res.append("\n102938475")
        res.append("\n")
        res.append("------------------------------------------")
        res.append("\nBILL                                $invNumber")
        res.append("\n------------------------------------------")
        res.append("\nMOMENT                       CASH REGISTER")
        res.append("\n$currentDate                     $cashRegister")
        res.append("\n------------------------------------------")
        res.append("\n")
        res.append("\nNIF: -")
        res.append("\nNAME: FINAL CUSTOMER")
        res.append("\n")
        res.append("\nPRODUCTS")
        res.append("\n------------------------------------------")
        res.append("\n          CODE - PRODUCT NAME")
        res.append("\nQTY X PRICE                  IVA(%)  TOTAL")
        res.append("\n------------------------------------------")
        for (prod in responseProds) {
            val prodValRounded = df.format(prod.subTotalIva?.div(prod.quantity!!))
            val subTotalRounded = df.format(prod.subTotalIva)
            res.append("\n              ${prod.productId} - ${prod.productName}")
            res.append("\n${prod.quantity} X $prodValRounded                       ${prod.ivaValue}    ${subTotalRounded}\n")
        }
        res.append("------------------------------------------")
        res.append("\nTRANSACTION                       $transaction")
        res.append("\n------------------------------------------")
        res.append("\nTOTAL                                $invTotalRounded")
        if (transaction == "DEBIT")
        {
            res.append("\nRECEIVED                             $receivedRounded")
            res.append("\nCHANGE                               $changeRounded")
        }
        res.append("\n")
        res.append("RESPONSIBLE                         $employee")
        res.append("\n------------------------------------------")
        res.append("\n                  STORE")
        res.append("\nADDRESS                       $storeAddress")
        res.append("\nZIP CODE                          $storeZipCode")
        res.append("\nCONTACT                          $storeContact")
        res.append("\n------------------------------------------")
        res.append("\nwinretail@winretail.com")
        res.append("\n------------------------------------------\n\n\n")

        return res.toString()
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
                executeOtherActivity(UserProfileActivity::class.java, 0)
            }
            R.id.changePasswordMenu -> executeOtherActivity(ChangePasswordActivity::class.java, 0)
            R.id.signOutMenu -> Utils.logout(this@InvoicesActivity)
        }
        return true
    }

    /**
     * Method to go to the next activity.
     * @param otherActivity     next activity
     * @param id    global id intended to pass to next activity
     */
    private fun executeOtherActivity(otherActivity: Class<*>,
                                     id: Long) {
        gv.invoiceNumber = id
        val x = Intent(this@InvoicesActivity, otherActivity)
        startActivity(x)
    }

    /**
     * When update or create a new invoice, this activity must "auto refresh" to show immediatly the changes. So the method "onRestart()",
     * which is a method that is called an activity is finished and the app goes back to the previous activity, was rewritten this way.
     */
    override fun onRestart() {
        super.onRestart()
        finish()
        overridePendingTransition(0, 0)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }
}