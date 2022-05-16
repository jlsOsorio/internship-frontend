package com.internship.retailmanagement.dataclasses.stores

data class UpdateStoreItem(
    val address: String? = null,
    val council: String? = null,
    val zipCode: String? = null,
    val contact: String? = null,
    val status: String? = null,
    val numberCashRegisters: Int? = null
)
