package com.internship.retailmanagement.services

import com.internship.retailmanagement.dataclasses.UserItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    //////////////// USERS ////////////////
    
    //All users
    @GET("/users")
    fun getUsers(): Call<MutableList<UserItem>>

    //Specific user
    @GET("/users/{id}")
    fun getUser(@Path(value = "id", encoded = false) id: Long): Call<UserItem>

    //Update user
    @PUT("/users/{id}")
    fun editUser(@Path(value = "id", encoded = false) id: Long): Call<UserItem>
}