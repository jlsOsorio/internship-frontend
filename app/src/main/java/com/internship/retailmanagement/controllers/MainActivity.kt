package com.internship.retailmanagement.controllers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.AppCompatButton
import com.internship.retailmanagement.R
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var gv: GlobalVar
    private lateinit var newOrder: AppCompatButton
    private lateinit var users: AppCompatButton
    private lateinit var products: AppCompatButton
    private lateinit var operatingFunds: AppCompatButton
    private lateinit var invoices: AppCompatButton
    private lateinit var stores: AppCompatButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        gv = application as GlobalVar
        newOrder = binding.newOrder
        users = binding.users
        products = binding.products
        operatingFunds = binding.operatingFunds
        invoices = binding.invoices
        stores = binding.stores

        newOrder.setOnClickListener{
            executeOtherActivity(InvoicesActivity::class.java, 1)
        }

        users.setOnClickListener{
            executeOtherActivity(UsersActivity::class.java, 1)
        }

        products.setOnClickListener{
            executeOtherActivity(ProductsActivity::class.java, 1)
        }

        operatingFunds.setOnClickListener{
            executeOtherActivity(OperatingFundsActivity::class.java, 1)
        }

        invoices.setOnClickListener{
            executeOtherActivity(InvoicesActivity::class.java, 1)
        }

        stores.setOnClickListener{
            executeOtherActivity(StoresActivity::class.java, 1)
        }

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

    /**
     * Method to go to the next activity.
     * @param otherActivity     next activity
     * @param id    global id intended to pass to next activity
     */
    private fun executeOtherActivity(otherActivity: Class<*>,
                                     id: Long) {
        gv.userId = id
        val x = Intent(this@MainActivity, otherActivity)
        startActivity(x)
    }
}