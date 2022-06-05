package com.internship.retailmanagement.controllers

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.internship.retailmanagement.R
import com.internship.retailmanagement.common.ErrorDialog
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.common.Utils
import com.internship.retailmanagement.config.SessionManager
import com.internship.retailmanagement.controllers.adapters.UsersAdapter
import com.internship.retailmanagement.controllers.register.FirstRegisterActivity
import com.internship.retailmanagement.databinding.ActivityUsersBinding
import com.internship.retailmanagement.dataclasses.users.UserItem
import com.internship.retailmanagement.services.ApiService
import com.internship.retailmanagement.services.ServiceGenerator
import kotlinx.android.synthetic.main.activity_users.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UsersActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUsersBinding
    private lateinit var mAdapter: UsersAdapter
    private lateinit var usersList: MutableList<UserItem>
    private lateinit var gv: GlobalVar
    private lateinit var fab: FloatingActionButton
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityUsersBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        gv = application as GlobalVar

        usersList = arrayListOf()
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

        myRecyclerView.adapter = UsersAdapter(usersList, gv, { _, _, _, _, _ -> "" }, { _, _ -> "" })

        //Data update on scroll
        swipeRefreshUsers.setOnRefreshListener {
            //Show data in recycler view
            getUsers()
            myRecyclerView.adapter!!.notifyDataSetChanged()
        }

        getUsers()

        if (gv.userRole == "SUPERVISOR")
        {
            fab.visibility = View.VISIBLE
            fab.setOnClickListener{
                executeOtherActivity(FirstRegisterActivity::class.java)
            }
        }
    }

    //Get users from API
    @Synchronized
    private fun getUsers() {
        val serviceGenerator = ServiceGenerator.buildService(ApiService::class.java)

        val usersCall = serviceGenerator.getUsers("Bearer ${sessionManager.fetchAuthToken()}")

        usersCall.enqueue(
            object : Callback<MutableList<UserItem>> {
                override fun onResponse(
                    call: Call<MutableList<UserItem>>,
                    response: Response<MutableList<UserItem>>
                ) {
                    if (response.isSuccessful) {
                        usersList.clear()
                        usersList.addAll(response.body()!!.toMutableList())
                        mAdapter = UsersAdapter(usersList, gv, { _, id, storeId, userStatus, userCategory ->
                            gv.userId = id
                            gv.storeId = storeId
                            gv.userStatus = userStatus
                            gv.userCategory = userCategory
                            executeOtherActivity(ChangeUserDataActivity::class.java)
                        }, { _, id ->
                            gv.userId = id
                            executeOtherActivity(UserProfileActivity::class.java)
                        })
                        mAdapter.notifyDataSetChanged()
                        myRecyclerView.apply {
                            layoutManager = LinearLayoutManager(this@UsersActivity)
                            setHasFixedSize(true)
                            adapter = mAdapter
                        }
                    }
                    else
                    {
                        if (response.code() == 401)
                        {
                            val errorMessage = response.errorBody()!!.string()
                            Utils.redirectUnauthorized(this@UsersActivity, errorMessage)
                        }
                        else if (response.code() == 403)
                        {
                            val errorMessage = response.errorBody()!!.string()
                            ErrorDialog.setPermissionDialog(this@UsersActivity, errorMessage).show()
                        }
                        else if (response.code() >= 400)
                        {
                            val jsonObject = JSONObject(response.errorBody()!!.string())
                            val message: String = jsonObject.getString("message")
                            ErrorDialog.setDialog(this@UsersActivity, message).show()
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
            R.id.signOutMenu -> Utils.logout(this@UsersActivity)
        }
        return true
    }

    //Go to next activity
    private fun executeOtherActivity(otherActivity: Class<*>) {
        val x = Intent(this@UsersActivity, otherActivity)
        startActivity(x)
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
