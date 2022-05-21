package com.internship.retailmanagement.dataclasses.invoices

import com.internship.retailmanagement.dataclasses.CashRegisterItem
import com.internship.retailmanagement.dataclasses.users.UserItem

data class InsertInvItem(
    val transaction: String? = null,
    val userId: Long? = null,
    val cashRegisterId: Long? = null,
    val invoicedProducts: Map<String, Int>? = null
)
