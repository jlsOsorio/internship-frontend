package com.internship.retailmanagement.services

import com.internship.retailmanagement.dataclasses.*
import com.internship.retailmanagement.dataclasses.invoices.InsertInvItem
import com.internship.retailmanagement.dataclasses.invoices.InvProdItem
import com.internship.retailmanagement.dataclasses.invoices.InvoiceItem
import com.internship.retailmanagement.dataclasses.operatingfunds.InsertOpFundItem
import com.internship.retailmanagement.dataclasses.operatingfunds.OpFundItem
import com.internship.retailmanagement.dataclasses.products.InsertProductItem
import com.internship.retailmanagement.dataclasses.products.ProductItem
import com.internship.retailmanagement.dataclasses.products.UpdateProductItem
import com.internship.retailmanagement.dataclasses.stockmovements.InsertStockMovItem
import com.internship.retailmanagement.dataclasses.stockmovements.StockMovItem
import com.internship.retailmanagement.dataclasses.stores.StoreItem
import com.internship.retailmanagement.dataclasses.stores.UpdateStoreItem
import com.internship.retailmanagement.dataclasses.users.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface ApiService {

    //////////////// USERS ////////////////

    //All users
    @GET("/users")
    fun getUsers(@Header("Authorization") token: String): Call<MutableList<UserItem>>

    //Specific user
    @GET("/users/{id}")
    fun getUser(@Header("Authorization") token: String, @Path(value = "id", encoded = false) id: Long): Call<UserItem>

    //Get user authenticated
    @GET("/users/me")
    fun getMe(@Header("Authorization") token: String): Call<UserItem>

    //Update user
    @PUT("/users/{id}")
    fun updateUser(@Header("Authorization") token: String, @Path("id") id: Long?, @Body user: UpdateUserItem?): Call<ResponseBody?>

    //Add user
    @POST("/users")
    fun addUser(@Header("Authorization") token: String, @Body user: InsertUserItem): Call<ResponseBody>

    //Login
    @POST("/auth/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    //Change password
    @PATCH("/users/me/changepassword")
    fun changePassword(@Header("Authorization") token: String, @Body userPassword: UserPassword): Call<ResponseBody>

    //////////////// OPERATING FUNDS ////////////////
    //By user
    @GET("/operatingfunds/{userId}")
    fun getOpFunds(@Header("Authorization") token: String, @Path(value = "userId", encoded = false) id: Long): Call<MutableList<OpFundItem>>

    //Get user authenticated
    @GET("/operatingfunds/me")
    fun getMyOpFund(@Header("Authorization") token: String): Call<MutableList<OpFundItem>>

    //Post user operating fund
    @POST("/operatingfunds/me")
    fun addOperatingFund(@Header("Authorization") token: String, @Body opFund: InsertOpFundItem): Call<ResponseBody>

    //Update user's operating fund
    @PUT("/operatingfunds/{userId}")
    fun updateOpFund(@Header("Authorization") token: String, @Path("userId") id: Long?, @Body opFund: InsertOpFundItem?): Call<ResponseBody?>

    //////////////// STORES ////////////////
    //All stores
    @GET("/stores")
    fun getStores(@Header("Authorization") token: String): Call<MutableList<StoreItem>>

    //Specific store
    @GET("/stores/{id}")
    fun getStore(@Header("Authorization") token: String, @Path(value = "id", encoded = false) id: Long): Call<StoreItem>

    //Create store
    @POST("/stores")
    fun addStore(@Header("Authorization") token: String, @Body store: UpdateStoreItem): Call<ResponseBody>

    //Update store
    @PUT("/stores/{id}")
    fun updateStore(@Header("Authorization") token: String, @Path("id") id: Long?, @Body store: UpdateStoreItem?): Call<ResponseBody?>


    //////////////// PRODUCTS ////////////////
    //All products
    @GET("/products")
    fun getProducts(@Header("Authorization") token: String): Call<MutableList<ProductItem>>

    //Specific product by id
    @GET("/products/{id}")
    fun getProduct(@Header("Authorization") token: String, @Path(value = "id", encoded = false) id: Long): Call<ProductItem>

    //Create product
    @POST("/products")
    fun addProduct(@Header("Authorization") token: String, @Body opFund: InsertProductItem): Call<ResponseBody>

    //Update product
    @PUT("/products/{id}")
    fun updateProduct(@Header("Authorization") token: String, @Path("id") id: Long?, @Body product: UpdateProductItem?): Call<ResponseBody?>

    //////////////// STOCK MOVEMENTS ////////////////
    //By product
    @GET("/stockmovements/{productId}")
    fun getStockMovements(@Header("Authorization") token: String, @Path(value = "productId", encoded = false) id: Long): Call<MutableList<StockMovItem>>

    //Create by product
    @POST("/stockmovements/{productId}")
    fun addStockMovement(@Header("Authorization") token: String, @Path(value = "productId", encoded = false) id: Long, @Body stockMov: InsertStockMovItem): Call<ResponseBody>

    //////////////// IVA ////////////////
    @GET("/iva")
    fun getIvaValues(@Header("Authorization") token: String): Call<MutableList<IvaItem>>


    //////////////// INVOICES ////////////////
    //All invoices
    @GET("/invoices")
    fun getInvoices(@Header("Authorization") token: String): Call<MutableList<InvoiceItem>>

    //Specific invoice
    @GET("/invoices/{id}")
    fun getInvoice(@Header("Authorization") token: String, @Path(value = "id", encoded = false) id: Long): Call<InvoiceItem>

    //Create invoice
    @POST("/invoices")
    fun addInvoice(@Header("Authorization") token: String, @Body invoice: InsertInvItem): Call<ResponseBody>

    //////////////// INVOICED PRODUCTS ////////////////
    //All invoiced products
    //@GET("/invoicedproducts")
    //fun getInvoicedProducts(@Header("Authorization") token: String): Call<MutableList<InvProdItem>>

    //////////////// CASH REGISTERS ////////////////
    //All cash registers
    @GET("/cashregisters")
    fun getCashRegisters(@Header("Authorization") token: String): Call<MutableList<CashRegisterItem>>

}