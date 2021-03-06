package com.internship.retailmanagement.dataclasses.users

data class InsertUserItem(
    val name: String? = null,
    val email: String? = null,
    val password: String? = null,
    val phone: String? = null,
    val birthDate: String? = null,
    val nif: Long? = null,
    val category: String? = null,
    val status: String? = null,
    val address: String? = null,
    val council: String? = null,
    val zipCode: String? = null,
    val storeId: Long? = null
)
