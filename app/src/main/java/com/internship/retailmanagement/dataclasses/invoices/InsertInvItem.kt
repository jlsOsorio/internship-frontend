package com.internship.retailmanagement.dataclasses.invoices

data class InsertInvItem(
    val transaction: String? = null,
    val userId: Long? = null,
    val cashRegisterId: Long? = null,
    val invoicedProducts: Map<String, Int>? = null
)
