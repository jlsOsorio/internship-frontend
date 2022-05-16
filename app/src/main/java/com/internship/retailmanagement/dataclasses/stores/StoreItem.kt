package com.internship.retailmanagement.dataclasses.stores

import com.internship.retailmanagement.dataclasses.CashRegisterItem

data class StoreItem(
    val id: Long? = null,
    val address: String? = null,
    val council: String? = null,
    val zipCode: String? = null,
    val contact: String? = null,
    val status: String? = null,
    val cashRegisters: MutableList<CashRegisterItem>? = null
)
