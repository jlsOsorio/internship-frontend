package com.internship.retailmanagement.controllers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.internship.retailmanagement.R
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.controllers.adapters.InvoicesAdapter
import com.internship.retailmanagement.databinding.ActivityInvoicesBinding
import com.internship.retailmanagement.dataclasses.invoices.InvProdItem
import com.internship.retailmanagement.dataclasses.invoices.InvoiceItem
import com.internship.retailmanagement.services.ApiService
import com.internship.retailmanagement.services.ServiceGenerator
import kotlinx.android.synthetic.main.activity_users.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import java.lang.StringBuilder
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityInvoicesBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        invoicesList = arrayListOf()
        fab = binding.fab
        gv = application as GlobalVar

        gv.fileInvoices = openFileOutput("data.txt", Context.MODE_APPEND)


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

        myRecyclerView.adapter = InvoicesAdapter(invoicesList, { _, _ -> "" }, { _, _ -> "" }, { _, _ -> "" })

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

        val invoicesCall = serviceGenerator.getInvoices()

        invoicesCall.enqueue(
            object : Callback<MutableList<InvoiceItem>> {
                override fun onResponse(
                    call: Call<MutableList<InvoiceItem>>,
                    response: Response<MutableList<InvoiceItem>>
                ) {
                    if (response.isSuccessful) {
                        invoicesList.clear()
                        invoicesList.addAll(response.body()!!.toMutableList())
                        val outputWriter = OutputStreamWriter(gv.fileInvoices)
                        for (invoice in invoicesList)
                        {
                            val payment = Random.nextDouble(invoice.totalIva!!, invoice.totalIva + 100)
                            outputWriter.append(invoiceTemplate(invoice.invoiceNumber!!,
                                invoice.cashRegister!!.id!!,
                                invoice.user!!.name!!,
                                invoice.user.store!!.address,
                                invoice.user.store.zipCode,
                                invoice.user.store.contact,
                                invoice.totalIva, payment,
                                invoice.invoicedProducts!!))
                            outputWriter.flush()
                        }
                        /*outputWriter.write("ESTE É O PRIMEIRO TESTE COM A FACTURA COM O NÚMERO 1231032103")
                        outputWriter.append("\nE ESTA É UMA STRING ADICIONAL!")
                        outputWriter.flush()*/
                        //outputWriter.close()
                        mAdapter = InvoicesAdapter(invoicesList, { _, id ->""
                            //executeOtherActivity(ChangeUserDataActivity::class.java, id)
                        }, { _, id ->""
                            executeOtherActivity(InvoiceDetailsActivity::class.java, id)
                        }, { _, id ->
                            /*val outputWriter = OutputStreamWriter(gv.fileInvoices)
                            outputWriter.append("\nMAIS UMA!!")
                            outputWriter.flush()
                            Toast.makeText(this@InvoicesActivity, "File saved successfully!", Toast.LENGTH_SHORT).show()


                            val fileOutputStream = FileOutputStream("data.txt", true)
                            val outputWriter = OutputStreamWriter(fileOutputStream)
                            outputWriter.append("\nMAIS UMA!!!")
                            outputWriter.close()
                            Toast.makeText(this@InvoicesActivity, "Text added successfully!", Toast.LENGTH_SHORT).show()
                            */
                        })
                        mAdapter.notifyDataSetChanged()
                        myRecyclerView.apply {
                            layoutManager = LinearLayoutManager(this@InvoicesActivity)
                            setHasFixedSize(true)
                            adapter = mAdapter
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

    private fun invoiceTemplate(invNumber: Long,
                                cashRegister: Long,
                                employee: String,
                                storeAddress: String,
                                storeZipCode: String,
                                storeContact: String,
                                invTotal: Double,
                                received: Double,
                                responseProds: MutableList<InvProdItem>) : String {

        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
        val currentDate = sdf.format(Date())
        val df = DecimalFormat("#.##")
        val invTotalRounded = df.format(invTotal)
        val receivedRounded = df.format(received)
        val changeRounded = df.format(received-invTotal)
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
            res.append("\n              ${prod.id} - ${prod.productName}")
            res.append("\n${prod.quantity} X $prodValRounded                       ${prod.ivaValue}    ${subTotalRounded}\n")
        }
        res.append("------------------------------------------")
        res.append("\nTOTAL                                $invTotalRounded")
        res.append("\nRECEIVED                             $receivedRounded")
        res.append("\nCHANGE                               $changeRounded")
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
}