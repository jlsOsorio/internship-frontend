package com.internship.retailmanagement.controllers.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.internship.retailmanagement.R
import com.internship.retailmanagement.common.GlobalVar
import com.internship.retailmanagement.dataclasses.users.UserItem
import kotlinx.android.synthetic.main.user_card.view.*

class UsersAdapter(private var usersList: MutableList<UserItem>, private val gv: GlobalVar, private val editListener: (UserItem, Long, Long, String, String) -> Unit, private val infoListener: (UserItem, Long) -> Unit) :
    RecyclerView.Adapter<UsersAdapter.UserCardViewHolder>(), Filterable {

    val usersListClone: List<UserItem>

    init{
        usersListClone = usersList.toMutableList() //set list clone
    }

    class UserCardViewHolder(itemView: View) :
            RecyclerView.ViewHolder(itemView) {
                private val emailView: TextView = itemView.emailCard
                private val nifView: TextView = itemView.nifCard
                private val statusCheckView: ImageView = itemView.statusCheckCard
                private val statusUncheckView: ImageView = itemView.statusUncheckCard
                private val editView: ImageView = itemView.updateCard

                fun bindView(gv: GlobalVar, userItem: UserItem, editListener: (UserItem, Long, Long, String, String) -> Unit, infoListener: (UserItem, Long) -> Unit) {
                    statusCheckView.visibility = View.INVISIBLE
                    statusUncheckView.visibility = View.INVISIBLE
                    editView.visibility = View.INVISIBLE

                    emailView.text = userItem.email
                    nifView.text = userItem.nif.toString()

                    if (gv.userRole == "SUPERVISOR")
                    {
                        editView.visibility = View.VISIBLE
                        itemView.setOnClickListener{
                            infoListener(userItem, userItem.id!!)
                        }
                    }

                    if (userItem.status == "ACTIVE")
                    {
                        statusCheckView.visibility = View.VISIBLE
                    }
                    else
                    {
                        statusUncheckView.visibility = View.VISIBLE
                    }

                    editView.setOnClickListener{
                        editListener(userItem, userItem.id!!, userItem.store!!.id, userItem.status!!, userItem.category!!) //Go to changing content activity of this specific user
                    }

                }
            }

    //Mandatory methods
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserCardViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.user_card, parent, false
        )
        return UserCardViewHolder(itemView)
    }

    override fun getItemCount() = usersList.size

    override fun onBindViewHolder(holder: UserCardViewHolder, position: Int) {
        return holder.bindView(gv, usersList[position], editListener, infoListener)
    }

    override fun getFilter(): Filter = UserFilter()
    private inner class UserFilter : Filter() {
        @SuppressLint("DefaultLocale")
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = mutableListOf<UserItem>()
            if (constraint == null || constraint.isEmpty())
                filteredList.addAll(usersListClone)
            else {
                val filterPattern = constraint.toString().lowercase().trim()
                for (user in usersListClone) {
                    if (user.email!!.lowercase()
                            .contains(filterPattern) || user.nif.toString()
                            .contains(filterPattern)
                    ) // .contains or .startsWith
                        filteredList.add(user)
                }
            }
            val filterRes = FilterResults()
            filterRes.values = filteredList
            return filterRes
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            usersList.clear()
            usersList.addAll(results?.values as List<UserItem>)
            notifyDataSetChanged()
        }
    }
}