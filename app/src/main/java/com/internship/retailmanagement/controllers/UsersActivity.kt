package com.internship.retailmanagement.controllers

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
import com.internship.retailmanagement.controllers.adapters.UsersAdapter
import com.internship.retailmanagement.databinding.ActivityUsersBinding
import com.internship.retailmanagement.dataclasses.users.UserItem
import com.internship.retailmanagement.services.ApiService
import com.internship.retailmanagement.services.ServiceGenerator
import kotlinx.android.synthetic.main.activity_users.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UsersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUsersBinding
    private lateinit var mAdapter: UsersAdapter
    private lateinit var usersList: MutableList<UserItem>
    private lateinit var gv: GlobalVar
    private lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityUsersBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        gv = application as GlobalVar

        usersList = arrayListOf()
        fab = binding.fab

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

        myRecyclerView.adapter = UsersAdapter(usersList, { _, _, _, _, _ -> "" }, { _, _ -> "" })

        //Data update on scroll
        swipeRefreshUsers.setOnRefreshListener {
            //Show data in recycler view
            getUsers()
            myRecyclerView.adapter!!.notifyDataSetChanged()
        }

        getUsers()
    }

    //Get users from API
    @Synchronized
    private fun getUsers() {
        val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)

        val usersCall = serviceGenerator.getUsers()

        usersCall.enqueue(
            object : Callback<MutableList<UserItem>> {
                override fun onResponse(
                    call: Call<MutableList<UserItem>>,
                    response: Response<MutableList<UserItem>>
                ) {
                    if (response.isSuccessful) {
                        usersList.clear()
                        usersList.addAll(response.body()!!.toMutableList())
                        mAdapter = UsersAdapter(usersList, { _, id, storeId, userStatus, userCategory ->
                            executeOtherActivity(ChangeUserDataActivity::class.java, id, storeId, userStatus, userCategory)
                        }, { _, id ->
                            executeOtherActivity(UserProfileActivity::class.java, id, 0, "", "")
                        })
                        mAdapter.notifyDataSetChanged()
                        myRecyclerView.apply {
                            layoutManager = LinearLayoutManager(this@UsersActivity)
                            setHasFixedSize(true)
                            adapter = mAdapter
                        }
                    }
                }

                override fun onFailure(call: Call<MutableList<UserItem>>, t: Throwable) {
                    t.printStackTrace()
                    Log.e("UsersActivity", "Error:" + t.message.toString())
                }
            })
        swipeRefreshUsers.isRefreshing = false
    }

    //Save user email in global var to set it in the update page
    private fun executeOtherActivity(otherActivity: Class<*>,
                                     id: Long, storeId: Long, userStatus: String, userCategory: String) {
        gv.userId = id
        gv.storeId = storeId
        gv.userStatus = userStatus
        gv.userCategory = userCategory
        val x = Intent(this@UsersActivity, otherActivity)
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
            R.id.profileMenu -> executeOtherActivity(UserProfileActivity::class.java, gv.userId!!, gv.storeId!!, gv.userStatus!!, gv.userCategory!!)
            R.id.changePasswordMenu -> executeOtherActivity(ChangePasswordActivity::class.java, gv.userId!!, gv.storeId!!, gv.userStatus!!, gv.userCategory!!)
            R.id.signOutMenu -> null
        }
        return true
    }

    /**
     * When update or create a new user, this activity must "auto refresh" to show immediatly the changes. So the method "onRestart()",
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
