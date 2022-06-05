package com.internship.retailmanagement.controllers

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.internship.retailmanagement.R
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.common.Utils
import com.internship.retailmanagement.controllers.adapters.OrderProdAdapter
import com.internship.retailmanagement.databinding.ActivityOrderProductsBinding
import com.internship.retailmanagement.dataclasses.invoices.OrderProdItem
import com.internship.retailmanagement.config.SwipeToDeleteCallback
import kotlinx.android.synthetic.main.activity_order_products.*
import java.util.ArrayList

class OrderProductsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderProductsBinding
    private lateinit var gv: GlobalVar
    private lateinit var quantity: EditText
    private lateinit var add: FloatingActionButton
    private lateinit var confirm: AppCompatButton
    private lateinit var cancel: AppCompatButton
    private lateinit var mAdapter: OrderProdAdapter
    private lateinit var prodsName: Spinner

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.binding = ActivityOrderProductsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        gv = application as GlobalVar
        var name = ""
        quantity = binding.productQuantity
        add = binding.addFab
        confirm = binding.buttonConfirm
        cancel = binding.buttonCancel
        prodsName = binding.productsName

        val defaultValue : ArrayList<String> = arrayListOf("SELECT PRODUCT")

        val defaultAdapter = ArrayAdapter(this@OrderProductsActivity, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, defaultValue)
        prodsName.adapter = defaultAdapter

        prodsName.setOnTouchListener { _, _ ->
            val namesAdapter = ArrayAdapter(this@OrderProductsActivity, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, gv.prodsNames)
            prodsName.adapter = namesAdapter
            false
        }

        prodsName.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                name = gv.prodsNames[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        mAdapter = OrderProdAdapter(gv.prodsList)
        myRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@OrderProductsActivity)
            adapter = mAdapter
        }

        //Remove item from list swiping left or right
        val swipeToDeleteCallback = object : SwipeToDeleteCallback() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                gv.prodsList.removeAt(position)
                myRecyclerView.adapter?.notifyItemRemoved(position)
            }

        }

        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(myRecyclerView)

        add.setOnClickListener{
            val orderItem = OrderProdItem(name, quantity.text.toString().toInt())
            gv.prodsList.add(orderItem)
            mAdapter.notifyDataSetChanged()
        }

        confirm.setOnClickListener{
            finish()
        }

        cancel.setOnClickListener{
            gv.prodsList.clear()
            finish()
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
            R.id.profileMenu ->{
                gv.isMyProfile = true
                executeOtherActivity(UserProfileActivity::class.java)
            }
            R.id.changePasswordMenu -> executeOtherActivity(ChangePasswordActivity::class.java)
            R.id.signOutMenu -> Utils.logout(this@OrderProductsActivity)
        }
        return true
    }

    //Go to next activity
    private fun executeOtherActivity(otherActivity: Class<*>) {
        val x = Intent(this@OrderProductsActivity, otherActivity)
        startActivity(x)
    }
}