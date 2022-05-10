package com.internship.retailmanagement.controllers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.internship.retailmanagement.R
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.controllers.adapters.InvoicesAdapter
import com.internship.retailmanagement.databinding.ActivityInvoicesBinding
import com.internship.retailmanagement.dataclasses.InvoiceItem
import com.internship.retailmanagement.services.ApiService
import com.internship.retailmanagement.services.ServiceGenerator
import kotlinx.android.synthetic.main.activity_users.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
            getMyData()
            myRecyclerView.adapter!!.notifyDataSetChanged()
        }

        getMyData()
    }

    //Get invoices from API
    @Synchronized
    private fun getMyData() {
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
                        mAdapter = InvoicesAdapter(invoicesList, { _, id ->""
                            //executeOtherActivity(ChangeUserDataActivity::class.java, id)
                        }, { _, id ->""
                            //executeOtherActivity(UserProfileActivity::class.java, id)
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

    //Save user email in global var to set it in the update page
    private fun executeOtherActivity(otherActivity: Class<*>,
                                     id: Long) {
        gv.userId = id
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
}