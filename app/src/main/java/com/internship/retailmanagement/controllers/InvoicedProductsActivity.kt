package com.internship.retailmanagement.controllers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.internship.retailmanagement.R
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.common.Utils
import com.internship.retailmanagement.controllers.adapters.InvProdAdapter
import com.internship.retailmanagement.databinding.ActivityInvoicedProductsBinding
import com.internship.retailmanagement.dataclasses.invoices.InvProdItem
import kotlinx.android.synthetic.main.activity_users.*

class InvoicedProductsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInvoicedProductsBinding
    private lateinit var productsList: MutableList<InvProdItem>
    private lateinit var gv: GlobalVar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityInvoicedProductsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        productsList = arrayListOf()
        gv = application as GlobalVar

        myRecyclerView.adapter = InvProdAdapter(gv.invProdsList)

        //Data update on scroll
        swipeRefreshUsers.setOnRefreshListener {
            //Show data in recycler view
            myRecyclerView.adapter!!.notifyDataSetChanged()
            swipeRefreshUsers.isRefreshing = false
        }
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
            R.id.signOutMenu -> Utils.logout(this@InvoicedProductsActivity)
        }
        return true
    }

    //Go to next activity
    private fun executeOtherActivity(otherActivity: Class<*>) {
        val x = Intent(this@InvoicedProductsActivity, otherActivity)
        startActivity(x)
    }
}