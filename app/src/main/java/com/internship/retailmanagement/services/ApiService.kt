package com.internship.retailmanagement.services

import com.internship.retailmanagement.dataclasses.UserItem
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {

    //All users
    @GET("/users")
    fun getUsers(): Call<MutableList<UserItem>>
}