package com.internship.retailmanagement.dataclasses.users

data class LoginResponse(
    val id: Long? = null,
    val email: String? = null,
    val category: String? = null,
    val token: String? = null
)
