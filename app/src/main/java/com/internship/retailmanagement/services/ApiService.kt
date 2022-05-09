package com.internship.retailmanagement.services

import com.internship.retailmanagement.controllers.ChangeUserDataActivity
import com.internship.retailmanagement.dataclasses.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
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
    fun updateUser(@Path("id") id: Long?, @Body user: UserItem?): Call<ResponseBody?>

    //////////////// OPERATING FUNDS ////////////////
    //By user
    @GET("/operatingfunds/{userId}")
    fun getOpFunds(@Path(value = "userId", encoded = false) id: Long): Call<MutableList<OpFundItem>>

    //////////////// STORES ////////////////
    //All stores
    @GET("/stores")
    fun getStores(): Call<MutableList<StoreItem>>

    //Specific store
    @GET("/stores/{id}")
    fun getStore(@Path(value = "id", encoded = false) id: Long): Call<StoreItem>


    //////////////// PRODUCTS ////////////////
    //All products
    @GET("/products")
    fun getProducts(): Call<MutableList<ProductItem>>

    //////////////// STOCK MOVEMENTS ////////////////
    //By product
    @GET("/stockmovements/{productId}")
    fun getStockMovements(@Path(value = "id", encoded = false) id: Long): Call<StockMovItem>

}