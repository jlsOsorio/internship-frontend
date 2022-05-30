package com.internship.retailmanagement.dataclasses.users

data class LoginRequest(
    val email: String? = null,
    val password: String? = null
)
