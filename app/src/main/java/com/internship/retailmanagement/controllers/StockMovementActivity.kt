package com.internship.retailmanagement.controllers

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.internship.retailmanagement.R
import com.internship.retailmanagement.common.ErrorDialog
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.common.Utils
import com.internship.retailmanagement.config.SessionManager
import com.internship.retailmanagement.controllers.adapters.StockMovAdapter
import com.internship.retailmanagement.databinding.ActivityStockMovementBinding
import com.internship.retailmanagement.dataclasses.stockmovements.StockMovItem
import com.internship.retailmanagement.services.ApiService
import com.internship.retailmanagement.services.ServiceGenerator
import kotlinx.android.synthetic.main.activity_users.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StockMovementActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStockMovementBinding
    private lateinit var stockMovsList: MutableList<StockMovItem>
    private lateinit var mAdapter: StockMovAdapter
    private lateinit var fab: FloatingActionButton
    private lateinit var gv: GlobalVar
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityStockMovementBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        gv = application as GlobalVar

        stockMovsList = arrayListOf()
        fab = binding.fab
        sessionManager = SessionManager(this)

        fab.visibility = View.INVISIBLE

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

        myRecyclerView.adapter = StockMovAdapter(stockMovsList)

        //Data update on scroll
        swipeRefreshUsers.setOnRefreshListener {
            //Show data in recycler view
            getStockMovements()
            myRecyclerView.adapter!!.notifyDataSetChanged()
        }

        getStockMovements()

        if (gv.userRole == "SUPERVISOR") {
            fab.visibility = View.VISIBLE
            fab.setOnClickListener {
                executeOtherActivity(CreateStockMovementActivity::class.java)
            }
        }
    }

    //Get stock movements from API
    @Synchronized
    private fun getStockMovements() {
        val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)

        val stockMovsCall = serviceGenerator.getStockMovements("Bearer ${sessionManager.fetchAuthToken()}", gv.productId!!)

        stockMovsCall.enqueue(
            object : Callback<MutableList<StockMovItem>> {
                override fun onResponse(
                    call: Call<MutableList<StockMovItem>>,
                    response: Response<MutableList<StockMovItem>>
                ) {
                    if (response.isSuccessful) {
                        stockMovsList.clear()
                        stockMovsList.addAll(response.body()!!.toMutableList())
                        mAdapter = StockMovAdapter(stockMovsList)
                        mAdapter.notifyDataSetChanged()
                        myRecyclerView.apply {
                            layoutManager = LinearLayoutManager(this@StockMovementActivity)
                            setHasFixedSize(true)
                            adapter = mAdapter
                        }
                    }
                    else
                    {
                        if (response.code() == 401)
                        {
                            val errorMessage = response.errorBody()!!.string()
                            Utils.redirectUnauthorized(this@StockMovementActivity, errorMessage)
                        }
                        else if (response.code() == 403)
                        {
                            val errorMessage = response.errorBody()!!.string()
                            ErrorDialog.setPermissionDialog(this@StockMovementActivity, errorMessage).show()
                        }
                        else if (response.code() >= 400)
                        {
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            val message: String = jsonObject.getString("message")
                            ErrorDialog.setDialog(this@StockMovementActivity, message).show()
                        }
                    }
                }

                override fun onFailure(call: Call<MutableList<StockMovItem>>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("StockMovementActivity", "Error:" + t.message.toString())
                }
            })

        swipeRefreshUsers.isRefreshing = false
    }

    /**
     * Override method to generate menu in action bar.
     * @param menu: menu Type.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_bar, menu)
        val searchItems = menu?.findItem(R.id.searchMenu)
        searchItems?.isVisible = true
        val research = searchItems?.actionView as SearchView
        research.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                mAdapter.filter.filter(newText)
                return false
            }
        })
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
            R.id.signOutMenu -> Utils.logout(this@StockMovementActivity)
        }
        return true
    }

    //Go to next activity
    private fun executeOtherActivity(otherActivity: Class<*>) {
        val x = Intent(this@StockMovementActivity, otherActivity)
        startActivity(x)
    }

    /**
     * When update or create a new operating fund, this activity must "auto refresh" to show immediatly the changes. So the method "onRestart()",
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