package com.internship.retailmanagement.services

import com.internship.retailmanagement.dataclasses.*
import com.internship.retailmanagement.dataclasses.operatingfunds.InsertOpFundItem
import com.internship.retailmanagement.dataclasses.operatingfunds.OpFundItem
import com.internship.retailmanagement.dataclasses.users.InsertUserItem
import com.internship.retailmanagement.dataclasses.users.UserItem
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path


interface ApiService {

    //////////////// USERS ////////////////

    //All users
    @GET("/users")
    fun getUsers(): Call<MutableList<UserItem>>

    //All users
    @GET("/users")
    suspend fun getUsersProfile(): Response<MutableList<UserItem>>

    //Specific user
    @GET("/users/{id}")
    fun getUser(@Path(value = "id", encoded = false) id: Long): Call<UserItem>

    //Update user
    @PUT("/users/{id}")
    fun updateUser(@Path("id") id: Long?, @Body user: InsertUserItem?): Call<ResponseBody?>

    //////////////// OPERATING FUNDS ////////////////
    //By user
    @GET("/operatingfunds/{userId}")
    fun getOpFunds(@Path(value = "userId", encoded = false) id: Long): Call<MutableList<OpFundItem>>

    //Update user's operating fund
    @PUT("/operatingfunds/{userId}")
    fun updateOpFund(@Path("userId") id: Long?, @Body opFund: InsertOpFundItem?): Call<ResponseBody?>

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


    //////////////// INVOICES ////////////////
    @GET("/invoices")
    fun getInvoices(): Call<MutableList<InvoiceItem>>


    //////////////// CASH REGISTERS ////////////////
    @GET("/cashregisters")
    fun getCashRegisters(): Call<MutableList<CashRegisterItem>>

}