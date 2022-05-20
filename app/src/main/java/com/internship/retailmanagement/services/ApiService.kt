package com.internship.retailmanagement.services

import com.internship.retailmanagement.dataclasses.*
import com.internship.retailmanagement.dataclasses.operatingfunds.InsertOpFundItem
import com.internship.retailmanagement.dataclasses.operatingfunds.OpFundItem
import com.internship.retailmanagement.dataclasses.products.InsertProductItem
import com.internship.retailmanagement.dataclasses.products.ProductItem
import com.internship.retailmanagement.dataclasses.products.UpdateProductItem
import com.internship.retailmanagement.dataclasses.stockmovements.InsertStockMovItem
import com.internship.retailmanagement.dataclasses.stockmovements.StockMovItem
import com.internship.retailmanagement.dataclasses.stores.StoreItem
import com.internship.retailmanagement.dataclasses.stores.UpdateStoreItem
import com.internship.retailmanagement.dataclasses.users.InsertUserItem
import com.internship.retailmanagement.dataclasses.users.UserItem
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


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

    //Post user operating fund
    @POST("/operatingfunds/{userId}")
    fun addOperatingFund(@Path("userId") id: Long?, @Body opFund: InsertOpFundItem): Call<ResponseBody>

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

    //Create store
    @POST("/stores")
    fun addStore(@Body store: UpdateStoreItem): Call<ResponseBody>

    //Update store
    @PUT("/stores/{id}")
    fun updateStore(@Path("id") id: Long?, @Body store: UpdateStoreItem?): Call<ResponseBody?>


    //////////////// PRODUCTS ////////////////
    //All products
    @GET("/products")
    fun getProducts(): Call<MutableList<ProductItem>>

    //Create product
    @POST("/products")
    fun addProduct(@Body opFund: InsertProductItem): Call<ResponseBody>

    //Update product
    @PUT("/products/{id}")
    fun updateProduct(@Path("id") id: Long?, @Body product: UpdateProductItem?): Call<ResponseBody?>

    //////////////// STOCK MOVEMENTS ////////////////
    //By product
    @GET("/stockmovements/{productId}")
    fun getStockMovements(@Path(value = "productId", encoded = false) id: Long): Call<MutableList<StockMovItem>>

    //Create by product
    @POST("/stockmovements/{productId}")
    fun addStockMovement(@Path(value = "productId", encoded = false) id: Long, @Body stockMov: InsertStockMovItem): Call<ResponseBody>

    //////////////// IVA ////////////////
    @GET("/iva")
    fun getIvaValues(): Call<MutableList<IvaItem>>


    //////////////// INVOICES ////////////////
    //All invoices
    @GET("/invoices")
    fun getInvoices(): Call<MutableList<InvoiceItem>>

    //Specific invoice
    @GET("/invoices/{id}")
    fun getInvoice(@Path(value = "id", encoded = false) id: Long): Call<InvoiceItem>

    //////////////// INVOICED PRODUCTS ////////////////
    //All invoiced products
    @GET("/invoicedproducts")
    fun getInvoicedProducts(): Call<MutableList<InvProdItem>>

    //////////////// CASH REGISTERS ////////////////
    //All cash registers
    @GET("/cashregisters")
    fun getCashRegisters(): Call<MutableList<CashRegisterItem>>

}