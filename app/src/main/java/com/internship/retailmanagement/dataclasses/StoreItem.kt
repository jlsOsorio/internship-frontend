package com.internship.retailmanagement.dataclasses

data class StoreItem(
    val id: Long? = null,
    val address: String? = null,
    val council: String? = null,
    val zipCode: String? = null,
    val contact: String? = null,
    val status: String? = null,
    val cashRegister: CashRegisterItem? = null
)
