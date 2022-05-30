package com.internship.retailmanagement.controllers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.AppCompatButton
import com.auth0.android.jwt.Claim
import com.auth0.android.jwt.JWT
import com.internship.retailmanagement.R
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.common.Utils
import com.internship.retailmanagement.config.SessionManager
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
    private lateinit var sessionManager: SessionManager

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
        sessionManager = SessionManager(this)

        var jwt = JWT(sessionManager.fetchAuthToken()!!)
        gv.userId = jwt.getClaim("id").asLong()
        gv.userRole = jwt.getClaim("category").asString()
        gv.emailLoggedIn = jwt.getClaim("email").asString()
        gv.storeUserLogged = jwt.getClaim("store").asLong()

        newOrder.setOnClickListener{
            executeOtherActivity(CreateInvoiceActivity::class.java)
        }

        users.setOnClickListener{
            executeOtherActivity(UsersActivity::class.java)
        }

        products.setOnClickListener{
            executeOtherActivity(ProductsActivity::class.java)
        }

        operatingFunds.setOnClickListener{
            executeOtherActivity(OperatingFundsActivity::class.java)
        }

        invoices.setOnClickListener{
            executeOtherActivity(InvoicesActivity::class.java)
        }

        stores.setOnClickListener{
            executeOtherActivity(StoresActivity::class.java)
        }
    }

    /*//Get user from API
    @Synchronized
    private fun getUser() {
        val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)
        val userCall = serviceGenerator.getUser(1)

        userCall.enqueue(object : Callback<UserItem> {
            override fun onResponse(
                call: Call<UserItem>,
                response: Response<UserItem>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()!!
                    gv.emailLoggedIn = responseBody.email
                    gv.storeUserLogged = responseBody.store!!.id
                }


            }

            override fun onFailure(call: Call<UserItem>, t: Throwable) {
                Log.e("MainActivity", "Error:" + t.message.toString())
            }
        })
    }*/


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
            R.id.signOutMenu -> Utils.logout(this@MainActivity)
        }
        return true
    }

    //Go to next activity
    private fun executeOtherActivity(otherActivity: Class<*>) {
        val x = Intent(this@MainActivity, otherActivity)
        startActivity(x)
    }
}