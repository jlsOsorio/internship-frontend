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
import com.internship.retailmanagement.dataclasses.UserItem
import kotlinx.android.synthetic.main.user_card.view.*

class UsersAdapter(private var usersList: MutableList<UserItem>, private val clickListener: (UserItem, String) -> Unit) :
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

                fun bindView(userItem: UserItem, clickListener: (UserItem, String) -> Unit) {
                    statusCheckView.visibility = View.INVISIBLE
                    statusUncheckView.visibility = View.INVISIBLE

                    emailView.text = userItem.email
                    nifView.text = userItem.nif.toString()
                    if (userItem.status == "ACTIVE")
                    {
                        statusUncheckView.visibility = View.VISIBLE
                    }
                    else
                    {
                        statusCheckView.visibility = View.VISIBLE
                    }
                    editView.setOnClickListener{
                        clickListener(userItem, userItem.email!!) //Go to changing content activity of this specific user
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
        return holder.bindView(usersList[position], clickListener)
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